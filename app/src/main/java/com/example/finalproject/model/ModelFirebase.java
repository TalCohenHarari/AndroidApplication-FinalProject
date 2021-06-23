package com.example.finalproject.model;


import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

public class ModelFirebase {

    final static String userCollection = "users";
    final static String barbershopCollection = "barbershops";
    final static String queueCollection = "queues";


    private ModelFirebase() {}

    //--------------------------------------User--------------------------------------------

    public interface GetAllUsersListener {
        public void onComplete(List<User> users);
    }

    public static void getAllUsers(Long since, GetAllUsersListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(userCollection)
                .whereGreaterThanOrEqualTo(User.LAST_UPDATED,new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> list = new LinkedList<User>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(User.create(document.getData()));
                            }
                        } else {}
                        listener.onComplete(list);
                    }
                });
    }

    //Save and signUp:
    public static void saveUser(User user, String action, Model.OnCompleteListener listener) {

        if (action.equals("signUp"))
        {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(user.name + "@a.com", user.password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    user.setId(firebaseUser.getUid());
                    save(user,action,()->listener.onComplete());
                }
            });
        }
        else if(action.equals("updateEmail"))//If it's an update username based on firebase authentication:
        {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            currentUser.updateEmail(user.getName()+"@a.com")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                save(user,action,()->listener.onComplete());
                            }
                        }
                    });
        }
        else if(action.equals("delete")) //delete user 'Auth'
        {
            save(user,action,()->{
            FirebaseUser deletedUser = FirebaseAuth.getInstance().getCurrentUser();
            deletedUser.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                               listener.onComplete();
                            }
                        }
                    });});
        }
        else //If it's an update details:
            save(user,action,listener);
    }

    public static void save(User user,String action ,Model.OnCompleteListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(userCollection).document(user.getId()).set(user.toJson())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                            getCurrentUser(listener);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete();
            }
        });
    }

    public static void login(String username, String password, Model.OnCompleteListener listener) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(username +"@a.com", password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getCurrentUser(listener);
                        }
                    }
                });
    }

    public static void isLoggedIn(Model.OnCompleteListener listener) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Model.instance.loadingStateDialog.setValue(Model.LoadingState.loading);
            getCurrentUser(()->listener.onComplete());
        }
    }

    public static void getCurrentUser(Model.OnCompleteListener listener)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        db.collection(userCollection).document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Model.instance.setUser(User.create(documentSnapshot.getData()),()->listener.onComplete());
            }
        });
    }

    public static void signOut(){
        FirebaseAuth.getInstance().signOut();
    }


    //---------------------------------------Queue--------------------------------------------

    public interface GetAllQueuesListener{
        public void onComplete(List<Queue> queues);
    }

    public static void getAllQueues(Long since, GetAllQueuesListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(queueCollection)
                .whereGreaterThanOrEqualTo(Queue.LAST_UPDATED,new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Queue> list = new LinkedList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(Queue.create(document.getData()));
                            }
                        } else { }
                        listener.onComplete(list);
                    }
                });
    }


    public static void saveQueue(Queue queue, Model.OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(queueCollection).document(queue.id).set(queue.toJson())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onComplete();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete();
            }
        });
    }

    //----------------------------------Barbershop------------------------------------------

    public interface GetAllBarbershopsListener{
        public void onComplete(List<Barbershop> barbershops);
    }
    public static void getAllBarbershops(Long since, GetAllBarbershopsListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(barbershopCollection)
                .whereGreaterThanOrEqualTo(Barbershop.LAST_UPDATED,new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Barbershop> list = new LinkedList<Barbershop>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(Barbershop.create(document.getData()));
                            }
                        } else {}
                        listener.onComplete(list);
                    }
                });
    }


    public static void saveBarbershop(Barbershop barbershop, Model.OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(barbershopCollection).document(barbershop.owner).set(barbershop.toJson())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getCurrentUser(()->listener.onComplete());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete();
            }
        });
    }

    public static void deleteBarbershopImage(Barbershop barbershop, Model.OnCompleteListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Create a reference to the file to delete
        StorageReference desertRef = storageRef.child("pictures/barbershops/"+barbershop.owner);
        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onComplete();            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                listener.onComplete();            }
        });
    }

    //--------------------------------SaveImages in storage----------------------------

    public static void uploadImage(Bitmap imageBmp, String name,String who, final Model.UpLoadImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imagesRef;
        if(who.equals("barbershop"))
            imagesRef = storage.getReference().child("pictures/barbershops").child(name);
        else
            imagesRef = storage.getReference().child("pictures/users").child(name);
        //Convert image:
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        // Now we upload the data to firebase storage:
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.onComplete("");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Getting from fireBase the image url:
                        Uri downloadUrl = uri;
                        listener.onComplete(downloadUrl.toString());
                    }
                });
            }
        });
    }
}
