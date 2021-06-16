package com.example.finalproject.ui.users_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.model.Model;
import com.example.finalproject.model.User;

import java.util.List;

public class usersListViewModel extends ViewModel {
    private LiveData<List<User>> usersList;

    public usersListViewModel() {
        usersList = Model.instance.getAllUsers();
    }

    public LiveData<List<User>> getData() {
        return usersList;
    }
}