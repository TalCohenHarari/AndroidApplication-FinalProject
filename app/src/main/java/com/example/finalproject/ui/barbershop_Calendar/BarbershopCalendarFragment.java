package com.example.finalproject.ui.barbershop_Calendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.ui.new_queue.new_queue_FragmentDirections;


public class BarbershopCalendarFragment extends Fragment {

    View view;
    CalendarView calendarView;
    String fullDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_barbershop_calendar, container, false);
        calendarView = view.findViewById(R.id.barbershopCalendar_calendarView);
        fullDate="";

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                fullDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                BarbershopCalendarFragmentDirections.ActionNavBarbershopCalendarFragmentToNavQueuesListFragment
                        action = BarbershopCalendarFragmentDirections.actionNavBarbershopCalendarFragmentToNavQueuesListFragment(fullDate);

                Navigation.findNavController(view).navigate(action);
            }
        });

        return view;
    }
}