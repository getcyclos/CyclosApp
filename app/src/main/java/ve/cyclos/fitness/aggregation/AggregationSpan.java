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

import android.annotation.SuppressLint;

import androidx.annotation.StringRes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import ve.cyclos.fitness.R;

public enum AggregationSpan {

    SINGLE(R.string.singleWorkout, R.string.month, TimeUnit.MINUTES.toMillis(1), "MMM/yy") {
        @Override
        public void applyToCalendar(Calendar calendar) {
        }
    },
    DAY(R.string.day, R.string.dayInMonth, TimeUnit.DAYS.toMillis(1), "dd"),
    WEEK(R.string.week, R.string.calendarWeekYear, TimeUnit.DAYS.toMillis(7), "ww/yy") {
        @Override
        public void applyToCalendar(Calendar calendar) {
            super.applyToCalendar(calendar);
            calendar.set(Calendar.DAY_OF_WEEK, 1);
        }
    },
    MONTH(R.string.month, R.string.month, TimeUnit.DAYS.toMillis(30), "MMM") {
        @Override
        public void applyToCalendar(Calendar calendar) {
            super.applyToCalendar(calendar);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
    },
    YEAR(R.string.year, R.string.year, TimeUnit.DAYS.toMillis(365), "yyyy") {
        @Override
        public void applyToCalendar(Calendar calendar) {
            super.applyToCalendar(calendar);
            calendar.set(Calendar.DAY_OF_YEAR, 1);
        }
    };

    @StringRes
    public final int title;
    @StringRes
    public final int axisLabel;
    public final long spanInterval;
    public final SimpleDateFormat dateFormat;

    @SuppressLint("SimpleDateFormat")
    AggregationSpan(int title, int axisLabel, long spanInterval, String formatString) {
        this.title = title;
        this.axisLabel = axisLabel;
        this.spanInterval = spanInterval;
        this.dateFormat = new SimpleDateFormat(formatString);
    }

    public void applyToCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
