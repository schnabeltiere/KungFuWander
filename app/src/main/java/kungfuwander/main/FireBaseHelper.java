package kungfuwander.main;


import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FireBaseHelper {
    // at some point location database will be useless because it is in hiking db
    private static final String DB_NAME_USERS = "users_db";
    private static final String DB_NAME_LOCATIONS = "location_db";
    private static final String DB_NAME_HIKINGS = "hiking_db";
    private static final String TAG = "FireBaseHelper";

    public static void listenOnDatabaseChangedLocation(Consumer<MyLocation> consumer) {
        FirebaseFirestore.getInstance()
                .collection(DB_NAME_LOCATIONS)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        MyLocation myLocation = dc.getDocument().toObject(MyLocation.class);

                        switch (dc.getType()) {
                            case ADDED:
                                // this is called the first time at reading the database
                                // so everything will be under ADDED the first time

                                consumer.accept(myLocation);
                                Log.d(TAG, "New Location: " + dc.getDocument().getData());
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified Location: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed Location: " + dc.getDocument().getData());
                                break;
                        }
                    }
                });
    }
    
    public static void fetchAllUsers(Consumer<List<UserBean>> consumer){
        FirebaseFirestore.getInstance()
                .collection(DB_NAME_USERS)
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
        List<Hiking> hikings = new ArrayList<>();

        FirebaseFirestore.getInstance()
                .collection(DB_NAME_USERS)
                .document(userUuid)
                .collection(DB_NAME_HIKINGS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Hiking hiking = document.toObject(Hiking.class);
                            hikings.add(hiking);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }

                    consumer.accept(hikings);
                });
    }

    public static void fetchLoggedInUserHikes(Consumer<List<Hiking>> consumer) {
        Log.d(TAG, "Is the UID really null? " + MainActivity.currentFirebaseUser);
        fetchSpecificUserHikes(MainActivity.currentFirebaseUser.getUid(), consumer);
    }

    public static void addToGeneralDatabase(Hiking hiking) {
        // Add a new document with a generated ID
        // will call ADDED listener, so list gets updated
        FirebaseFirestore.getInstance().collection(DB_NAME_HIKINGS)
                .add(hiking)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static void addToLoggedInUser(Hiking hiking) {
        // user get sub-collection with hiking
        // hiking has an array of locations. because it will never get changed
        // no need for sub-collection
        FirebaseFirestore.getInstance()
                .collection(DB_NAME_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
                .collection(DB_NAME_HIKINGS)
                .add(hiking)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static void addToGeneralDatabase(MyLocation myLocation) {
        // Add a new document with a generated ID
        // will call ADDED listener, so list gets updated
        FirebaseFirestore.getInstance()
                .collection(DB_NAME_LOCATIONS)
                .add(myLocation)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static void addToLoggedInUser(MyLocation myLocation) {
        // Add a new document with a generated ID
        // will call ADDED listener, so list gets updated
        FirebaseFirestore.getInstance()
                .collection(DB_NAME_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
                .collection(DB_NAME_LOCATIONS)
                .add(myLocation)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }
}
