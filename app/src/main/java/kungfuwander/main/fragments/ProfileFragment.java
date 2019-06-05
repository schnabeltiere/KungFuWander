package kungfuwander.main.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.stream.Collectors;

import kungfuwander.main.MainActivity;
import kungfuwander.main.R;
import kungfuwander.main.beans.Hike;
import kungfuwander.main.beans.User;
import kungfuwander.main.helper.FirebaseHelper;
import kungfuwander.main.helper.FriendsListAdapter;

public class ProfileFragment extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private ImageView imageView;
    private TextView textViewUsrName,
            textViewUsrEmail,
            textViewDisplayHikingCount,
            textViewDisplayFriendsCount,
            textViewDisplayOverallSteps;

    private Button buttonAddFriends;
    private ListView listViewFriends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(v);


        return v;
    }

    private void initUI(View v) {
        imageView = v.findViewById(R.id.imageViewProfilePicture);
        imageView.setOnClickListener(l -> {
            if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });

        textViewUsrName = v.findViewById(R.id.textViewUsrName);
        textViewUsrEmail = v.findViewById(R.id.textViewUsrEmail);
        textViewDisplayHikingCount = v.findViewById(R.id.textViewDisplayHikingCount);
        textViewDisplayFriendsCount = v.findViewById(R.id.textViewDisplayFriendsCount);
        textViewDisplayOverallSteps = v.findViewById(R.id.textViewDisplayOverallSteps);

        buttonAddFriends = v.findViewById(R.id.buttonAddFriend);
        listViewFriends = v.findViewById(R.id.listViewFriends);

        View headerView = getLayoutInflater().inflate(R.layout.listview_friends_header, null);
        listViewFriends.addHeaderView(headerView);

        initProfileWithFirebase();
        buttonAddFriends.setOnClickListener(view -> {
            FirebaseHelper.fetchAllUsers(allUsers -> {
                FirebaseHelper.fetchFriendsOfLoggedInUser(friends -> {
                    allUsers.removeAll(friends);
                    createDialog(allUsers);
                });
            });
        });
    }


    private void createDialog(List<User> users) {
        Log.d("Profile", "gotten: " + users);
        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View alertView = inflater.inflate(R.layout.alert_all_users, null);

        setUpListViewAllUsers(users, alertView);

        new AlertDialog.Builder(getContext())
                .setView(alertView)
                .setPositiveButton("Cool", (dialog, which) -> dialog.cancel())
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
                .show();
    }

    private void setUpListViewAllUsers(List<User> users, View alertView) {
        FriendsListAdapter adapter = new FriendsListAdapter(getContext(), 0, users);
        ListView listView =  alertView.findViewById(R.id.lvAllUsers);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            User friend = users.get(position);

            new AlertDialog.Builder(getContext())
                    .setMessage("Willst du " + friend.getName() + " als Freund hinzufügen?")
                    .setPositiveButton("Ja", (dialog, which) -> {
                        FirebaseHelper.addFriendToLoggedInUser(friend);
                        // TODO: 05.06.2019 update friend view
                        // just for testing, is a bad solution
                        initProfileWithFirebase();
                    })
                    .setNegativeButton("Nein", (dialog, which) -> dialog.cancel())
                    .show();
        });
    }

    @Deprecated
    private void initProfileWithFirebase() {
        // this is a cheat and takes a lot of data base requests

        FirebaseHelper.fetchLoggedInUserHikes(hikes -> {
            // should be string.valueOf otherwise it's an id
            int steps = hikes.stream()
                    .mapToInt(Hike::getSteps)
                    .sum();
            textViewDisplayOverallSteps.setText(String.valueOf(steps));
            textViewDisplayHikingCount.setText(String.valueOf(hikes.size()));
        });
        FirebaseHelper.fetchFriendsOfLoggedInUser(friends -> {
            // should be string.valueOf otherwise it's an id
            textViewDisplayFriendsCount.setText(String.valueOf(friends.size()));
            setUpListViewAdapter(friends);
        });
        textViewUsrEmail.setText(MainActivity.currentFirebaseUser.getEmail());
        textViewUsrName.setText(MainActivity.currentFirebaseUser.getDisplayName());
    }

    private void setUpListViewAdapter(List<User> friends) {
        // TODO: 04.06.2019 maybe change to custom
        List<String> items = friends.stream()
                .map(User::getName)
                .collect(Collectors.toList());

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
        listViewFriends.setAdapter(itemsAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {

            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }
}
