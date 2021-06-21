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
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.util.Icon;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {

    public static class IconViewHolder extends RecyclerView.ViewHolder {

        final ImageView iconView;

        IconViewHolder(@NonNull ImageView itemView) {
            super(itemView);
            this.iconView = itemView;
        }
    }

    private final Icon[] icons;
    private final IconAdapterListener listener;
    private final int tintColor;

    public IconAdapter(Icon[] icons, IconAdapterListener listener, int tintColor) {
        this.icons = icons;
        this.listener = listener;
        this.tintColor = tintColor;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ImageView imageView = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_icon, parent, false);
        return new IconViewHolder(imageView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(IconViewHolder holder, final int position) {
        Icon icon = icons[position];
        holder.iconView.setImageResource(icon.iconRes);
        holder.iconView.setColorFilter(tintColor);
        holder.iconView.setOnClickListener(v -> listener.onItemClick(position, icon));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return icons.length;
    }

    public interface IconAdapterListener {
        void onItemClick(int pos, Icon icon);
    }

}