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

package ve.cyclos.fitness.recording.information;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.annotation.StringRes;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.recording.WorkoutRecorder;
import ve.cyclos.fitness.recording.announcement.Announcement;
import ve.cyclos.fitness.util.unit.DistanceUnitUtils;
import ve.cyclos.fitness.util.unit.EnergyUnitUtils;

public abstract class RecordingInformation implements Announcement {

    private final Context context;

    RecordingInformation(Context context) {
        this.context = context;
    }

    public boolean isAnnouncementEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("announcement_" + getId(), isEnabledByDefault());
    }

    protected String getString(@StringRes int resId) {
        return context.getString(resId);
    }

    protected DistanceUnitUtils getDistanceUnitUtils() {
        return Instance.getInstance(context).distanceUnitUtils;
    }

    protected EnergyUnitUtils getEnergyUnitUtils() {
        return Instance.getInstance(context).energyUnitUtils;
    }

    public abstract String getId();

    abstract boolean isEnabledByDefault();

    abstract boolean canBeDisplayed();

    public abstract String getTitle();

    abstract String getDisplayedText(WorkoutRecorder recorder);

}
