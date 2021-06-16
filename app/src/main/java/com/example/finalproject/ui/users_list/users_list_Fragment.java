package com.example.finalproject.ui.users_list;

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
import com.example.finalproject.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class users_list_Fragment extends Fragment {
    usersListViewModel usersList;
    MyAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        usersList  = new ViewModelProvider(this).
                get(usersListViewModel.class);

        usersList.getData().observe(getViewLifecycleOwner(),
                (data)->{
                    adapter.notifyDataSetChanged();
                });

//        data = Model.instance.getAllUsers().getValue();
//        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_list_, container, false);

        RecyclerView  stList = view.findViewById(R.id.usersList_RecyclerView);
        // :שיפור ביצועים
        stList.setHasFixedSize(true);
        // LayoutManager:
        RecyclerView.LayoutManager manager = new LinearLayoutManager(MyApplication.context);
        stList.setLayoutManager(manager);
        //Connect the Adapter to the RecyclerView:
         adapter = new MyAdapter();
        stList.setAdapter(adapter);
        //Add the onItemClickListener:
        adapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //row was selected...
//              Navigation.createNavigateOnClickListener(R.id.action_global_newStudentFragment);
                Navigation.findNavController(view).navigate(R.id.nav_userDetailsFragment);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.usersList_add_btn);
//        fab.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.));


        ProgressBar pb = view.findViewById(R.id.usersList_progressBar);
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
        TextView nameTv;
        TextView isAdminTv;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.usersListrow_Name_textView);
            isAdminTv = itemView.findViewById(R.id.usersListrow_isAdmin_textView);
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
        public void bind(User user){
            nameTv.setText(user.getName());
//            isAdminTv.setText(user.getIsAdmin());
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
            View view= getLayoutInflater().inflate(R.layout.users_list_row,parent,false);
            MyViewHolder holder= new MyViewHolder(view,listener);
            return holder;
        }
        // make the variables bind to the created view from "onCreateViewHolder" function:
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            User user = usersList.getData().getValue().get(position);
            holder.bind(user);
        }
        //Give me the items count:
        @Override
        public int getItemCount() {
            return usersList.getData().getValue().size();
        }

    }
}