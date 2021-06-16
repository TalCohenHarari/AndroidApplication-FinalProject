package com.example.finalproject.ui.new_queue;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.CalendarView;
import com.example.finalproject.R;
import com.example.finalproject.model.Model;
import com.example.finalproject.ui.baebershops_list.barbershops_list_FragmentDirections;


public class new_queue_Fragment extends Fragment {


    CalendarView calendarView;
    String fullDate;
    String barbershopId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_queue_, container, false);
        calendarView = view.findViewById(R.id.newQueue_calander_calendarView);
        fullDate="";

        barbershopId = new_queue_FragmentArgs.fromBundle(getArguments()).getBaebershopId();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                fullDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//                if(!fullDate.equals("")){
                    new_queue_FragmentDirections.ActionNavNewQueueToHoursListFragment
                            action = new_queue_FragmentDirections.actionNavNewQueueToHoursListFragment(fullDate, barbershopId);
                    Navigation.findNavController(view).navigate(action);
//            }
            }
        });

        Button backBtn = view.findViewById(R.id.newQueue_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        return view;
    }
}