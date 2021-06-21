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
import android.app.Dialog;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.ui.workout.diagram.ConverterManager;
import ve.cyclos.fitness.ui.workout.diagram.SampleConverter;

public class SampleConverterPickerDialog {

    private static final int MAX_SELECTED = 2;

    private final Activity context;
    private final SampleConverterSelectListener listener;
    private final ConverterManager manager;
    private Dialog dialog;

    public SampleConverterPickerDialog(Activity context, SampleConverterSelectListener listener, ConverterManager manager) {
        this.context = context;
        this.listener = listener;
        this.manager = manager;
    }

    public void show() {
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (final SampleConverter converter : manager.availableConverters) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(converter.getName());
            checkBox.setChecked(manager.selectedConverters.contains(converter));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (manager.selectedConverters.size() >= MAX_SELECTED) {
                        checkBox.setChecked(false);
                        return;
                    }
                    manager.selectedConverters.add(converter);
                    if (manager.selectedConverters.size() >= MAX_SELECTED && dialog != null) {
                        dialog.dismiss();
                    }
                } else {
                    manager.selectedConverters.remove(converter);
                }
            });
            linearLayout.addView(checkBox);
        }

        ScrollView scrollView = new ScrollView(context);

        scrollView.addView(linearLayout);
        builderSingle.setView(scrollView);

        builderSingle.setCancelable(true);

        builderSingle.setPositiveButton(R.string.okay, null);
        builderSingle.setOnDismissListener(dialog -> listener.onDialogClose());

        dialog = builderSingle.create();

        dialog.show();
    }

    public interface SampleConverterSelectListener {

        void onDialogClose();

    }

}
