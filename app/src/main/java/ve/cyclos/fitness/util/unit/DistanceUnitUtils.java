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
import ve.cyclos.fitness.R;

public class DistanceUnitUtils extends UnitUtils {

    private DistanceUnitSystem distanceUnitSystem;

    public DistanceUnitUtils(Context context) {
        super(context);
        setUnit();
    }

    public void setUnit() {
        String id = Instance.getInstance(context).userPreferences.getDistanceUnitSystemId();
        distanceUnitSystem = getUnitById(Integer.parseInt(id));
    }

    private DistanceUnitSystem getUnitById(int id) {
        switch (id) {
            default:
            case 1:
                return new Metric();
            case 2:
                return new MetricPhysical();
            case 3:
                return new Imperial();
            case 4:
                return new ImperialWithMeters();
            case 5:
                return new USCustomary();
        }
    }

    public String getHourMinuteTime(long time) {
        return getHourMinuteTime(time, false);
    }

    public String getHourMinuteTime(long time, boolean useLongUnits) {
        long seks = time / 1000;
        long mins = seks / 60;
        int hours = (int) mins / 60;
        int remainingMinutes = (int) mins % 60;

        if (hours > 0) {
            if (useLongUnits) {
                String hourText = hours == 1 ? getString(R.string.timeHourSingular) : getString(R.string.timeHourPlural);
                return hours + " " + hourText + " " + getMinuteText(remainingMinutes);
            } else {
                return hours + " h " + remainingMinutes + " m";
            }
        } else {
            if (useLongUnits) {
                return getMinuteText(remainingMinutes);
            } else {
                return remainingMinutes + " min";
            }
        }
    }

    public String getHourMinuteSecondTime(long time) {
        long totalSecs = time / 1000;
        long totalMins = totalSecs / 60;
        long hours = totalMins / 60;
        long mins = totalMins % 60;
        long secs = totalSecs % 60;
        String minStr = (mins < 10 ? "0" : "") + mins;
        String sekStr = (secs < 10 ? "0" : "") + secs;
        return hours + ":" + minStr + ":" + sekStr;
    }

    public String getPace(double metricPace) {
        return getPace(metricPace, false);
    }

    public String getPace(double metricPace, boolean useLongUnits) {
        if (!Double.isInfinite(metricPace)) {
            double one = distanceUnitSystem.getDistanceFromKilometers(1);
            double secondsTotal = 60 * metricPace / one;
            int minutes = (int) secondsTotal / 60;
            int seconds = (int) secondsTotal % 60;
            if (useLongUnits) {
                return getMinuteText(minutes) + " " + getString(R.string.and) +
                        " " + getSecondsText(seconds) + " " + getString(R.string.per) +
                        " " + getString(distanceUnitSystem.getLongDistanceUnitTitle(false));
            } else {
                return minutes + ":" + (seconds < 10 ? "0" : "") + seconds + " min/" + distanceUnitSystem.getLongDistanceUnit();
            }
        }
        else {
            return "-";
        }
    }

    public String getDistance(int distanceInMeters) {
        return getDistance(distanceInMeters, false);
    }

    public String getDistance(int distanceInMeters, boolean useLongUnitNames) {
        if (distanceInMeters >= 1000) {
            String lengthInLongUnit = round(distanceUnitSystem.getDistanceFromKilometers((double) distanceInMeters / 1000d), 2);
            if (useLongUnitNames) {
                return lengthInLongUnit + " " + getString(distanceUnitSystem.getLongDistanceUnitTitle(false));
            } else {
                return lengthInLongUnit + " " + distanceUnitSystem.getLongDistanceUnit();
            }
        } else {
            int value = (int) distanceUnitSystem.getDistanceFromMeters(distanceInMeters);
            if (useLongUnitNames) {
                return value + " " + getString(distanceUnitSystem.getShortDistanceUnitTitle(value != 1));
            } else {
                return (int) distanceUnitSystem.getDistanceFromMeters(distanceInMeters) + " " + distanceUnitSystem.getShortDistanceUnit();
            }
        }
    }

    public String getElevation(int elevationInMeters) {
        return getElevation(elevationInMeters, false);
    }

    public String getElevation(int elevationInMeters, boolean useLongUnitNames) {
        int value = (int) distanceUnitSystem.getElevationFromMeters(elevationInMeters);
        if (useLongUnitNames) {
            return value + " " + getString(distanceUnitSystem.getElevationUnitTitle(value != 1));
        } else {
            return value + " " + distanceUnitSystem.getElevationUnit();
        }
    }

    public String getSpeed(double speed) {
        return getSpeed(speed, false);
    }

    /**
     * @param speed speed in m/s
     * @return speed in km/h
     */
    public String getSpeed(double speed, boolean useLongNames) {
        String value = round(distanceUnitSystem.getSpeedFromMeterPerSecond(speed), 1);
        if (useLongNames) {
            return value + " " + getString(distanceUnitSystem.getSpeedUnitTitle());
        } else {
            return value + " " + distanceUnitSystem.getSpeedUnit();
        }
    }

    private String getHourText(int hours) {
        return hours + " " + (hours == 1 ? getString(R.string.timeHourSingular) : getString(R.string.timeHourPlural));
    }

    private String getMinuteText(int minutes) {
        return minutes + " " + (minutes == 1 ? getString(R.string.timeMinuteSingular) : getString(R.string.timeMinutePlural));
    }

    private String getSecondsText(int seconds) {
        return seconds + " " + (seconds == 1 ? getString(R.string.timeSecondsSingular) : getString(R.string.timeSecondsPlural));
    }

    public DistanceUnitSystem getDistanceUnitSystem() {
        return distanceUnitSystem;
    }
}
