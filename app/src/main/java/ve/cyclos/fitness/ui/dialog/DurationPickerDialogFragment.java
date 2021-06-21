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


import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.NumberPicker;

import ve.cyclos.fitness.R;

public class DurationPickerDialogFragment {

    public Activity context;
    public DurationPickListener listener;
    public long initialDuration;

    public DurationPickerDialogFragment(Activity context, DurationPickListener listener, long initialDuration) {
        this.context = context;
        this.listener = listener;
        this.initialDuration = initialDuration;
    }

    public void show() {
        final AlertDialog.Builder d = new AlertDialog.Builder(context);
        d.setTitle(R.string.setDuration);
        View v = context.getLayoutInflater().inflate(R.layout.dialog_duration_picker, null);
        NumberPicker hours = v.findViewById(R.id.durationPickerHours);
        hours.setFormatter(value -> value + " " + context.getString(R.string.timeHourShort));
        hours.setMinValue(0);
        hours.setMaxValue(24);
        hours.setValue(getInitialHours());

        NumberPicker minutes = v.findViewById(R.id.durationPickerMinutes);
        minutes.setFormatter(value -> value + " " + context.getString(R.string.timeMinuteShort));
        minutes.setMinValue(0);
        minutes.setMaxValue(60);
        minutes.setValue(getInitialMinutes());

        NumberPicker seconds = v.findViewById(R.id.durationPickerSeconds);
        seconds.setFormatter(value -> value + " " + context.getString(R.string.timeSecondsShort));
        seconds.setMinValue(0);
        seconds.setMaxValue(60);
        seconds.setValue(getInitialSeconds());

        d.setView(v);

        d.setNegativeButton(R.string.cancel, null);
        d.setPositiveButton(R.string.okay, (dialog, which) -> {
            listener.onDurationPick(getMillisFromPick(hours.getValue(), minutes.getValue(), seconds.getValue()));
        });

        d.create().show();
    }

    private int getInitialHours() {
        return (int) (initialDuration / 1000 / 60 / 60);
    }

    private int getInitialMinutes() {
        return (int) (initialDuration / 1000 / 60 % 60);
    }

    private int getInitialSeconds() {
        return (int) (initialDuration / 1000 % 60);
    }

    private static long getMillisFromPick(int hours, int minutes, int seconds) {
        long secondInMillis = 1000L;
        long minuteInMillis = secondInMillis * 60;
        long hourInMillis = minuteInMillis * 60;
        return hours * hourInMillis + minutes * minuteInMillis + seconds * secondInMillis;
    }

    public interface DurationPickListener {
        void onDurationPick(long duration);
    }

}
