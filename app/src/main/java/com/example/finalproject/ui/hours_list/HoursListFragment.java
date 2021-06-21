package com.example.finalproject.ui.hours_list;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.MyApplication;
import com.example.finalproject.R;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;


public class HoursListFragment extends Fragment {

    hoursListViewModel hoursListViewModel;
    public static String fullDate;
    public static String barbershopId;
    MyAdapter adapter;
    TextView dateTv;
    RecyclerView queueList;
    ProgressBar pb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        fullDate = HoursListFragmentArgs.fromBundle(getArguments()).getDate();
        barbershopId = HoursListFragmentArgs.fromBundle(getArguments()).getBarbershopId();

        hoursListViewModel  = new ViewModelProvider(this).
                get(hoursListViewModel.class);

        hoursListViewModel.getData().observe(getViewLifecycleOwner(),
                (data)->{
                    hoursListViewModel.getFilterData(fullDate,barbershopId);
                    adapter.notifyDataSetChanged();
                });


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hours_list, container, false);

        dateTv = view.findViewById(R.id.hoursList_selectedDate_tv);
        dateTv.setText(fullDate);

        queueList = view.findViewById(R.id.hoursList_RecyclerView);
        // :שיפור ביצועים
        queueList.setHasFixedSize(true);
        // LayoutManager:
        RecyclerView.LayoutManager manager = new LinearLayoutManager(MyApplication.context);
        queueList.setLayoutManager(manager);
        //Connect the Adapter to the RecyclerView:
        adapter = new MyAdapter();
        queueList.setAdapter(adapter);
        //Add the onItemClickListener:
        adapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //row was selected...
                Queue queue = hoursListViewModel.list.get(position);
                if(queue.isQueueAvailable) {
                    queue.setQueueAvailable(false);
                    queue.userId= Model.instance.getUser().getId();
                    Model.instance.saveQueue(queue,()->{
                        Navigation.findNavController(view).navigate(R.id.nav_queues_list_Fragment);
                    });
                }
            }
        });

        pb = view.findViewById(R.id.hoursList_progressBar);
        pb.setVisibility(View.GONE);

        Model.instance.loadingState.observe(getViewLifecycleOwner(),(state)->{
            switch(state){
                case loaded:
                    pb.setVisibility(View.GONE);
                    break;
                case loading:
                    pb.setVisibility(View.VISIBLE);
                    break;
                case error:
                    //TODO: display error message
            }
        });

        return view;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        OnItemClickListener listener;
        TextView timeTv;
        TextView isQueueAvailableTv;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.hoursListRow_hour_tv);
            isQueueAvailableTv = itemView.findViewById(R.id.hoursListRow_isAvailable_tv);
            //After we created the listener we connect it to this row:
            this.listener=listener;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onClick(position);
                        }
                    }
                }
            });
        }
        public void bind(Queue queue){
            timeTv.setText(queue.getQueueTime());
            if(!queue.isQueueAvailable)
                isQueueAvailableTv.setText("Not Available");
            else
                isQueueAvailableTv.setText("");
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        OnItemClickListener listener;

        public void setOnClickListener(OnItemClickListener listener){
            this.listener=listener;
        }
        //Create a viewHolder for the view:
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= getLayoutInflater().inflate(R.layout.hours_list_row,parent,false);
            MyViewHolder holder= new MyViewHolder(view,listener);
            return holder;
        }
        // make the variables bind to the created view from "onCreateViewHolder" function:
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Queue queue = hoursListViewModel.list.get(position);
            holder.bind(queue);
        }
        //Give me the items count:
        @Override
        public int getItemCount() {
            return hoursListViewModel.list.size();
        }

    }
}