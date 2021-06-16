package com.example.finalproject.ui.barbershopDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.example.finalproject.ui.login.LoginFragment;

import java.util.List;

public class BarbershopDetailsViewModel extends ViewModel {
    private LiveData<List<Barbershop>> barbershopsList;

    public BarbershopDetailsViewModel() {
        barbershopsList = Model.instance.getAllBarbershops();
    }

    public LiveData<List<Barbershop>> getData() {
        return barbershopsList;
    }

}