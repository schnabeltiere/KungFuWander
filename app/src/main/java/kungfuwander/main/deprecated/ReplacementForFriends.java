package kungfuwander.main.deprecated;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

import im.delight.android.location.SimpleLocation;
import kungfuwander.main.R;
import kungfuwander.main.beans.Hike;
import kungfuwander.main.helper.FirebaseHelper;

import static kungfuwander.main.deprecated.NotificationService.CHANNEL_ID;
import static kungfuwander.main.deprecated.NotificationService.CONTENT_TITLE;
import static kungfuwander.main.deprecated.NotificationService.CURRENT_HIKING_ID;

public class ReplacementForFriends extends AppCompatActivity implements SensorEventListener {

    private static String TAG = ReplacementForFriends.class.getName();
    private int currentSteps = 0;
    private TextView tvSteps;
    private TextView tvHikes;

    private Hike actualHike;
    private SimpleLocation simpleLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replacement_for_friends);

        tvSteps = findViewById(R.id.tvSteps);
        tvHikes = findViewById(R.id.tvHikes);

        simpleLocation = setUpSimpleLocation();

        // if we can't access the location yet
        if (!simpleLocation.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        setUpStartHiking();
        setUpStopHiking();
        setUpChartIntent();
        setUpCompareFriendsIntent();
        // just for testing, remove later
        setUpRename();
    }


    private SimpleLocation.Listener defineCustomListener() {
        return () -> {
            // new location data has been received and can be accessed
            double latitude = simpleLocation.getLatitude();
            double longitude = simpleLocation.getLongitude();

            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            if (actualHike == null) {
                tvHikes.setText("No actualHike init..." + Math.random());
                Log.w(TAG, "No actualHike init, should not happen, listener should be detached");
            } else {
                actualHike.addGeoPoint(geoPoint);
                // TODO: 22.05.2019 replace this with method call from Example Service
                updateNotification();
                tvHikes.setText("Added: " + geoPoint.toString() + ", size of actualHike: " + actualHike.getGeoPoints().size());
                Log.d(TAG, "Added: " + geoPoint.toString() + ", size of actualHike: " + actualHike.getGeoPoints().size());
            }
        };
    }

    @Deprecated
    private void updateNotification() {
        // but the manager is always null
//                notificationService.updateNotification(manager, "You are walking " + Math.random() + " steps");
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(CONTENT_TITLE)
                .setContentText("You are walking " + Math.random() + " steps")
                .setSmallIcon(R.drawable.kfwl2)
                .build();

        manager.notify(CURRENT_HIKING_ID, notification);
    }

    private void stopStepCounter() {
        // TODO: 22.05.2019 give user possibility to not add this hiking
        // the notification should definitely stop
        stopStickyNotification(); // also stop step counter
        // cannot stop if it hasn't started
        if (actualHike == null) { // a bit tricky, because maybe the actual hiking is there
            Toast.makeText(this, "You haven't even started yet", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Hike didn't start");
            return;
        }

        actualHike.setSteps(currentSteps);
        actualHike.setEnd(Timestamp.now());

        FirebaseHelper.addToLoggedInUser(actualHike);
        simpleLocation.endUpdates();

        // TODO: 22.05.2019 maybe congrats for walking
        tvHikes.setText("Well done! Meters: " + actualHike.inMeter());
        actualHike = null;
    }

    private void stopStickyNotification() {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        stopService(serviceIntent);
    }

    private void showStickyNotification() {
        String input = "You are walking " + Math.random() + " steps";

        Intent serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void startHiking() {
        // let user decide what to because hiking is already started
        if (actualHike != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("Do you want to start all over again?")
                    .setPositiveButton("Yes", (dialog, id) -> {
                        // User wants to start again
                        actualHike = null;
                        startHiking();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> {
                        // User wants everything back to normal - do nothing
                    });

            builder.create().show();
            return;
        }

        // no actual hiking -> start one
        actualHike = new Hike();
        actualHike.setStart(Timestamp.now());

        showStickyNotification();
        startStepCounter();

        simpleLocation.setListener(defineCustomListener()); // should i start?
        simpleLocation.beginUpdates();
    }

    private void startStepCounter() {
        try {
            SensorManager sensorManager = (SensorManager) Objects.requireNonNull(this)
                    .getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            Objects.requireNonNull(sensorManager).registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_UI);
        } catch (NullPointerException ne) {
            Log.w(TAG, "something is null at sensor for steps - happens at emulator", ne);
        }
    }

    private void setUpRename() {
        Button btnRename = findViewById(R.id.btnRename);
        EditText name = findViewById(R.id.editTextNameRename);
        btnRename.setOnClickListener(v -> FirebaseHelper.updateLoggedInUserName(name.getText().toString()));
    }

    private SimpleLocation setUpSimpleLocation() {
        Context context = this;
        boolean requireFineGranularity = true;
        boolean passiveMode = false;
        long updateIntervalInMilliseconds = 100;
        boolean requireNewLocation = true;

        return new SimpleLocation(context, requireFineGranularity, passiveMode, updateIntervalInMilliseconds, requireNewLocation);
    }

    private void setUpStartHiking() {
        Button btnStartHiking = findViewById(R.id.btnStartHiking);
        btnStartHiking.setOnClickListener(v -> startHiking());
    }

    private void setUpStopHiking() {
        Button btnStopHiking = findViewById(R.id.btnStopHiking);
        btnStopHiking.setOnClickListener(v -> stopStepCounter());
    }

    private void setUpChartIntent() {
        Button btnTestChart = findViewById(R.id.btnTestChart);
        btnTestChart.setOnClickListener(v -> {
            Intent intent = new Intent(this, TestRecentHikes.class);
            startActivity(intent);
        });
    }

    private void setUpCompareFriendsIntent() {
        Button btnCompareFriends = findViewById(R.id.btnCompareFriends);
        btnCompareFriends.setOnClickListener(v -> {
            Intent intent = new Intent(this, FriendsList.class);
            startActivity(intent);
        });
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

    @Override
    public void onResume() {
        super.onResume();

        // don't continue, because it is never stopped
        // make the device update its location
//        simpleLocation.beginUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();

        // don't end, because we need it to run without app
        // stop location updates (saves battery)
//        simpleLocation.endUpdates();
    }

}
