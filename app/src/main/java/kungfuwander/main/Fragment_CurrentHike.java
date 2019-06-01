package kungfuwander.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Fragment_CurrentHike extends Fragment {
    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_hike, null);
//        Button btnShowReplacement = view.findViewById(R.id.btnShowReplacement);
//        btnShowReplacement.setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), ReplacementForFriends.class);
//            startActivity(intent);
//        });

    }

}
