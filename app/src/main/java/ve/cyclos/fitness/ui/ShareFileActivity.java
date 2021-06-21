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

package ve.cyclos.fitness.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ve.cyclos.fitness.R;

public class ShareFileActivity extends CyclosAppActivity {

    public static String EXTRA_FILE_URI = "file_uri";

    private static final int REQUEST_CODE_TARGET_DIRECTORY = 1;

    private Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.shareFile);

        file = Uri.parse(getIntent().getStringExtra(EXTRA_FILE_URI));

        showDialog();
    }

    private void showDialog() {
        String[] options = {getString(R.string.share), getString(R.string.save)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                shareFile();
            } else {
                saveFile();
            }
        });
        builder.setOnCancelListener(dialog -> finish());
        builder.show();
    }

    private void saveFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");
        intent.putExtra(Intent.EXTRA_TITLE, file.getLastPathSegment());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Show Download-Folder as default
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY));
        }

        startActivityForResult(intent, REQUEST_CODE_TARGET_DIRECTORY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQUEST_CODE_TARGET_DIRECTORY &&
                resultCode == Activity.RESULT_OK &&
                resultData != null) {
            Uri target = resultData.getData();
            copyFileSafe(target);
        } else {
            finish();
        }
    }

    private void copyFileSafe(Uri target) {
        try {
            copyFile(target);
            Toast.makeText(this, R.string.savedSuccessfully, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.savingFailed, Toast.LENGTH_LONG).show();
        } finally {
            finish();
        }
    }

    private void copyFile(Uri target) throws IOException {
        InputStream input = getContentResolver().openInputStream(file);
        if (input == null) {
            throw new IOException("Source file not found");
        }
        OutputStream output = getContentResolver().openOutputStream(target);
        if (output == null) {
            throw new IOException("Target file not found");
        }
        IOUtils.copy(input, output);
    }

    private void shareFile() {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setDataAndType(file, getContentResolver().getType(file));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, file);
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intentShareFile, getString(R.string.shareFile)));

        Log.d("Export", file.toString());
        Log.d("Export", getContentResolver().getType(file));
        try {
            Log.d("Export", new BufferedInputStream(getContentResolver().openInputStream(file)).toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finish();
    }
}