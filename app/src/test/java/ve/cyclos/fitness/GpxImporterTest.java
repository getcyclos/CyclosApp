/*
 * Copyright (c) 2021 Gabriel Estrada <dev@getcyclos.com>
 *
 * This file is part of CyclosApp
 *
 *     CyclosApp is free software: you can redistribute it and/or modify
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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import ve.cyclos.fitness.data.WorkoutType;
import ve.cyclos.fitness.util.io.GpxImporter;
import ve.cyclos.fitness.util.io.general.IWorkoutImporter;

public class GpxImporterTest {
    GpxImporter importer = new GpxImporter();

    @Test(expected = IOException.class)
    public void testImportFail() throws IOException {
        importer.readWorkouts(new ByteArrayInputStream("".getBytes()));
    }

    @Test
    public void testImportCyclosAppGpx() throws IOException {
        IWorkoutImporter.WorkoutImportResult importResult = importer.readWorkouts(new ByteArrayInputStream(CyclosAppGpx.getBytes()));

        // Main test is that above method runs without error, additionally perform some checks:
        Assert.assertEquals(importResult.workout.comment, "...");
        Assert.assertEquals(importResult.workout.workoutTypeId, WorkoutType.WORKOUT_TYPE_ID_RUNNING);
        Assert.assertEquals(importResult.samples.size(), 10);
        Assert.assertEquals(importResult.samples.get(0).elevation, 148.5465381985937, 0.001);
        Assert.assertEquals(importResult.samples.get(6).speed, 3.25, 0.001);
        Assert.assertEquals(importResult.samples.get(7).lat, 42.25636801, 0.001);
        Assert.assertEquals(importResult.samples.get(9).lon, 30.23244414, 0.001);
    }

    @Test
    public void testImportOpenTracksGpx() throws IOException {
        IWorkoutImporter.WorkoutImportResult importResult = importer.readWorkouts(new ByteArrayInputStream(opentracksGpx.getBytes()));

        // Main test is that above method runs without error, additionally perform some checks:
        Assert.assertEquals(importResult.workout.comment, "...");
        Assert.assertEquals(importResult.samples.size(), 10);
        Assert.assertEquals(importResult.samples.get(0).elevation, 152.5, 0.001);
        Assert.assertEquals(importResult.samples.get(7).lat, 30.232106, 0.001);
        Assert.assertEquals(importResult.samples.get(9).lon, 10.534532, 0.001);
    }

    @Test
    public void testImportRuntasticGpx() throws IOException {
        IWorkoutImporter.WorkoutImportResult importResult = importer.readWorkouts(new ByteArrayInputStream(runtasticGpx.getBytes()));

        Assert.assertEquals(importResult.workout.comment, "runtastic_20160215_0843");
        Assert.assertEquals(importResult.workout.workoutTypeId, WorkoutType.WORKOUT_TYPE_ID_OTHER);
        Assert.assertEquals(importResult.samples.size(), 10);
        //TrackSegment / samples not tested cause its similar to other imports
    }

    @Test
    public void testImportKomootGpx() throws IOException {
        IWorkoutImporter.WorkoutImportResult importResult = importer.readWorkouts(new ByteArrayInputStream(komootGpx.getBytes()));

        Assert.assertEquals(importResult.workout.comment, "NameOfTrack");
        Assert.assertEquals(importResult.workout.workoutTypeId, WorkoutType.WORKOUT_TYPE_ID_OTHER);
        Assert.assertEquals(importResult.samples.size(), 10);
        //TrackSegment / samples not tested cause its similar to other imports
    }

    //region Data
    // here are example "files" from different trackers, to check if special stuff like the workout type or spee can be imported correctly

    //region CyclosApp
    private final String CyclosAppGpx ="<gpx creator=\"CyclosApp\" version=\"1.1\"><desc/><metadata><desc>...</desc><name>...</name><time>2020-03-31T16:55:26.080+02:00</time></metadata><name>...</name><trk><cmt>...</cmt><desc>...</desc><name>...</name><number>0</number><src>CyclosApp</src>"+
            "<trkseg>"+
            "<trkpt lat=\"42.25689651\" lon=\"30.23230041\"><ele>148.5465381985937</ele><extensions><speed>3.200000047683716</speed></extensions><fix>gps</fix><time>2020-03-31T17:55:19.000+02:00</time></trkpt>"+
            "<trkpt lat=\"42.25685617\" lon=\"30.23242502\"><ele>148.31195393520085</ele><extensions><speed>3.2200000286102295</speed></extensions><fix>gps</fix><time>2020-03-31T17:55:22.000+02:00</time></trkpt>"+
            "<trkpt lat=\"42.25678044\" lon=\"30.23249831\"><ele>148.18751633056917</ele><extensions><speed>3.2799999713897705</speed></extensions><fix>gps</fix><time>2020-03-31T17:55:25.000+02:00</time></trkpt>"+
            "<trkpt lat=\"42.25668789\" lon=\"30.2325111\"><ele>148.08797671003344</ele><extensions><speed>3.319999933242798</speed></extensions><fix>gps</fix><time>2020-03-31T17:55:28.000+02:00</time></trkpt>"+
            "<trkpt lat=\"42.25659685\" lon=\"30.23250966\"><ele>148.10125621617183</ele><extensions><speed>3.299999952316284</speed></extensions><fix>gps</fix><time>2020-03-31T17:55:31.000+02:00</time></trkpt>"+
            "<trkpt lat=\"42.25651232\" lon=\"30.23244248\"><ele>148.15522001360486</ele><extensions><speed>3.2799999713897705</speed></extensions><fix>gps</fix><time>2020-03-31T17:55:34.000+02:00</time></trkpt>"+
            "<trkpt lat=\"42.2564395\" lon=\"30.23236122\"><ele>148.14491815116068</ele><extensions><speed>3.25</speed></extensions><fix>gps</fix><time>2020-03-31T17:55:37.000+02:00</time></trkpt>"+
            "<trkpt lat=\"42.25636801\" lon=\"30.23229645\"><ele>148.22296031773433</ele><extensions><speed>3.309999942779541</speed></extensions><fix>gps</fix><time>2020-03-31T17:55:40.000+02:00</time></trkpt>"+
            "<trkpt lat=\"42.25629204\" lon=\"30.23234997\"><ele>148.20784539725443</ele><extensions><speed>3.309999942779541</speed></extensions><fix>gps</fix><time>2020-03-31T17:55:43.000+02:00</time></trkpt>"+
            "<trkpt lat=\"42.25626391\" lon=\"30.23244414\"><ele>148.34956903006693</ele><extensions><speed>3.2799999713897705</speed></extensions><fix>gps</fix><time>2020-03-31T17:55:45.000+02:00</time></trkpt>"+
            "</trkseg>"+
            "<type>running</type></trk></gpx>";
    //endregion

    //region OpenTracks
    private final String opentracksGpx ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<gpx\n" +
            "version=\"1.1\"\n" +
            "creator=\"OpenTracks\"\n" +
            "xmlns=\"http://www.topografix.com/GPX/1/1\"\n" +
            "xmlns:topografix=\"http://www.topografix.com/GPX/Private/TopoGrafix/0/1\"\n" +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.topografix.com/GPX/Private/TopoGrafix/0/1 http://www.topografix.com/GPX/Private/TopoGrafix/0/1/topografix.xsd\">\n" +
            "<metadata>\n" +  
            "<name><![CDATA[...]]></name>\n" +    
            "<desc><![CDATA[...]]></desc>\n" +   
            "</metadata>\n" +
            "<trk>\n" +
            "<name><![CDATA[...]]></name>\n" +
            "<desc><![CDATA[...]]></desc>\n" +
            "<type><![CDATA[Laufen]]></type>\n" +
            "<extensions><topografix:color>c0c0c0</topografix:color></extensions>\n" +
            "<trkseg>\n"+
            "<trkpt lat=\"30.232601\" lon=\"10.534186\">\n" +
            "<ele>152.5</ele>\n" +
            "<time>2020-03-31T15:56:34Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"30.232541\" lon=\"10.534275\">\n" +
            "<ele>152.4</ele>\n" +
            "<time>2020-03-31T15:56:37Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"30.232447\" lon=\"10.534253\">\n" +
            "<ele>152.3</ele>\n" +
            "<time>2020-03-31T15:56:40Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"30.232355\" lon=\"10.534253\">\n" +
            "<ele>152.4</ele>\n" +
            "<time>2020-03-31T15:56:43Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"30.232282\" lon=\"10.534323\">\n" +
            "<ele>152.5</ele>\n" +
            "<time>2020-03-31T15:56:46Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"30.232227\" lon=\"10.534434\">\n" +
            "<ele>152.6</ele>\n" +
            "<time>2020-03-31T15:56:49Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"30.232174\" lon=\"10.534544\">\n" +
            "<ele>152.5</ele>\n" +
            "<time>2020-03-31T15:56:52Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"30.232106\" lon=\"10.534594\">\n" +
            "<ele>152.4</ele>\n" +
            "<time>2020-03-31T15:56:55Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"30.232046\" lon=\"10.534553\">\n" +
            "<ele>152.2</ele>\n" +
            "<time>2020-03-31T15:57:00Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"30.232111\" lon=\"10.534532\">\n" +
            "<ele>152.2</ele>\n" +
            "<time>2020-03-31T15:57:18Z</time>\n" +
            "</trkpt>"+
            "\n" +
            "</trkseg>\n" +      
            "</trk>\n" +     
            "</gpx>";
    //endregion

    //region Runtastic
    //the workout type is secured in an additional json file for runtastic activities
    private final String runtasticGpx ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<gpx creator=\"Runtastic: Life is short - live long, http://www.runtastic.com\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">\n" +
            "<metadata>\n" +
            "<name>runtastic_date</name>\n" +
            "<copyright author=\"www.runtastic.com\">\n" +
            "<year>2020</year>\n" +
            "<license>http://www.runtastic.com</license>\n" +
            "</copyright>\n" +
            "<link href=\"http://www.runtastic.com\"><text>runtastic</text>\n" +
            "</link>\n" +
            "<time>2016-02-15T07:43:22.000Z</time>\n" +
            "</metadata>\n" +
            "<trk>\n" +
            "<name>runtastic_20160215_0843</name>\n" +
            "<link href=\"http://www.runtastic.com/\"><text>Visit this link to view this activity on runtastic.com</text>\n" +
            "</link>\n" +
            "<trkseg>\n" +
            "<trkpt lat=\"10.533128814697266\" lon=\"3.5184867572784424\">\n" +
            "<ele>116.0</ele>\n" +
            "<time>2016-02-15T07:43:34.000Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"10.533128814697266\" lon=\"3.5184867572784424\">\n" +
            "<ele>116.0</ele>\n" +
            "<time>2016-02-15T07:43:36.000Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"10.533128814697266\" lon=\"3.5184867572784424\">\n" +
            "<ele>116.0</ele>\n" +
            "<time>2016-02-15T07:43:38.000Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"10.53315170288086\" lon=\"3.5188417625427246\">\n" +
            "<ele>113.0</ele>\n" +
            "<time>2016-02-15T07:43:42.000Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"10.53330047607422\" lon=\"3.5189917278289795\">\n" +
            "<ele>112.0</ele>\n" +
            "<time>2016-02-15T07:43:46.000Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"10.53341491699219\" lon=\"3.5190150928497314\">\n" +
            "<ele>113.0</ele>\n" +
            "<time>2016-02-15T07:43:48.000Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"10.53341491699219\" lon=\"3.5190150928497314\">\n" +
            "<ele>113.0</ele>\n" +
            "<time>2016-02-15T07:43:50.000Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"10.53341491699219\" lon=\"3.5190150928497314\">\n" +
            "<ele>113.0</ele>\n" +
            "<time>2016-02-15T07:44:02.000Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"10.53341491699219\" lon=\"3.5190150928497314\">\n" +
            "<ele>113.0</ele>\n" +
            "<time>2016-02-15T07:44:04.000Z</time>\n" +
            "</trkpt>\n" +
            "<trkpt lat=\"10.53353317260742\" lon=\"3.518856782913208\">\n" +
            "<ele>108.0</ele>\n" +
            "<time>2016-02-15T07:44:58.000Z</time>\n" +
            "</trkpt>\n" +
            "</trkseg>\n" +
            "</trk>\n" +
            "</gpx>\n";
    //endregion

    //region komoot
    private final String komootGpx ="<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<gpx version=\"1.1\" creator=\"https://www.komoot.de\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n" +
            "  <metadata>\n" +
            "    <name>NameOfTrack</name>\n" +
            "    <author>\n" +
            "      <link href=\"https://www.komoot.de\">\n" +
            "        <text>komoot</text>\n" +
            "        <type>text/html</type>\n" +
            "      </link>\n" +
            "    </author>\n" +
            "  </metadata>"+
            "    <name>NameOfTrack</name>\n" +
            "    <trk>\n" +
            "    <trkseg>\n" +
            "      <trkpt lat=\"56.854918\" lon=\"12.225131\">\n" +
            "        <ele>197.022936</ele>\n" +
            "        <time>2020-04-11T08:25:57.630Z</time>\n" +
            "      </trkpt>\n" +
            "      <trkpt lat=\"56.854834\" lon=\"12.225246\">\n" +
            "        <ele>197.022936</ele>\n" +
            "        <time>2020-04-11T08:26:01.929Z</time>\n" +
            "      </trkpt>\n" +
            "      <trkpt lat=\"56.854780\" lon=\"12.225388\">\n" +
            "        <ele>197.022936</ele>\n" +
            "        <time>2020-04-11T08:26:05.919Z</time>\n" +
            "      </trkpt>\n" +
            "      <trkpt lat=\"56.854674\" lon=\"12.225327\">\n" +
            "        <ele>197.022936</ele>\n" +
            "        <time>2020-04-11T08:26:08.051Z</time>\n" +
            "      </trkpt>\n" +
            "      <trkpt lat=\"56.854570\" lon=\"12.225260\">\n" +
            "        <ele>197.022936</ele>\n" +
            "        <time>2020-04-11T08:26:10.931Z</time>\n" +
            "      </trkpt>\n" +
            "      <trkpt lat=\"56.854440\" lon=\"12.225163\">\n" +
            "        <ele>197.022936</ele>\n" +
            "        <time>2020-04-11T08:26:13.933Z</time>\n" +
            "      </trkpt>\n" +
            "      <trkpt lat=\"56.854340\" lon=\"12.225044\">\n" +
            "        <ele>197.022936</ele>\n" +
            "        <time>2020-04-11T08:26:15.937Z</time>\n" +
            "      </trkpt>\n" +
            "      <trkpt lat=\"56.854228\" lon=\"12.224942\">\n" +
            "        <ele>197.022936</ele>\n" +
            "        <time>2020-04-11T08:26:17.934Z</time>\n" +
            "      </trkpt>\n" +
            "      <trkpt lat=\"56.854090\" lon=\"12.224830\">\n" +
            "        <ele>197.073568</ele>\n" +
            "        <time>2020-04-11T08:26:19.934Z</time>\n" +
            "      </trkpt>\n" +
            "      <trkpt lat=\"56.853990\" lon=\"12.224693\">\n" +
            "        <ele>197.148469</ele>\n" +
            "        <time>2020-04-11T08:26:21.934Z</time>\n" +
            "      </trkpt>" +
            "    </trkseg>\n" +
            "  </trk>\n" +
            "</gpx>";
    //endregion

    //endregion

}
