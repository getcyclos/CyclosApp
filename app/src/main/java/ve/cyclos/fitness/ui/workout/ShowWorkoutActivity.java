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

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ve.cyclos.fitness.BuildConfig;
import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.WorkoutSample;
import ve.cyclos.fitness.osm.OAuthAuthentication;
import ve.cyclos.fitness.osm.OsmTraceUploader;
import ve.cyclos.fitness.ui.ShareFileActivity;
import ve.cyclos.fitness.ui.dialog.ProgressDialogController;
import ve.cyclos.fitness.ui.record.RecordWorkoutActivity;
import ve.cyclos.fitness.ui.workout.diagram.HeartRateConverter;
import ve.cyclos.fitness.ui.workout.diagram.HeightConverter;
import ve.cyclos.fitness.ui.workout.diagram.SampleConverter;
import ve.cyclos.fitness.ui.workout.diagram.SpeedConverter;
import ve.cyclos.fitness.util.DataManager;
import ve.cyclos.fitness.util.DialogUtils;
import ve.cyclos.fitness.util.io.general.IOHelper;
import ve.cyclos.fitness.util.sections.SectionListModel;
import ve.cyclos.fitness.util.sections.SectionListPresenter;
import ve.cyclos.fitness.util.sections.SectionListView;
import de.westnordost.osmapi.traces.GpsTraceDetails;
import oauth.signpost.OAuthConsumer;

public class ShowWorkoutActivity extends WorkoutActivity implements DialogUtils.WorkoutDeleter {

    TextView commentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBeforeContent();

        setContentView(R.layout.activity_show_workout);
        initRoot();

        initAfterContent();

        commentView = addText("", true);
        commentView.setOnClickListener(v -> openEditCommentDialog());
        updateCommentText();

        if (hasSamples()) {
            addTitle(getString(R.string.workoutRoute));

            addMap();

            mapView.setClickable(false);
            mapRoot.setOnClickListener(v -> startFullscreenMapActivity());

        }

                
        addTitle(getString(R.string.workoutTime));
        addKeyValue(getString(R.string.workoutDate), getDate());
        addKeyValue(getString(R.string.workoutDuration), distanceUnitUtils.getHourMinuteSecondTime(workout.duration),
                getString(R.string.workoutPauseDuration), distanceUnitUtils.getHourMinuteSecondTime(workout.pauseDuration));
        addKeyValue(getString(R.string.workoutStartTime), Instance.getInstance(this).userDateTimeUtils.formatTime(new Date(workout.start)),
                getString(R.string.workoutEndTime), Instance.getInstance(this).userDateTimeUtils.formatTime(new Date(workout.end)));

        addKeyValue(getString(R.string.workoutDistance), distanceUnitUtils.getDistance(workout.length), getString(R.string.workoutPace), distanceUnitUtils.getPace(workout.avgPace));




        addTitle(getString(R.string.workoutSpeed));

        if (hasSamples()) {
            addKeyValue(getString(R.string.avgSpeedInMotion), distanceUnitUtils.getSpeed(workout.avgSpeed),
                    getString(R.string.avgSpeedTotalShort), distanceUnitUtils.getSpeed(workout.getAvgSpeedTotal()));

            addKeyValue(getString(R.string.workoutTopSpeed), distanceUnitUtils.getSpeed(workout.topSpeed));

            addDiagram(new SpeedConverter(this), ShowWorkoutMapDiagramActivity.DIAGRAM_TYPE_SPEED);
        } else {
            addKeyValue(getString(R.string.workoutAvgSpeedShort), distanceUnitUtils.getSpeed(workout.avgSpeed));
        }

        if (workout.hasHeartRateData()) {
            addTitle(getString(R.string.workoutHeartRate));
            addKeyValue(getString(R.string.workoutAvgHeartRate), workout.avgHeartRate + " bpm",
                    getString(R.string.workoutMaxHeartRate), workout.maxHeartRate + " bpm");

            addDiagram(new HeartRateConverter(this), ShowWorkoutMapDiagramActivity.DIAGRAM_TYPE_HEART_RATE);
        }

        addTitle(getString(R.string.workoutBurnedEnergy));
        addKeyValue(getString(R.string.workoutTotalEnergy), energyUnitUtils.getEnergy(workout.calorie),
                getString(R.string.workoutEnergyConsumption), energyUnitUtils.getRelativeEnergy((double) workout.calorie / ((double) workout.duration / 1000 / 60)));

        if (hasSamples()) {
            addTitle(getString(R.string.height));

            addKeyValue(getString(R.string.workoutAscent), distanceUnitUtils.getElevation((int) workout.ascent),
                    getString(R.string.workoutDescent), distanceUnitUtils.getElevation((int) workout.descent));

            addDiagram(new HeightConverter(this), ShowWorkoutMapDiagramActivity.DIAGRAM_TYPE_HEIGHT);

            addTitle(getString(R.string.sections));
            addSectionList();
        }
    }

    private void addSectionList() {
        SectionListView listView = new SectionListView(this);
        SectionListModel listModel = new SectionListModel(workout, samples);
        SectionListPresenter listPresenter = new SectionListPresenter(listView, listModel);
        root.addView(listView);
    }

    void addDiagram(SampleConverter converter, String mapDiagramActivityExtra) {
        addDiagram(converter).setOnClickListener(v -> startDiagramActivity(mapDiagramActivityExtra));
    }

    private void startDiagramActivity(String diagramType) {
        final Intent intent = new Intent(this, ShowWorkoutMapDiagramActivity.class);
        intent.putExtra(ShowWorkoutActivity.WORKOUT_ID_EXTRA, workout.id);
        intent.putExtra(ShowWorkoutMapDiagramActivity.DIAGRAM_TYPE_EXTRA, diagramType);
        startActivity(intent);
    }

    private void startFullscreenMapActivity() {
        final Intent intent = new Intent(this, ShowWorkoutMapActivity.class);
        intent.putExtra(ShowWorkoutActivity.WORKOUT_ID_EXTRA, workout.id);
        startActivity(intent);
    }


    private void openEditCommentDialog() {
        final EditText editText = new EditText(this);
        editText.setText(workout.comment);
        editText.setSingleLine(true);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        requestKeyboard(editText);
        new AlertDialog.Builder(this)
                .setTitle(R.string.enterComment)
                .setPositiveButton(R.string.okay, (dialog, which) -> changeComment(editText.getText().toString()))
                .setView(editText).create().show();
    }

    private void changeComment(String comment) {
        workout.comment = comment;
        Instance.getInstance(this).db.workoutDao().updateWorkout(workout);
        updateCommentText();
    }

    private void updateCommentText() {
        String str = "";
        if (workout.edited) {
            str += getString(R.string.workoutEdited);
        }
        if (workout.comment != null && workout.comment.length() > 0) {
            if (str.length() > 0) {
                str += "\n";
            }
            str += getString(R.string.comment) + ": " + workout.comment;
        }
        if (str.length() == 0) {
            str = getString(R.string.noComment);
        }
        commentView.setText(str);
    }

    private String getDate() {
        return Instance.getInstance(this).userDateTimeUtils.formatDate(new Date(workout.start));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_workout_menu, menu);
        menu.findItem(R.id.actionResumeWorkout).setVisible(isLastWorkout());
        menu.findItem(R.id.actionUploadOSM).setVisible(hasSamples());
        menu.findItem(R.id.actionExportGpx).setVisible(hasSamples());
        menu.findItem(R.id.actionShareWorkout).setVisible(hasSamples());
        return true;
    }

    private boolean isLastWorkout() {
        return Instance.getInstance(this).db.workoutDao().getLastWorkout().id == workout.id;
    }

    public void deleteWorkout() {
        Instance.getInstance(this).db.workoutDao().deleteWorkout(workout);
        finish();
    }

    private void showDeleteDialog() {
        DialogUtils.showDeleteWorkoutDialog(this, this);
    }

    private void exportToGpx() {
        if (!hasStoragePermission()) {
            requestStoragePermissions();
            return;
        }
        ProgressDialogController dialogController = new ProgressDialogController(this, getString(R.string.exporting));
        dialogController.setIndeterminate(true);
        dialogController.show();
        new Thread(() -> {
            try {
                String file = DataManager.getSharedDirectory(this) + String.format("/workout-%s-%s.gpx", workout.getSafeDateString(), workout.getSafeComment());
                File parent = new File(file).getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    throw new IOException("Cannot write to " + file);
                }
                Uri uri = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(file));


                IOHelper.GpxExporter.exportWorkout(workout, samples, new File(file));
                mHandler.post(() -> {
                    dialogController.cancel();
                    Intent intent = new Intent(this, ShareFileActivity.class);
                    intent.putExtra(ShareFileActivity.EXTRA_FILE_URI, uri.toString());
                    startActivity(intent);
                });
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.post(() -> {
                    dialogController.cancel();
                    showErrorDialog(e, R.string.error, R.string.errorGpxExportFailed);
                });
            }
        }).start();
    }

    private OAuthConsumer oAuthConsumer = null;

    private void prepareUpload() {
        OAuthAuthentication authentication = new OAuthAuthentication(mHandler, this, new OAuthAuthentication.OAuthAuthenticationListener() {
            @Override
            public void authenticationFailed() {
                new AlertDialog.Builder(ShowWorkoutActivity.this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.authenticationFailed)
                        .setPositiveButton(R.string.okay, null)
                        .create().show();
            }

            @Override
            public void authenticationComplete(OAuthConsumer consumer) {
                oAuthConsumer = consumer;
                showUploadOptions();
            }
        });

        authentication.authenticateIfNecessary();
    }

    private AlertDialog dialog = null;

    private void showUploadOptions() {
        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.actionUploadToOSM)
                .setView(R.layout.dialog_upload_osm)
                .setPositiveButton(R.string.upload, null) // Listener added later so that we can control if the dialog is dismissed on click
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                CheckBox checkBox= dialog.findViewById(R.id.uploadCutting);
                Spinner spinner= dialog.findViewById(R.id.uploadVisibility);
                EditText descriptionEdit= dialog.findViewById(R.id.uploadDescription);
                String description= descriptionEdit.getText().toString().trim();
                if(description.length() <= 2){
                    descriptionEdit.setError(getString(R.string.enterDescription));
                    requestKeyboard(descriptionEdit);
                    return;
                }
                GpsTraceDetails.Visibility visibility;
                switch (spinner.getSelectedItemPosition()){
                    case 0: visibility= GpsTraceDetails.Visibility.IDENTIFIABLE; break;
                    default:
                    case 1: visibility= GpsTraceDetails.Visibility.TRACKABLE; break;
                    case 2: visibility= GpsTraceDetails.Visibility.PRIVATE; break;
                }
                dialog.dismiss();
                uploadToOsm(checkBox.isChecked(), visibility, description);
            });
        });
        dialog.show();

    }

    private void uploadToOsm(boolean cut, GpsTraceDetails.Visibility visibility, String description) {
        List<WorkoutSample> samples = new ArrayList<>(this.samples);
        new OsmTraceUploader(this, mHandler, workout, samples, visibility, oAuthConsumer, cut, description).upload();
    }

    private void openEditWorkoutActivity() {
        final Intent intent = new Intent(this, EnterWorkoutActivity.class);
        intent.putExtra(EnterWorkoutActivity.WORKOUT_ID_EXTRA, workout.id);
        startActivity(intent);
        finish();
    }

    private void shareWorkoutActivity() {
        try {
            final Intent intent = new Intent(this, ShareWorkoutActivity.class);
            intent.putExtra(ShowWorkoutActivity.WORKOUT_ID_EXTRA, workout.id);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionDeleteWorkout:
                showDeleteDialog();
                return true;
            case R.id.actionExportGpx:
                exportToGpx();
                return true;
            case R.id.actionUploadOSM:
                prepareUpload();
                return true;
            /*case R.id.actionEditWorkout:
                openEditWorkoutActivity();
                return true;*/
            case R.id.actionResumeWorkout:
                showResumeConfirmation();
                return true;
            case R.id.actionShareWorkout:
                shareWorkoutActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showResumeConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.resumeWorkout)
                .setMessage(R.string.resumeWorkoutConfirmation)
                .setPositiveButton(R.string.actionResume, (dialog, which) -> resumeWorkout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void resumeWorkout() {
        // Load workout into instance
        Instance.getInstance(this).prepareResume(this, workout);

        // Start recording activity
        Intent recorderActivityIntent = new Intent(this, RecordWorkoutActivity.class);
        recorderActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        recorderActivityIntent.setAction(RecordWorkoutActivity.RESUME_ACTION);
        startActivity(recorderActivityIntent);
        finish();
    }

    @Override
    protected void initRoot() {
        root = findViewById(R.id.showWorkoutRoot);
    }
}
