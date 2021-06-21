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

package ve.cyclos.fitness.recording.announcement;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import java.util.List;

import ve.cyclos.fitness.data.Interval;
import ve.cyclos.fitness.recording.WorkoutRecorder;
import ve.cyclos.fitness.recording.announcement.interval.IntervalAnnouncements;

public class VoiceAnnouncements {

    private final InformationAnnouncements informationAnnouncements;
    private final IntervalAnnouncements intervalAnnouncements;
    private final TelephonyManager telephonyManager;
    private final boolean supressOnCall;

    public VoiceAnnouncements(Context context, WorkoutRecorder recorder, TTSController ttsController, List<Interval> intervals) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.supressOnCall = prefs.getBoolean("announcementSuppressDuringCall", true);
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.informationAnnouncements = new InformationAnnouncements(context, recorder, ttsController);
        this.intervalAnnouncements = new IntervalAnnouncements(context, recorder, ttsController, intervals);
    }

    public void check() {
        // Suppress all announcements when currently on call
        if (supressOnCall && isOnCall()) {
            return;
        }
        intervalAnnouncements.check();
        informationAnnouncements.check();
    }

    public void applyIntervals(List<Interval> intervals) {
        intervalAnnouncements.setIntervals(intervals);
    }

    private boolean isOnCall() {
        return this.telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE;
    }

}
