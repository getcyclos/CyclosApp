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

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.recording.WorkoutRecorder;

public class AverageSpeedTotal extends RecordingInformation {

    public AverageSpeedTotal(Context context) {
        super(context);
    }

    @Override
    public String getId() {
        return "avgSpeedTotal";
    }

    @Override
    boolean isEnabledByDefault() {
        return false;
    }

    @Override
    boolean canBeDisplayed() {
        return true;
    }

    @Override
    public String getTitle() {
        return getString(R.string.avgSpeedTotalShort);
    }

    @Override
    String getDisplayedText(WorkoutRecorder recorder) {
        return getDistanceUnitUtils().getSpeed(recorder.getAvgSpeedTotal());
    }

    @Override
    public String getSpokenText(WorkoutRecorder recorder) {
        String avgSpeed = getDistanceUnitUtils().getSpeed(recorder.getAvgSpeedTotal(), true);
        return getString(R.string.avgSpeedTotalLong) + ": " + avgSpeed + ".";
    }
}