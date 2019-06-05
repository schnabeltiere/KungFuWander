package kungfuwander.main.helper;


import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import kungfuwander.main.MainActivity;
import kungfuwander.main.beans.Hike;
import kungfuwander.main.beans.User;

import static kungfuwander.main.MainActivity.currentFirebaseUser;

public class FirebaseHelper {
    private static final String TAG = FirebaseHelper.class.getName();
    // at some point location database will be useless because it is in hiking db
    private static final String DB_USERS = "users";
    private static final String DB_HIKES = "hikes";
    private static final String DB_FRIENDS = "friends";

    public static void fetchFriendsWithRoundTrip(Consumer<List<User>> consumer) {
        List<User> friends = new ArrayList<>();

        // this fetch gets some wrong data, userBeam is returned, but some are filled
        // with default_stuff.
        fetchFriendsOfLoggedInUser(userBeans -> {
            Log.d(TAG, "Friends of Logged in user: " + userBeans);

            userBeans.forEach(bean -> {
                // fetch real user based on uid
                Log.d(TAG, "Started with " + bean);
                fetchUserById(bean.getUid(), userBean -> {
                    Log.d(TAG, "Fetched: " + userBean);
                    friends.add(userBean);
                    consumer.accept(friends);
                });
            });
            // this comes to early - wait until all fetched
//            consumer.accept(friends);
        });
    }

    public static void fetchUserById(String userId, Consumer<User> consumer) {
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            consumer.accept(user);
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
    }

    public static void fetchFriendsOfLoggedInUser(Consumer<List<User>> consumer) {
        List<User> friends = new ArrayList<>();

        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(currentFirebaseUser.getUid())
                .collection(DB_FRIENDS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            friends.add(user);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }

                    consumer.accept(friends);
                });
    }

    // TODO: 21.05.2019 read username from db or save in app?
    // also add myself to other friend
    // maybe change to only uid
    public static void addFriendToLoggedInUser(User user) {
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(currentFirebaseUser.getUid())
                .collection(DB_FRIENDS)
                .document(user.getUid())
                .set(user) // TODO: 23.05.2019 instead of set check if user is already friend
                .addOnSuccessListener(documentReference -> Log.d(TAG, "UserReference set with ID: " + documentReference))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static void addLoggedInUserAsFriend(User user) {
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(user.getUid())
                .collection(DB_FRIENDS)
                .document(currentFirebaseUser.getUid())
                // TODO: 23.05.2019 replace with just reference
                .set(new User(currentFirebaseUser.getUid(), "deprecated_cheat_new_user"))
                .addOnSuccessListener(documentReference -> Log.d(TAG, "UserReference set with ID: " + documentReference))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static void createNewUserDatabase(String userName) {
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(currentFirebaseUser.getUid())
                .set(new User(currentFirebaseUser.getUid(), userName));
    }

    // if there is more to update -> write methods
    public static void updateLoggedInUserName(String userName) {
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(currentFirebaseUser.getUid())
                .set(new User(currentFirebaseUser.getUid(), userName), SetOptions.mergeFields("name"));
    }

    public static void fetchAllUsers(Consumer<List<User>> consumer) {
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        List<User> users = documents.stream()
                                .map(d -> d.toObject(User.class))
                                .collect(Collectors.toList());
                        consumer.accept(users);
                        Log.d(TAG, "Fetched " + users.size() + " users");
                    }
                });
    }

    public static void fetchSpecificUserHikes(String userUuid, Consumer<List<Hike>> consumer) {
        List<Hike> hikes = new ArrayList<>();

        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(userUuid)
                .collection(DB_HIKES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Hike hike = document.toObject(Hike.class);
                            hikes.add(hike);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }

                    consumer.accept(hikes);
                });
    }

    public static void fetchLoggedInUserHikes(Consumer<List<Hike>> consumer) {
        fetchSpecificUserHikes(currentFirebaseUser.getUid(), consumer);
    }

    public static void addToLoggedInUser(Hike hike) {
        // user get sub-collection with hike
        // hike has an array of locations. because it will never get changed
        // no need for sub-collection
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(currentFirebaseUser.getUid())
                .collection(DB_HIKES)
                .add(hike)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static void updateDisplayName(String username) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        currentFirebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated: "
                                + currentFirebaseUser.getDisplayName()
                                + " = " + username);
                    }
                });
    }
}
