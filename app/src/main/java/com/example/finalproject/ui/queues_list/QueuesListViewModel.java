package com.example.finalproject.ui.queues_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;
import com.example.finalproject.model.User;
import com.example.finalproject.ui.login.LoginFragment;

import java.util.LinkedList;
import java.util.List;

public class QueuesListViewModel extends ViewModel {

    private LiveData<List<Queue>> queuesList;
    private LiveData<List<Barbershop>> barbershopsList;
    private LiveData<List<User>> usersList;
    public List<Queue> list;

    public QueuesListViewModel() {
        queuesList = Model.instance.getAllQueues();
        barbershopsList =Model.instance.getAllBarbershops();
        usersList = Model.instance.getAllUsers();
    }

    public LiveData<List<Queue>> getData() {
        getFilterData(Model.instance.getUser().id);
        return queuesList;
    }

    public List<Queue> getFilterData(String userId) {
        list = new LinkedList<>();
        for (Queue queue:   queuesList.getValue()) {
            if(Model.instance.getUser().isBarbershop()) {
                if (queue.getBarbershopId().equals(userId) && !queue.isDeleted())
                    list.add(queue);
            }
            else {
                if (queue.getUserId().equals(userId))
                    list.add(queue);
            }
        }
        return list;
    }

    public List<Queue> getFilterDataWithDate(String date) {
        list = new LinkedList<>();
        for (Queue queue : queuesList.getValue()) {
            if(queue.getBarbershopId().equals(Model.instance.getUser().getId())
                    && queue.getQueueDate().equals(date)
                    && !(queue.isDeleted()))
                list.add(queue);
        }
        return list;
    }

    public List<Barbershop> getBarbershop() {
        return barbershopsList.getValue();
    }

    public String getUserName(String userId) {
        List<User> temp = usersList.getValue();
        for (User user: temp)
            if(user.getId().equals(userId))
                return user.getName();

        return "";
    }


}