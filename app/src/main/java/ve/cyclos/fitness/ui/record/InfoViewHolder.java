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

package ve.cyclos.fitness.ui.record;

import android.view.View;
import android.widget.TextView;

class InfoViewHolder {
    private final int slot;
    private final InfoViewClickListener listener;
    private final TextView titleView;
    private final TextView valueView;

    InfoViewHolder(int slot, InfoViewClickListener listener, TextView titleView, TextView valueView) {
        this.slot = slot;
        this.listener = listener;
        this.titleView = titleView;
        this.valueView = valueView;
        setOnClickListeners();
    }

    void setText(String title, String value) {
        this.titleView.setText(title);
        this.valueView.setText(value);
    }

    private void setOnClickListeners() {
        titleView.setOnClickListener(getOnClickListener());
        valueView.setOnClickListener(getOnClickListener());
    }

    private View.OnClickListener getOnClickListener() {
        return v -> listener.onInfoViewClick(slot);
    }

    public interface InfoViewClickListener {
        void onInfoViewClick(int slot);
    }
}
