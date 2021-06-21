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

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.aggregation.WorkoutInformation;
import ve.cyclos.fitness.util.unit.DistanceUnitUtils;
import ve.cyclos.fitness.util.unit.EnergyUnitUtils;

public abstract class AbstractWorkoutInformation implements WorkoutInformation {

    protected Context context;
    protected DistanceUnitUtils distanceUnitUtils;
    protected EnergyUnitUtils energyUnitUtils;

    AbstractWorkoutInformation(Context context) {
        this.context = context;
        this.distanceUnitUtils = Instance.getInstance(context).distanceUnitUtils;
        this.energyUnitUtils = Instance.getInstance(context).energyUnitUtils;
    }

}
