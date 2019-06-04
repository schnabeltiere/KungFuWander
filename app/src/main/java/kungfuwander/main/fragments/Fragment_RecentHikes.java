package kungfuwander.main.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kungfuwander.main.deprecated.CompareActivity;
import kungfuwander.main.R;

public class Fragment_RecentHikes extends Fragment {
    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_hikes, null);
        TextView tv = view.findViewById(R.id.tvFriendsFragment);
        tv.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CompareActivity.class);
            startActivity(intent);
        });
        return view;
    }
}
