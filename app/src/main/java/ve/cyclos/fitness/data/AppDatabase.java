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
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(version = 11, entities = {Workout.class, WorkoutSample.class, Interval.class, IntervalSet.class, WorkoutType.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "cyclos-data-base";

    public abstract WorkoutDao workoutDao();

    public abstract WorkoutTypeDao workoutTypeDao();

    public abstract IntervalDao intervalDao();

    public static AppDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .addMigrations(new Migration(1, 2) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout add descent REAL NOT NULL DEFAULT 0;");
                            database.execSQL("ALTER table workout add ascent REAL NOT NULL DEFAULT 0");

                            database.execSQL("ALTER TABLE workout_sample RENAME TO workout_sample2;");

                            database.execSQL("CREATE TABLE workout_sample (" +
                                    "id INTEGER NOT NULL DEFAULT NULL PRIMARY KEY," +
                                    "relativeTime INTEGER NOT NULL DEFAULT NULL," +
                                    "elevation REAL NOT NULL DEFAULT NULL," +
                                    "absoluteTime INTEGER NOT NULL DEFAULT NULL," +
                                    "lat REAL NOT NULL DEFAULT NULL," +
                                    "lon REAL NOT NULL DEFAULT NULL," +
                                    "speed REAL NOT NULL DEFAULT NULL," +
                                    "workout_id INTEGER NOT NULL DEFAULT NULL," +
                                    "FOREIGN KEY (workout_id) REFERENCES workout(id) ON DELETE CASCADE);");

                            database.execSQL("INSERT INTO workout_sample (id, relativeTime, elevation, absoluteTime, lat, lon, speed, workout_id) " +
                                    "SELECT id, relativeTime, elevation, absoluteTime, lat, lon, speed, workout_id FROM workout_sample2");

                            database.execSQL("DROP TABLE workout_sample2");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(2, 3) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout add COLUMN edited INTEGER not null default 0");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(3, 4) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("CREATE TABLE interval (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    delay_millis integer NOT NULL,\n" +
                                    "    queue_id integer NOT NULL,\n" +
                                    "   FOREIGN KEY (queue_id) \n" +
                                    "      REFERENCES interval_queue (id) \n" +
                                    "         ON DELETE CASCADE \n" +
                                    "         ON UPDATE NO ACTION" +
                                    ");");

                            database.execSQL("CREATE TABLE interval_queue (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    state integer not null\n" +
                                    ");");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(3, 5) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("CREATE TABLE interval (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    delay_millis integer NOT NULL,\n" +
                                    "    set_id integer NOT NULL,\n" +
                                    "   FOREIGN KEY (set_id) \n" +
                                    "      REFERENCES interval_set (id) \n" +
                                    "         ON DELETE CASCADE \n" +
                                    "         ON UPDATE NO ACTION" +
                                    ");");

                            database.execSQL("CREATE TABLE interval_set (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    state integer not null\n" +
                                    ");");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(4, 5) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("drop table interval");

                            database.execSQL("drop table interval_queue");

                            database.execSQL("CREATE TABLE interval (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    delay_millis integer NOT NULL,\n" +
                                    "    set_id integer NOT NULL,\n" +
                                    "   FOREIGN KEY (set_id) \n" +
                                    "      REFERENCES interval_set (id) \n" +
                                    "         ON DELETE CASCADE \n" +
                                    "         ON UPDATE NO ACTION" +
                                    ");");

                            database.execSQL("CREATE TABLE interval_set (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    state integer not null\n" +
                                    ");");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(5, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout add COLUMN interval_set_used_id INTEGER not null default 0");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(6, 7) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout add COLUMN interval_set_include_pauses INTEGER not null default 0");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(7, 8) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout_sample add COLUMN pressure REAL not null default 0");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(8, 9) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout_sample add COLUMN elevation_msl REAL not null default 0;");

                            database.execSQL("UPDATE workout_sample set elevation_msl = elevation;");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(9, 10) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout_sample add COLUMN heart_rate INTEGER not null default 0;");
                            database.execSQL("ALTER table workout add COLUMN avg_heart_rate INTEGER not null default 0;");
                            database.execSQL("ALTER table workout add COLUMN max_heart_rate INTEGER not null default 0;");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(10, 11) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("CREATE TABLE workout_type (\n" +
                                    "    id text NOT NULL primary key,\n" +
                                    "    icon text,\n" +
                                    "    title text,\n" +
                                    "    min_distance integer NOT NULL,\n" +
                                    "    color integer NOT NULL,\n" +
                                    "    met integer NOT NULL\n" +
                                    ");");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                })
                .allowMainThreadQueries()
                .build();
    }
}
