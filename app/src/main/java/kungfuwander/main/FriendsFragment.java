package kungfuwander.main;

import android.content.Context;
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

public class FriendsFragment extends Fragment implements SensorEventListener {

    private String TAG = getClass().getName();
    private TextView tvSteps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        // inflater.inflate(R.layout.fragment_friends, null);
        tvSteps = view.findViewById(R.id.tvSteps);
        Button btnStartHiking = view.findViewById(R.id.btnStartHiking);

        btnStartHiking.setOnClickListener(v -> startStepCounter());


        return view;
    }

    private void startStepCounter() {
        try {
            SensorManager sensorManager = (SensorManager) getActivity()
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

        this.tvSteps.setText("Steps: " + steps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
