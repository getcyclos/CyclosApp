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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "gpx")
public class Gpx {

    @JacksonXmlProperty(isAttribute = true)
    String version;

    @JacksonXmlProperty(isAttribute = true)
    String creator;

    @JacksonXmlProperty(isAttribute = true, localName = "xsi:schemaLocation")
    String schemaLocation = "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsi")
    String schemaInstance = "http://www.w3.org/2001/XMLSchema-instance";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:gpxtpx")
    String tpxSchemaLocation = "http://www.garmin.com/xmlschemas/TrackPointExtension/v1";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    String xmlNamespace = "http://www.topografix.com/GPX/1/1";

    Metadata metadata;

    String name;
    private String desc;

    @JacksonXmlElementWrapper(useWrapping = false)
    List<Track> trk;

    public Gpx() {
    }

    public Gpx(String version, String creator, String schemaLocation, String schemaInstance, String tpxSchemaLocation, String xmlNamespace, Metadata metadata, String name, String desc, List<Track> trk) {
        this.version = version;
        this.creator = creator;
        this.schemaLocation = schemaLocation;
        this.schemaInstance = schemaInstance;
        this.tpxSchemaLocation = tpxSchemaLocation;
        this.xmlNamespace = xmlNamespace;
        this.metadata = metadata;
        this.name = name;
        this.desc = desc;
        this.trk = trk;
    }

    public Gpx(String version, String creator, Metadata metadata, String name, String desc, List<Track> trk) {
        this.version = version;
        this.creator = creator;
        this.metadata = metadata;
        this.name = name;
        this.desc = desc;
        this.trk = trk;
    }

    public String getVersion() {
        return version;
    }

    public String getCreator() {
        return creator;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public List<Track> getTrk() {
        return trk;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTrk(List<Track> trk) {
        this.trk = trk;
    }
}
