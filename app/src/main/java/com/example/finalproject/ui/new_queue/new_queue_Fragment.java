package com.example.finalproject.ui.new_queue;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.finalproject.R;


public class new_queue_Fragment extends Fragment {

    View view;
    CalendarView calendarView;
    String barbershopId;
    String fullDate;
    Button backBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initialise Params
        view = inflater.inflate(R.layout.fragment_new_queue_, container, false);
        calendarView = view.findViewById(R.id.newQueue_calander_calendarView);
        backBtn = view.findViewById(R.id.newQueue_back_btn);
        fullDate="";

        //Get data from bundle
        barbershopId = new_queue_FragmentArgs.fromBundle(getArguments()).getBaebershopId();

        //Listeners
        backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fullDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//                if(!fullDate.equals("")){
                new_queue_FragmentDirections.ActionNavNewQueueToHoursListFragment
                        action = new_queue_FragmentDirections.actionNavNewQueueToHoursListFragment(fullDate, barbershopId);
                Navigation.findNavController(view).navigate(action);
//            }
        });

        return view;
    }
}