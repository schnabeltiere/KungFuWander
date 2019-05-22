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

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

import im.delight.android.location.SimpleLocation;

public class FriendsFragment extends Fragment {

    private String TAG = getClass().getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        // inflater.inflate(R.layout.fragment_friends, null);

        Button btnShowReplacement = view.findViewById(R.id.btnShowReplacement);
        btnShowReplacement.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ReplacementForFriends.class);
            startActivity(intent);
        });

        return view;
    }


}
