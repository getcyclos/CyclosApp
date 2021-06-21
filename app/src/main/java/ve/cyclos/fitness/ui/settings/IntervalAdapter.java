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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.Interval;

public class IntervalAdapter extends RecyclerView.Adapter<IntervalAdapter.IntervalViewHolder> {

    public static class IntervalViewHolder extends RecyclerView.ViewHolder {

        final View root;
        final TextView nameText;
        final TextView lengthText;
        final View deleteButton;

        IntervalViewHolder(@NonNull View itemView) {
            super(itemView);
            this.root = itemView;
            nameText = itemView.findViewById(R.id.intervalName);
            lengthText = itemView.findViewById(R.id.intervalLength);
            deleteButton = itemView.findViewById(R.id.intervalDelete);
        }
    }

    public final List<Interval> intervals;
    private final IntervalAdapterListener listener;

    public IntervalAdapter(List<Interval> intervals, IntervalAdapterListener listener) {
        this.intervals = intervals;
        this.listener = listener;
    }

    @Override
    public IntervalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_interval, parent, false);
        return new IntervalViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(IntervalViewHolder holder, final int position) {
        Interval interval = intervals.get(position);
        Context context = holder.root.getContext();

        final long minute = 1000L * 60;
        double minutes = (double) interval.delayMillis / minute;

        holder.nameText.setText(interval.name);
        holder.lengthText.setText(minutes + " " + context.getString(R.string.timeMinuteShort));
        holder.deleteButton.setOnClickListener(v -> listener.onItemDelete(intervals.indexOf(interval), interval));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return intervals.size();
    }

    public interface IntervalAdapterListener {
        void onItemDelete(int pos, Interval interval);
    }

}
