package ve.cyclos.fitness.recording.event;

import android.location.Location;

public class LocationChangeEvent {

    public final Location location;

    public LocationChangeEvent(Location location) {
        this.location = location;
    }
}
