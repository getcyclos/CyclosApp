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

package ve.cyclos.fitness.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.WorkoutType;
import ve.cyclos.fitness.util.Icon;

public class WorkoutTypeAdapter extends RecyclerView.Adapter<WorkoutTypeAdapter.WorkoutTypeHolder> {


    public static class WorkoutTypeHolder extends RecyclerView.ViewHolder {

        final View root;
        final TextView nameText;
        final ImageView iconView;

        WorkoutTypeHolder(@NonNull View itemView) {
            super(itemView);
            this.root = itemView;
            nameText = itemView.findViewById(R.id.workoutTypeName);
            iconView = itemView.findViewById(R.id.workoutTypeImage);
        }
    }

    private final List<WorkoutType> types;
    private final WorkoutTypeAdapterListener listener;

    public WorkoutTypeAdapter(List<WorkoutType> types, WorkoutTypeAdapterListener listener) {
        this.types = types;
        this.listener = listener;
    }

    @Override
    public WorkoutTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_workout_type, parent, false);
        return new WorkoutTypeHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(WorkoutTypeHolder holder, final int position) {
        WorkoutType type = types.get(position);
        holder.iconView.setImageResource(Icon.getIcon(type.icon));
        holder.iconView.setColorFilter(type.color);
        holder.nameText.setText(type.title);
        holder.root.setOnClickListener(v -> listener.onItemSelect(position, type));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return types.size();
    }

    public interface WorkoutTypeAdapterListener {
        void onItemSelect(int pos, WorkoutType type);
    }


}
