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

package ve.cyclos.fitness.export;

import android.content.Context;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.AppDatabase;
import ve.cyclos.fitness.data.Interval;
import ve.cyclos.fitness.data.IntervalSet;
import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.data.WorkoutSample;
import ve.cyclos.fitness.data.WorkoutType;

public class RestoreController {

    private final Context context;
    private final Uri input;
    private final ImportStatusListener listener;
    private final boolean replace;
    private CyclosAppDataContainer dataContainer;
    private final AppDatabase database;

    public RestoreController(Context context, Uri input, boolean replace, ImportStatusListener listener) {
        this.context = context;
        this.input = input;
        this.replace = replace;
        this.listener = listener;
        this.database = Instance.getInstance(context).db;
    }

    public void restoreData() throws IOException, UnsupportedVersionException {
        listener.onStatusChanged(0, context.getString(R.string.loadingFile));
        loadDataFromFile();
        checkVersion();
        restoreDatabase();
        listener.onStatusChanged(100, context.getString(R.string.finished));
    }

    private void loadDataFromFile() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        dataContainer = xmlMapper.readValue(context.getContentResolver().openInputStream(input), CyclosAppDataContainer.class);
    }

    private void checkVersion() throws UnsupportedVersionException {
        if (dataContainer.getVersion() > BackupController.VERSION) {
            throw new UnsupportedVersionException("Version Code" + dataContainer.getVersion() + " is unsupported!");
        }
    }

    private void restoreDatabase() {
        database.runInTransaction(() -> {
            if (replace) {
                resetDatabase();
            }
            restoreWorkouts();
            restoreSamples();
            restoreIntervalSets();
            restoreWorkoutTypes();
        });
    }

    private void resetDatabase() {
        database.clearAllTables();
    }

    private void restoreWorkouts() {
        listener.onStatusChanged(60, context.getString(R.string.workouts));
        if (dataContainer.getWorkouts() != null) {
            for (Workout workout : dataContainer.getWorkouts()) {
                // Only Import Unknown Workouts on merge
                if (replace || database.workoutDao().findById(workout.id) == null) {
                    database.workoutDao().insertWorkout(workout);
                }
            }
        }
    }

    private void restoreSamples() {
        listener.onStatusChanged(70, context.getString(R.string.locationData));
        if (dataContainer.getSamples() != null) {
            for (WorkoutSample sample : dataContainer.getSamples()) {
                // Only import unknown samples with known workout on merge
                // Query not necessary on replace because data was cleared
                if (replace || (database.workoutDao().findById(sample.workoutId) != null &&
                        database.workoutDao().findSampleById(sample.id) == null)) {
                    database.workoutDao().insertSample(sample);
                }
            }
        }
    }

    private void restoreIntervalSets() {
        listener.onStatusChanged(90, context.getString(R.string.intervalSets));
        if (dataContainer.getIntervalSets() != null) {
            for (IntervalSetContainer container : dataContainer.getIntervalSets()) {
                restoreIntervalSet(container);
            }
        }
    }

    private void restoreIntervalSet(IntervalSetContainer container) {
        IntervalSet set = container.getSet();
        // Only Import unknownInterval Sets
        if(database.intervalDao().getSet(set.id) == null) {
            database.intervalDao().insertIntervalSet(set);
        }
        if (container.getIntervals() != null) {
            for (Interval interval : container.getIntervals()) {
                // Only Import Unknown Intervals
                if (database.intervalDao().findById(interval.id) == null) {
                    database.intervalDao().insertInterval(interval);
                }
            }
        }
    }

    private void restoreWorkoutTypes() {
        listener.onStatusChanged(95, context.getString(R.string.customWorkoutTypesTitle));
        if (dataContainer.getWorkoutTypes() != null) {
            for (WorkoutType type : dataContainer.getWorkoutTypes()) {
                // Only import unknown workout types
                if (database.workoutTypeDao().findById(type.id) == null) {
                    database.workoutTypeDao().insert(type);
                }
            }
        }
    }


    public interface ImportStatusListener {
        void onStatusChanged(int progress, String action);
    }

    static class UnsupportedVersionException extends Exception {
        UnsupportedVersionException(String message) {
            super(message);
        }
    }

}
