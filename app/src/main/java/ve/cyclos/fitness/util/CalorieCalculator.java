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

import ve.cyclos.fitness.data.Workout;

public class CalorieCalculator {

    /**
     * workoutType, duration, ascent and avgSpeed of workout have to be set
     *
     * @param workout the workout
     * @param weight  the weight of the person in kilogram
     * @return calories burned
     */
    public static int calculateCalories(Context context, Workout workout, double weight) {
        double mins = (double) (workout.duration / 1000) / 60;
        int ascent = (int) workout.ascent; // 1 calorie per meter
        return (int) (mins * (getMET(context, workout) * 3.5 * weight) / 200) + ascent;
    }

    /**
     * calorie calculation based on @link { https://sites.google.com/site/compendiumofphysicalactivities/Activity-Categories }
     * <p>
     * How do we get to this calculation from the values of the compendium of physical activities?
     * Easy: we have given some points like
     * - 5kmh -> 4 MET or
     * - 8kmh -> 6 MET.
     * Using regression the function "MET = f(speedInKmh)" can be created.
     * That's what you can see below for different activities.
     * <p>
     * workoutType and avgSpeed of workout have to be set
     *
     * @return MET
     */
    private static double getMET(Context context, Workout workout) {
        double speedInKmh = workout.avgSpeed * 3.6;

        switch (workout.workoutTypeId) {
            case "running":
            case "walking":
            case "hiking":
                return Math.max(3, speedInKmh * 0.97);
            case "cycling":
                return Math.max(3.5, 0.00818 * Math.pow(speedInKmh, 2) + 0.1925 * speedInKmh + 1.13);
            case "inline_skating":
                return Math.max(3, 0.6747 * speedInKmh - 2.1893);
            case "skateboarding":
                return Math.max(4, 0.43 * speedInKmh + 0.89);
            case "rowing":
                return Math.max(2.5, 0.18 * Math.pow(speedInKmh, 2) - 1.375 * speedInKmh + 5.2);
            default:
                return workout.getWorkoutType(context).MET;
        }
    }

}
