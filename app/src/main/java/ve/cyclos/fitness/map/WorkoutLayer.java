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

package ve.cyclos.fitness.map;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import ve.cyclos.fitness.data.WorkoutSample;

public class WorkoutLayer extends Polyline {

    private static Paint getDEFAULT_PAINT_STROKE(int color) {
        Paint paint= AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setStyle(Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(14f);
        return paint;
    }

    private final List<WorkoutSample> samples;

    public WorkoutLayer(List<WorkoutSample> samples, int color) {
        this(getDEFAULT_PAINT_STROKE(color), samples);
    }

    private WorkoutLayer(Paint paintStroke, List<WorkoutSample> samples) {
        super(paintStroke, AndroidGraphicFactory.INSTANCE);
        this.samples = samples;
        init();
    }

    private void init(){
        List<LatLong> points = new ArrayList<LatLong>();
        for(WorkoutSample sample : samples){
            points.add(sample.toLatLong());
        }
        addPoints(points);
    }
}
