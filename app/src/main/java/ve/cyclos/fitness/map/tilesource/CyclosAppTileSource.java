/*
 * Copyright (c) 2021 Gabriel Estrada <dev@getcyclos.com>
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

package ve.cyclos.fitness.map.tilesource;

import org.mapsforge.map.layer.download.tilesource.AbstractTileSource;

public abstract class CyclosAppTileSource extends AbstractTileSource {

    CyclosAppTileSource(String[] hostNames, int port) {
        super(hostNames, port);
    }

    @Override
    public boolean hasAlpha() {
        return false;
    }

    public abstract String getName();
}
