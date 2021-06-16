package com.example.finalproject.ui.baebershops_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;

import java.util.List;

public class barbershopsListViewModel extends ViewModel {
    private LiveData<List<Barbershop>> barbershopsList;

    public barbershopsListViewModel() {
        barbershopsList = Model.instance.getAllBarbershops();
    }

    public LiveData<List<Barbershop>> getData() {
        return barbershopsList;
    }
}