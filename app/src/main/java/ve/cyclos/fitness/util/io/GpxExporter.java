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

import android.annotation.SuppressLint;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.data.WorkoutSample;
import ve.cyclos.fitness.util.gpx.Gpx;
import ve.cyclos.fitness.util.gpx.GpxTpxExtension;
import ve.cyclos.fitness.util.gpx.Metadata;
import ve.cyclos.fitness.util.gpx.Track;
import ve.cyclos.fitness.util.gpx.TrackPoint;
import ve.cyclos.fitness.util.gpx.TrackPointExtensions;
import ve.cyclos.fitness.util.gpx.TrackSegment;
import ve.cyclos.fitness.util.io.general.IWorkoutExporter;

public class GpxExporter implements IWorkoutExporter {

    @SuppressLint("SimpleDateFormat") // This has nothing to do with localisation
    public final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public GpxExporter() {
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void exportWorkout(Workout workout, List<WorkoutSample> samples, OutputStream fileStream) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        mapper.enable(ToXmlGenerator.Feature.WRITE_XML_1_1);
        mapper.writeValue(fileStream, getGpxFromWorkout(workout, samples));
    }

    private Gpx getGpxFromWorkout(Workout workout, List<WorkoutSample> samples) {
        Track track = getTrackFromWorkout(workout, samples, 0);
        ArrayList<Track> tracks = new ArrayList<>();
        tracks.add(track);
        Metadata meta = new Metadata(workout.toString(), workout.comment, getDateTime(workout.start));
        return new Gpx("1.0", "CyclosApp", meta, workout.toString(), workout.comment, tracks);
    }

    private Track getTrackFromWorkout(Workout workout, List<WorkoutSample> samples, int number) {
        Track track = new Track();
        track.setNumber(number);
        track.setName(workout.toString());
        track.setCmt(workout.comment);
        track.setDesc(workout.comment);
        track.setSrc("CyclosApp");
        track.setType(workout.workoutTypeId);
        track.setTrkseg(new ArrayList<>());

        TrackSegment segment = new TrackSegment();
        ArrayList<TrackPoint> trkpt = new ArrayList<>();

        for(WorkoutSample sample : samples){
            trkpt.add(new TrackPoint(sample.lat, sample.lon, sample.elevation,
                    getDateTime(sample.absoluteTime), new TrackPointExtensions(sample.speed, new GpxTpxExtension(sample.heartRate))));
        }
        segment.setTrkpt(trkpt);

        ArrayList<TrackSegment> segments = new ArrayList<>();
        segments.add(segment);
        track.setTrkseg(segments);

        return track;
    }

    private String getDateTime(long time) {
        return getDateTime(new Date(time));
    }

    private String getDateTime(Date date) {
        // Why adding a 'Z'?
        // Normally we could use the 'X' char to specify the timezone but this is only available in Android 7+
        // Since this minSdkVersion is 21 (Android 5) we cannot use it
        // Solution: add a 'Z'. This indicates a UTC-timestamp and the 'formatter' always returns UTC-timestamps (see constructor)
        return formatter.format(date) + "Z";
    }
}
