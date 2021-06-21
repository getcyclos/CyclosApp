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

package ve.cyclos.fitness.ui.workout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ve.cyclos.fitness.BuildConfig;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.util.DataManager;

public class ShareWorkoutActivity extends WorkoutActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBeforeContent();

        setContentView(R.layout.activity_share_workout);

        initRoot();

        initContents();

        initAfterContent();

        fullScreenItems = true;
        addMap();

        mapView.setClickable(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_workout_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionShareWorkout) {
            shareWorkoutActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initRoot() {
        root = findViewById(R.id.showWorkoutMapParent);
    }

    private void initContents(){
        TextView workoutType = this.findViewById(R.id.workoutTypeTitle);
        workoutType.setText(workout.getWorkoutType(this).title);

        TextView workoutDistance = this.findViewById(R.id.workoutDistance);
        String distance=distanceUnitUtils.getDistance(workout.length);
        Spannable distanceSpan = new SpannableString(distance);
        distanceSpan.setSpan(new RelativeSizeSpan(0.8f), distance.lastIndexOf(" "), distance.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        workoutDistance.setText(distanceSpan);

        TextView workoutPace     = this.findViewById(R.id.workoutPace);
        String pace=distanceUnitUtils.getPace(workout.avgPace);
        Spannable paceSpan = new SpannableString(pace);
        paceSpan.setSpan(new RelativeSizeSpan(0.8f), pace.lastIndexOf(" "), pace.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        workoutPace.setText(paceSpan);

        TextView workoutTime     = this.findViewById(R.id.workoutTime);
        workoutTime.setText(distanceUnitUtils.getHourMinuteSecondTime(workout.duration));
    }

    private void shareWorkoutActivity() {
        Bitmap bitmap = getBitmapFromView(findViewById(R.id.shareWorkout));

        try {
            String ts = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            File parent = new File(DataManager.getSharedDirectory(this));
            File file = new File(parent, "cyclos-workout_" + ts + ".png");
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IOException("Cannot write");
            }
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();


            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri imgURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
                intent.setDataAndType(imgURI, "image/png");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, imgURI);
            } else {
                intent.setDataAndType(Uri.fromFile(file), "image/png");
            }
            startActivity(Intent.createChooser(intent,null));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }
}
