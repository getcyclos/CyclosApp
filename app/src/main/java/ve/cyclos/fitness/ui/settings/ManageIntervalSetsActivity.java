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

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.IntervalSet;
import ve.cyclos.fitness.ui.CyclosAppActivity;

public class ManageIntervalSetsActivity extends CyclosAppActivity implements IntervalSetAdapter.IntervalSetAdapterListener {

    private RecyclerView recyclerView;
    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_interval_sets);

        setTitle(R.string.manageIntervalSets);
        setupActionBar();

        recyclerView = findViewById(R.id.intervalSetsList);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.intervalSetsAdd).setOnClickListener(v -> showCreateDialog());
        hint = findViewById(R.id.intervalSetsHint);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        IntervalSet[] sets = Instance.getInstance(this).db.intervalDao().getVisibleSets();
        RecyclerView.Adapter adapter = new IntervalSetAdapter(sets, this);
        recyclerView.setAdapter(adapter);
        hint.setVisibility(sets.length == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    void showCreateDialog() {
        EditText text = new EditText(this);
        text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setTitle(R.string.createIntervalSet);
        db.setView(text);
        db.setPositiveButton(R.string.create, (dialog, which) -> createIntervalSet(text.getText().toString()));
        db.create().show();

        requestKeyboard(text);
    }

    void createIntervalSet(String name) {
        IntervalSet set = new IntervalSet();
        set.id = System.currentTimeMillis();
        set.name = name;
        set.state = IntervalSet.STATE_VISIBLE;
        Instance.getInstance(this).db.intervalDao().insertIntervalSet(set);
        startEditSetActivity(set);
    }

    public void startEditSetActivity(IntervalSet set) {
        Intent intent = new Intent(this, EditIntervalSetActivity.class);
        intent.putExtra("setId", set.id);
        startActivity(intent);
    }

    @Override
    public void onItemSelect(int pos, IntervalSet set) {
        startEditSetActivity(set);
    }

    @Override
    public void onItemDelete(int pos, IntervalSet set) {

    }
}
