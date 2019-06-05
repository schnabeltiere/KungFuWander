package kungfuwander.main.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

import im.delight.android.location.SimpleLocation;
import kungfuwander.main.R;
import kungfuwander.main.beans.Hike;
import kungfuwander.main.helper.FirebaseHelper;
import kungfuwander.main.helper.NotificationHelper;
import kungfuwander.main.helper.TimeControllAccessor;


public class CurrentHikeFragment extends Fragment implements SensorEventListener {

    private static final int NOTI_PRIMARY = 1100;
    private static final String TAG = CurrentHikeFragment.class.getName();

    private int currentSteps = 0;
    private TextView tvSteps;
    private TextView tvMeter;
    private TextView tvDuration;
    private TextView tvCalories;

    private Hike actualHike;
    private SimpleLocation simpleLocation;
    private NotificationHelper notificationHelper;
    Thread controlTime;
    TimeControllAccessor accessor = new TimeControllAccessor();


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_hike, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        simpleLocation = setUpSimpleLocation();
        notificationHelper = new NotificationHelper(getContext());

        // if we can't access the location yet
        if (!simpleLocation.hasLocationEnabled()) {
            // ask the user to enable location access
            // TODO: 05.06.2019 what if not granted?
            SimpleLocation.openSettings(getContext());
        }

        tvSteps = view.findViewById(R.id.textViewDisplayStepCount);
        tvMeter = view.findViewById(R.id.tvDisplayWalkedMeters);
        tvDuration = view.findViewById(R.id.tvDisplayDuration);
        tvCalories = view.findViewById(R.id.tvDisplayCaloriesBurned);

        setUpStartHiking(view);
        setUpStopHiking(view);

    }
    private void setUpStartHiking(View view) {
        Button btnStartHiking = view.findViewById(R.id.buttonStart);
        btnStartHiking.setOnClickListener(v ->{
            startHiking();
            Toast.makeText(notificationHelper, "Have fun walking!", Toast.LENGTH_SHORT).show();
        } );
    }

    private void startHiking() {
        clearTextViews();
        // let user decide what to because hiking is already started
        if (actualHike != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setMessage("Do you want to start all over again?")
                    .setPositiveButton("Yes", (dialog, id) -> {
                        // User wants to start again
                        actualHike = null;
                        startHiking();
                        tvSteps.setText(String.valueOf(0));
                        currentSteps = 0;
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

        // TODO: 05.06.2019 start notification here and update later ?
        // currently notification only gets update with new geolocation
        startStepCounter();

        simpleLocation.setListener(defineCustomListener()); // should i start?
        simpleLocation.beginUpdates();

        accessor.setRun(true);
        controlTime = new Thread(new Runnable() {
            int second = 0;
            int secondI = 0;
            int minute = 0;
            int minuteI = 0;
            int hourI = 0;
            int hour = 0;

            @Override
            public void run() {
                //(hourIhour:minuteIminute:secondIsecond)
                while (accessor.getRun()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    second++;
                    if (second % 10 == 0) {
                        second = 0;
                        secondI++;
                    }
                    if (secondI == 6) {
                        secondI = 0;
                        minute++;
                    }
                    if (minute != 0 && minute % 10 == 0) {
                        minute = 0;
                        minuteI++;
                    }
                    if (minuteI == 6) {
                        minuteI = 0;
                        hour++;
                    }
                    if (hour != 0 && hour % 10 == 0) {
                        hour = 0;
                        hourI++;
                    }
                    if(getActivity() != null){
                        getActivity().runOnUiThread(()-> {
                            if(tvDuration != null){
                                tvDuration.setText(String.valueOf(hourI) + String.valueOf(hour) + ":" + String.valueOf(minuteI) + String.valueOf(minute) + ":" + String.valueOf(secondI) + String.valueOf(second));
                            }
                        });
                    }
                }
            }
        });
        controlTime.start();
    }

    private void clearTextViews() {
        tvMeter.setText("0");
        tvCalories.setText("0");
        tvSteps.setText("0");
        tvDuration.setText("00:00:00");
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        int steps = (int) values[0];
        currentSteps = steps;


        this.tvSteps.setText(String.valueOf(steps));
        this.tvCalories.setText(String.valueOf(steps*0.3));
        this.tvMeter.setText(String.valueOf(steps*0.7));

    }

    private void setUpStopHiking(View view) {
        Button btnStopHiking = view.findViewById(R.id.buttonStop);
        btnStopHiking.setOnClickListener(v -> {
            stopStepCounter();
            Toast.makeText(notificationHelper, "Hope it was successful!", Toast.LENGTH_SHORT).show();
        });
    }

    private void stopStepCounter() {
        // TODO: 22.05.2019 give user possibility to not add this hiking
        // cannot stop if it hasn't started
        if (actualHike == null) { // a bit tricky, because maybe the actual hiking is there
            Toast.makeText(getContext(), "You haven't even started yet", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Hike didn't start");
            return;
        }

        actualHike.setSteps(currentSteps);
        actualHike.setEnd(Timestamp.now());
        accessor.setRun(false);


        FirebaseHelper.addToLoggedInUser(actualHike);
        simpleLocation.endUpdates();
        notificationHelper.cancel(NOTI_PRIMARY);

        // TODO: 22.05.2019 maybe congrats for walking
        Toast.makeText(getContext(), "Congrats!", Toast.LENGTH_LONG).show();
        actualHike = null;
    }

    private SimpleLocation setUpSimpleLocation() {
        Context context = getContext();
        boolean requireFineGranularity = true;
        boolean passiveMode = false;
        long updateIntervalInMilliseconds = 100;
        boolean requireNewLocation = true;

        return new SimpleLocation(context, requireFineGranularity, passiveMode, updateIntervalInMilliseconds, requireNewLocation);
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
                // TODO: 05.06.2019 other calculation for meter
                tvMeter.setText(String.valueOf((int) (currentSteps*0.7)));

                notificationHelper.sendNotification(NOTI_PRIMARY,
                        "Hike - Sike", geoPoint.toString());
                Log.d(TAG, "Added: " + geoPoint.toString() + ", size of actualHike: "
                        + actualHike.getGeoPoints().size());
            }
        };
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
