package com.example.finalproject.ui.baebershops_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;

import java.util.LinkedList;
import java.util.List;

public class BarbershopsListViewModel extends ViewModel {

    private LiveData<List<Barbershop>> barbershopsList;
    public List<Barbershop> list;

    public BarbershopsListViewModel() {
        barbershopsList = Model.instance.getAllBarbershops();
        list = new LinkedList<>();
    }

    public LiveData<List<Barbershop>> getData() {
        return barbershopsList;
    }

    public void refresh(){
        Model.instance.getAllBarbershops();
    }

}