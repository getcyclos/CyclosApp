/*
 * Copyright (c) 2021 Gabriel Estrada <dev@getcyclos.com>
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

import ve.cyclos.fitness.R;

public class ShowWorkoutMapActivity extends WorkoutActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBeforeContent();

        setContentView(R.layout.activity_show_workout_map);
        initRoot();

        initAfterContent();

        fullScreenItems = true;
        showPauses = true;
        addMap();

        mapView.setClickable(true);
    }


    @Override
    protected void initRoot() {
        root = findViewById(R.id.showWorkoutMapParent);
    }
}
