package kungfuwander.main.deprecated;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import kungfuwander.main.R;
import kungfuwander.main.beans.User;
import kungfuwander.main.helper.FirebaseHelper;
import kungfuwander.main.helper.FriendsListAdapter;

public class FriendsList extends AppCompatActivity {

    public static final String UID_COMPARE = "uid_compare";
    private FriendsListAdapter adapter;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        FirebaseHelper.fetchAllUsers(this::setUpListView);
    }

    private boolean compareWithUser(int position) {
        User user = users.get(position);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View alertView = inflater.inflate(R.layout.alert_about_user, null);

        TextView tvUserName = alertView.findViewById(R.id.atvUserName);
        TextView tvHikingSince = alertView.findViewById(R.id.atvUserHikingSince);
        TextView tvNHikes = alertView.findViewById(R.id.atvUserNHikings);

        FirebaseHelper.fetchSpecificUserHikes(user.getUid(), hikes -> {
            tvUserName.setText("Challenger: " + user.getName());
            tvHikingSince.setText("Hike since: " + user.getUid());
            tvNHikes.setText("Here comes nHikings..." + hikes.size());
        });

        new AlertDialog.Builder(this)
                .setView(alertView)
                .setPositiveButton("Compare", (dialog, which) -> {
                    Intent intent = new Intent(this, CompareFriends.class);
                    intent.putExtra(UID_COMPARE, user.getUid());
                    startActivity(intent);
                })
                .show();
        return true;
    }

    private void setUpListView(List<User> users) {
        ListView listView = findViewById(R.id.listViewUsers);
        this.users = users;

        adapter = new FriendsListAdapter(this, 0, users);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> compareWithUser(position));
        listView.setOnItemLongClickListener((parent, view, position, id) -> showAddUserDialog(position));
        // do something with long click listener
    }

    private boolean showAddUserDialog(int position) {
        User user = users.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Dou you want to add him as your friend?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    FirebaseHelper.addFriendToLoggedInUser(user);
                    FirebaseHelper.addLoggedInUserAsFriend(user);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {});

        builder.create().show();
        return true;
    }

}
