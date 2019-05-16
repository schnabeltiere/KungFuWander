package kungfuwander.main;

import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.function.Consumer;

public class FireBaseHelper {
    // at some point location database will be useless because it is in hiking db
    private static final String DB_NAME_USERS = "users_db";
    private static final String DB_NAME_LOCATION = "location_db";
    private static final String DB_NAME_HIKING = "hiking_db";
    private final String TAG = getClass().getName();
    private FirebaseFirestore db;

    public FireBaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void listenOnDatabaseChangedLocation(Consumer<MyLocation> addLocationConsumer) {
        db.collection(DB_NAME_LOCATION)
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

                                addLocationConsumer.accept(myLocation);
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

    // TODO: 16.05.2019 fetch all hikings 

    // TODO: 16.05.2019 something about user specific
//    public void listenOnNewUserAdded(Consumer<MyLocation> addLocationConsumer) {
//        db.collection(DB_NAME_USERS)
//                .addSnapshotListener((snapshots, e) -> {
//                    if (e != null) {
//                        Log.w(TAG, "listen:error", e);
//                        return;
//                    }
//
//                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
//                        MyLocation myLocation = dc.getDocument().toObject(MyLocation.class);
//
//                        switch (dc.getType()) {
//                            case ADDED:
//                                // this is called the first time at reading the database
//                                // so everything will be under ADDED the first time
//
//                                addLocationConsumer.accept(myLocation);
//                                Log.d(TAG, "New Location: " + dc.getDocument().getData());
//                                break;
//                            case MODIFIED:
//                                Log.d(TAG, "Modified Location: " + dc.getDocument().getData());
//                                break;
//                            case REMOVED:
//                                Log.d(TAG, "Removed Location: " + dc.getDocument().getData());
//                                break;
//                        }
//                    }
//
//                });
//    }

    public void addToGeneralDatabase(Wanderung wanderung){
        // Add a new document with a generated ID
        // will call ADDED listener, so list gets updated
        db.collection(DB_NAME_HIKING)
                .add(wanderung)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public void addToSpecificUser(Wanderung wanderung){
        // user get sub-collection with hiking
        // hiking has an array of locations. because it will never get changed
        // no need for sub-collection
        db.collection(DB_NAME_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
                .collection(DB_NAME_HIKING)
                .add(wanderung)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    // adds in general database
    public void addToGeneralDatabase(MyLocation myLocation) {
        // Add a new document with a generated ID
        // will call ADDED listener, so list gets updated
        db.collection(DB_NAME_LOCATION)
                .add(myLocation)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public void addToSpecificUser(MyLocation myLocation){
        // Add a new document with a generated ID
        // will call ADDED listener, so list gets updated
        db.collection(DB_NAME_USERS)
                .document(MainActivity.currentFirebaseUser.getUid())
                .collection(DB_NAME_LOCATION)
                .add(myLocation)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }
}
