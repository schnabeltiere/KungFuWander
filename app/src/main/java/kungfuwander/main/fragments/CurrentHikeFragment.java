package kungfuwander.main.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import kungfuwander.main.deprecated.FriendsList;
import kungfuwander.main.deprecated.TestRecentHikes;
import kungfuwander.main.helper.FirebaseHelper;
import kungfuwander.main.helper.NotificationService;

import static kungfuwander.main.helper.NotificationService.CHANNEL_ID;
import static kungfuwander.main.helper.NotificationService.CONTENT_TITLE;
import static kungfuwander.main.helper.NotificationService.CURRENT_HIKING_ID;


public class CurrentHikeFragment extends Fragment implements SensorEventListener {

    private String TAG = getClass().getName();
    private int currentSteps = 0;
    private TextView tvSteps;
    private TextView tvMeter;

    private Hike actualHike;
    private SimpleLocation simpleLocation;

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_hike, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        simpleLocation = setUpSimpleLocation();

        // if we can't access the location yet
        if (!simpleLocation.hasLocationEnabled()) {
            // ask the user to enable location access
            // TODO: 05.06.2019 what if not granted?
            SimpleLocation.openSettings(getContext());
        }

        tvSteps = view.findViewById(R.id.textViewDisplayStepCount);
        tvMeter = view.findViewById(R.id.tvDisplayWalkedMeters);

        setUpStartHiking(view);
        setUpStopHiking(view);
    }

    private SimpleLocation.Listener defineCustomListener() {
        return () -> {
            // new location data has been received and can be accessed
            double latitude = simpleLocation.getLatitude();
            double longitude = simpleLocation.getLongitude();

            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            if (actualHike == null) {
                Log.w(TAG, "No actualHike init, should not happen, listener should be detached");
            } else {
                actualHike.addGeoPoint(geoPoint);
                tvMeter.setText(String.valueOf(currentSteps*0.7));
                // TODO: 22.05.2019 replace this with method call from Example Service
                updateNotification();
                Log.d(TAG, "Added: " + geoPoint.toString() + ", size of actualHike: " + actualHike.getGeoPoints().size());
            }
        };
    }

    @Deprecated
    private void updateNotification() {
        // but the manager is always null
//                notificationService.updateNotification(manager, "You are walking " + Math.random() + " steps");
        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
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
            Toast.makeText(getContext(), "You haven't even started yet", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Hike didn't start");
            return;
        }

        actualHike.setSteps(currentSteps);
        actualHike.setEnd(Timestamp.now());

        FirebaseHelper.addToLoggedInUser(actualHike);
        simpleLocation.endUpdates();

        // TODO: 22.05.2019 maybe congrats for walking
        Toast.makeText(getContext(), "Congrats!", Toast.LENGTH_LONG).show();
        actualHike = null;
    }

    private void stopStickyNotification() {
        Intent serviceIntent = new Intent(getContext(), NotificationService.class);
        getContext().stopService(serviceIntent);
    }

    private void showStickyNotification() {
        String input = "You are walking " + Math.random() + " steps";

        Intent serviceIntent = new Intent(getContext(), NotificationService.class);
        serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(getContext(), serviceIntent);
    }

    private void startHiking() {
        // let user decide what to because hiking is already started
        if (actualHike != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
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
            SensorManager sensorManager = (SensorManager) Objects.requireNonNull(getContext())
                    .getSystemService(Context.SENSOR_SERVICE);

            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            Objects.requireNonNull(sensorManager).registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_UI);
        } catch (NullPointerException ne) {
            Log.w(TAG, "something is null at sensor for steps - happens at emulator", ne);
        }
    }

    private SimpleLocation setUpSimpleLocation() {
        Context context = getContext();
        boolean requireFineGranularity = true;
        boolean passiveMode = false;
        long updateIntervalInMilliseconds = 100;
        boolean requireNewLocation = true;

        return new SimpleLocation(context, requireFineGranularity, passiveMode, updateIntervalInMilliseconds, requireNewLocation);
    }

    private void setUpStartHiking(View view) {
        Button btnStartHiking = view.findViewById(R.id.buttonStart);
        btnStartHiking.setOnClickListener(v -> startHiking());
    }

    private void setUpStopHiking(View view) {
        Button btnStopHiking = view.findViewById(R.id.buttonStop);
        btnStopHiking.setOnClickListener(v -> stopStepCounter());
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
