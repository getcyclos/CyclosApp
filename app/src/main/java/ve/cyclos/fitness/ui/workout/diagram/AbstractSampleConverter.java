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

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.github.mikephil.charting.charts.CombinedChart;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.util.unit.DistanceUnitUtils;
import ve.cyclos.fitness.util.unit.EnergyUnitUtils;

public abstract class AbstractSampleConverter implements SampleConverter {

    private final Context context;
    protected final DistanceUnitUtils distanceUnitUtils;
    protected final EnergyUnitUtils energyUnitUtils;

    public AbstractSampleConverter(Context context) {
        this.context = context;
        this.distanceUnitUtils = Instance.getInstance(context).distanceUnitUtils;
        this.energyUnitUtils = Instance.getInstance(context).energyUnitUtils;
    }

    protected String getString(@StringRes int resId) {
        return context.getString(resId);
    }

    @Override
    public void afterAdd(CombinedChart chart) {
    } // Mostly not needed

    @Override
    public String getDescription() {
        return "min - " + getUnit();
    }

    @Override
    public boolean isIntervalSetVisible() {
        return false;
    } // Defaults to false

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj != null && getClass().equals(obj.getClass());
    }
}
