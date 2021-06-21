/*
 * Copyright (c) 2019 Gabriel Estrada <dev@getcyclos.com>
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

public class Imperial implements DistanceUnitSystem {

    @Override
    public double getMetersFromShortDistance(double shortdistance) {
        return shortdistance * 0.9144d;
    }

    @Override
    public double getShortDistanceFromLong(double longdistance) {
        return longdistance * 1760d;
    }

    @Override
    public double getDistanceFromMeters(double meters) {
        return meters * 1.093613d;
    }

    @Override
    public double getDistanceFromKilometers(double kilometers) {
        return kilometers * 0.62137d;
    }

    @Override
    public double getWeightFromKilogram(double kilogram) {
        return kilogram * 2.2046;
    }

    @Override
    public double getKilogramFromUnit(double unit) {
        return unit / 2.2046;
    }

    @Override
    public double getSpeedFromMeterPerSecond(double meterPerSecond) {
        return meterPerSecond*3.6*0.62137d;
    }

    @Override
    public String getLongDistanceUnit() {
        return "mi";
    }

    @Override
    public String getShortDistanceUnit() {
        return "yd";
    }

    @Override
    public String getWeightUnit() {
        return "lbs";
    }

    @Override
    public String getSpeedUnit() {
        return "mi/h";
    }

    @Override
    public int getLongDistanceUnitTitle(boolean isPlural) {
        return isPlural ? R.string.unitMilesPlural : R.string.unitMilesSingular;
    }

    @Override
    public int getShortDistanceUnitTitle(boolean isPlural) {
        return isPlural ? R.string.unitYardsPlural : R.string.unitYardsSingular;
    }

    @Override
    public int getSpeedUnitTitle() {
        return R.string.unitMilesPerHour;
    }
}
