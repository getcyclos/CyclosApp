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

package ve.cyclos.fitness.util.io.general;

import android.content.Context;

import ve.cyclos.fitness.data.WorkoutData;
import ve.cyclos.fitness.data.WorkoutSample;
import ve.cyclos.fitness.recording.WorkoutSaver;

public class ImportWorkoutSaver extends WorkoutSaver {

    public ImportWorkoutSaver(Context context, WorkoutData data) {
        super(context, data);
    }

    public void saveWorkout() {
        setIds();
        setLength();
        setSpeed();
        setTopSpeed();

        setHeartRate();

        setMSLElevation();
        setAscentAndDescent();

        setCalories();

        storeInDatabase();
    }

    private void setSpeed() {
        setTopSpeed();
        if (samples.size() == 0) {
            return;
        }
        if (workout.topSpeed != 0) {
            // Speed values already present
            return;
        }
        WorkoutSample lastSample = samples.get(0);
        for(WorkoutSample sample : samples){
            double distance = lastSample.toLatLong().sphericalDistance(sample.toLatLong());
            long timeDiff = sample.absoluteTime - lastSample.absoluteTime;
            if (timeDiff != 0) {
                sample.speed = distance / ((double) timeDiff / 1000);
            }
            lastSample = sample;
        }
    }

}
