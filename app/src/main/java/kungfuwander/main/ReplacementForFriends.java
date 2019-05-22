package kungfuwander.main;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

import im.delight.android.location.SimpleLocation;

public class ReplacementForFriends extends AppCompatActivity implements SensorEventListener {

    private String TAG = getClass().getName();
    private int currentSteps = 0;
    private TextView tvSteps;
    private TextView tvHikes;

    private Hiking hiking;
    private SimpleLocation simpleLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replacement_for_friends);

        tvSteps = findViewById(R.id.tvSteps);
        tvHikes = findViewById(R.id.tvHikings);

        Button btnStartHiking = findViewById(R.id.btnStartHiking);
        Button btnStopHiking = findViewById(R.id.btnStopHiking);
        Button btnTestChart = findViewById(R.id.btnTestChart);
        Button btnCompareFriends = findViewById(R.id.btnCompareFriends);
        Button btnNotification = findViewById(R.id.btnNotification);

        btnStartHiking.setOnClickListener(v -> startStepCounter());
        btnStopHiking.setOnClickListener(v -> stopStepCounter());
        btnTestChart.setOnClickListener(v -> openChart());
        btnCompareFriends.setOnClickListener(v -> compareFriends());
        btnNotification.setOnClickListener(v -> startServiceNotification());

        simpleLocation = setUpSimpleLocation();

        // if we can't access the location yet
        if (!simpleLocation.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        simpleLocation.setListener(() -> {
            // new location data has been received and can be accessed
            double latitude = simpleLocation.getLatitude();
            double longitude = simpleLocation.getLongitude();

            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            if (hiking == null) {
                Log.w(TAG, "No hiking init...");
            } else {
                hiking.addGeoPoint(geoPoint);
                tvHikes.setText("Added: " + geoPoint.toString() + ", size of hiking: " + hiking.getGeoPoints().size());
                Log.d(TAG, "Added: " + geoPoint.toString() + ", size of hiking: " + hiking.getGeoPoints().size());
            }
        });

    }

    private void startServiceNotification() {
        Intent intent = new Intent(this, ActivityJustForNotification.class);
        startActivity(intent);
    }

    private SimpleLocation setUpSimpleLocation() {
        Context context = this;
        boolean requireFineGranularity = true;
        boolean passiveMode = false;
        long updateIntervalInMilliseconds = 100;
        boolean requireNewLocation = true;

        return new SimpleLocation(context, requireFineGranularity, passiveMode, updateIntervalInMilliseconds, requireNewLocation);
    }

    @Override
    public void onResume() {
        super.onResume();

        // make the device update its location
        simpleLocation.beginUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();

        // stop location updates (saves battery)
        simpleLocation.endUpdates();
    }

    @Deprecated
    private void updateUserNameToJustin() {
        FireBaseHelper.updateLoggedInUserName("Justin");
    }

    private void compareFriends() {
        Intent intent = new Intent(this, FriendsList.class);
        startActivity(intent);
    }

    private void openChart() {
        Intent intent = new Intent(this, TestRecentHikings.class);
        startActivity(intent);
    }

    private void stopStepCounter() {
        // TODO: 16.05.2019 stop this
        // look if steps < 100
        // now just add hiking and stop it
        hiking.setSteps(currentSteps);
        hiking.setEnd(Timestamp.now());
        // TODO: 16.05.2019 fetch locations and time

        FireBaseHelper.addToLoggedInUser(hiking);
    }

    private void startStepCounter() {
        // TODO: 20.05.2019 warning if hiking is already started
        hiking = new Hiking();
        hiking.setStart(Timestamp.now());

        try {
            SensorManager sensorManager = (SensorManager) Objects.requireNonNull(this)
                    .getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            Objects.requireNonNull(sensorManager).registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_UI);
        } catch (NullPointerException ne) {
            currentSteps = 4711; // so there is something in database
            Log.w(TAG, "something is null at sensor for steps - happens at emulator", ne);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        int steps = (int) values[0];
        currentSteps = steps;

        this.tvSteps.setText("Steps: " + steps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
