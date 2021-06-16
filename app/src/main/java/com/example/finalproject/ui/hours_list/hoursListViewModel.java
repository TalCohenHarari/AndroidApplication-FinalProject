package com.example.finalproject.ui.hours_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;
import com.example.finalproject.ui.login.LoginFragment;

import java.util.LinkedList;
import java.util.List;

public class hoursListViewModel extends ViewModel {

    private LiveData<List<Queue>> queuesList;
    public static List<Queue> list;
    public hoursListViewModel() {
        queuesList = Model.instance.getAllQueues();
    }

    public LiveData<List<Queue>> getData() {
//        getFilterData(HoursListFragment.fullDate,HoursListFragment.barbershopId);
        return queuesList;
    }

    public List<Queue> getFilterData(String fullDate,String barbershopId)
    {
        list = new LinkedList<>();
        for (Queue queue:   queuesList.getValue()) {
                if (queue.barbershopId.equals(barbershopId) &&
                        queue.queueDate.equals(fullDate))
                    list.add(queue);
            }

        return list;
    }
}