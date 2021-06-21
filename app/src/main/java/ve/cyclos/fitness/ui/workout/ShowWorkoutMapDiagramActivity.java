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

package ve.cyclos.fitness.ui.workout;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.ui.dialog.SampleConverterPickerDialog;
import ve.cyclos.fitness.ui.workout.diagram.ConverterManager;
import ve.cyclos.fitness.ui.workout.diagram.HeartRateConverter;
import ve.cyclos.fitness.ui.workout.diagram.HeightConverter;
import ve.cyclos.fitness.ui.workout.diagram.SampleConverter;
import ve.cyclos.fitness.ui.workout.diagram.SpeedConverter;

public class ShowWorkoutMapDiagramActivity extends WorkoutActivity {

    public static final String DIAGRAM_TYPE_EXTRA = "ve.cyclos.fitness.ShowWorkoutMapDiagramActivity.DIAGRAM_TYPE";

    public static final String DIAGRAM_TYPE_HEIGHT = "height";
    public static final String DIAGRAM_TYPE_SPEED = "speed";
    public static final String DIAGRAM_TYPE_HEART_RATE = "heartrate";

    private ConverterManager converterManager;

    private CombinedChart chart;
    private TextView selection;
    private CheckBox showIntervals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBeforeContent();

        converterManager = new ConverterManager(this, getWorkoutData());

        setContentView(R.layout.activity_show_workout_map_diagram);
        initRoot();

        this.selection = findViewById(R.id.showWorkoutDiagramInfo);
        this.showIntervals = findViewById(R.id.showWorkoutDiagramIntervals);

        initAfterContent();

        fullScreenItems = true;
        addMap();
        mapView.setClickable(true);

        diagramsInteractive = true;
        root = findViewById(R.id.showWorkoutDiagramParent);

        initDiagram();

        findViewById(R.id.showWorkoutDiagramSelector).setOnClickListener(v -> new SampleConverterPickerDialog(this, this::updateChart, converterManager).show());
        showIntervals.setOnCheckedChangeListener((buttonView, isChecked) -> updateChart());
        showIntervals.setVisibility(intervals != null && intervals.length > 0 ? View.VISIBLE : View.GONE);
    }

    private void initDiagram() {
        SampleConverter defaultConverter = getDefaultConverter();
        converterManager.selectedConverters.add(defaultConverter);
        chart = addDiagram(defaultConverter);
        updateChart();
    }

    private SampleConverter getDefaultConverter() {
        String typeExtra = getIntent().getStringExtra(DIAGRAM_TYPE_EXTRA);
        if (typeExtra == null) typeExtra = "";
        switch (typeExtra) {
            default:
            case DIAGRAM_TYPE_SPEED:
                return new SpeedConverter(this);
            case DIAGRAM_TYPE_HEIGHT:
                return new HeightConverter(this);
            case DIAGRAM_TYPE_HEART_RATE:
                return new HeartRateConverter(this);
        }
    }

    private void updateChart() {
        updateChart(chart, converterManager.selectedConverters, showIntervals.isChecked());
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (SampleConverter converter : converterManager.selectedConverters) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(converter.getName());
        }
        selection.setText(converterManager.selectedConverters.size() > 0 ? sb.toString() : getString(R.string.nothingSelected));
    }


    @Override
    protected void initRoot() {
        root = findViewById(R.id.showWorkoutMapParent);
    }
}