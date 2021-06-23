package com.example.finalproject.ui.barbershop_Calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.finalproject.R;


public class BarbershopCalendarFragment extends Fragment {

    View view;
    CalendarView calendarView;
    String fullDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initialise Params
        view = inflater.inflate(R.layout.fragment_barbershop_calendar, container, false);
        calendarView = view.findViewById(R.id.barbershopCalendar_calendarView);
        fullDate="";

        //Listeners
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fullDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            BarbershopCalendarFragmentDirections.ActionNavBarbershopCalendarFragmentToNavQueuesListFragment
                    action = BarbershopCalendarFragmentDirections.actionNavBarbershopCalendarFragmentToNavQueuesListFragment(fullDate);
            Navigation.findNavController(view).navigate(action);
        });

        return view;
    }
}