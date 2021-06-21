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

package ve.cyclos.fitness.aggregation.information;

import android.content.Context;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.aggregation.AggregationType;
import ve.cyclos.fitness.data.Workout;

public class Distance extends AbstractWorkoutInformation {
    public Distance(Context context) {
        super(context);
    }

    @Override
    public int getTitleRes() {
        return R.string.workoutDistance;
    }

    @Override
    public String getUnit() {
        return distanceUnitUtils.getDistanceUnitSystem().getLongDistanceUnit();
    }

    @Override
    public double getValueFromWorkout(Workout workout) {
        return distanceUnitUtils.getDistanceUnitSystem().getDistanceFromKilometers(workout.length / 1000d);
    }

    @Override
    public AggregationType getAggregationType() {
        return AggregationType.SUM;
    }
}
