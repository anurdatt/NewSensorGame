package org.adgames.newsensorgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;

public class GameActivity extends AppCompatActivity implements SensorEventListener2 {

    public GameView gameView;
    private SensorManager sensorManager;

    //@RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        }

        Point size = new Point();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Display display = getWindowManager().getDefaultDisplay();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
                size.x = display.getWidth();
                size.y = display.getHeight();
            } else {
                display.getSize(size);

                if (!(size.x > 0 && size.y > 0)) {
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    display.getMetrics(displayMetrics);
                    size.x = displayMetrics.widthPixels;
                    size.y = displayMetrics.heightPixels;
                }
            }
        } else {
            final WindowMetrics metrics = getWindowManager().getCurrentWindowMetrics();
            // Gets all excluding insets
            final WindowInsets windowInsets = metrics.getWindowInsets();
            Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars()
                    | WindowInsets.Type.displayCutout());

            int insetsWidth = insets.right + insets.left;
            int insetsHeight = insets.top + insets.bottom;

            // Legacy size that Display#getSize reports
            final Rect bounds = metrics.getBounds();
            size.set(bounds.width() - insetsWidth,
                    bounds.height() - insetsHeight);
        }
        GameView gameView = new GameView(this, size, MediaPlayer.create(this, R.raw.ballholed), MediaPlayer.create(this, R.raw.holeclosing), getIntent().getStringExtra("EXTRA_PLAYER_NAME"));
        this.gameView = gameView;
        setContentView((View) gameView);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(1), 1);
    }

    @Override
    protected void onStop() {
        this.sensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to exit ?");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                GameActivity.this.gameView.updateScores();
                GameActivity.this.finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GameActivity.this.getWindow().getDecorView().setSystemUiVisibility(4);
            }
        });
        alertDialogBuilder.create().show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == 1) {
            this.gameView.updateBalls(event.values[0], -event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }
}