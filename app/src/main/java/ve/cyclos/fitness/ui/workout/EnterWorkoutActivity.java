/*
 * Copyright (c) 2020 Gabriel Estrada <dev@getcyclos.com>
 *
 * This file is part of CyclosApp
 *
 * CyclosApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CyclosApp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ve.cyclos.fitness.ui.workout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.data.WorkoutBuilder;
import ve.cyclos.fitness.data.WorkoutType;
import ve.cyclos.fitness.ui.dialog.DatePickerFragment;
import ve.cyclos.fitness.ui.dialog.DurationPickerDialogFragment;
import ve.cyclos.fitness.ui.dialog.SelectWorkoutTypeDialog;
import ve.cyclos.fitness.ui.dialog.TimePickerFragment;
import ve.cyclos.fitness.util.unit.DistanceUnitSystem;
import ve.cyclos.fitness.util.unit.UnitUtils;

public class EnterWorkoutActivity extends InformationActivity implements SelectWorkoutTypeDialog.WorkoutTypeSelectListener,
        DatePickerFragment.DatePickerCallback, TimePickerFragment.TimePickerCallback, DurationPickerDialogFragment.DurationPickListener {

    public static final String WORKOUT_ID_EXTRA = "ve.cyclos.fitness.EnterWorkoutActivity.WORKOUT_ID_EXTRA";

    WorkoutBuilder workoutBuilder;
    TextView typeTextView, dateTextView, timeTextView, durationTextView;
    EditText distanceEditText, commentEditText;
    private DistanceUnitSystem unitSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_workout);

        initRoot();

        setTitle(R.string.enterWorkout);
        setupActionBar();

        workoutBuilder = new WorkoutBuilder(this);
        unitSystem = Instance.getInstance(this).distanceUnitUtils.getDistanceUnitSystem();

        addTitle(getString(R.string.info));
        KeyValueLine typeLine = addKeyValueLine(getString(R.string.type));
        typeTextView = typeLine.value;
        typeLine.lineRoot.setOnClickListener(v -> showTypeSelection());

        distanceEditText = createEditText();
        distanceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        distanceEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void afterTextChanged(Editable arg0) {
                String str = distanceEditText.getText().toString();
                if (str.isEmpty()) return;
                String str2 = PerfectDecimal(str, 3, 2);

                if (!str2.equals(str)) {
                    distanceEditText.setText(str2);
                    int pos = distanceEditText.getText().length();
                    distanceEditText.setSelection(pos);
                }
            }
        });
        distanceEditText.setOnEditorActionListener((v, actionId, event) -> {
            // If the User clicks on the finish button on the keyboard, continue by showing the date selection
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_NEXT ||
                    event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event == null || !event.isShiftPressed()) {
                    showDateSelection();
                    return true;
                }
            }
            return false;
        });
        KeyValueLine distanceLine = addKeyValueLine(getString(R.string.workoutDistance), distanceEditText, unitSystem.getLongDistanceUnit());
        distanceLine.lineRoot.setOnClickListener(v -> requestKeyboard(distanceEditText));


        KeyValueLine dateLine = addKeyValueLine(getString(R.string.workoutDate));
        dateLine.lineRoot.setOnClickListener(v -> showDateSelection());
        dateTextView = dateLine.value;

        KeyValueLine timeLine = addKeyValueLine(getString(R.string.workoutStartTime));
        timeLine.lineRoot.setOnClickListener(v -> showTimeSelection());
        timeTextView = timeLine.value;

        KeyValueLine durationLine = addKeyValueLine(getString(R.string.workoutDuration));
        durationLine.lineRoot.setOnClickListener(v -> showDurationSelection());
        durationTextView = durationLine.value;

        addTitle(getString(R.string.comment));

        commentEditText = new EditText(this);
        commentEditText.setSingleLine(true);
        root.addView(commentEditText);

        Intent intent = getIntent();
        long workoutId = intent.getLongExtra(WORKOUT_ID_EXTRA, 0);
        if (workoutId != 0) {
            Workout workout = Instance.getInstance(this).db.workoutDao().getWorkoutById(workoutId);
            if (workout != null) {
                loadFromWorkout(workout);
            } else {
                Toast.makeText(this, R.string.cannotFindWorkout, Toast.LENGTH_LONG).show();
                finish();
            }
        }

        updateTextViews();
    }

    private void loadFromWorkout(Workout workout) {
        workoutBuilder = WorkoutBuilder.fromWorkout(this, workout);
        distanceEditText.setText(String.valueOf(
                UnitUtils.roundDouble(unitSystem.getDistanceFromKilometers(workoutBuilder.getLength() / 1000d), 3)
        ));
        commentEditText.setText(workoutBuilder.getComment());
        setTitle(R.string.editWorkout);
    }

    private void saveWorkout() {
        workoutBuilder.setComment(commentEditText.getText().toString());
        try {
            // uses LongDistance, needs to be converted to meters (long => short => meters)
            double longDistance = Double.parseDouble(distanceEditText.getText().toString());
            workoutBuilder.setLength((int) unitSystem.getMetersFromLongDistance(longDistance));
        } catch (NumberFormatException ignored) {
            distanceEditText.requestFocus();
            distanceEditText.setError(getString(R.string.errorEnterValidNumber));
            return;
        }
        if (workoutBuilder.getStart().getTimeInMillis() > System.currentTimeMillis()) {
            Toast.makeText(this, R.string.errorWorkoutAddFuture, Toast.LENGTH_LONG).show();
            return;
        }
        if (workoutBuilder.getDuration() < 1000) {
            Toast.makeText(this, R.string.errorEnterValidDuration, Toast.LENGTH_LONG).show();
            return;
        }
        Workout workout = workoutBuilder.saveWorkout(this);
        final Intent intent = new Intent(this, ShowWorkoutActivity.class);
        intent.putExtra(ShowWorkoutActivity.WORKOUT_ID_EXTRA, workout.id);
        startActivity(intent);
        finish();
    }

    private void updateTextViews() {
        typeTextView.setText(workoutBuilder.getWorkoutType().title);
        dateTextView.setText(SimpleDateFormat.getDateInstance().format(workoutBuilder.getStart().getTime()));
        timeTextView.setText(SimpleDateFormat.getTimeInstance().format(workoutBuilder.getStart().getTime()));
        durationTextView.setText(Instance.getInstance(this).distanceUnitUtils.getHourMinuteSecondTime(workoutBuilder.getDuration()));
    }

    private void showTypeSelection() {
        new SelectWorkoutTypeDialog(this, this).show();
    }

    @Override
    public void onSelectWorkoutType(WorkoutType workoutType) {
        workoutBuilder.setWorkoutType(workoutType);
        updateTextViews();
        requestKeyboard(distanceEditText);
    }

    private void showDateSelection() {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.callback = this;
        fragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDatePick(int year, int month, int day) {
        workoutBuilder.getStart().set(year, month, day);
        updateTextViews();
        showTimeSelection();
    }

    private void showTimeSelection() {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.callback = this;
        fragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onTimePick(int hour, int minute) {
        workoutBuilder.getStart().set(Calendar.HOUR_OF_DAY, hour);
        workoutBuilder.getStart().set(Calendar.MINUTE, minute);
        workoutBuilder.getStart().set(Calendar.SECOND, 0);
        updateTextViews();
        showDurationSelection();
    }

    private void showDurationSelection() {
        DurationPickerDialogFragment fragment = new DurationPickerDialogFragment(this, this, workoutBuilder.getDuration());
        fragment.listener = this;
        fragment.initialDuration = workoutBuilder.getDuration();
        fragment.show();
    }

    @Override
    public void onDurationPick(long duration) {
        workoutBuilder.setDuration(duration);
        updateTextViews();
        requestKeyboard(commentEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enter_workout_menu, menu);
        menu.findItem(R.id.actionEnterWorkoutAdd).setIcon(
                workoutBuilder.isFromExistingWorkout() ? R.drawable.ic_save : R.drawable.ic_add_white);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionEnterWorkoutAdd:
                saveWorkout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initRoot() {
        root = findViewById(R.id.enterWorkoutRoot);
    }

    /**
     * to fix how many number appear before and after point
     * @param str
     * @param MAX_BEFORE_POINT
     * @param MAX_DECIMAL
     * @return
     */
    public String PerfectDecimal(String str, int MAX_BEFORE_POINT, int MAX_DECIMAL){
        if(str.charAt(0) == '.') str = "0"+str;
        int max = str.length();

        StringBuilder rFinal = new StringBuilder();
        boolean after = false;
        int i = 0, up = 0, decimal = 0; char t;
        while(i < max){
            t = str.charAt(i);
            if(t != '.' && !after){
                up++;
                if(up > MAX_BEFORE_POINT)
                    return rFinal.toString();
            }else if(t == '.'){
                after = true;
            }else{
                decimal++;
                if(decimal > MAX_DECIMAL)
                    return rFinal.toString();
            }
            rFinal.append(t);
            i++;
        }return rFinal.toString();
    }
}
