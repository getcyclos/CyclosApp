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

import android.content.Context;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "workout")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Workout{

    @PrimaryKey
    public long id;

    public long start;
    public long end;

    public long duration;

    public long pauseDuration;

    public String comment;

    /**
     * Length of workout in meters
     */
    public int length;

    /**
     * Average speed (moving) of workout in m/s
     */
    public double avgSpeed;

    /**
     * Average speed (total)
     *
     * @return speed in m/s
     */
    @JsonIgnore
    public double getAvgSpeedTotal() {
        return (double) length / ((double) (end - start) / 1000);
    }

    /**
     * Top speed in m/s
     */
    public double topSpeed;

    /**
     * Average pace of workout in min/km
     */
    public double avgPace;

    @ColumnInfo(name = "workoutType")
    @JsonProperty(value = "workoutType")
    public String workoutTypeId;

    public float ascent;

    public float descent;

    public int calorie;

    public boolean edited;

    // No foreign key is intended
    @ColumnInfo(name = "interval_set_used_id")
    public long intervalSetUsedId = 0;

    @ColumnInfo(name = "interval_set_include_pauses")
    public boolean intervalSetIncludesPauses;

    @ColumnInfo(name = "avg_heart_rate")
    public int avgHeartRate = -1;

    @ColumnInfo(name = "max_heart_rate")
    public int maxHeartRate = -1;

    public String toString() {
        if (comment != null && comment.length() > 2) {
            return comment;
        } else {
            return getDateString();
        }
    }

    @JsonIgnore
    public String getDateString() {
        return SimpleDateFormat.getDateTimeInstance().format(new Date(start));
    }

    @JsonIgnore
    public String getSafeDateString(){
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(new Date(start));
    }

    @JsonIgnore
    public String getSafeComment(){
        if (comment == null) return "";
        String safeComment = this.comment.replaceAll("[^0-9a-zA-Z-_]+", "_"); // replace all unwanted chars by `_`
        return safeComment.substring(0, Math.min(safeComment.length(), 50)); // cut the comment after 50 Chars
    }

    @JsonIgnore
    public WorkoutType getWorkoutType(Context context) {
        return WorkoutType.getWorkoutTypeById(context, workoutTypeId);
    }

    @JsonIgnore
    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutTypeId = workoutType.id;
    }

    @JsonIgnore
    public boolean hasHeartRateData() {
        return avgHeartRate > 0;
    }


}