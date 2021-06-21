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
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.WorkoutType;
import ve.cyclos.fitness.ui.CyclosAppActivity;
import ve.cyclos.fitness.ui.adapter.WorkoutTypeAdapter;
import ve.cyclos.fitness.ui.settings.EditWorkoutTypeActivity;
import ve.cyclos.fitness.util.Icon;

public class SelectWorkoutTypeDialog implements WorkoutTypeAdapter.WorkoutTypeAdapterListener {

    private static final String ID_ADD = "_add";

    private final Activity context;
    private final WorkoutTypeSelectListener listener;
    protected List<WorkoutType> options;
    private Dialog dialog;

    public SelectWorkoutTypeDialog(CyclosAppActivity context, WorkoutTypeSelectListener listener) {
        this.context = context;
        this.listener = listener;
        this.options = WorkoutType.getAllTypes(context);
        this.options.add(new WorkoutType(ID_ADD, context.getString(R.string.workoutTypeAdd), 0, context.getThemePrimaryColor(), Icon.ADD.name, 0));
    }

    public void show() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        RecyclerView recyclerView = new RecyclerView(context);
        WorkoutTypeAdapter adapter = new WorkoutTypeAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        builderSingle.setView(recyclerView);
        dialog = builderSingle.create();
        dialog.show();
    }

    @Override
    public void onItemSelect(int pos, WorkoutType type) {
        dialog.dismiss();
        if (type.id.equals(ID_ADD)) {
            openAddCustomWorkoutActivity();
        } else {
            listener.onSelectWorkoutType(type);
        }
    }

    private void openAddCustomWorkoutActivity() {
        context.startActivity(new Intent(context, EditWorkoutTypeActivity.class));
    }

    public interface WorkoutTypeSelectListener {
        void onSelectWorkoutType(WorkoutType workoutType);
    }
}
