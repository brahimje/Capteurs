package com.example.capteurs;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView accelerationText, xAxisText, yAxisText, zAxisText, magneticFieldText, orientationText;
    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private float[] accelerometerValues = new float[3];
    private float[] magnetometerValues = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerationText = findViewById(R.id.accelerationText);
        xAxisText = findViewById(R.id.xAxisText);
        yAxisText = findViewById(R.id.yAxisText);
        zAxisText = findViewById(R.id.zAxisText);
        magneticFieldText = findViewById(R.id.magneticFieldText);
        orientationText = findViewById(R.id.orientationText);

        // Initialize the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Get the accelerometer sensor
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the sensor listeners when the activity is resumed
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the sensor listeners when the activity is paused
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values.clone();
                updateAccelerometerUI();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerValues = event.values.clone();
                updateMagnetometerUI();
                break;
        }

        // Update the orientation UI when both accelerometer and magnetometer data are available
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER || event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            updateOrientationUI();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this example
    }

    private void updateAccelerometerUI() {
        float x = accelerometerValues[0];
        float y = accelerometerValues[1];
        float z = accelerometerValues[2];

        // Update the UI with accelerometer values
        accelerationText.setText("Acceleration: " + String.valueOf(Math.sqrt(x * x + y * y + z * z)));
        xAxisText.setText("X-Axis: " + String.valueOf(x));
        yAxisText.setText("Y-Axis: " + String.valueOf(y));
        zAxisText.setText("Z-Axis: " + String.valueOf(z));
    }

    private void updateMagnetometerUI() {
        float x = magnetometerValues[0];
        float y = magnetometerValues[1];
        float z = magnetometerValues[2];

        // Update the UI with magnetometer values
        magneticFieldText.setText("Magnetic Field: X=" + String.valueOf(x) + ", Y=" + String.valueOf(y) + ", Z=" + String.valueOf(z));
    }

    private void updateOrientationUI() {
        float[] rotationMatrix = new float[9];
        float[] orientationValues = new float[3];

        // Get the rotation matrix from the accelerometer and magnetometer data
        boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues);

        if (success) {
            // Get the orientation values from the rotation matrix
            SensorManager.getOrientation(rotationMatrix, orientationValues);

            // Convert the orientation values to degrees
            float azimuthDegrees = (float) Math.toDegrees(orientationValues[0]);
            float pitchDegrees = (float) Math.toDegrees(orientationValues[1]);
            float rollDegrees = (float) Math.toDegrees(orientationValues[2]);

            // Update the UI with orientation values
            orientationText.setText("Orientation: Azimuth=" + azimuthDegrees + ", Pitch=" + pitchDegrees + ", Roll=" + rollDegrees);
        }
    }
}
