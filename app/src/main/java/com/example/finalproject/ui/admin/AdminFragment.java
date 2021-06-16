package com.example.finalproject.ui.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.finalproject.R;

public class AdminFragment extends Fragment {

    Button allBarbershopsBtn;
    Button allUsersBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        allBarbershopsBtn = view.findViewById(R.id.admin_allBarbershops_btn);
        allUsersBtn= view.findViewById(R.id.admin_allUsers_btn);

        allBarbershopsBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_barbershops_list_Fragment));
//        allUsersBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_users_list_Fragment));

        return view;
    }
}