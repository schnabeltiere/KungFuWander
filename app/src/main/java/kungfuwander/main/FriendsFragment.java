package kungfuwander.main;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

// TODO: 17.05.2019 remove this from this fragment
public class FriendsFragment extends Fragment implements SensorEventListener {

    private String TAG = getClass().getName();
    private int currentSteps = 0;
    private TextView tvSteps;
    private TextView tvHikings;

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

        btnStartHiking.setOnClickListener(v -> startStepCounter());
        btnStopHiking.setOnClickListener(v -> stopStepCounter());
        btnTestChart.setOnClickListener(v -> openChart());
        btnCompareFriends.setOnClickListener(v -> compareFriends());

        return view;
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
        // now just add hiking and stop it
        Hiking hiking = new Hiking();
        hiking.setSteps(currentSteps);
        // TODO: 16.05.2019 fetch locations and time

        FireBaseHelper.addToGeneralDatabase(hiking);
        FireBaseHelper.addToLoggedInUser(hiking);
    }

    private void startStepCounter() {
        try {
            SensorManager sensorManager = (SensorManager) Objects.requireNonNull(getActivity())
                    .getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            Objects.requireNonNull(sensorManager).registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_UI);
        } catch (NullPointerException ne){
            Log.d(TAG, "something is null", ne);
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
