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

package ve.cyclos.fitness.ui.settings;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.NumberPicker;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;

public class VoiceAnnouncementsSettingsActivity extends CyclosAppSettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        setTitle(R.string.voiceAnnouncementsTitle);

        addPreferencesFromResource(R.xml.preferences_voice_announcements);

        bindPreferenceSummaryToValue(findPreference("announcementMode"));

        findPreference("speechConfig").setOnPreferenceClickListener(preference -> {
            showSpeechConfig();
            return true;
        });

    }

    private void showSpeechConfig() {
        Instance.getInstance(this).distanceUnitUtils.setUnit();

        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        d.setTitle(getString(R.string.pref_announcements_config_title));
        View v = getLayoutInflater().inflate(R.layout.dialog_spoken_updates_picker, null);

        NumberPicker npT = v.findViewById(R.id.spokenUpdatesTimePicker);
        npT.setMaxValue(60);
        npT.setMinValue(0);
        final String updateTimeVariable = "spokenUpdateTimePeriod";
        npT.setValue(preferences.getInt(updateTimeVariable, 0));
        npT.setWrapSelectorWheel(false);
        String[] npTValues = new String[61];
        npTValues[0]=  getString(R.string.speechConfigNoSpeech);
        for (int i=1; i<=60; i++){
            npTValues[i] = i + " " + getString(R.string.timeMinuteShort);
        }
        npT.setDisplayedValues(npTValues);

        final String distanceUnit = " " + Instance.getInstance(this).distanceUnitUtils.getDistanceUnitSystem().getLongDistanceUnit();
        NumberPicker npD = v.findViewById(R.id.spokenUpdatesDistancePicker);
        npD.setMaxValue(10);
        npD.setMinValue(0);
        final String updateDistanceVariable = "spokenUpdateDistancePeriod";
        npD.setValue(preferences.getInt(updateDistanceVariable, 0));
        npD.setWrapSelectorWheel(false);
        String[] npDValues = new String[11];
        npDValues[0]=  getString(R.string.speechConfigNoSpeech);
        for (int i=1; i<=10; i++){
            npDValues[i] = i + distanceUnit;
        }
        npD.setDisplayedValues(npDValues);

        d.setView(v);

        d.setNegativeButton(R.string.cancel, null);
        d.setPositiveButton(R.string.okay, (dialog, which) ->
                preferences.edit()
                        .putInt(updateTimeVariable, npT.getValue())
                        .putInt(updateDistanceVariable, npD.getValue())
                        .apply());

        d.create().show();
    }

}
