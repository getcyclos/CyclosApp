package ve.cyclos.fitness.recording.event;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public class HeartRateChangeEvent {

    public final BluetoothDevice device;
    public final int heartRate;
    public final boolean contactDetected;
    public final int energyExpanded;
    public final List<Integer> rrIntervals;

    public HeartRateChangeEvent(BluetoothDevice device, int heartRate, boolean contactDetected, int energyExpanded, List<Integer> rrIntervals) {
        this.device = device;
        this.heartRate = heartRate;
        this.contactDetected = contactDetected;
        this.energyExpanded = energyExpanded;
        this.rrIntervals = rrIntervals;
    }

}
