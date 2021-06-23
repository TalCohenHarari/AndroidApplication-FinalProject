package com.example.finalproject.model;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Model {

    final public static Model instance = new Model();
    ExecutorService executorService = Executors.newCachedThreadPool();
    User user;

    private Model() {}

    public interface OnCompleteListener {
        void onComplete();
    }

    public enum LoadingState {
        loaded,
        loading,
        error
    }

    public MutableLiveData<LoadingState> loadingStateDialog =
            new MutableLiveData<LoadingState>(LoadingState.loaded);

    public MutableLiveData<LoadingState> loadingState =
            new MutableLiveData<LoadingState>(LoadingState.loaded);

    public User getUser() {
        return user;
    }

    public void setUser(User user,OnCompleteListener listener) {
        this.user = user;
        listener.onComplete();
    }

    //---------------------------------------users---------------------------------------

    LiveData<List<User>> allUsers =   AppLocalDB.db.userDao().getAll();

    public LiveData<List<User>> getAllUsers() {
    loadingState.setValue(LoadingState.loading);
    //read the local last update time
    Long localLastUpdate = User.getLocalLastUpdateTime();
    //ge all updates from firebase
    ModelFirebase.getAllUsers(localLastUpdate,(users)->{
        executorService.execute(()->
        {
            Long lastUpdate = new Long(0);
            //update the local DB with the new records
            for (User user: users)
            {

                if(!(user.isAvailable()))
                {
                    AppLocalDB.db.userDao().delete(user);
                }
                else{
                    AppLocalDB.db.userDao().insertAll(user);
                }
                //update the local last update time
                if(lastUpdate < user.lastUpdated)
                {
                    lastUpdate = user.lastUpdated;
                }
            }
            User.setLocalLastUpdateTime(lastUpdate);
            //postValue make it happen in main thread of the view and not in this thread:
            loadingState.postValue(LoadingState.loaded);
            //read all the data from the local DB -> return the data to the caller
            //automatically perform by room -> live data gets updated
        });
    });

    return allUsers;
}

    public void saveUser(User user, String action, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.saveUser(user, action, () -> {
            getAllUsers();
            listener.onComplete();
        });
    }

    public void login(String username, String password, OnCompleteListener listener) {
        ModelFirebase.login(username, password, () -> listener.onComplete());
    }


    public void isLoggedIn(OnCompleteListener listener) {
        ModelFirebase.isLoggedIn(() ->{
            loadingStateDialog.setValue(LoadingState.loaded);
            listener.onComplete();
        });
    }

    public static void signOut() {
        ModelFirebase.signOut();
    }


    //-----------------------------------------Queues----------------------------------------

    LiveData<List<Queue>> allQueues = AppLocalDB.db.QueueDao().getAll();

    public LiveData<List<Queue>> getAllQueues() {

        loadingState.setValue(LoadingState.loading);
        //read the local last update time
        Long localLastUpdate = Queue.getLocalLastUpdateTime();
        //ge all updates from firebase
        ModelFirebase.getAllQueues(localLastUpdate,(queues)->{
            executorService.execute(()->
            {

                Long lastUpdate = new Long(0);
                //update the local DB with the new records
                for (Queue queue : queues)
                {

                    if(queue.isDeleted())
                    {
                        AppLocalDB.db.QueueDao().delete(queue);
                    }
                    else {
                        AppLocalDB.db.QueueDao().insertAll(queue);
                    }
                    //update the local last update time
                    if (lastUpdate < queue.lastUpdated) {
                        lastUpdate = queue.lastUpdated;
                    }
                }
                Queue.setLocalLastUpdateTime(lastUpdate);
                //postValue make it happen in main thread of the view and not in this thread:
                loadingState.postValue(LoadingState.loaded);
                //read all the data from the local DB -> return the data to the caller
                //automatically perform by room -> live data gets updated
            });
        });

        return allQueues;
    }

    public void saveQueue(Queue queue, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.saveQueue(queue, () -> {
            getAllQueues();
            listener.onComplete();
        });
    }

    public void createCalendar(List<Queue> queues, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        for (int i = 0; i < queues.size(); i++) {
            ModelFirebase.saveQueue(queues.get(i),()->{
                getAllQueues();
                listener.onComplete();
            });
        }
    }


    //---------------------------------------barberShops-----------------------------------

    LiveData<List<Barbershop>> allBarbershops = AppLocalDB.db.BarbershopDao().getAll();

    public LiveData<List<Barbershop>> getAllBarbershops() {

        loadingState.setValue(LoadingState.loading);
        //read the local last update time
        Long localLastUpdate = Barbershop.getLocalLastUpdateTime();
        //ge all updates from firebase
        ModelFirebase.getAllBarbershops(localLastUpdate,(barbershops)->{
                executorService.execute(()->
                {

                Long lastUpdate = new Long(0);
                //update the local DB with the new records
                for (Barbershop barbershop : barbershops) {
                    if (barbershop.isDeleted()) {
                        AppLocalDB.db.BarbershopDao().delete(barbershop);
                    } else {
                        AppLocalDB.db.BarbershopDao().insertAll(barbershop);
                    }
                    //update the local last update time
                    if (lastUpdate < barbershop.lastUpdated) {
                        lastUpdate = barbershop.lastUpdated;
                    }
                }
                Barbershop.setLocalLastUpdateTime(lastUpdate);
                //postValue make it happen in main thread of the view and not in this thread:
                loadingState.postValue(LoadingState.loaded);
                //read all the data from the local DB -> return the data to the caller
                //automatically perform by room -> live data gets updated

            });
        });

        return allBarbershops;
    }

    public void saveBarbershop(Barbershop barbershop, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.saveBarbershop(barbershop,()->{
            getAllBarbershops();
            listener.onComplete();
        });
    }


    //----------------------------Save images in firebase----------------------------------
    public interface  UpLoadImageListener{
        void onComplete(String url);
    }

    public static void uploadImage(Bitmap imageBmp, String name, String who, final UpLoadImageListener listener) {
        ModelFirebase.uploadImage(imageBmp,name,who,listener);
    }

}
