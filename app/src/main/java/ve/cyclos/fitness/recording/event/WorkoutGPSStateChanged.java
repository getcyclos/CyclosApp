package ve.cyclos.fitness.recording.event;

import ve.cyclos.fitness.recording.WorkoutRecorder;

public class WorkoutGPSStateChanged {

    public final WorkoutRecorder.GpsState oldState;
    public final WorkoutRecorder.GpsState newState;

    public WorkoutGPSStateChanged(WorkoutRecorder.GpsState oldState, WorkoutRecorder.GpsState newState) {
        this.oldState = oldState;
        this.newState = newState;
    }
}
