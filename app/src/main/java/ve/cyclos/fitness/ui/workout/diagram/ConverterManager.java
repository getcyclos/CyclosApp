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

import java.util.ArrayList;
import java.util.List;

import ve.cyclos.fitness.data.WorkoutData;

public class ConverterManager {

    public List<SampleConverter> availableConverters = new ArrayList<>();
    public List<SampleConverter> selectedConverters = new ArrayList<>();

    public ConverterManager(Context context, WorkoutData data) {
        availableConverters.add(new SpeedConverter(context));
        availableConverters.add(new HeightConverter(context));
        availableConverters.add(new InclinationConverter(context));
        if (data.getWorkout().hasHeartRateData()) {
            availableConverters.add(new HeartRateConverter(context));
        }
    }


}
