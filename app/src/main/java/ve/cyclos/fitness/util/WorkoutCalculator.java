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

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;
import java.util.List;

import ve.cyclos.fitness.data.Interval;
import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.data.WorkoutData;
import ve.cyclos.fitness.data.WorkoutSample;

public class WorkoutCalculator {

    public static List<Pause> getPausesFromWorkout(WorkoutData data) {
        List<Pause> result = new ArrayList<>();
        List<WorkoutSample> samples = data.getSamples();

        long absoluteTime = data.getWorkout().start;
        long relativeTime = 0;
        boolean lastWasPause = false;

        for (WorkoutSample sample : samples) {
            long absoluteDiff = sample.absoluteTime - absoluteTime;
            long relativeDiff = sample.relativeTime - relativeTime;
            long diff = absoluteDiff - relativeDiff;

            if (diff > 10000) {
                if (lastWasPause) {
                    // Add duration to last pause if there is no sample between detected pauses
                    result.get(result.size() - 1).addDuration(diff);
                } else {
                    result.add(new Pause(absoluteTime, relativeTime, diff, sample.toLatLong()));
                }
                lastWasPause = true;
            } else {
                lastWasPause = false;
            }
            absoluteTime = sample.absoluteTime;
            relativeTime = sample.relativeTime;
        }
        return result;
    }

    public static class Pause {
        public final long absoluteTimeStart;
        public final long relativeTimeStart;
        public long duration;
        public final LatLong location;

        public Pause(long absoluteTimeStart, long relativeTimeStart, long duration, LatLong location) {
            this.absoluteTimeStart = absoluteTimeStart;
            this.relativeTimeStart = relativeTimeStart;
            this.duration = duration;
            this.location = location;
        }

        private void addDuration(long duration) {
            this.duration += duration;
        }

    }

    public static List<Long> getIntervalSetTimesFromWorkout(WorkoutData data, Interval[] intervals) {
        List<Long> result = new ArrayList<>();
        Workout workout = data.getWorkout();
        List<WorkoutSample> samples = data.getSamples();

        int index = 0;
        long time = 0;
        if (workout.intervalSetIncludesPauses) {
            long lastTime = samples.get(0).absoluteTime;
            for (WorkoutSample sample : samples) {
                if (index >= intervals.length) {
                    index = 0;
                }
                Interval currentInterval = intervals[index];
                time += sample.absoluteTime - lastTime;
                if (time > currentInterval.delayMillis) {
                    time = 0;
                    index++;
                    result.add(sample.relativeTime);
                }
                lastTime = sample.absoluteTime;
            }
        } else {
            while (time < workout.duration) {
                if (index >= intervals.length) {
                    index = 0;
                }
                Interval interval = intervals[index];

                result.add(time);

                time += interval.delayMillis;
                index++;
            }
        }
        return result;
    }

}