package kungfuwander.main;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
                switch (v.getId()) {
                    case R.id.imageViewProfilePicture:
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        break;
                }
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
