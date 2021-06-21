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

import android.app.Activity;
import android.os.Handler;

public class ThreadSafeProgressDialogController extends ProgressDialogController {

    private final Handler handler = new Handler();

    public ThreadSafeProgressDialogController(Activity context, String title) {
        super(context, title);
    }

    @Override
    public void setIndeterminate(boolean indeterminate) {
        handler.post(() -> super.setIndeterminate(indeterminate));
    }

    @Override
    public void setProgress(int progress) {
        handler.post(() -> super.setProgress(progress));
    }

    @Override
    public void setProgress(int progress, String info) {
        handler.post(() -> super.setProgress(progress, info));
    }

    @Override
    public void show() {
        handler.post(super::show);
    }

    @Override
    public void cancel() {
        handler.post(super::cancel);
    }
}
