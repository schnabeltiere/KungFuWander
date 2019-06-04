package kungfuwander.main.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import kungfuwander.main.helper.FirebaseHelper;
import kungfuwander.main.R;
import kungfuwander.main.beans.User;

public class Fragment_Profile extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private ImageView imageView;
    private TextView textViewUsrName,
            textViewUsrEmail,
            textViewDisplayHikingCount,
            textViewDisplayFriendsCount,
            textViewDisplayChallengeCount,
            textViewDisplayCurrentChallenge;

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
        textViewDisplayChallengeCount = v.findViewById(R.id.textViewDisplayChallangesCount);
        textViewDisplayCurrentChallenge = v.findViewById(R.id.textViewDisplayCurrentChallange);

        buttonAddFriends = v.findViewById(R.id.buttonAddFriend);
        listViewFriends = v.findViewById(R.id.listViewFriends);

        View headerView = getLayoutInflater().inflate(R.layout.listview_friends_header, null);
        listViewFriends.addHeaderView(headerView);

        initProfileWithFirebase();
        buttonAddFriends.setOnClickListener(this::showAllUsers);
    }

    private void showAllUsers(View view) {
        // TODO: 04.06.2019 gets only added to logged in user, like a following
        // later expand to firebase function with permission


    }


    @Deprecated
    private void initProfileWithFirebase() {
        // this is a cheat and takes a lot of data base requests

        FirebaseHelper.fetchLoggedInUserHikes(hikes -> {
            // should be string.valueOf otherwise it's an id
            textViewDisplayHikingCount.setText(String.valueOf(hikes.size()));
        });
        FirebaseHelper.fetchFriendsOfLoggedInUser(friends -> {
            // should be string.valueOf otherwise it's an id
            textViewDisplayFriendsCount.setText(String.valueOf(friends.size()));
            // TODO: 04.06.2019 change to custom
            List<String> items = friends.stream()
                    .map(User::getName)
                    .collect(Collectors.toList());

            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
            listViewFriends.setAdapter(itemsAdapter);
        });
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
