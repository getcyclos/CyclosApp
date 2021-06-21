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

package ve.cyclos.fitness.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.WorkoutType;
import ve.cyclos.fitness.ui.CyclosAppActivity;
import ve.cyclos.fitness.ui.adapter.WorkoutTypeAdapter;

public class ManageWorkoutTypesActivity extends CyclosAppActivity implements WorkoutTypeAdapter.WorkoutTypeAdapterListener {

    private RecyclerView recyclerView;
    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_workout_types);

        setTitle(R.string.customWorkoutTypesTitle);
        setupActionBar();

        recyclerView = findViewById(R.id.workoutTypesList);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.workoutTypesAdd).setOnClickListener(v -> openEditActivity(null));
        hint = findViewById(R.id.workoutTypesHint);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        WorkoutType[] types = Instance.getInstance(this).db.workoutTypeDao().findAll();
        WorkoutTypeAdapter adapter = new WorkoutTypeAdapter(Arrays.asList(types), this);
        recyclerView.setAdapter(adapter);
        hint.setVisibility(types.length == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onItemSelect(int pos, WorkoutType type) {
        openEditActivity(type);
    }

    void openEditActivity(@Nullable WorkoutType type) {
        Intent intent = new Intent(this, EditWorkoutTypeActivity.class);
        if (type != null) {
            intent.putExtra(EditWorkoutTypeActivity.EXTRA_TYPE_ID, type.id);
        }
        startActivity(intent);
    }

}