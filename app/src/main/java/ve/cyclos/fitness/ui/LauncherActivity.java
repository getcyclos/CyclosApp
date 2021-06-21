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

package ve.cyclos.fitness.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.map.MapManager;
import ve.cyclos.fitness.recording.WorkoutRecorder;
import ve.cyclos.fitness.ui.record.RecordWorkoutActivity;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeNoActionbar);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume(){
        super.onResume();
        new Handler().postDelayed(this::init, 100);
    }

    private void init() {
        Instance.getInstance(this);
        MapManager.initMapProvider(this);
        start();
    }

    private void start() {
        WorkoutRecorder recorder = Instance.getInstance(this).recorder;
        if (recorder.getState() == WorkoutRecorder.RecordingState.PAUSED ||
                recorder.getState() == WorkoutRecorder.RecordingState.RUNNING) {
            // Resume to running Workout
            Intent recorderActivityIntent = new Intent(this, RecordWorkoutActivity.class);
            recorderActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            recorderActivityIntent.setAction(RecordWorkoutActivity.RESUME_ACTION);
            startActivity(recorderActivityIntent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.stay);
        } else {
            // Go to Workout List
            Intent listWorkoutActivityIntent = new Intent(this, ListWorkoutsActivity.class);
            listWorkoutActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(listWorkoutActivityIntent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.stay);
        }
    }
}
