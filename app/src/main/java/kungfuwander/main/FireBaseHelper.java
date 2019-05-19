package kungfuwander.main;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private FirebaseFirestore db;

    // TODO: 19.05.2019 make class static
    public FireBaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void listenOnDatabaseChangedLocation(Consumer<MyLocation> consumer) {
        db.collection(DB_NAME_LOCATIONS)
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

    public void fetchUserHikings(Consumer<List<Hiking>> consumer) {
        List<Hiking> hikings = new ArrayList<>();

        db.collection(DB_NAME_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
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

    public void addToGeneralDatabase(Hiking hiking) {
        // Add a new document with a generated ID
        // will call ADDED listener, so list gets updated
        db.collection(DB_NAME_HIKINGS)
                .add(hiking)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public void addToSpecificUser(Hiking hiking) {
        // user get sub-collection with hiking
        // hiking has an array of locations. because it will never get changed
        // no need for sub-collection
        db.collection(DB_NAME_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
                .collection(DB_NAME_HIKINGS)
                .add(hiking)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    // adds in general database
    public void addToGeneralDatabase(MyLocation myLocation) {
        // Add a new document with a generated ID
        // will call ADDED listener, so list gets updated
        db.collection(DB_NAME_LOCATIONS)
                .add(myLocation)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public void addToSpecificUser(MyLocation myLocation) {
        // Add a new document with a generated ID
        // will call ADDED listener, so list gets updated
        db.collection(DB_NAME_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
                .collection(DB_NAME_LOCATIONS)
                .add(myLocation)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }
}
