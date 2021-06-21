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
        list = new LinkedList<>();
    }

    public LiveData<List<Queue>> getData() {
        return queuesList;
    }

    public List<Queue> getFilterForUser(String userId) {
        list = new LinkedList<>();
        if( queuesList.getValue()!=null)
            for (Queue queue:   queuesList.getValue())
                if (queue.getUserId().equals(userId))
                        list.add(queue);
        return list;
    }

    public List<Queue> getFilterForBarbershop(String date) {
        list = new LinkedList<>();
        for (Queue queue : queuesList.getValue()) {
            if(queue.getBarbershopId().equals(Model.instance.getUser().getId())
                    && queue.getQueueDate().equals(date))
                list.add(queue);
        }
        return list;
    }

    public List<Barbershop> getBarbershop() {
        return barbershopsList.getValue();
    }

    public String getUserName(String userId) {

        if(usersList.getValue()!=null)
        {
            for (User user : usersList.getValue())
                if (user.getId().equals(userId))
                    return user.getName();
        }

        return "";
    }


}