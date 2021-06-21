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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.IntervalSet;

public class IntervalSetAdapter extends RecyclerView.Adapter<IntervalSetAdapter.IntervalSetViewHolder> {

    public static class IntervalSetViewHolder extends RecyclerView.ViewHolder {

        final View root;
        final TextView nameText;

        IntervalSetViewHolder(@NonNull View itemView) {
            super(itemView);
            this.root= itemView;
            nameText = itemView.findViewById(R.id.intervalSetName);
        }
    }

    private final IntervalSet[] intervalSets;
    private final IntervalSetAdapterListener listener;

    public IntervalSetAdapter(IntervalSet[] intervalSets, IntervalSetAdapterListener listener) {
        this.intervalSets = intervalSets;
        this.listener = listener;
    }

    @Override
    public IntervalSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_interval_set, parent, false);
        return new IntervalSetViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(IntervalSetViewHolder holder, final int position) {
        IntervalSet intervalSet = intervalSets[position];
        holder.nameText.setText(intervalSet.name);
        holder.root.setOnClickListener(view -> listener.onItemSelect(position, intervalSet));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return intervalSets.length;
    }

    public interface IntervalSetAdapterListener {
        void onItemSelect(int pos, IntervalSet set);

        void onItemDelete(int pos, IntervalSet set);
    }

}
