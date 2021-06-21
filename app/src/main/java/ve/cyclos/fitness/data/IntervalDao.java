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
public interface IntervalDao {

    @Query("SELECT * FROM interval WHERE id = :id")
    Interval findById(long id);

    @Query("SELECT * FROM interval_set WHERE id = :id")
    IntervalSet getSet(long id);

    @Query("SELECT * FROM interval WHERE set_id = :setId")
    Interval[] getAllIntervalsOfSet(long setId);

    @Query("SELECT * FROM interval_set where state = 0")
    IntervalSet[] getVisibleSets();

    @Query("SELECT * FROM interval_set")
    IntervalSet[] getAllSets();

    @Insert
    void insertIntervalSet(IntervalSet set);

    @Insert
    void insertInterval(Interval interval);

    @Update
    void updateIntervalSet(IntervalSet set);

    @Delete
    void deleteInterval(Interval interval);
}
