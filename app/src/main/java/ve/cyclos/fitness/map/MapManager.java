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

package ve.cyclos.fitness.map;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.StreamRenderTheme;
import org.mapsforge.map.rendertheme.XmlRenderTheme;

import java.io.FileInputStream;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.map.tilesource.CyclosAppTileSource;
import ve.cyclos.fitness.map.tilesource.HumanitarianTileSource;
import ve.cyclos.fitness.map.tilesource.MapnikTileSource;

public class MapManager {

    public static void initMapProvider(Activity activity) {
        // This sets the device scale factor so the map is displayed accordingly
        AndroidGraphicFactory.createInstance(activity.getApplication());
        DisplayModel.setDefaultUserScaleFactor(0.85f);
    }

    public static MapView setupMap(Activity activity) {
        MapView mapView = new MapView(activity);
        initMapProvider(activity);
        String chosenTileLayer = Instance.getInstance(mapView.getContext()).userPreferences.getMapStyle();
        boolean isOffline = chosenTileLayer.startsWith("offline");
        mapView.setBuiltInZoomControls(false);
        mapView.setZoomLevel((byte) 18);
        new Thread(() -> {
            TileCache tileCache = AndroidUtil.createTileCache(mapView.getContext(), chosenTileLayer,
                    mapView.getModel().displayModel.getTileSize(), 1f,
                    mapView.getModel().frameBufferModel.getOverdrawFactor(), true);
            if (isOffline) {
                setupOfflineMap(mapView, tileCache);
            } else {
                setupOnlineMap(mapView, tileCache, chosenTileLayer);
            }
            mapView.getLayerManager().redrawLayers();
        }).start();
        return mapView;
    }

    private static void setupOnlineMap(MapView mapView, TileCache tileCache, String chosenTileLayer) {
        CyclosAppTileSource tileSource;
        switch (chosenTileLayer) {
            case "osm.humanitarian":
                tileSource = HumanitarianTileSource.INSTANCE;
                break;
            case "osm.mapnik":
            default:
                tileSource = MapnikTileSource.INSTANCE;
                break;
        }
        tileSource.setUserAgent("mapsforge-android");
        TileDownloadLayer downloadLayer = new TileDownloadLayer(tileCache, mapView.getModel().mapViewPosition, tileSource,
                                                                AndroidGraphicFactory.INSTANCE);
        mapView.getLayerManager().getLayers().add(0, downloadLayer);
        mapView.setZoomLevelMin(tileSource.getZoomLevelMin());
        mapView.setZoomLevelMax(tileSource.getZoomLevelMax());
    }

    public static void setupOfflineMap(MapView mapView, TileCache tileCache) {
        Context context = mapView.getContext();
        MultiMapDataStore multiMapDataStore = new MultiMapDataStore(MultiMapDataStore.DataPolicy.RETURN_ALL);
        XmlRenderTheme theme = null;

        String directoryPath = Instance.getInstance(mapView.getContext()).userPreferences.getOfflineMapFileName();
        if (directoryPath == null) {
            return;
        }
        Uri mapDirectoryUri = Uri.parse(directoryPath);

        DocumentFile documentFile = DocumentFile.fromTreeUri(context, mapDirectoryUri);
        DocumentFile[] files = documentFile.listFiles();
        for (DocumentFile file : files) {
            // Go through all files in the map directory
            if (file.isFile() && file.canRead() && (file.getName().endsWith(".map") || file.getName().endsWith(".xml"))) {
                try {
                    Uri fileUri = file.getUri();
                    if (file.getName().endsWith(".map")) {
                        // For map files: load as MapFile and add to data store
                        FileInputStream inputStream = (FileInputStream) context.getContentResolver().openInputStream(fileUri);
                        MapFile mapFile = new MapFile(inputStream, 0, null);
                        multiMapDataStore.addMapDataStore(mapFile, true, true);
                    } else {
                        // For the first xml file: load as XmlRenderTheme
                        if (theme == null) {
                            theme = new StreamRenderTheme(fileUri.getPath(), context.getContentResolver().openInputStream(fileUri));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (theme == null) {
            theme = InternalRenderTheme.DEFAULT;
        }
        TileRendererLayer renderLayer = new TileRendererLayer(tileCache, multiMapDataStore, mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        renderLayer.setXmlRenderTheme(theme);
        mapView.getLayerManager().getLayers().add(0, renderLayer);
    }
}
