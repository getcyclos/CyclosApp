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
import android.content.Intent;
import android.widget.ArrayAdapter;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.IntervalSet;
import ve.cyclos.fitness.ui.settings.ManageIntervalSetsActivity;

public class SelectIntervalSetDialog {

    private final Activity context;
    private final IntervalSetSelectListener listener;
    private final IntervalSet[] sets;

    public SelectIntervalSetDialog(Activity context, IntervalSetSelectListener listener) {
        this.context = context;
        this.listener = listener;
        this.sets = Instance.getInstance(context).db.intervalDao().getVisibleSets();
    }

    public void show() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.select_dialog_singlechoice_material);
        for (IntervalSet set : sets) {
            arrayAdapter.add(set.name);
        }

        builderSingle.setTitle(R.string.selectIntervalSet);
        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> listener.onIntervalSetSelect(sets[which]));
        builderSingle.setNeutralButton(R.string.manageIntervalSets, (dialog, which) -> openManageSetsActivity());
        builderSingle.show();
    }

    private void openManageSetsActivity() {
        context.startActivity(new Intent(context, ManageIntervalSetsActivity.class));
    }

    public interface IntervalSetSelectListener {
        void onIntervalSetSelect(IntervalSet set);
    }

}
