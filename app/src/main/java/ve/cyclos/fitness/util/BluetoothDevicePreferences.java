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

package ve.cyclos.fitness.util;

import android.content.Context;
import android.content.SharedPreferences;

public class BluetoothDevicePreferences {

    public static final String DEVICE_HEART_RATE = "hr";

    private final SharedPreferences preferences;

    public BluetoothDevicePreferences(Context context) {
        this.preferences = context.getSharedPreferences("bluetooth", Context.MODE_PRIVATE);
    }

    public String getAddress(String device) {
        return preferences.getString(device, "");
    }

    public void setAddress(String device, String address) {
        preferences.edit().putString(device, address).apply();
    }

}
