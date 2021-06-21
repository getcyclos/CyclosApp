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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.Interval;
import ve.cyclos.fitness.data.IntervalSet;
import ve.cyclos.fitness.ui.CyclosAppActivity;

public class EditIntervalSetActivity extends CyclosAppActivity implements IntervalAdapter.IntervalAdapterListener {

    private RecyclerView recyclerView;
    private IntervalAdapter adapter;
    private IntervalSet intervalSet;
    private long intervalSetId;
    private TextView intervalSetsHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_interval_set);

        intervalSetId = getIntent().getExtras().getLong("setId", -1);
        if (intervalSetId == -1) {
            finish();
            return;
        }

        intervalSet = Instance.getInstance(this).db.intervalDao().getSet(intervalSetId);

        setTitle(intervalSet.name);
        setupActionBar();

        recyclerView = findViewById(R.id.intervalsList);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.intervalAdd).setOnClickListener(v -> showAddDialog());
        intervalSetsHint = findViewById(R.id.intervalSetsHint);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_interval_set_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionDeleteIntervalSet) {
            showDeleteSetDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        Interval[] intervals = Instance.getInstance(this).db.intervalDao().getAllIntervalsOfSet(intervalSetId);
        adapter = new IntervalAdapter(new ArrayList<>(Arrays.asList(intervals)), this);
        recyclerView.setAdapter(adapter);
        intervalSetsHint.setVisibility(intervals.length == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onItemDelete(int pos, Interval interval) {
        Instance.getInstance(this).db.intervalDao().deleteInterval(interval);
        adapter.intervals.remove(interval);
        adapter.notifyItemRemoved(pos);
        if (adapter.intervals.size() == 0) {
            intervalSetsHint.setVisibility(View.VISIBLE);
        }
    }

    private void showAddDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.add_interval)
                .setView(R.layout.dialog_add_interval)
                .setPositiveButton(R.string.add, null) // Listener added later so that we can control if the dialog is dismissed on click
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            requestKeyboard(dialog.findViewById(R.id.intervalName));
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                EditText nameEditText = dialog.findViewById(R.id.intervalName);
                EditText lengthText = dialog.findViewById(R.id.intervalLengthInMinutes);
                String name = nameEditText.getText().toString();
                if (name.length() <= 2) {
                    nameEditText.setError(getString(R.string.enterName));
                    nameEditText.requestFocus();
                    return;
                }
                double lengthInMinutes;
                try {
                    lengthInMinutes = Double.parseDouble(lengthText.getText().toString());
                } catch (NumberFormatException e) {
                    lengthText.setError(getString(R.string.errorEnterValidNumber));
                    lengthText.requestFocus();
                    return;
                }
                if (lengthInMinutes < 0.1 || lengthInMinutes > 300) {
                    lengthText.setError(getString(R.string.errorEnterValidDuration));
                    lengthText.requestFocus();
                    return;
                }

                Interval interval = new Interval();
                interval.id = System.currentTimeMillis();
                interval.name = name;
                interval.delayMillis = (long) (TimeUnit.MINUTES.toMillis(1) * lengthInMinutes);
                interval.setId = intervalSetId;
                addInterval(interval);

                dialog.dismiss();
            });
        });
        dialog.show();

    }

    private void addInterval(Interval interval) {
        Instance.getInstance(this).db.intervalDao().insertInterval(interval);
        adapter.intervals.add(interval);
        adapter.notifyItemInserted(adapter.intervals.size() - 1);
        intervalSetsHint.setVisibility(View.INVISIBLE);
    }

    private void showDeleteSetDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.deleteIntervalSet)
                .setMessage(R.string.deleteIntervalSetMessage)
                .setPositiveButton(R.string.delete, (dialogInterface, which) -> deleteSet())
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    private void deleteSet() {
        intervalSet.state = IntervalSet.STATE_DELETED;
        Instance.getInstance(this).db.intervalDao().updateIntervalSet(intervalSet);
        finish();
    }

}
