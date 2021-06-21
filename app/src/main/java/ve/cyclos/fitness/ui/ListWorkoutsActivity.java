/*
 * Copyright (c) 2021 Gabriel Estrada <dev@getcyclos.com>
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

package ve.cyclos.fitness.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.InputStream;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.data.WorkoutType;
import ve.cyclos.fitness.ui.adapter.WorkoutAdapter;
import ve.cyclos.fitness.ui.dialog.ProgressDialogController;
import ve.cyclos.fitness.ui.dialog.SelectWorkoutTypeDialog;
import ve.cyclos.fitness.ui.dialog.ThreadSafeProgressDialogController;
import ve.cyclos.fitness.ui.record.RecordWorkoutActivity;
import ve.cyclos.fitness.ui.settings.MainSettingsActivity;
import ve.cyclos.fitness.ui.workout.AggregatedWorkoutStatisticsActivity;
import ve.cyclos.fitness.ui.workout.EnterWorkoutActivity;
import ve.cyclos.fitness.ui.workout.ShowWorkoutActivity;
import ve.cyclos.fitness.util.DialogUtils;
import ve.cyclos.fitness.util.Icon;
import ve.cyclos.fitness.util.io.general.IOHelper;

public class ListWorkoutsActivity extends CyclosAppActivity implements WorkoutAdapter.WorkoutAdapterListener {

    private RecyclerView listView;
    private WorkoutAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionMenu menu;
    private Workout[] workouts;
    private TextView hintText;
    private int listSize;
    private int lastClickedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_workouts);

        listView = findViewById(R.id.workoutList);
        listView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        adapter = new WorkoutAdapter(workouts, this);
        listView.setAdapter(adapter);

        menu = findViewById(R.id.workoutListMenu);
        menu.setOnMenuButtonLongClickListener(v -> {
            if (workouts.length > 0) {
                startRecording(workouts[0].getWorkoutType(this));
                return true;
            } else {
                return false;
            }
        });

        hintText = findViewById(R.id.hintAddWorkout);

        findViewById(R.id.workoutListRecord).setOnClickListener(v -> showWorkoutSelection());

        /*findViewById(R.id.workoutListEnter).setOnClickListener(v -> startEnterWorkoutActivity());

          findViewById(R.id.workoutListImport).setOnClickListener(v -> showImportDialog());
        */
        checkFirstStart();

        refresh();
    }

    private final Handler mHandler = new Handler();

    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }
    }

    private void showImportDialog() {
        if (!hasPermission()) {
            requestPermissions();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.importWorkout)
                .setMessage(R.string.importWorkoutMultipleQuestion)
                .setPositiveButton(R.string.actionImport, (dialog, which) -> importWorkout())
                .setNeutralButton(R.string.actionImportMultiple, (dialog, which) -> showMassImportGpx())
                .show();
        refresh();
        menu.close(true);
    }

    private static final int FILE_IMPORT_SELECT_CODE = 21;
    private static final int FOLDER_IMPORT_SELECT_CODE = 23;

    private void importWorkout() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.importWorkout)), FILE_IMPORT_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ignored) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FILE_IMPORT_SELECT_CODE) {
                importFile(data.getData());
            } else if (requestCode == FOLDER_IMPORT_SELECT_CODE) {
                massImportGpx(data.getData());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importFile(Uri uri){
        ProgressDialogController dialogController= new ProgressDialogController(this, getString(R.string.importWorkout));
        dialogController.show();

        new Thread(() -> {
            try {
                InputStream stream = getContentResolver().openInputStream(uri);
                IOHelper.GpxImporter.importWorkout(getApplicationContext(), stream);
                mHandler.post(() -> {
                    Toast.makeText(this, R.string.workoutImported, Toast.LENGTH_LONG).show();
                    dialogController.cancel();
                    refresh();
                });
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.post(() -> {
                    dialogController.cancel();
                    showErrorDialog(e, R.string.error, R.string.errorImportFailed);
                });
            }
        }).start();
    }

    private void showMassImportGpx() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.importMultipleGpxFiles)
                .setMessage(R.string.importMultipleMessageSelectFolder)
                .setPositiveButton(R.string.okay, (dialog, which) -> openMassImportFolderSelector())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void openMassImportFolderSelector() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, FOLDER_IMPORT_SELECT_CODE);
    }

    private void massImportGpx(Uri dirUri) {
        Log.d("MassImport", dirUri.toString());
        ThreadSafeProgressDialogController dialog = new ThreadSafeProgressDialogController(this, getString(R.string.importingFiles));
        dialog.show();
        new Thread(() -> {
            try {
                int imported = 0;
                DocumentFile documentFile = DocumentFile.fromTreeUri(this, dirUri);
                DocumentFile[] files = documentFile.listFiles();
                for (int i = 0; i < files.length; i++) {
                    dialog.setProgress(100 * i / files.length);
                    DocumentFile file = files[i];
                    if (file.isFile() && file.canRead()) {
                        try {
                            Uri fileUri = file.getUri();
                            Log.d("MassImport", "Importing " + fileUri.toString());
                            IOHelper.GpxImporter.importWorkout(this, getContentResolver().openInputStream(fileUri));
                            imported++;
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (imported == 0 && i == files.length - 1) {
                                // If all workouts failed throw exception so it is shown to the user
                                throw e;
                            }
                        }
                    }
                }
                dialog.setProgress(100);
                final int tmpImported = imported; // Needs to be a final variable to use in the handler lambda
                mHandler.post(() -> {
                    dialog.cancel();
                    Toast.makeText(this, String.format(getString(R.string.importedWorkouts), tmpImported), Toast.LENGTH_LONG).show();
                    refresh();
                });
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.post(() -> {
                    dialog.cancel();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void checkFirstStart() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("firstStart", true)) {
            preferences.edit().putBoolean("firstStart", false).apply();
            new AlertDialog.Builder(this)
                    .setTitle(R.string.setPreferencesTitle)
                    .setMessage(R.string.setPreferencesMessage)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.settings, (dialog, which) -> startActivity(new Intent(ListWorkoutsActivity.this, MainSettingsActivity.class)))
                    .create().show();
        }
    }

    private void startEnterWorkoutActivity() {
        menu.close(true);
        final Intent intent = new Intent(this, EnterWorkoutActivity.class);
        new Handler().postDelayed(() -> startActivity(intent), 300);
    }


    private void showWorkoutSelection() {
        menu.close(true);
        new SelectWorkoutTypeDialog(this, this::startRecording).show();
    }

    private void startRecording(WorkoutType activity) {
        menu.close(true);
        final Intent intent = new Intent(this, RecordWorkoutActivity.class);
        intent.setAction(RecordWorkoutActivity.LAUNCH_ACTION);
        intent.putExtra(RecordWorkoutActivity.WORKOUT_TYPE_EXTRA, activity);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        menu.close(true);
    }

    @Override
    public void onItemClick(int pos, Workout workout) {
        final Intent intent = new Intent(this, ShowWorkoutActivity.class);
        intent.putExtra(ShowWorkoutActivity.WORKOUT_ID_EXTRA, workout.id);
        startActivity(intent);
        lastClickedIndex = pos;
    }

    @Override
    public void onItemLongClick(int pos, Workout workout) {
        DialogUtils.showDeleteWorkoutDialog(this, () -> {
            Instance.getInstance(ListWorkoutsActivity.this).db.workoutDao().deleteWorkout(workout);
            refresh();
        });
    }

    private void refresh() {
        loadData();
        if (workouts.length > lastClickedIndex) {
            adapter.notifyItemChanged(lastClickedIndex, workouts[lastClickedIndex]);
        }
        if (listSize != workouts.length) {
            adapter.notifyDataSetChanged();
        }
        listSize = workouts.length;
        refreshFABMenu();
    }

    private void loadData() {
        workouts = Instance.getInstance(this).db.workoutDao().getWorkouts();
        hintText.setVisibility(workouts.length == 0 ? View.VISIBLE : View.INVISIBLE);
        adapter.setWorkouts(workouts);
    }

    private void refreshFABMenu() {
        FloatingActionButton lastFab = findViewById(R.id.workoutListRecordLast);
        if (workouts.length > 0) {
            WorkoutType lastType = workouts[0].getWorkoutType(this);
            lastFab.setLabelText(lastType.title);
            lastFab.setImageResource(Icon.getIcon(lastType.icon));
            lastFab.setColorNormal(lastType.color);
            lastFab.setColorPressed(lastFab.getColorNormal());
            lastFab.setOnClickListener(v -> {
                menu.close(true);
                new Handler().postDelayed(() -> startRecording(lastType), 300);
            });
        } else {
            lastFab.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_workout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionOpenSettings) {
            startActivity(new Intent(this, MainSettingsActivity.class));
            return true;
        }

        if (id == R.id.actionOpenStatisticss) {
            startActivity(new Intent(this, AggregatedWorkoutStatisticsActivity.class));
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
