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

package ve.cyclos.fitness.util.gpx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackPointExtensions {

    private double speed;

    @JacksonXmlProperty(localName = "TrackPointExtension", namespace = "gpxtpx")
    private GpxTpxExtension gpxTpxExtension;

    public TrackPointExtensions() {
    }

    public TrackPointExtensions(double speed, GpxTpxExtension gpxTpxExtension) {
        this.speed = speed;
        this.gpxTpxExtension = gpxTpxExtension;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public GpxTpxExtension getGpxTpxExtension() {
        return gpxTpxExtension;
    }

    public void setGpxTpxExtension(GpxTpxExtension gpxTpxExtension) {
        this.gpxTpxExtension = gpxTpxExtension;
    }
}
