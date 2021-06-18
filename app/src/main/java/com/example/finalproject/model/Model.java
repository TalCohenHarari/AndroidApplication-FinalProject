package com.example.finalproject.model;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Model {

    final public static Model instance = new Model();
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public enum LoadingState {
        loaded,
        loading,
        error
    }

    public MutableLiveData<LoadingState> loadingState =
            new MutableLiveData<LoadingState>(LoadingState.loaded);

    private Model() {
    }

    public interface GetDataListener {
        void onComplete(List<Object> data);
    }

    public interface OnCompleteListener {
        void onComplete();
    }
    //---------------------------------------users---------------------------------------


    MutableLiveData<List<User>> allUsers = new MutableLiveData<List<User>>(new LinkedList<User>());

    public LiveData<List<User>> getAllUsers() {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.getAllUsers((users) -> {
            allUsers.setValue(users);
            loadingState.setValue(LoadingState.loaded);
        });
        return allUsers;
    }

    public void saveUser(User user, String action, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.saveUser(user, action, () -> {
            listener.onComplete();
        });
        getAllUsers();//TODO: we need it?
    }

    public void login(String username, String password, OnCompleteListener listener) {
        ModelFirebase.login(username, password, () -> listener.onComplete());
    }


    public static void isLoggedIn(OnCompleteListener listener) {
        ModelFirebase.isLoggedIn(() -> listener.onComplete());
    }

    public static void signOut() {
        ModelFirebase.signOut();
    }


    //------------------------------------Queues---------------------------------


    MutableLiveData<List<Queue>> allQueues = new MutableLiveData<List<Queue>>(new LinkedList<Queue>());

    public LiveData<List<Queue>> getAllQueues() {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.getAllQueues((queues) -> {
            allQueues.setValue(queues);
            loadingState.setValue(LoadingState.loaded);
        });
        return allQueues;
    }

    public void saveQueue(Queue queue, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.saveQueue(queue, () -> {
            listener.onComplete();
        });
        getAllQueues();
    }

    public void createCalendar(List<Queue> queues, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        for (int i = 0; i < queues.size(); i++) {
            ModelFirebase.saveQueue(queues.get(i),()->listener.onComplete());
        }
        getAllQueues();
    }


    //----------------------barberShops----------------------


    MutableLiveData<List<Barbershop>> allBarbershops = new MutableLiveData<List<Barbershop>>(new LinkedList<Barbershop>());

    public LiveData<List<Barbershop>> getAllBarbershops() {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.getAllBarbershops((barbershops)->{
            allBarbershops.setValue(barbershops);
            loadingState.setValue(LoadingState.loaded);
        });
        return allBarbershops;
    }

    public void saveBarbershop(Barbershop barbershop, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.saveBarbershop(barbershop,()->{
            listener.onComplete();
        });
        getAllBarbershops();
    }


    //----------------------------Save images in firebase----------------------------------
    public interface  UpLoadImageListener{
        void onComplete(String url);
    }
    public static void uploadImage(Bitmap imageBmp, String name, String who, final UpLoadImageListener listener) {
        ModelFirebase.uploadImage(imageBmp,name,who,listener);
    }

}
