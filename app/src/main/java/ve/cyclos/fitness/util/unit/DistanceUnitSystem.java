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

import androidx.annotation.StringRes;

public interface DistanceUnitSystem {

    double getMetersFromShortDistance(double shortdistance);

    default double getMetersFromLongDistance(double longdistance) {
        return getMetersFromShortDistance(getShortDistanceFromLong(longdistance));
    }

    default double getElevationFromMeters(double meters) {
        return getDistanceFromMeters(meters);
    }

    double getShortDistanceFromLong(double longdistance);

    double getDistanceFromMeters(double meters);

    double getDistanceFromKilometers(double kilometers);

    double getWeightFromKilogram(double kilogram);

    double getKilogramFromUnit(double unit);

    double getSpeedFromMeterPerSecond(double meterPerSecond);


    String getLongDistanceUnit();

    @StringRes
    int getLongDistanceUnitTitle(boolean isPlural);

    String getShortDistanceUnit();

    @StringRes
    int getShortDistanceUnitTitle(boolean isPlural);

    default String getElevationUnit() {
        return getShortDistanceUnit();
    }

    @StringRes
    default int getElevationUnitTitle(boolean isPlural) {
        return getShortDistanceUnitTitle(isPlural);
    }

    String getWeightUnit();

    String getSpeedUnit();

    @StringRes
    int getSpeedUnitTitle();

}
