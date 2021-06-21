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

package ve.cyclos.fitness.aggregation;

import java.util.Date;

public class AggregatedInformationDataPoint {

    private final Date date;
    private double sum;
    private int count;

    public AggregatedInformationDataPoint(Date date, double sum, int count) {
        this.date = date;
        this.sum = sum;
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public double getSum() {
        return sum;
    }

    public double getAvg() {
        if (count > 0) {
            return sum / count;
        } else {
            return 0;
        }
    }

    public int getCount() {
        return count;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
