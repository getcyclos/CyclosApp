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

package ve.cyclos.fitness.ui.dialog;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.WorkoutType;
import ve.cyclos.fitness.aggregation.WorkoutTypeFilter;
import ve.cyclos.fitness.ui.CyclosAppActivity;

public class SelectWorkoutTypeDialogAll extends SelectWorkoutTypeDialog {

    public SelectWorkoutTypeDialogAll(CyclosAppActivity context, WorkoutTypeSelectListener listener) {
        super(context, listener);
        this.options.add(0, new WorkoutType(WorkoutTypeFilter.ID_ALL,
                context.getString(R.string.workoutTypeAll),0,
                context.getThemePrimaryColor(), "list", 0));
    }

}
