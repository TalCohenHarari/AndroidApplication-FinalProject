package com.example.finalproject.ui.edit_Barbershop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;

import java.util.List;

public class EditBarbershopViewModel extends ViewModel {

    private LiveData<List<Barbershop>> barbershopsList;
    private LiveData<List<Queue>> queuesList;

    public EditBarbershopViewModel() {
        barbershopsList = Model.instance.getAllBarbershops();
        queuesList = Model.instance.getAllQueues();
    }

    public LiveData<List<Barbershop>> getData() {
        return barbershopsList;
    }

    public LiveData<List<Queue>> getQueues() {
        return queuesList;
    }


    public Barbershop getCurrentBarbershop(String id) {
        Barbershop barbershop = new Barbershop();
        for (Barbershop b: barbershopsList.getValue())
        {
            if(b.getOwner().equals(id)){
                barbershop = b;
                break;
            }
        }
        return barbershop;
    }
}