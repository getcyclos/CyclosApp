package ve.cyclos.fitness.recording.event;

import ve.cyclos.fitness.recording.RecorderService;

public class HeartRateConnectionChangeEvent {

    public final RecorderService.HeartRateConnectionState state;

    public HeartRateConnectionChangeEvent(RecorderService.HeartRateConnectionState state) {
        this.state = state;
    }
}
