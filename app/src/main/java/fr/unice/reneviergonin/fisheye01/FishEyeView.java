package fr.unice.reneviergonin.fisheye01;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import fr.unice.reneviergonin.fisheye01.deform.AbstractFormula;
import fr.unice.reneviergonin.fisheye01.deform.BasicDeform;
import fr.unice.reneviergonin.fisheye01.images.DeformableView;
import fr.unice.reneviergonin.fisheye01.sensors.AccelerometerManager;

public class FishEyeView extends Activity implements AccelerometerManager.AccelerometerListener {

    private static final AbstractFormula[] formulas = {new BasicDeform()};
    private int currentFormula = 0;
    private int offset = 150;

    private DeformableView view;

    private boolean isPartiallyDeformed = true;
    private boolean touchOffset = false;

    private AccelerometerManager accelManager;

    private Button accelButton;
    private Button touchButton;
    private LinearLayout offsetLayout;
    private Switch offsetSwitch;
    private double topHeight;

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

        accelButton = (Button) findViewById(R.id.button_accel);
        touchButton = (Button) findViewById(R.id.button_touch);
        offsetLayout = (LinearLayout) findViewById(R.id.layout_decalage);
        offsetSwitch = (Switch) findViewById(R.id.switch_offset);
        offsetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("FISHEYE", "switch changed " + b);
                touchOffset = b;
                view.resetCenter(touchOffset?2:1);
            }
        });

        formulas[0].setParams(80., 300., 220.);

        int actionBarHeight = getActionBarHeight();
        int statusBarHeight = getStatusBarHeight();
        topHeight = statusBarHeight + actionBarHeight;

        // Accelerometre
        accelManager = new AccelerometerManager(this);
        if (!accelManager.isSupported()) {
            Log.w("FISHEYE", "PAS DACCEL");
            accelButton.setEnabled(false);
            accelButton.setVisibility(View.GONE);
        }
    }

    protected void onResume() {
        super.onResume();
        if (accelManager.isSupported())
            accelManager.resumeListening();
    }

    protected void onPause() {
        super.onPause();
        accelManager.pauseListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        accelManager.stopListening();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (accelManager.isListening())
            return super.onTouchEvent(event);

        int x = (int) event.getRawX();
        int y = (int) event.getRawY() - (int) topHeight;

        if (touchOffset) {
            Log.i("FISHEYE", "offset");
            x -= offset;
            y -= offset;
        }

        x = Math.max(x, (int) topHeight);
        y = Math.max(y, 0);
        this.view.setCenter(x, y);

        return true;
    }

    @Override
    public void onMove(float x, float y) {
        int previousCentreX = this.view.getCentreX();
        int previousCentreY = this.view.getCentreY();

        if (x != 0) {
            x *= 2;
        }
        if (y != 0) {
            y *= 2;
        }

        x = Math.max(previousCentreX - x, (int) topHeight);
        y = Math.max(previousCentreY + y, 0);

        this.view.setCenter((int) x, (int) y);
    }

    public void startAccelerometer(View view) {
        if (accelManager.isListening()) {
            accelManager.resetSensorData();
            this.view.resetCenter(0);
            return;
        }
        if (!accelManager.isSupported()) {
            accelButton.setVisibility(View.GONE);
            Toast.makeText(this, "Pas d'accéléromètre sur cet appareil", Toast.LENGTH_SHORT).show();
            return;
        }
        offsetLayout.setVisibility(View.INVISIBLE);
        accelButton.setBackgroundResource(R.drawable.selector_button_red);
        touchButton.setBackgroundResource(R.drawable.selector_button_dark);
        this.view.resetCenter(0);
        accelManager.startListening(this);
    }

    public void startTouch(View view) {
        if (!accelManager.isListening())
            return;
        if (accelManager.isSupported() && accelManager.isListening())
            accelManager.stopListening();
        accelButton.setBackgroundResource(R.drawable.selector_button_dark);
        touchButton.setBackgroundResource(R.drawable.selector_button_red);
        offsetLayout.setVisibility(View.VISIBLE);

        this.view.resetCenter(touchOffset?2:1);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getActionBarHeight() {
        int result;
        final TypedArray styledAttributes = this.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        result = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return result;
    }
}
