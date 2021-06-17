package com.example.finalproject.ui.signUp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.model.Model;
import com.example.finalproject.model.User;

import java.util.List;

public class SignUpViewModel extends ViewModel {


    private LiveData<List<User>> usersList;

    public SignUpViewModel() {
        usersList = Model.instance.getAllUsers();
    }

    public LiveData<List<User>> getData() {
        return usersList;
    }


    public boolean isUserNameExist(String userName) {

        for (User user:usersList.getValue() )
            if (user.getName().equals(userName))
                return true;

        return false;
    }
}