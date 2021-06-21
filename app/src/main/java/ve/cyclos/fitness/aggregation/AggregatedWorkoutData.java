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

package ve.cyclos.fitness.aggregation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ve.cyclos.fitness.data.Workout;

public class AggregatedWorkoutData {

    private final List<WorkoutInformationResult> data;
    private double min, avg, max, sum;
    private final AggregationSpan span;

    private final Map<Long, AggregatedInformationDataPoint> dataPoints = new HashMap<>();

    public AggregatedWorkoutData(List<WorkoutInformationResult> data, AggregationSpan span) {
        this.data = data;
        this.span = span;
        aggregateAll();
    }

    public List<AggregatedInformationDataPoint> getDataPoints() {
        List<AggregatedInformationDataPoint> dataPoints = new ArrayList<>(this.dataPoints.values());
        Collections.sort(dataPoints, (o1, o2) -> Long.compare(o1.getDate().getTime(), o2.getDate().getTime()));
        return dataPoints;
    }

    private void aggregateAll() {
        if (data.size() > 0) {
            min = data.get(0).getValue();
            max = data.get(0).getValue();
        }
        for (WorkoutInformationResult informationResult : data) {
            if (informationResult.getValue() > max) {
                max = informationResult.getValue();
            }
            if (informationResult.getValue() < min) {
                min = informationResult.getValue();
            }
            sum += informationResult.getValue();
            aggregateWorkout(informationResult);
        }
        avg = sum / data.size();
    }

    private void aggregateWorkout(WorkoutInformationResult informationResult) {
        AggregatedInformationDataPoint dataPoint = getDataPoint(informationResult.getWorkout());
        dataPoint.setSum(dataPoint.getSum() + informationResult.getValue());
        dataPoint.setCount(dataPoint.getCount() + 1);
    }

    private AggregatedInformationDataPoint getDataPoint(Workout workout) {
        long key = getDataPointDateFromWorkout(workout);
        if (!dataPoints.containsKey(key)) {
            dataPoints.put(key, new AggregatedInformationDataPoint(new Date(key), 0, 0));
        }
        return dataPoints.get(key);
    }

    private long getDataPointDateFromWorkout(Workout workout) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(workout.start);
        span.applyToCalendar(calendar);
        return calendar.getTimeInMillis();
    }

    public double getMin() {
        return min;
    }

    public double getAvg() {
        return avg;
    }

    public double getMax() {
        return max;
    }

    public double getSum() {
        return sum;
    }
}
