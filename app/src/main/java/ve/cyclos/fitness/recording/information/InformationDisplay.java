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

package ve.cyclos.fitness.recording.information;

import android.content.Context;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.data.UserPreferences;
import ve.cyclos.fitness.recording.WorkoutRecorder;

public class InformationDisplay {

    private final UserPreferences preferences;
    private final InformationManager manager;

    public InformationDisplay(Context context) {
        this.preferences = Instance.getInstance(context).userPreferences;
        this.manager = new InformationManager(context);
    }

    public DisplaySlot getDisplaySlot(WorkoutRecorder recorder, int slot) {
        String informationId = preferences.getIdOfDisplayedInformation(slot);
        RecordingInformation information = manager.getInformationById(informationId);
        if (information != null) {
            return new DisplaySlot(slot, information.getTitle(), information.getDisplayedText(recorder));
        } else {
            return new DisplaySlot(slot, "", "");
        }
    }

    public static class DisplaySlot {

        private final int slot;
        private final String title;
        private final String value;

        public DisplaySlot(int slot, String title, String value) {
            this.slot = slot;
            this.title = title;
            this.value = value;
        }

        public int getSlot() {
            return slot;
        }

        public String getTitle() {
            return title;
        }

        public String getValue() {
            return value;
        }
    }

}
