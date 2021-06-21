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

package ve.cyclos.fitness.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ve.cyclos.fitness.ui.CyclosAppActivity;
import ve.cyclos.fitness.ui.adapter.IconAdapter;
import ve.cyclos.fitness.util.Icon;

public class IconPickerDialog implements IconAdapter.IconAdapterListener {

    private final CyclosAppActivity context;
    private final IconSelectListener listener;
    private final Icon[] options;
    private Dialog dialog;

    public IconPickerDialog(CyclosAppActivity context, IconSelectListener listener) {
        this.context = context;
        this.listener = listener;
        this.options = Icon.values();
    }

    public void show() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        RecyclerView recyclerView = new RecyclerView(context);
        IconAdapter adapter = new IconAdapter(options, this, context.getThemePrimaryColor());
        recyclerView.setLayoutManager(new GridLayoutManager(context, 5));
        recyclerView.setAdapter(adapter);

        builderSingle.setView(recyclerView);
        dialog = builderSingle.create();
        dialog.show();
    }

    @Override
    public void onItemClick(int pos, Icon icon) {
        dialog.dismiss();
        listener.onSelectIcon(icon);
    }

    public interface IconSelectListener {
        void onSelectIcon(Icon icon);
    }

}
