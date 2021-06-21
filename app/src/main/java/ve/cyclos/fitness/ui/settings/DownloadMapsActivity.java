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

package ve.cyclos.fitness.ui.settings;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.documentfile.provider.DocumentFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.ui.CyclosAppActivity;

/**
 * Parts of this file were taken from the OpenTracks OSMDashboard
 * -> https://github.com/OpenTracksApp/OSMDashboard/blob/main/src/main/java/de/storchp/opentracks/osmplugin/DownloadMapsActivity.java
 * <p>
 * it is licensed under the Apache License 2.0
 */
public class DownloadMapsActivity extends CyclosAppActivity {

    private String mapsUrl;

    private DownloadTask downloadTask;

    private WebView webView;

    private TextView infoView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_maps);
        setupActionBar();
        setTitle(R.string.downloadMapsTitle);

        this.mapsUrl = getString(R.string.urlDownloadMaps);

        this.webView = findViewById(R.id.downloadMapsWebView);
        this.progressBar = findViewById(R.id.downloadMapsProgress);
        this.infoView = findViewById(R.id.downloadMapsInfo);

        setupWebClient();
    }

    private void setupWebClient() {
        WebViewClient webClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                Log.d("DownloadMaps", "URL: " + url);
                if (!url.startsWith(mapsUrl)) {
                    return true; // don't load URLs outside the base URL
                }
                final Uri uri = Uri.parse(url);
                final String lastPathSegment = uri.getLastPathSegment();
                if (lastPathSegment != null && lastPathSegment.endsWith(".map")) {
                    if (isDownloadInProgress()) {
                        Toast.makeText(DownloadMapsActivity.this.getApplicationContext(), R.string.alreadyDownloading, Toast.LENGTH_LONG).show();
                        return true;
                    }
                    new AlertDialog.Builder(DownloadMapsActivity.this)
                            .setTitle(R.string.actionDownloadMap)
                            .setMessage(getString(R.string.downloadMapConfirmation, lastPathSegment))
                            .setPositiveButton(R.string.okay, (dialog, which) -> {
                                startMapDownload(uri);
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .create().show();
                    return true;
                }
                return false;
            }

        };
        webView.setWebViewClient(webClient);
        webView.loadUrl(mapsUrl);
    }

    private boolean isDownloadInProgress() {
        return downloadTask != null;
    }

    private void startMapDownload(Uri downloadMapUri) {
        final Uri mapDirectoryUri = Uri.parse(Instance.getInstance(this).userPreferences.getOfflineMapFileName());
        final DocumentFile mapDirectoryFile = DocumentFile.fromTreeUri(this, mapDirectoryUri);
        final String mapName = downloadMapUri.getLastPathSegment();

        final DocumentFile file = mapDirectoryFile.findFile(mapName);
        if (file != null) {
            new AlertDialog.Builder(DownloadMapsActivity.this)
                    .setTitle(R.string.app_name)
                    .setMessage(getString(R.string.overwriteMapConfirmation, mapName))
                    .setPositiveButton(R.string.okay, (dialog, which) -> {
                        file.delete();
                        startMapDownload(downloadMapUri);
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create().show();
            return;
        }

        setProgress(0);

        downloadTask = new DownloadTask(this, downloadMapUri, mapDirectoryFile.createFile("application/binary", mapName).getUri());
        downloadTask.start();
    }

    private void downloadEnded(final boolean success, final boolean canceled) {
        setProgress(100);
        final Uri targetMapUri = downloadTask.targetMapUri;
        downloadTask = null;
        if (canceled) {
            final DocumentFile documentFile = DocumentFile.fromSingleUri(this, targetMapUri);
            if (documentFile != null) {
                documentFile.delete();
            }
            Toast.makeText(this, R.string.downloadCanceled, Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }

        @StringRes int message = success ? R.string.downloadSuccess : R.string.downloadFailed;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        infoView.setText(message);
    }

    private static class DownloadTask extends Thread {
        private final WeakReference<DownloadMapsActivity> ref;
        private final Uri downloadMapUri;
        private final Uri targetMapUri;
        private long contentLength = -1;
        private boolean success = false;
        private boolean canceled = false;

        public DownloadTask(final DownloadMapsActivity activity, final Uri downloadMapUri, final Uri targetMapUri) {
            ref = new WeakReference<>(activity);
            this.downloadMapUri = downloadMapUri;
            this.targetMapUri = targetMapUri;
        }

        protected void publishProgress(final long progress) {
            final DownloadMapsActivity activity = ref.get();
            if (activity != null) {
                activity.runOnUiThread(() -> activity.updateProgress((int) (progress * 100 / contentLength)));
            }
        }

        protected void end() {
            final DownloadMapsActivity activity = ref.get();
            if (activity != null) {
                activity.runOnUiThread(() -> activity.downloadEnded(success, canceled));
            }
        }

        @Override
        public void run() {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            long bytesWritten = 0;
            try {
                final URL sUrl = new URL(downloadMapUri.toString());
                connection = (HttpURLConnection) sUrl.openConnection();
                connection.connect();
                contentLength = connection.getContentLength();

                input = connection.getInputStream();
                output = ref.get().getContentResolver().openOutputStream(targetMapUri);

                final byte[] data = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    if (canceled) {
                        input.close();
                        output.close();
                        end();
                        return;
                    }
                    output.write(data, 0, count);
                    bytesWritten += count;
                    publishProgress(bytesWritten);
                }
                success = true;
            } catch (final Exception e) {
                Log.e("DownloadMaps", "Download failed", e);
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                } catch (final IOException ignored) {
                }

                if (connection != null) {
                    connection.disconnect();
                }
            }
            end();
        }

        public void cancelDownload() {
            canceled = true;
        }
    }

    private void updateProgress(int percent) {
        infoView.setText(getString(R.string.downloading) + "... " + percent + "%");
        if (percent <= 0) {
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setIndeterminate(false);
            progressBar.setProgress(percent);
        }
    }

    @Override
    public void onBackPressed() {
        if (isDownloadInProgress()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.actionCancelDownload)
                    .setMessage(getString(R.string.cancelDownloadConfirmation))
                    .setPositiveButton(R.string.okay, (dialog, which) -> downloadTask.cancelDownload())
                    .setNegativeButton(R.string.cancel, null)
                    .create().show();
        } else {
            super.onBackPressed();
        }
    }

}