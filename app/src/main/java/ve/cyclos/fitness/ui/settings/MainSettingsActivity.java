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

import android.content.Intent;
import android.os.Bundle;

import ve.cyclos.fitness.R;

public class MainSettingsActivity extends CyclosAppSettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        setTitle(R.string.settings);

        addPreferencesFromResource(R.xml.preferences_main);

        findPreference("userInterface").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(this, InterfaceSettingsActivity.class));
            return true;
        });

        findPreference("recordingSettings").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(this, RecordingSettingsActivity.class));
            return true;
        });

        findPreference("workoutTypeSettings").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(this, ManageWorkoutTypesActivity.class));
            return true;
        });

        findPreference("backupSettings").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(this, BackupSettingsActivity.class));
            return true;
        });

        findPreference("about").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        });

    }

}
