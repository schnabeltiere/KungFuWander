package kungfuwander.main;


import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FireBaseHelper {
    // at some point location database will be useless because it is in hiking db
    private static final String DB_USERS = "users";
    private static final String DB_HIKES = "hikes";
    private static final String DB_FRIENDS = "friends";
    private static final String TAG = "FireBaseHelper";
    // TODO: 21.05.2019 add friends database

    // TODO: 21.05.2019 read username from db or save in app?
    // maybe change to only uid
    public static void addFriendToLoggedInUser(UserBean user){
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
                .collection(DB_FRIENDS)
                .add(user)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));;
    }

    public static void createNewUserDatabase(){
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
                .set(new UserBean(MainActivity.currentFirebaseUser.getUid()));
    }

    // if there is more to update -> write methods
    public static void updateLoggedInUserName(String userName){
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
                .set(new UserBean(MainActivity.currentFirebaseUser.getUid(), userName), SetOptions.mergeFields("name"));
    }

    public static void fetchAllUsers(Consumer<List<UserBean>> consumer){
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        List<UserBean> users = documents.stream()
                                .map(d -> d.toObject(UserBean.class))
                                .collect(Collectors.toList());
                        consumer.accept(users);
                        Log.d(TAG, "Fetched " + users.size() + " users");
                    }
                });
    }

    public static void fetchSpecificUserHikes(String userUuid, Consumer<List<Hiking>> consumer){
        List<Hiking> hikes = new ArrayList<>();

        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(userUuid)
                .collection(DB_HIKES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Hiking hiking = document.toObject(Hiking.class);
                            hikes.add(hiking);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }

                    consumer.accept(hikes);
                });
    }

    public static void fetchLoggedInUserHikes(Consumer<List<Hiking>> consumer) {
        fetchSpecificUserHikes(MainActivity.currentFirebaseUser.getUid(), consumer);
    }

    public static void addToLoggedInUser(Hiking hiking) {
        // user get sub-collection with hiking
        // hiking has an array of locations. because it will never get changed
        // no need for sub-collection
        FirebaseFirestore.getInstance()
                .collection(DB_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
                .collection(DB_HIKES)
                .add(hiking)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

}
