package com.example.inlamning1_shake;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, lightSensor;

    private RelativeLayout rootLayout;
    private ImageView arrowImage;
    private ToggleButton toggleGyro;
    private SeekBar sensitivitySeekBar;
    private TextView gyroStatusText, gyroData, sensitivityText, accelData, statusText;

    private boolean useGyro = false;
    private float lastDegree = 0f;
    private float sensitivity = 10f;

    private float lastX = 0f, lastY = 0f, lastZ = 0f;
    private boolean firstAccelRead = true;
    private Toast currentToast;
    private long lastToastTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeSensors();
        setupToggleGyro();
        setupSensitivitySeekBar();
    }

    // Initializes the UI components (views) and sets up the layout for the app
    private void initializeViews() {
        rootLayout = findViewById(R.id.rootLayout);
        arrowImage = findViewById(R.id.arrowImage);
        toggleGyro = findViewById(R.id.toggleGyro);
        sensitivitySeekBar = findViewById(R.id.sensitivitySeekBar);
        gyroStatusText = findViewById(R.id.gyroStatusText);
        sensitivityText = findViewById(R.id.sensitivityText);
        accelData = findViewById(R.id.accelData);
        gyroData = findViewById(R.id.gyroData);
        statusText = findViewById(R.id.statusText);
    }

    // Initializes the sensors (accelerometer, gyroscope, and light sensor)
    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    // Sets up the toggle button for the gyroscope activation
    private void setupToggleGyro() {
        toggleGyro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            useGyro = isChecked;
            updateGyroStatus();
            if (!useGyro) {
                stopArrowAnimation();
            }
        });
    }

    // Updates the gyroscope status text based on whether its enabled or not
    private void updateGyroStatus() {
        gyroStatusText.setText(useGyro ? "ACTIVATED - arrow spins" : "DISABLED - arrow is still");
        gyroData.setVisibility(useGyro ? View.VISIBLE : View.GONE);
    }

    // Stops the rotation animation on the arrow image
    private void stopArrowAnimation() {
        arrowImage.clearAnimation();
    }

    // Sets up the sensitivity seek bar to adjust the movement sensitivity
    private void setupSensitivitySeekBar() {
        sensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensitivity = progress;
                sensitivityText.setText("Sensitivity: " + sensitivity);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensors();
    }

    // Registers the sensors to start receiving updates from them
    private void registerSensors() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensors();
        clearToast();
        resetValues();
        stopArrowAnimation();
    }

    // Unregisters the sensors to stop receiving updates when the activity is paused
    private void unregisterSensors() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    // Clears any active toast message
    private void clearToast() {
        if (currentToast != null) {
            currentToast.cancel();
        }
    }

    // Resets all sensor values and flags
    private void resetValues() {
        lastX = lastY = lastZ = 0f;
        lastDegree = 0f;
        firstAccelRead = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterSensors();
        resetValues();
        stopArrowAnimation();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            handleAccelerometerData(event, currentTime);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            handleGyroscopeData(event, currentTime);
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            handleLightSensorData(event);
        }
    }

    // Handles data from the accelerometer and updates the UI accordingly
    private void handleAccelerometerData(SensorEvent event, long currentTime) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (firstAccelRead) {
            lastX = x; lastY = y; lastZ = z;
            firstAccelRead = false;
            return;
        }

        float deltaX = Math.abs(x - lastX);
        float deltaY = Math.abs(y - lastY);
        float deltaZ = Math.abs(z - lastZ);

        lastX = x; lastY = y; lastZ = z;

        float movementIntensity = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        updateAccelDataText(x, y, z, movementIntensity);
        updateBackgroundAndStatus(y);
        showToastIfMovementDetected(movementIntensity, currentTime);
    }

    // Updates the accelerometer data display on the UI
    private void updateAccelDataText(float x, float y, float z, float movementIntensity) {
        accelData.setText(String.format(
                "Accelerometer\nX: %.2f\nY: %.2f\nZ: %.2f\n\n" +
                        "Movement intensity (change): %.2f\n\n" +
                        "Explanation:\n" +
                        "- X: movement LEFT/RIGHT\n" +
                        "- Y: movement UP/DOWN\n" +
                        "- Z: movement FORWARD/BACKWARD (in/out of room)",
                x, y, z, movementIntensity
        ));
    }

    // Updates the background color and status text based on the phone's tilt
    private void updateBackgroundAndStatus(float y) {
        if (Math.abs(y - 9.81) < 1.0) {
            rootLayout.setBackgroundColor(Color.parseColor("#90EE90"));
            statusText.setText("Phone is ALMOST STRAIGHT (Y close to 9.81)");
        }
        else if (y < 0) {
            rootLayout.setBackgroundColor(Color.parseColor("#FFA07A"));
            statusText.setText("Phone is ALMOST BACKWARDS (negative Y)");
        }
        else if (y > 0) {
            rootLayout.setBackgroundColor(Color.parseColor("#ADD8E6"));
            statusText.setText("Phone is ALMOST PLAIN (positive Y)");
        }
    }

    // Displays a toast message if the movement intensity exceeds the threshold
    private void showToastIfMovementDetected(float movementIntensity, long currentTime) {
        if (movementIntensity > sensitivity && (currentTime - lastToastTime) > 1000) {
            currentToast = Toast.makeText(this, "Strong movement detected!", Toast.LENGTH_SHORT);
            currentToast.show();
            Log.d("Sensor", "Movement intensity over threshold: " + movementIntensity);
            lastToastTime = currentTime;
        }
    }

    // Handles gyroscope data, rotating the arrow image based on the phones rotation
    private void handleGyroscopeData(SensorEvent event, long currentTime) {
        if (!useGyro) return;

        float rotationZ = event.values[2];

        updateGyroDataText(rotationZ);
        rotateArrow(rotationZ);
        showToastIfRotationDetected(rotationZ, currentTime);
    }

    // Updates the gyroscope data display on the UI
    private void updateGyroDataText(float rotationZ) {
        gyroData.setText(String.format(
                "Gyroscope\nRotation Z (rad/s): %.3f\n\n" +
                        "Explanation:\n" +
                        "- Rotation Z: rotates the phone around its center (screen up)\n" +
                        "- Rotation X: tilts forward/backward over short side (nodding)\n" +
                        "- Rotation Y: tilts left/right over long side (shaking head)",
                rotationZ
        ));
    }

    // Rotates the arrow image based on the gyroscopes rotation
    private void rotateArrow(float rotationZ) {
        float degrees = (float) Math.toDegrees(rotationZ);
        RotateAnimation rotate = new RotateAnimation(
                lastDegree,
                lastDegree + degrees,
                arrowImage.getWidth() / 2,
                arrowImage.getHeight() / 2
        );
        rotate.setDuration(150);
        rotate.setFillAfter(true);
        arrowImage.startAnimation(rotate);
        lastDegree += degrees;
    }

    // Displays a toast message if a rapid rotation is detected
    private void showToastIfRotationDetected(float rotationZ, long currentTime) {
        if (Math.abs(rotationZ) > sensitivity / 10 && (currentTime - lastToastTime) > 1000) {
            currentToast = Toast.makeText(this, "Rapid rotation detected!", Toast.LENGTH_SHORT);
            currentToast.show();
            Log.d("Sensor", "Rotation over threshold: " + rotationZ);
            lastToastTime = currentTime;
        }
    }

    // Handles light sensor data and adjusts the UI brightness
    private void handleLightSensorData(SensorEvent event) {
        float lux = event.values[0];
        float alpha = lux < 10 ? 0.3f : 1f;
        rootLayout.setAlpha(alpha);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}