package fr.unice.reneviergonin.fisheye01;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.SeekBar;

import fr.unice.reneviergonin.fisheye01.deform.AbstractFormula;
import fr.unice.reneviergonin.fisheye01.deform.BasicDeform;
import fr.unice.reneviergonin.fisheye01.images.DeformableView;
import fr.unice.reneviergonin.fisheye01.sensors.AccelerometerManager;

public class FishEyeView extends Activity implements AccelerometerManager.AccelerometerListener{

    private static final AbstractFormula[] formulas = {new BasicDeform()};
    private int currentFormula = 0;

    private DeformableView view;

    private boolean isPartiallyDeformed = true;
    private boolean isAccelerometerActivated = false;

    private AccelerometerManager accelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_eye_view);

        if (savedInstanceState != null)
            currentFormula = savedInstanceState.getInt("currentFormula", 0);

        // View
        view = (DeformableView) findViewById(R.id.deformed);
        view.setPartiallyDeformed(isPartiallyDeformed);
        view.setDeformation(formulas[currentFormula]);

        // Seekbars
        SeekBar r = (SeekBar) findViewById(R.id.seekR);
        r.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                formulas[0].setParams(null, new Double(seekBar.getProgress()), null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        SeekBar z = (SeekBar) findViewById(R.id.seekZ);
        z.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                formulas[0].setParams(new Double(seekBar.getProgress()), null, null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        SeekBar o = (SeekBar) findViewById(R.id.seekO);
        o.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                formulas[0].setParams(null, null, new Double(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        formulas[0].setParams(new Double(z.getProgress()), new Double(r.getProgress()), new Double(o.getProgress()));

        // Accelerometre
        accelManager = new AccelerometerManager(this);
        if(!accelManager.isSupported()) {
            //TODO remove accel button
        }else{
            accelManager.startListening(this);
        }
    }

    protected void onResume() {
        super.onResume();
        if(isAccelerometerActivated)
            accelManager.resumeListening();
    }

    protected void onPause() {
        super.onPause();
        accelManager.pauseListening();
    }

    @Override
    protected void onStop () {
        super.onStop();
        accelManager.stopListening();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //TODO nettoyage
        ActionBar actionBar = getActionBar();
        int actionBarHeight = actionBar.getHeight();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;

        int x = (int) event.getRawX();
        int y = (int) event.getRawY() - actionBarHeight;

        this.view.setCenter(x, y);

        return super.onTouchEvent(event);
    }

    @Override
    public void onMove (float x, float y) {
        int previousCentreX = this.view.getCentreX();
        int previousCentreY = this.view.getCentreY();
        this.view.setCenter(previousCentreX + (int)x, previousCentreY + (int)y);
    }
}
