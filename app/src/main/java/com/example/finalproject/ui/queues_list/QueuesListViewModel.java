package com.example.finalproject.ui.queues_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;
import com.example.finalproject.ui.login.LoginFragment;

import java.util.LinkedList;
import java.util.List;

public class QueuesListViewModel extends ViewModel {
    private LiveData<List<Queue>> queuesList;
    public List<Queue> list;
    public QueuesListViewModel() {
        queuesList = Model.instance.getAllQueues();
    }

    //TODO: Get only user's queues....
    public LiveData<List<Queue>> getData() {
        getFilterData(Model.instance.getUser().id);
        return queuesList;
    }

    public List<Queue> getFilterData(String userId)
    {
       list = new LinkedList<>();
        for (Queue queue:   queuesList.getValue()) {
            if(Model.instance.getUser().isBarbershop) {
                if (queue.barbershopId.equals(userId))
                    list.add(queue);
            }
             else {
                if (queue.userId.equals(userId))
                    list.add(queue);
            }
        }
        return list;
    }

    public List<Queue> getFilterDataWithDate(String date) {
        list = new LinkedList<>();
        for (Queue queue : queuesList.getValue()) {
           if(queue.getBarbershopId().equals(Model.instance.getUser().getId()) && queue.getQueueDate().equals(date))
               list.add(queue);
        }
        return list;
    }
}