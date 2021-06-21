/*
 * Copyright (c) 2021 Gabriel Estrada <dev@getcyclos.com>
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

package ve.cyclos.fitness.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferences {

    private final SharedPreferences preferences;

    public UserPreferences(Context context) {
        this.preferences= PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getUserWeight(){
        return preferences.getInt("weight", 80);
    }

    public int getSpokenUpdateTimePeriod(){
        return preferences.getInt("spokenUpdateTimePeriod", 0);
    }

    public int getSpokenUpdateDistancePeriod(){
        return preferences.getInt("spokenUpdateDistancePeriod", 0);
    }

    public String getMapStyle(){
        return preferences.getString("mapStyle", "osm.mapnik");
    }

    public boolean intervalsIncludePauses() {
        return preferences.getBoolean("intervalsIncludePause", true);
    }

    public String getIdOfDisplayedInformation(int slot) {
        String defValue = "";
        switch (slot) {
            case 0:
                defValue = "distance";
                break;
            case 1:
                defValue = "energy_burned";
                break;
            case 2:
                defValue = "avgSpeedMotion";
                break;
            case 3:
                defValue = "pause_duration";
                break;
        }
        return preferences.getString("information_display_" + slot, defValue);
    }

    public void setIdOfDisplayedInformation(int slot, String id) {
        preferences.edit().putString("information_display_" + slot, id).apply();
    }

    public String getDateFormatSetting() {
        return preferences.getString("dateFormat", "system");
    }

    public String getTimeFormatSetting() {
        return preferences.getString("timeFormat", "system");
    }

    public String getDistanceUnitSystemId() {
        return preferences.getString("unitSystem", "1");
    }

    public String getEnergyUnit() {
        return preferences.getString("energyUnit", "kcal");
    }

    public boolean getShowOnLockScreen() {
        return preferences.getBoolean("showOnLockScreen", false);
    }

    public String getOfflineMapFileName() {
        return preferences.getString("offlineMapFileName", null);
    }
}
