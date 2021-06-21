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

package ve.cyclos.fitness;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import ve.cyclos.fitness.data.AppDatabase;
import ve.cyclos.fitness.data.UserPreferences;
import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.data.WorkoutSample;
import ve.cyclos.fitness.data.WorkoutType;
import ve.cyclos.fitness.recording.WorkoutRecorder;
import ve.cyclos.fitness.util.DataManager;
import ve.cyclos.fitness.util.CyclosAppThemes;
import ve.cyclos.fitness.util.UserDateTimeUtils;
import ve.cyclos.fitness.util.unit.DistanceUnitUtils;
import ve.cyclos.fitness.util.unit.EnergyUnitUtils;

public class Instance {

    private static Instance instance;
    public static Instance getInstance(Context context){
        if (context == null) {
            Log.e("Instance", "no Context Provided");
        }
        if(instance == null){
            instance= new Instance(context);
        }
        return instance;
    }


    public final AppDatabase db;
    public WorkoutRecorder recorder;
    public final UserPreferences userPreferences;
    public final CyclosAppThemes themes;
    public final UserDateTimeUtils userDateTimeUtils;
    public final DistanceUnitUtils distanceUnitUtils;
    public final EnergyUnitUtils energyUnitUtils;

    private Instance(Context context) {
        instance = this;
        userPreferences = new UserPreferences(context);
        themes = new CyclosAppThemes(context);
        userDateTimeUtils = new UserDateTimeUtils(userPreferences);
        distanceUnitUtils = new DistanceUnitUtils(context);
        energyUnitUtils = new EnergyUnitUtils(context);
        db = AppDatabase.provideDatabase(context);

        recorder = restoreRecorder(context);

        startBackgroundClean(context);
    }

    private void startBackgroundClean(Context context) {
        DataManager.cleanFilesASync(context);
    }

    private WorkoutRecorder restoreRecorder(Context context) {
        Workout lastWorkout = db.workoutDao().getLastWorkout();
        if (lastWorkout != null && lastWorkout.end == -1) {
            return restoreRecorder(context, lastWorkout);
        }
        return new WorkoutRecorder(context, WorkoutType.getWorkoutTypeById(context, WorkoutType.WORKOUT_TYPE_ID_OTHER));
    }

    private WorkoutRecorder restoreRecorder(Context context, Workout workout) {
        List<WorkoutSample> samples = Arrays.asList(db.workoutDao().getAllSamplesOfWorkout(workout.id));
        return new WorkoutRecorder(context, workout, samples);
    }

    public void prepareResume(Context context, Workout workout) {
        recorder = restoreRecorder(context, workout);
    }
}
