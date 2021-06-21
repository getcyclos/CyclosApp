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

package ve.cyclos.fitness.util;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.annotation.StyleRes;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.WorkoutType;

public class CyclosAppThemes {

    private static final int THEME_SETTING_LIGHT = 0;
    private static final int THEME_SETTING_DARK = 1;

    private final Context context;

    public CyclosAppThemes(Context context) {
        this.context = context;
    }

    @StyleRes
    public int getDefaultTheme() {
        if (shouldUseLightMode()) {
            return R.style.AppTheme;
        } else {
            return R.style.AppThemeDark;
        }
    }

    @StyleRes
    public int getWorkoutTypeTheme(WorkoutType type) {
        if (shouldUseLightMode()) {
            switch (type.id) {
                case "walking":
                case "running":
                    return R.style.Running;
                case "hiking":
                    return R.style.Hiking;
                case "cycling":
                    return R.style.Bicycling;
                case "e-bike":
                case "e-scooter":
                    return R.style.Skating;
                case "bike_scooter":
                    return R.style.Rowing;
                default:
                    return R.style.AppTheme;
            }
        } else {
            switch (type.id) {
                case "walking":
                case "running":
                    return R.style.RunningDark;
                case "hiking":
                    return R.style.HikingDark;
                case "cycling":
                    return R.style.BicyclingDark;
                case "e-bike":
                case "e-scooter":
                    return R.style.SkatingDark;
                case "bike_scooter":
                    return R.style.RowingDark;
                default:
                    return R.style.AppThemeDark;
            }
        }
    }

    public boolean shouldUseLightMode() {
        switch (getThemeSetting()) {
            default:
            case THEME_SETTING_LIGHT:
                return true;
            case THEME_SETTING_DARK:
                return false;
        }
    }

    private int getThemeSetting() {
        String setting = PreferenceManager.getDefaultSharedPreferences(context).getString("themeSetting", String.valueOf(THEME_SETTING_LIGHT));
        assert setting != null;
        return Integer.parseInt(setting);
    }

}
