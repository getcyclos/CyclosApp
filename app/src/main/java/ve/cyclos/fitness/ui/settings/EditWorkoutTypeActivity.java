/*
 * Copyright (c) 2020 Gabriel Estrada <dev@getcyclos.com>
 *
 * This file is part of CyclosApp
 *
 *     CyclosApp is free software: you can redistribute it and/or modify
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

package ve.cyclos.fitness.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.regex.Pattern;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.WorkoutType;
import ve.cyclos.fitness.ui.dialog.IconPickerDialog;
import ve.cyclos.fitness.ui.workout.InformationActivity;
import ve.cyclos.fitness.util.Icon;
import ve.cyclos.fitness.util.unit.DistanceUnitUtils;

public class EditWorkoutTypeActivity extends InformationActivity implements IconPickerDialog.IconSelectListener {

    public static final String EXTRA_TYPE_ID = "type_id";

    private boolean isNewType;
    private String workoutTypeId = "";
    private WorkoutType type;

    private EditText idText, titleText, minDistanceText, METText;
    private ImageView iconView;

    private DistanceUnitUtils distanceUnitUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout_type);
        initRoot();

        setTitle(R.string.editWorkoutType);
        setupActionBar();

        distanceUnitUtils = Instance.getInstance(this).distanceUnitUtils;

        if (getIntent() != null && getIntent().getExtras() != null) {
            workoutTypeId = getIntent().getExtras().getString(EXTRA_TYPE_ID, "");
        }
        if (workoutTypeId.equals("")) {
            isNewType = true;
            type = new WorkoutType("", "", 5, getThemePrimaryColor(), Icon.RUNNING.name, 0);
        } else {
            isNewType = false;
            type = Instance.getInstance(this).db.workoutTypeDao().findById(workoutTypeId);
        }
        idText = addEditTextLine(getString(R.string.workoutTypeEditId));
        idText.setText(type.id);
        if (!isNewType) {
            // Don't let the user edit the ID
            idText.setEnabled(false);
        }

        titleText = addEditTextLine(getString(R.string.workoutTypeEditName));
        titleText.setText(type.title);

        minDistanceText = addEditTextLine(getString(R.string.workoutTypeEditMinDistance), distanceUnitUtils.getDistanceUnitSystem().getShortDistanceUnit());
        minDistanceText.setInputType(InputType.TYPE_CLASS_NUMBER);
        minDistanceText.setText(String.valueOf(type.minDistance));

        METText = addEditTextLine(getString(R.string.workoutTypeEditMET));
        METText.setInputType(InputType.TYPE_CLASS_NUMBER);
        METText.setText(String.valueOf(type.MET));

        iconView = new ImageView(this);
        KeyValueLine iconLine = addKeyValueLine(getString(R.string.workoutTypeEditIcon), iconView);
        iconLine.lineRoot.setOnClickListener(v -> openIconSelection());
        iconView.setImageResource(Icon.getIcon(type.icon));
        iconView.setColorFilter(getThemePrimaryColor());
    }

    private void openIconSelection() {
        new IconPickerDialog(this, this).show();
    }

    @Override
    public void onSelectIcon(Icon icon) {
        type.icon = icon.name;
        iconView.setImageResource(icon.iconRes);
    }

    @Override
    protected void initRoot() {
        root = findViewById(R.id.enterWorkoutTypeRoot);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_workout_type_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.actionSaveWorkoutType).setIcon(isNewType ? R.drawable.ic_add_white : R.drawable.ic_save);
        menu.findItem(R.id.actionDeleteWorkoutType).setVisible(!isNewType);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionDeleteWorkoutType) {
            showDeleteConfirmationDialog();
            return true;
        } else if (item.getItemId() == R.id.actionSaveWorkoutType) {
            checkAndSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.deleteWorkoutType)
                .setMessage(R.string.deleteWorkoutTypeConfirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> delete())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void checkAndSave() {
        if (!loadEditTextValuesToType()) {
            return;
        }
        if (type.id.isEmpty()) {
            showError(idText, getString(R.string.workoutTypeEditIdErrorEmpty));
            return;
        }
        if (!Pattern.matches("[a-zA-Z0-9-_]*", type.id)) {
            showError(idText, getString(R.string.workoutTypeEditIdErrorCharacters));
            return;
        }
        WorkoutType otherType = WorkoutType.getWorkoutTypeById(this, type.id);
        if (isNewType && otherType != null && !otherType.id.equals(WorkoutType.WORKOUT_TYPE_ID_OTHER)) {
            showError(idText, getString(R.string.workoutTypeEditIdErrorUnique));
            return;
        }
        if (type.title.isEmpty()) {
            showError(titleText, getString(R.string.workoutTypeEditNameError));
            return;
        }
        saveAndClose();
    }

    private boolean loadEditTextValuesToType() {
        type.id = idText.getText().toString();
        type.title = titleText.getText().toString();
        try {
            type.minDistance = Integer.parseInt(minDistanceText.getText().toString());
        } catch (NumberFormatException ignored) {
            showError(minDistanceText, getString(R.string.errorEnterValidNumber));
            return false;
        }
        try {
            type.MET = Integer.parseInt(METText.getText().toString());
        } catch (NumberFormatException ignored) {
            showError(METText, getString(R.string.errorEnterValidNumber));
            return false;
        }
        return true;
    }

    private void showError(EditText editText, String message) {
        editText.setError(message);
        requestKeyboard(editText);
    }

    private void saveAndClose() {
        if (isNewType) {
            Instance.getInstance(this).db.workoutTypeDao().insert(type);
        } else {
            Instance.getInstance(this).db.workoutTypeDao().update(type);
        }
        finish();
    }

    private void delete() {
        Instance.getInstance(this).db.workoutTypeDao().delete(type);
        finish();
    }

}