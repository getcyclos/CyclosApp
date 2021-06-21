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

package ve.cyclos.fitness.aggregation;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.data.Workout;

public class WorkoutAggregator {

    private final Context context;
    private final WorkoutFilter filter;
    private final WorkoutInformation information;
    private final AggregationSpan span;

    public WorkoutAggregator(Context context, WorkoutFilter filter, WorkoutInformation information, AggregationSpan span) {
        this.context = context;
        this.filter = filter;
        this.information = information;
        this.span = span;
    }

    public AggregatedWorkoutData aggregate() {
        return new AggregatedWorkoutData(getResults(), span);
    }

    private List<WorkoutInformationResult> getResults() {
        List<WorkoutInformationResult> results = new ArrayList<>();
        Workout[] workouts = Instance.getInstance(context).db.workoutDao().getWorkouts();
        for (Workout workout : workouts) {
            if (filter.isAccepted(workout)) {
                results.add(getResultFor(workout));
            }
        }
        return results;
    }

    private WorkoutInformationResult getResultFor(Workout workout) {
        return new WorkoutInformationResult(workout, information.getValueFromWorkout(workout));
    }
}
