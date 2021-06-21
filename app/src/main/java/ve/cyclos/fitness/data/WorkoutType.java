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

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;

@Entity(tableName = "workout_type")
public class WorkoutType implements Serializable {

    public static final String WORKOUT_TYPE_ID_OTHER = "other";
    public static final String WORKOUT_TYPE_ID_RUNNING = "running";

    @PrimaryKey
    @NonNull
    public String id;

    public String title;

    @ColumnInfo(name = "min_distance")
    public int minDistance;

    public int color;

    public String icon;

    @ColumnInfo(name = "met")
    public int MET;

    @Ignore
    public WorkoutType(@NonNull String id, String title, int minDistance, int color, String icon, int MET) {
        this.id = id;
        this.title = title;
        this.minDistance = minDistance;
        this.color = color;
        this.icon = icon;
        this.MET = MET;
    }

    public WorkoutType() {
    }

    private static WorkoutType[] PRESETS = null;

    public static WorkoutType getWorkoutTypeById(Context context, String id) {
        buildPresets(context);
        for (WorkoutType type : PRESETS) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        WorkoutType type = Instance.getInstance(context).db.workoutTypeDao().findById(id);
        if (type == null && !id.equals(WORKOUT_TYPE_ID_OTHER)) {
            return getWorkoutTypeById(context, WORKOUT_TYPE_ID_OTHER);
        } else {
            return type;
        }
    }

    public static List<WorkoutType> getAllTypes(Context context) {
        buildPresets(context);
        List<WorkoutType> result = new ArrayList<>(Arrays.asList(PRESETS));
        WorkoutType[] fromDatabase = Instance.getInstance(context).db.workoutTypeDao().findAll();
        result.addAll(Arrays.asList(fromDatabase));
        return result;
    }

    private static void buildPresets(Context context) {
        if (PRESETS != null) return; // Don't build a second time
        PRESETS = new WorkoutType[]{
                new WorkoutType(WORKOUT_TYPE_ID_RUNNING,
                        context.getString(R.string.workoutTypeRunning),
                        5,
                        context.getResources().getColor(R.color.colorPrimaryRunning),
                        "running",
                        -1),
                new WorkoutType("walking",
                        context.getString(R.string.workoutTypeWalking),
                        5,
                        context.getResources().getColor(R.color.colorPrimaryRunning),
                        "walking",
                        -1),
                new WorkoutType("hiking",
                        context.getString(R.string.workoutTypeHiking),
                        5,
                        context.getResources().getColor(R.color.colorPrimaryHiking),
                        "walking",
                        -1),
                new WorkoutType("cycling", context.getString(R.string.workoutTypeCycling),
                        10,
                        context.getResources().getColor(R.color.colorPrimaryBicycling),
                        "cycling",
                        -1),
                new WorkoutType("e-bike", "E-Bike",
                        20,
                        context.getResources().getColor(R.color.colorPrimarySkating),
                        "e-bike",
                        -1),
                new WorkoutType("e-scooter", "Scooter",
                        7,
                        context.getResources().getColor(R.color.colorPrimarySkating),
                        "e-scooter",
                        -1),
                new WorkoutType("Skateboarding",
                        context.getString(R.string.workoutTypeSkateboarding),
                        7,
                        context.getResources().getColor(R.color.colorPrimaryRowing),
                        "skateboarding",
                        -1),
                new WorkoutType(WORKOUT_TYPE_ID_OTHER,
                        context.getString(R.string.workoutTypeOther),
                        7,
                        context.getResources().getColor(R.color.colorPrimary),
                        "other",
                        0),
        };
    }
}
