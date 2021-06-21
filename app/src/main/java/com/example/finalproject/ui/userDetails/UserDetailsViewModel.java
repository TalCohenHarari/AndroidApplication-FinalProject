package com.example.finalproject.ui.userDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.User;

import java.util.List;

public class UserDetailsViewModel extends ViewModel {
    private LiveData<List<Barbershop>> barbershopsList;

    public UserDetailsViewModel() {
        barbershopsList = Model.instance.getAllBarbershops();
    }

    public Integer geFilterByPosition(String userId){

        List<Barbershop> barbershops =  barbershopsList.getValue();

        if(barbershops!=null) {
            for (int i = 0; i < barbershops.size(); i++) {
                if (barbershops.get(i).owner.equals(userId)) {
                    return i;
                }
            }
        }
        return 0;
    }
}