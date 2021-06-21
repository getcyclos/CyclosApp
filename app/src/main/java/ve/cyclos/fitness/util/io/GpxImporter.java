/*
 * Copyright (c) 2021 Gabriel Estrada <dev@getcyclos.com>
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

package ve.cyclos.fitness.util.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.sisyphsu.dateparser.DateParserUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.data.WorkoutData;
import ve.cyclos.fitness.data.WorkoutSample;
import ve.cyclos.fitness.util.gpx.Gpx;
import ve.cyclos.fitness.util.gpx.Track;
import ve.cyclos.fitness.util.gpx.TrackPoint;
import ve.cyclos.fitness.util.gpx.TrackPointExtensions;
import ve.cyclos.fitness.util.gpx.TrackSegment;
import ve.cyclos.fitness.util.io.general.IWorkoutImporter;

public class GpxImporter implements IWorkoutImporter {

    private Gpx gpx;

    @Override
    public WorkoutImportResult readWorkouts(InputStream input) throws IOException {
        getGpx(input);

        if (gpx.getTrk().size() == 0
                || gpx.getTrk().get(0).getTrkseg().size() == 0
                || gpx.getTrk().get(0).getTrkseg().get(0).getTrkpt().size() == 0) {
            throw new IllegalArgumentException("given GPX file does not contain location data");
        }

        List<WorkoutData> workouts = new ArrayList<>();
        for (Track track : gpx.getTrk()) {
            workouts.add(getWorkoutDataFromTrack(track));
        }

        return new WorkoutImportResult(workouts);
    }

    private void getGpx(InputStream input) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        gpx = mapper.readValue(input, Gpx.class);
    }

    private WorkoutData getWorkoutDataFromTrack(Track track) {
        TrackSegment firstSegment = track.getTrkseg().get(0);
        TrackPoint firstPoint = firstSegment.getTrkpt().get(0);

        Workout workout = new Workout();
        workout.comment = track.getName();
        if (workout.comment == null) {
            workout.comment = track.getDesc();
        }
        if (gpx.getMetadata() != null) {
            if (workout.comment == null) {
                workout.comment = gpx.getName();
            }
            if (workout.comment == null) {
                workout.comment = gpx.getMetadata().getName();
            }
            if (workout.comment == null) {
                workout.comment = gpx.getMetadata().getDesc();
            }
        }

        String startTime = firstPoint.getTime();

        workout.start = parseDate(startTime).getTime();

        int index = firstSegment.getTrkpt().size();
        String lastTime = firstSegment.getTrkpt().get(index - 1).getTime();
        workout.end = parseDate(lastTime).getTime();
        workout.duration = workout.end - workout.start;
        workout.workoutTypeId = getTypeIdById(gpx.getTrk().get(0).getType());

        List<WorkoutSample> samples = getSamplesFromTrack(workout.start, gpx.getTrk().get(0));

        return new WorkoutData(workout, samples);
    }

    private static List<WorkoutSample> getSamplesFromTrack(long startTime, Track track) {
        List<WorkoutSample> samples = new ArrayList<>();

        for (TrackSegment segment : track.getTrkseg()) {
            samples.addAll(getSamplesFromTrackSegment(startTime, segment));
        }

        return samples;
    }

    private static List<WorkoutSample> getSamplesFromTrackSegment(long startTime, TrackSegment segment) {
        List<WorkoutSample> samples = new ArrayList<>();
        for (TrackPoint point : segment.getTrkpt()) {
            WorkoutSample sample = new WorkoutSample();
            sample.absoluteTime = parseDate(point.getTime()).getTime();
            sample.elevation = point.getEle();
            sample.lat = point.getLat();
            sample.lon = point.getLon();
            sample.relativeTime = sample.absoluteTime - startTime;
            TrackPointExtensions extensions = point.getExtensions();
            if (extensions != null) {
                sample.speed = extensions.getSpeed();
                if (extensions.getGpxTpxExtension() != null) {
                    sample.heartRate = extensions.getGpxTpxExtension().getHr();
                }
            }
            samples.add(sample);
        }
        return samples;
    }

    private static Date parseDate(String str) {
        return DateParserUtils.parseDate(str);
    }

    private static String getTypeIdById(String id) {
        if (id == null) {
            id = "";
        }
        switch (id) {
            // Strava IDs
            case "1":
                return "running";
            case "2":
                return "cycling";
            case "11":
                return "walking";

            default:
                return id;
        }
    }
}