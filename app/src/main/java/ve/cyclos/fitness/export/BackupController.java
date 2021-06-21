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

package ve.cyclos.fitness.export;

import android.content.Context;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.AppDatabase;
import ve.cyclos.fitness.data.Interval;
import ve.cyclos.fitness.data.IntervalSet;

public class BackupController {

    static final int VERSION = 1;

    private final Context context;
    private final File output;
    private final ExportStatusListener listener;
    private AppDatabase database;

    private CyclosAppDataContainer dataContainer;

    public BackupController(Context context, File output, ExportStatusListener listener) {
        this.context = context;
        this.output = output;
        this.listener = listener;
    }

    public void exportData() throws IOException {
        listener.onStatusChanged(0, context.getString(R.string.initialising));
        init();
        listener.onStatusChanged(5, context.getString(R.string.workouts));
        saveWorkoutsToContainer();
        listener.onStatusChanged(20, context.getString(R.string.locationData));
        saveSamplesToContainer();
        listener.onStatusChanged(50, context.getString(R.string.intervalSets));
        saveIntervalsToContainer();
        listener.onStatusChanged(55, context.getString(R.string.customWorkoutTypesTitle));
        saveWorkoutTypes();
        listener.onStatusChanged(60, context.getString(R.string.converting));
        writeContainerToOutputFile();
        listener.onStatusChanged(100, context.getString(R.string.finished));
    }

    private void init(){
        database= Instance.getInstance(context).db;
        newContainer();
    }

    private void newContainer(){
        dataContainer= new CyclosAppDataContainer();
        dataContainer.setVersion(VERSION);
    }

    private void saveWorkoutsToContainer(){
        dataContainer.getWorkouts().addAll(Arrays.asList(database.workoutDao().getWorkouts()));
    }

    private void saveSamplesToContainer(){
        dataContainer.getSamples().addAll(Arrays.asList(database.workoutDao().getSamples()));
    }

    private void saveIntervalsToContainer() {
        for (IntervalSet set : database.intervalDao().getAllSets()) {
            saveIntervalToContainer(set);
        }
    }

    private void saveIntervalToContainer(IntervalSet set) {
        List<Interval> intervals = Arrays.asList(database.intervalDao().getAllIntervalsOfSet(set.id));
        dataContainer.getIntervalSets().add(new IntervalSetContainer(set, intervals));
    }

    private void saveWorkoutTypes() {
        dataContainer.getWorkoutTypes().addAll(Arrays.asList(database.workoutTypeDao().findAll()));
    }

    private void writeContainerToOutputFile() throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.writeValue(output, dataContainer);
    }

    public interface ExportStatusListener {
        void onStatusChanged(int progress, String action);
    }

}
