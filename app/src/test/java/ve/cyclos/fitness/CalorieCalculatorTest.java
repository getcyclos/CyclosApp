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

package ve.cyclos.fitness;

import org.junit.Assert;
import org.junit.Test;

import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.util.CalorieCalculator;

public class CalorieCalculatorTest {

    @Test
    public void testCalculation() {
        Workout workout = new Workout();
        workout.avgSpeed = 2.7d;
        workout.workoutTypeId = "running";
        workout.duration = 1000L * 60 * 10;
        int calorie = CalorieCalculator.calculateCalories(workout, 80);
        System.out.println("Calories: " + calorie);
        Assert.assertEquals(130, calorie, 50);
    }

}
