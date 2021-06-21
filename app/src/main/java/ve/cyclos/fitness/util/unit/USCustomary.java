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

import ve.cyclos.fitness.R;

class USCustomary extends Imperial {

    @Override
    public double getMetersFromShortDistance(double shortdistance) {
        return 0.3048 * shortdistance;
    }

    @Override
    public double getShortDistanceFromLong(double longdistance) {
        return 5280 * longdistance; // miles -> feet
    }

    @Override
    public double getDistanceFromMeters(double meters) {
        return 3.28084 * meters;
    }

    @Override
    public String getShortDistanceUnit() {
        return "ft";
    }

    @Override
    public int getShortDistanceUnitTitle(boolean isPlural) {
        return isPlural ? R.string.unitFootPlural : R.string.unitFootSingular;
    }


}
