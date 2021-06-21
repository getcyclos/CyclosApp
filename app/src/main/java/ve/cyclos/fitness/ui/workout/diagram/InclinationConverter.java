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

package ve.cyclos.fitness.ui.workout.diagram;

import android.content.Context;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.WorkoutData;
import ve.cyclos.fitness.data.WorkoutManager;
import ve.cyclos.fitness.data.WorkoutSample;

public class InclinationConverter extends AbstractSampleConverter {

    public InclinationConverter(Context context) {
        super(context);
    }

    @Override
    public void onCreate(WorkoutData data) {
        WorkoutManager.calculateInclination(data.getSamples());
    }

    @Override
    public float getValue(WorkoutSample sample) {
        return sample.tmpInclination;
    }

    @Override
    public String getName() {
        return getString(R.string.inclination);
    }

    @Override
    public String getUnit() {
        return "%";
    }

    @Override
    public int getColor() {
        return R.color.diagramInclination;
    }
}
