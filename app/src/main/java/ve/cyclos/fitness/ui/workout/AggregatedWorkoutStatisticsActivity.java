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

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.aggregation.AggregatedInformationDataPoint;
import ve.cyclos.fitness.aggregation.AggregatedWorkoutData;
import ve.cyclos.fitness.aggregation.AggregationSpan;
import ve.cyclos.fitness.aggregation.AggregationType;
import ve.cyclos.fitness.aggregation.WorkoutAggregator;
import ve.cyclos.fitness.aggregation.WorkoutInformation;
import ve.cyclos.fitness.aggregation.WorkoutInformationManager;
import ve.cyclos.fitness.aggregation.WorkoutTypeFilter;
import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.data.WorkoutType;
import ve.cyclos.fitness.ui.CyclosAppActivity;
import ve.cyclos.fitness.ui.dialog.SelectWorkoutTypeDialog;
import ve.cyclos.fitness.ui.dialog.SelectWorkoutTypeDialogAll;
import ve.cyclos.fitness.util.Icon;
import ve.cyclos.fitness.util.unit.UnitUtils;

import static android.widget.AdapterView.OnItemSelectedListener;

public class AggregatedWorkoutStatisticsActivity extends CyclosAppActivity implements SelectWorkoutTypeDialog.WorkoutTypeSelectListener {

    CombinedChart chart;
    Spinner informationSelector, timeSpanSelector;
    View typeSelector;
    TextView infoMin, infoAvg, infoMax;
    TextView workoutTypeText;
    TextView axisLeftLabel, axisRightLabel, xAxisLabel;
    ImageView workoutTypeIcon;
    WorkoutInformationManager informationManager;

    WorkoutInformation selectedInformation;
    WorkoutType selectedWorkoutType;
    AggregationSpan selectedSpan = AggregationSpan.WEEK;

    AggregatedWorkoutData aggregatedWorkoutData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_workouts_aggregated);
        setTitle(getString(R.string.workout_statistics));
        setupActionBar();

        informationSelector = findViewById(R.id.aggregationInfo);
        typeSelector = findViewById(R.id.aggregationWorkoutTypeSelector);
        timeSpanSelector = findViewById(R.id.aggregationSpan);
        infoMin = findViewById(R.id.aggregationOverviewMin);
        infoAvg = findViewById(R.id.aggregationOverviewAvg);
        infoMax = findViewById(R.id.aggregationOverviewMax);
        axisLeftLabel = findViewById(R.id.aggregationDiagramLeftAxis);
        axisRightLabel = findViewById(R.id.aggregationDiagramRightAxis);
        xAxisLabel = findViewById(R.id.aggregationDiagramXAxis);
        workoutTypeText = findViewById(R.id.aggregationWorkoutTypeTitle);
        workoutTypeIcon = findViewById(R.id.aggregationWorkoutTypeIcon);
        chart = findViewById(R.id.aggregationChart);

        selectedWorkoutType = WorkoutType.getWorkoutTypeById(this, WorkoutType.WORKOUT_TYPE_ID_RUNNING);
        informationManager = new WorkoutInformationManager(this);
        selectedInformation = informationManager.getInformation().get(0);

        initInformationSpinner();
        initTypeSelector();
        initTimeSpanSpinner();

        initChart();

        refresh();

        timeSpanSelector.setSelection(2);

        if (getLastWorkout() != null) {
            onSelectWorkoutType(getLastWorkout().getWorkoutType(this));
        } else {
            Toast.makeText(this, R.string.no_workouts_recorded, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private Workout getLastWorkout() {
        return Instance.getInstance(this).db.workoutDao().getLastWorkout();
    }

    private void initInformationSpinner() {
        List<WorkoutInformation> informationList = informationManager.getInformation();
        List<String> strings = new ArrayList<>();
        for (WorkoutInformation information : informationList) {
            strings.add(getString(information.getTitleRes()));
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
        informationSelector.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                onWorkoutInformationSelect(informationList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        informationSelector.setAdapter(spinnerAdapter);
    }

    private void initTypeSelector() {
        typeSelector.setOnClickListener(v -> {
            new SelectWorkoutTypeDialogAll(this, this).show();
        });
    }

    private void initTimeSpanSpinner() {
        AggregationSpan[] spans = AggregationSpan.values();
        List<String> strings = new ArrayList<>();
        for (AggregationSpan span : spans) {
            strings.add(getString(span.title));
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
        timeSpanSelector.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                onTimeSpanSelect(spans[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpanSelector.setAdapter(spinnerAdapter);
    }

    private void initChart() {
        chart.getAxisLeft().setTextColor(getThemeTextColor());
        chart.getAxisLeft().setTextSize(12);
        chart.getAxisRight().setTextColor(getThemeTextColor());
        chart.getAxisRight().setTextSize(12);

        chart.getXAxis().setTextColor(getThemeTextColor());
        chart.getXAxis().setTextSize(12);
        chart.getXAxis().setYOffset(-1);

        chart.setNoDataText(getString(R.string.no_workouts_recorded_for_this_activity));
        chart.setNoDataTextColor(getThemeTextColor());

        chart.setScaleYEnabled(false);

        chart.getLegend().setTextColor(getThemeTextColor());
        chart.getLegend().setTextSize(12);
    }

    private void refresh() {
        aggregatedWorkoutData = new WorkoutAggregator(this, new WorkoutTypeFilter(selectedWorkoutType), selectedInformation, selectedSpan).aggregate();
        refreshValueTexts();
        refreshChart();
        setTitle(getString(selectedInformation.getTitleRes()) + " " + getString(R.string.per) + " " + getString(selectedSpan.title));
        workoutTypeText.setText(selectedWorkoutType.title);
        workoutTypeIcon.setImageResource(Icon.getIcon(selectedWorkoutType.icon));
        axisLeftLabel.setText(selectedInformation.getUnit());
        axisRightLabel.setText(selectedInformation.getUnit());
        xAxisLabel.setText(selectedSpan.axisLabel);
    }

    private void refreshValueTexts() {
        String unitSuffix = " " + selectedInformation.getUnit();
        infoMin.setText(getString(R.string.min) + ": " + UnitUtils.round(aggregatedWorkoutData.getMin(), 2) + unitSuffix);
        if (selectedInformation.getAggregationType() == AggregationType.SUM) {
            infoAvg.setText(getString(R.string.sum) + ": " + UnitUtils.round(aggregatedWorkoutData.getSum(), 2) + unitSuffix);
        } else {
            infoAvg.setText(getString(R.string.avg) + ": " + UnitUtils.round(aggregatedWorkoutData.getAvg(), 2) + unitSuffix);
        }
        infoMax.setText(getString(R.string.max) + ": " + UnitUtils.round(aggregatedWorkoutData.getMax(), 2) + unitSuffix);
    }

    private void refreshChart() {
        chart.resetTracking();
        chart.resetZoom();
        chart.resetViewPortOffsets();
        chart.clear();

        if (aggregatedWorkoutData.getDataPoints().size() == 0) {
            return;
        }

        CombinedData combinedData = new CombinedData();
        if (selectedInformation.getAggregationType() == AggregationType.SUM) {
            combinedData.setData(createBarData());
            combinedData.setData(new LineData(new LineDataSet(new ArrayList<>(), "")));
        } else {
            combinedData.setData(createLineData());
            combinedData.setData(new BarData(new BarDataSet(new ArrayList<>(), "")));
        }
        chart.setData(combinedData);

        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return selectedSpan.dateFormat.format(new Date((long) value + (selectedSpan.spanInterval / 2)));
            }
        });
        chart.getXAxis().setGranularity(selectedSpan.spanInterval);

        Description description = new Description();
        description.setTextColor(getThemeTextColor());
        description.setText(selectedInformation.getUnit());
        description.setTextSize(12);
        chart.setDescription(description);

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e.getData() != null && e.getData() instanceof AggregatedInformationDataPoint) {
                    AggregatedInformationDataPoint dataPoint = (AggregatedInformationDataPoint) e.getData();
                    String formattedDate = chart.getXAxis().getValueFormatter().getFormattedValue(dataPoint.getDate().getTime());
                    String text = getString(selectedSpan.title) + " " + formattedDate + ": " + UnitUtils.round(e.getY(), 2) + " " + selectedInformation.getUnit();
                    chart.getDescription().setText(text);
                    if (selectedSpan == AggregationSpan.SINGLE) {
                        openWorkoutAt(dataPoint.getDate().getTime());
                    }
                } else {
                    onNothingSelected();
                }
            }

            @Override
            public void onNothingSelected() {
                chart.getDescription().setText(selectedInformation.getUnit());
            }
        });

        chart.invalidate();
        chart.animateY(500, Easing.EaseOutCubic);
        chart.zoomAndCenterAnimated(1, 1, 0, 0, YAxis.AxisDependency.LEFT, 500);
    }


    private BarData createBarData() {
        BarDataSet barDataSet;
        barDataSet = new BarDataSet(getDiagramEntries(), getString(selectedInformation.getTitleRes()) + " - " + getString(selectedInformation.getAggregationType().title));
        barDataSet.setColor(getThemePrimaryColor());
        barDataSet.setBarBorderColor(getThemePrimaryColor());
        barDataSet.setBarBorderWidth(3f);
        barDataSet.setValueTextColor(getThemeTextColor());
        barDataSet.setValueTextSize(12);
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(selectedSpan.spanInterval * 0.85f);

        return barData;
    }

    private LineData createLineData() {
        LineDataSet lineDataSet;
        lineDataSet = new LineDataSet(new ArrayList<>(getDiagramEntries()), getString(selectedInformation.getTitleRes()) + " - " + getString(selectedInformation.getAggregationType().title));
        lineDataSet.setColor(getThemePrimaryColor());
        lineDataSet.setValueTextColor(getThemeTextColor());
        lineDataSet.setValueTextSize(12);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleColor(getThemePrimaryColor());
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(2);
        lineDataSet.setCircleHoleColor(getThemeTextColorInverse());
        lineDataSet.setLineWidth(4);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        if (selectedInformation.getAggregationType() == AggregationType.AVERAGE
                || selectedSpan == AggregationSpan.SINGLE) {
            float xMin = lineDataSet.getXMin();
            float xMax = lineDataSet.getXMax();

            dataSets.add(createHorizontalLineData(xMin, xMax, (float) aggregatedWorkoutData.getMax(), getResources().getColor(R.color.aggregatedDiagramMax), getString(R.string.max)));
            dataSets.add(createHorizontalLineData(xMin, xMax, (float) aggregatedWorkoutData.getAvg(), getResources().getColor(R.color.aggregatedDiagramAvg), getString(R.string.avg)));
            dataSets.add(createHorizontalLineData(xMin, xMax, (float) aggregatedWorkoutData.getMin(), getResources().getColor(R.color.aggregatedDiagramMin), getString(R.string.min)));
        }

        return new LineData(dataSets);
    }

    private List<BarEntry> getDiagramEntries() {
        final ArrayList<BarEntry> entries = new ArrayList<>();
        for (AggregatedInformationDataPoint dataPoint : aggregatedWorkoutData.getDataPoints()) {
            float value;
            switch (selectedInformation.getAggregationType()) {
                default:
                case SUM:
                    value = (float) dataPoint.getSum();
                    break;
                case AVERAGE:
                    value = (float) dataPoint.getAvg();
                    break;
            }
            entries.add(new BarEntry(dataPoint.getDate().getTime(), value, dataPoint));
        }
        return entries;
    }

    private LineDataSet createHorizontalLineData(float xMin, float xMax, float y, int color, String label) {
        final ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(xMin, y));
        entries.add(new Entry(xMax, y));

        LineDataSet lineDataSet = new LineDataSet(entries, label);
        lineDataSet.setColor(color);
        lineDataSet.setLineWidth(2);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.enableDashedLine(10, 10, 0);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        return lineDataSet;
    }

    private void openWorkoutAt(long time) {
        Workout workout = Instance.getInstance(this).db.workoutDao().getWorkoutByStart(time);
        if (workout != null) {
            final Intent intent = new Intent(this, ShowWorkoutActivity.class);
            intent.putExtra(ShowWorkoutActivity.WORKOUT_ID_EXTRA, workout.id);
            startActivity(intent);
        } else {
            Log.i("DiagramActivity", "Cannot get workout at time=" + time);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.workout_stats_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionStatsHelp) {
            showHelpDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.help)
                .setMessage(R.string.workoutStatsHelpText)
                .setPositiveButton(R.string.okay, null)
                .create().show();
    }

    @Override
    public void onSelectWorkoutType(WorkoutType workoutType) {
        selectedWorkoutType = workoutType;
        refresh();
    }

    private void onWorkoutInformationSelect(WorkoutInformation information) {
        this.selectedInformation = information;
        refresh();
    }

    private void onTimeSpanSelect(AggregationSpan span) {
        this.selectedSpan = span;
        refresh();
    }
}