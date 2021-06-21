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

import ve.cyclos.fitness.aggregation.information.AverageMotionSpeed;
import ve.cyclos.fitness.aggregation.information.AveragePace;
import ve.cyclos.fitness.aggregation.information.AverageTotalSpeed;
import ve.cyclos.fitness.aggregation.information.BurnedEnergy;
import ve.cyclos.fitness.aggregation.information.Distance;
import ve.cyclos.fitness.aggregation.information.Duration;
import ve.cyclos.fitness.aggregation.information.EnergyConsumption;
import ve.cyclos.fitness.aggregation.information.TopSpeed;
import ve.cyclos.fitness.aggregation.information.WorkoutCount;

public class WorkoutInformationManager {

    private final Context context;
    private final List<WorkoutInformation> information = new ArrayList<>();

    public WorkoutInformationManager(Context context) {
        this.context = context;
        addInformation();
    }

    private void addInformation() {
        information.add(new Distance(context));
        information.add(new Duration(context));
        information.add(new AverageMotionSpeed(context));
        information.add(new AverageTotalSpeed(context));
        information.add(new AveragePace(context));
        information.add(new TopSpeed(context));
        information.add(new BurnedEnergy(context));
        information.add(new EnergyConsumption(context));
        information.add(new WorkoutCount(context));

    }

    public List<WorkoutInformation> getInformation() {
        return information;
    }

}
