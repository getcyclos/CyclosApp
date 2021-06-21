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

package ve.cyclos.fitness.util.unit;

import android.content.Context;

import ve.cyclos.fitness.Instance;

public class EnergyUnitUtils extends UnitUtils {

    private EnergyUnit energyUnit;

    public EnergyUnitUtils(Context context) {
        super(context);
        setUnit();
    }

    public void setUnit() {
        energyUnit = getUnitById(Instance.getInstance(context).userPreferences.getEnergyUnit());
    }

    private EnergyUnit getUnitById(String id) {
        switch (id) {
            default:
            case "kcal":
                return new Kcal();
            case "joule":
                return new KJoule();
        }
    }

    public String getEnergy(double energyInKcal) {
        return getEnergy(energyInKcal, false);
    }

    public String getEnergy(double energyInKcal, boolean useLongNames) {
        int value = (int) Math.round(energyUnit.getEnergy(energyInKcal));
        if (useLongNames) {
            return value + " " + getString(energyUnit.getLongNameTitle());
        } else {
            return value + " " + energyUnit.getInternationalShortName();
        }
    }

    public String getRelativeEnergy(double energyInKcalPerMinute) {
        String value = round(energyUnit.getEnergy(energyInKcalPerMinute), 2);
        return value + " " + energyUnit.getInternationalShortName() + "/min";
    }

    public EnergyUnit getEnergyUnit() {
        return energyUnit;
    }
}
