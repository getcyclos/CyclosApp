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

package ve.cyclos.fitness.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import ve.cyclos.fitness.R;

public class ChooseBluetoothDeviceDialog {

    private final Activity context;
    private final BluetoothDeviceSelectListener listener;
    private List<BluetoothDevice> devices;
    private final BluetoothAdapter adapter;

    public ChooseBluetoothDeviceDialog(Activity context, BluetoothDeviceSelectListener listener) throws BluetoothNotAvailableException {
        this.context = context;
        this.listener = listener;
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        fetchDevices();
    }

    private void fetchDevices() throws BluetoothNotAvailableException {
        if (adapter == null || !adapter.isEnabled()) {
            throw new BluetoothNotAvailableException();
        }
        devices = new ArrayList<>(adapter.getBondedDevices());
    }

    public void show() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.select_dialog_singlechoice_material);
        for (BluetoothDevice device : devices) {
            arrayAdapter.add(device.getName() + " (" + device.getAddress() + ")");
        }

        builderSingle.setTitle(R.string.selectBluetoothDevice);
        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> listener.onSelectBluetoothDevice(devices.get(which)));
        builderSingle.show();
    }

    public interface BluetoothDeviceSelectListener {
        void onSelectBluetoothDevice(BluetoothDevice device);
    }

    public static class BluetoothNotAvailableException extends Exception {
    }

}
