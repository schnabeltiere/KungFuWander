package kungfuwander.main;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;

// TODO: 17.05.2019 remove this from this fragment
public class FriendsFragment extends Fragment implements SensorEventListener {

    private String TAG = getClass().getName();
    private int currentSteps = 0;
    private TextView tvSteps;
    private TextView tvHikings;

    private Hiking hiking;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        // inflater.inflate(R.layout.fragment_friends, null);
        tvSteps = view.findViewById(R.id.tvSteps);
        tvHikings = view.findViewById(R.id.tvHikings);

        Button btnStartHiking = view.findViewById(R.id.btnStartHiking);
        Button btnStopHiking = view.findViewById(R.id.btnStopHiking);
        Button btnTestChart = view.findViewById(R.id.btnTestChart);
        Button btnCompareFriends = view.findViewById(R.id.btnCompareFriends);
        Button btnNotification = view.findViewById(R.id.btnNotification);

        btnStartHiking.setOnClickListener(v -> startStepCounter());
        btnStopHiking.setOnClickListener(v -> stopStepCounter());
        btnTestChart.setOnClickListener(v -> openChart());
        btnCompareFriends.setOnClickListener(v -> compareFriends());
        btnNotification.setOnClickListener(v -> startNewIntentForNotification());

        return view;
    }

    private void startNewIntentForNotification(){
        Intent intent = new Intent(getActivity(), TestNotification.class);
        startActivity(intent);
    }

    @Deprecated
    // this does not work somehow? don't know?
    private void stackOverflowNotification() {
        Log.d(TAG, "Gets to stackoverflow notification...");

        NotificationManager mNotificationManager;
        Context mContext = getActivity();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
        Intent ii = new Intent(mContext.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Big Text");
        bigText.setBigContentTitle("Today's Bible Verse");
        bigText.setSummaryText("Text in detail");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText("Your text");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
    }

    @Deprecated
    private void sendNotification() {
        // Builds your notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("John's Android Studio Tutorials")
                .setContentText("A video has just arrived!");

        // Creates the intent needed to show the notification
        Intent notificationIntent = new Intent(getActivity(), SignUpActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void compareFriends() {
        Intent intent = new Intent(getActivity(), FriendsList.class);
        startActivity(intent);
    }

    private void openChart() {
        Intent intent = new Intent(getActivity(), TestRecentHikings.class);
        startActivity(intent);
    }

    private void stopStepCounter() {
        // TODO: 16.05.2019 stop this
        // look if steps < 100
        // now just add hiking and stop it
        hiking.setSteps(currentSteps);
        hiking.setEnd(Timestamp.now());
        // TODO: 16.05.2019 fetch locations and time

        FireBaseHelper.addToGeneralDatabase(hiking); // should be removed
        FireBaseHelper.addToLoggedInUser(hiking);
    }

    private void startStepCounter() {
        // TODO: 20.05.2019 warning if hiking is already started
        hiking = new Hiking();
        hiking.setStart(Timestamp.now());

        try {
            SensorManager sensorManager = (SensorManager) Objects.requireNonNull(getActivity())
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

        this.tvSteps.setText("Steps: " + steps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
