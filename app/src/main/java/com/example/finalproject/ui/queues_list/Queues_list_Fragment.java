package com.example.finalproject.ui.queues_list;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.MyApplication;
import com.example.finalproject.R;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;
import com.example.finalproject.model.User;
import com.example.finalproject.ui.login.LoginFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class Queues_list_Fragment extends Fragment {


    public QueuesListViewModel queuesListViewModel;
    MyAdapter adapter;
    Dialog dialog;
    Button cancelDialogBtn;
    Button deleteDialogBtn;
    View view;
    FloatingActionButton fab;
    String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(Model.instance.getUser().isBarbershop())
            date = Queues_list_FragmentArgs.fromBundle(getArguments()).getFuulDate();

        queuesListViewModel  = new ViewModelProvider(this).
                get(QueuesListViewModel.class);

        queuesListViewModel.getData().observe(getViewLifecycleOwner(),
                (data)->{
                    if(Model.instance.getUser().isBarbershop())
                        queuesListViewModel.getFilterDataWithDate(date);
                    else
                        queuesListViewModel.getFilterData(Model.instance.getUser().id);
                    adapter.notifyDataSetChanged();
                });

        // Inflate the layout for this fragment
       view = inflater.inflate(R.layout.fragment_queues_list, container, false);



        RecyclerView queueList = view.findViewById(R.id.myQueues_RecyclerView);
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
//              Navigation.createNavigateOnClickListener(R.id.action_global_newStudentFragment);
//                Navigation.findNavController(view).navigate(R.id.action_global_newUserFragment);
            }

            @Override
            public void onCancelClick(int position) {
                //popup  dialog:
                Queue queue = queuesListViewModel.list.get(position);
                if(!queue.isQueueAvailable)
                    openDialog(position);
            }
        });


        ProgressBar pb = view.findViewById(R.id.myQueues_progressBar);
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

    private void openDialog(int position) {

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_dialog_delete_queue);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.popup_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.popup_dialog_animation;


        deleteDialogBtn = dialog.findViewById(R.id.popup_dialod_delete_btn);
        cancelDialogBtn = dialog.findViewById(R.id.popup_dialod_cancel_btn);

        cancelDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        deleteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Queue queue = queuesListViewModel.list.get(position);
                queue.setQueueAvailable(true);
                queue.userId="";
                Model.instance.saveQueue(queue,()->{
                    if(!Model.instance.getUser().isBarbershop)
                        adapter.notifyItemRemoved(position);
                    else
                        adapter.notifyItemChanged(position);
                });
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder{
        OnItemClickListener listener;
        TextView nameTv;
        TextView dateTv;
        TextView timeTv;
        TextView addressTv;
        Button cancelBtn;
        TextView isAvailable;
        TextView titleName;
        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.queuesListrow_barbershopName_textView);
            titleName = itemView.findViewById((R.id.queuesListrow_Name_textView));
            dateTv = itemView.findViewById(R.id.queuesListrow_queueDate_textView);
            timeTv = itemView.findViewById(R.id.queuesListrow_queueTime_textView);
            addressTv = itemView.findViewById(R.id.queuesListrow_queueAddress_textView);
            cancelBtn = itemView.findViewById(R.id.queuesListrow_cancel_btn);
            isAvailable = itemView.findViewById(R.id.queuesListrow_isAvilable_tv);
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
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onCancelClick(position);
                        }
                    }
                }
            });
        }
        public void bind(Queue queue) {
            if (!(Model.instance.getUser().isBarbershop())){
                titleName.setText("Barbershop:");
                nameTv.setText(queue.getBarbershopName());
            }
            else {
                List<User> userList = Model.instance.getAllUsers().getValue();
                titleName.setText("Client Name:");
                for (User user:userList)
                    if(user.getId().equals(queue.getUserId()))
                        nameTv.setText(user.getName());
            }
            dateTv.setText(queue.getQueueDate());
            timeTv.setText(queue.getQueueTime());
            addressTv.setText(queue.getQueueAddress());
            if(Model.instance.getUser().isBarbershop && !queue.isQueueAvailable)
                isAvailable.setText("This queue is busy");
            else
                isAvailable.setText("");
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
        void onCancelClick(int position);
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
            View view= getLayoutInflater().inflate(R.layout.qeues_list_row,parent,false);
            MyViewHolder holder= new MyViewHolder(view,listener);
            return holder;
        }
        // make the variables bind to the created view from "onCreateViewHolder" function:
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Queue queue = queuesListViewModel.list.get(position);
            holder.bind(queue);
        }
        //Give me the items count:
        @Override
        public int getItemCount() {
            return queuesListViewModel.list.size();
        }

    }
}