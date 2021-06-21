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
package ve.cyclos.fitness.recording.sensors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import ve.cyclos.fitness.recording.event.HeartRateChangeEvent;
import ve.cyclos.fitness.util.BluetoothDevicePreferences;
import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.common.callback.hr.HeartRateMeasurementDataCallback;

public class HRManager extends BleManager {

    private static final UUID HR_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
    private static final UUID HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");

    private BluetoothGattCharacteristic heartRateCharacteristic;
    private final BluetoothAdapter bluetoothAdapter;
    private final HRManagerCallback callback;
    private final BluetoothDevicePreferences preferences;

    public HRManager(final Context context, HRManagerCallback callback) {
        super(context);
        this.callback = callback;
        this.preferences = new BluetoothDevicePreferences(context);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void start() {
        if (isConnectionPossible()) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                String deviceHardwareAddress = device.getAddress();
                if (deviceHardwareAddress.equals(getBluetoothAddress())) {
                    connect(device)
                            .useAutoConnect(true)
                            .retry(3, 100)
                            .enqueue();
                }
            }
        }
    }

    public boolean isConnectionPossible() {
        return isBluetoothAddressAvailable() && bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public boolean isBluetoothAddressAvailable() {
        return !getBluetoothAddress().isEmpty();
    }

    public String getBluetoothAddress() {
        return preferences.getAddress(BluetoothDevicePreferences.DEVICE_HEART_RATE);
    }

    public void stop() {
        disconnect();
        close();
    }

    @NonNull
    @Override
    protected HeartRateManagerCallback getGattCallback() {
        return new HeartRateManagerCallback();
    }

    private final class HeartRateManagerCallback extends BleManagerGattCallback {

        @Override
        protected void initialize() {
            super.initialize();
            setNotificationCallback(heartRateCharacteristic).with(new HeartRateMeasurementDataCallback() {
                @Override
                public void onHeartRateMeasurementReceived(
                        @NonNull BluetoothDevice device,
                        int heartRate,
                        @Nullable Boolean contactDetected,
                        @Nullable Integer energyExpanded,
                        @Nullable List<Integer> rrIntervals) {
                    callback.onHeartRateMeasure(new HeartRateChangeEvent(
                            device,
                            heartRate,
                            contactDetected != null ? contactDetected : false,
                            energyExpanded != null ? energyExpanded : 0,
                            rrIntervals));
                }
            });
            enableNotifications(heartRateCharacteristic).enqueue();
        }

        @Override
        protected boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(HR_SERVICE_UUID);
            if (service != null) {
                heartRateCharacteristic = service.getCharacteristic(HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID);
            }
            return heartRateCharacteristic != null;
        }

        @Override
        protected boolean isOptionalServiceSupported(@NonNull final BluetoothGatt gatt) {
            return super.isOptionalServiceSupported(gatt);
        }

        @Override
        protected void onDeviceDisconnected() {
            heartRateCharacteristic = null;
        }
    }

    public interface HRManagerCallback {
        void onHeartRateMeasure(HeartRateChangeEvent event);
    }
}