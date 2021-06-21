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

public class Duration extends RecordingInformation {

    public Duration(Context context) {
        super(context);
    }

    @Override
    public String getId() {
        return "duration";
    }

    @Override
    boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public String getSpokenText(WorkoutRecorder recorder) {
        return getString(R.string.workoutDuration) + ": " + getSpokenTime(recorder.getDuration()) + ".";
    }

    private String getSpokenTime(long duration) {
        final long minute = 1000L * 60;
        final long hour = minute * 60;

        StringBuilder spokenTime = new StringBuilder();

        if (duration > hour) {
            long hours = duration / hour;
            duration = duration % hour; // Set duration to the rest
            spokenTime.append(hours).append(" ");
            spokenTime.append(getString(hours == 1 ? R.string.timeHourSingular : R.string.timeHourPlural)).append(" ")
                    .append(getString(R.string.and)).append(" ");
        }
        long minutes = duration / minute;
        spokenTime.append(minutes).append(" ");
        spokenTime.append(getString(minutes == 1 ? R.string.timeMinuteSingular : R.string.timeMinutePlural));

        return spokenTime.toString();
    }

    @Override
    boolean canBeDisplayed() {
        return false;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    String getDisplayedText(WorkoutRecorder recorder) {
        return null;
    }
}
