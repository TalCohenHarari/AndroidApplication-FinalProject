package com.example.finalproject.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;

import java.util.List;

public class MapViewModel extends ViewModel {
    private LiveData<List<Barbershop>> barbershopsList;

    public MapViewModel() {
        barbershopsList = Model.instance.getAllBarbershops();
    }

    public LiveData<List<Barbershop>> getData() {
            return barbershopsList;
    }

    public int getBarbershopPosition(String barbershopName){

        for(int i=0; i<barbershopsList.getValue().size(); ++i)
        {
            if(barbershopsList.getValue().get(i).getName().equals(barbershopName))
                return i;
        }
        return 0;
    }
}