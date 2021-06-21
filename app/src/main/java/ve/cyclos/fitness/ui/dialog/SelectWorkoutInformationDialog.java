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
import android.widget.ArrayAdapter;

import java.util.List;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.recording.information.InformationManager;
import ve.cyclos.fitness.recording.information.RecordingInformation;

public class SelectWorkoutInformationDialog {

    private final Activity context;
    private final WorkoutInformationSelectListener listener;
    private final int slot;
    private final List<RecordingInformation> informationList;

    public SelectWorkoutInformationDialog(Activity context, int slot, WorkoutInformationSelectListener listener) {
        this.context = context;
        this.listener = listener;
        this.slot = slot;
        this.informationList = new InformationManager(context).getDisplayableInformation();
    }

    public void show() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.select_dialog_singlechoice_material);
        for (RecordingInformation information : informationList) {
            arrayAdapter.add(information.getTitle());
        }

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> onSelect(which));
        builderSingle.show();
    }

    private void onSelect(int which) {
        RecordingInformation information = informationList.get(which);
        Instance.getInstance(context).userPreferences.setIdOfDisplayedInformation(slot, information.getId());
        listener.onSelectWorkoutInformation(slot, information);
    }

    public interface WorkoutInformationSelectListener {
        void onSelectWorkoutInformation(int slot, RecordingInformation information);
    }

}
