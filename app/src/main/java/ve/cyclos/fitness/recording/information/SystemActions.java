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

public class SystemActions extends RecordingInformation {

    private WorkoutRecorder.RecordingState lastState = WorkoutRecorder.RecordingState.IDLE;

    public SystemActions(Context context) {
        super(context);
    }

    @Override
    public String getId() {
        return "system_actions";
    }

    @Override
    boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public String getSpokenText(WorkoutRecorder recorder) {
        String text = "";
        if (lastState != recorder.getState()) {
            text = speakState(recorder.getState());
        }
        lastState = recorder.getState();
        return text;
    }

    private String speakState(WorkoutRecorder.RecordingState newState) {
        if (lastState == WorkoutRecorder.RecordingState.IDLE) {
            return getSpokenStarted();
        }
        switch (newState) {
            case RUNNING:
                return getSpokenResumed();
            case PAUSED:
                return getSpokenPaused();
            case STOPPED:
                return getSpokenStopped();
        }
        return "";
    }

    public String getSpokenStarted() {
        return getString(R.string.workoutStarted);
    }

    public String getSpokenResumed() {
        return getString(R.string.workoutResumed);
    }

    public String getSpokenPaused() {
        return getString(R.string.workoutPaused);
    }

    public String getSpokenStopped() {
        return getString(R.string.workoutStopped);
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

    @Override
    public boolean isPlayedAlways() {
        return true;
    }
}
