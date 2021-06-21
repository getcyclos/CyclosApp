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

package ve.cyclos.fitness.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WorkoutDao {

    @Query("SELECT * FROM workout WHERE id = :id")
    Workout findById(long id);

    @Query("SELECT * FROM workout_sample WHERE id = :id")
    WorkoutSample findSampleById(long id);

    @Query("SELECT * FROM workout_sample WHERE workout_id = :workout_id")
    WorkoutSample[] getAllSamplesOfWorkout(long workout_id);

    @Query("SELECT * FROM workout ORDER BY start DESC")
    Workout[] getWorkouts();

    @Query("SELECT * FROM workout ORDER BY start DESC LIMIT 1")
    Workout getLastWorkout();

    @Query("SELECT * FROM workout ORDER BY start ASC")
    Workout[] getAllWorkoutsHistorically();

    @Query("SELECT * FROM workout WHERE workoutType = :workout_type ORDER BY start ASC ")
    Workout[] getWorkoutsHistorically(String workout_type);

    @Query("SELECT * FROM workout WHERE start = :start")
    Workout getWorkoutByStart(long start);

    @Query("SELECT * FROM workout WHERE id = :id")
    Workout getWorkoutById(long id);

    @Query("SELECT * FROM workout_sample")
    WorkoutSample[] getSamples();

    @Insert
    void insertWorkoutAndSamples(Workout workout, WorkoutSample[] samples);

    @Delete
    void deleteWorkoutAndSamples(Workout workout, WorkoutSample[] toArray);

    @Insert
    void insertWorkout(Workout workout);

    @Delete
    void deleteWorkout(Workout workout);

    @Update
    void updateWorkout(Workout workout);

    @Insert
    void insertSample(WorkoutSample sample);

    @Delete
    void deleteSample(WorkoutSample sample);

    @Update
    void updateSamples(WorkoutSample[] samples);
}
