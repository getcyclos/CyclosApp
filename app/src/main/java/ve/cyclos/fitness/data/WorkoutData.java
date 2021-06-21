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

package ve.cyclos.fitness.data;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import ve.cyclos.fitness.Instance;

public class WorkoutData {

    public static WorkoutData fromWorkout(Context context, Workout workout) {
        AppDatabase database = Instance.getInstance(context).db;
        return new WorkoutData(workout, Arrays.asList(database.workoutDao().getAllSamplesOfWorkout(workout.id)));
    }

    private Workout workout;
    private List<WorkoutSample> samples;

    public WorkoutData(Workout workout, List<WorkoutSample> samples) {
        this.workout = workout;
        this.samples = samples;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public List<WorkoutSample> getSamples() {
        return samples;
    }

    public void setSamples(List<WorkoutSample> samples) {
        this.samples = samples;
    }
}
