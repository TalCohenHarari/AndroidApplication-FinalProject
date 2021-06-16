package com.example.finalproject.ui.new_queue;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;

import java.util.List;

public class NewQueueViewModel extends ViewModel {
    private LiveData<List<Queue>> queuesList;

    public NewQueueViewModel() {
        queuesList = Model.instance.getAllQueues();
    }

    public LiveData<List<Queue>> getData() {
        return queuesList;
    }
}