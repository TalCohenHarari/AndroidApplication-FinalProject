package com.example.finalproject.ui.queues_list;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.MyApplication;
import com.example.finalproject.R;
import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;
import com.example.finalproject.model.User;
import com.example.finalproject.ui.login.LoginFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Random;


public class Queues_list_Fragment extends Fragment {


    View view;
    public static QueuesListViewModel queuesListViewModel;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton plusBtn;
    Button deleteDialogBtn;
    Button backDialogBtn;
    MyAdapter adapter;
    ProgressBar pb;
    Dialog dialog;
    String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialise Params
        view = inflater.inflate(R.layout.fragment_queues_list, container, false);
        swipeRefreshLayout = view.findViewById(R.id.myQueues_swipeRefreshLayout);
        plusBtn = view.findViewById(R.id.myQueues_add_btn);
        pb = view.findViewById(R.id.myQueues_progressBar);
        pb.setVisibility(View.GONE);

        //Get data from bundle
        if(Model.instance.getUser().isBarbershop())
            date = Queues_list_FragmentArgs.fromBundle(getArguments()).getFuulDate();

        //ViewModel
        queuesListViewModel  = new ViewModelProvider(this).
                get(QueuesListViewModel.class);
        queuesListViewModel.getData().observe(getViewLifecycleOwner(),
                (data)->{
                    if(Model.instance.getUser().isBarbershop())
                        queuesListViewModel.getFilterForBarbershop(date);
                    else
                        queuesListViewModel.getFilterForUser(Model.instance.getUser().getId());
                    adapter.notifyDataSetChanged();
                });
        queuesListViewModel.getBarbershopsList().observe(getViewLifecycleOwner(), new Observer<List<Barbershop>>() {@Override public void onChanged(List<Barbershop> barbershops) {}});
        queuesListViewModel.getUsersList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {@Override public void onChanged(List<User> users) {}});


        // RecyclerView
        RecyclerView queueList = view.findViewById(R.id.myQueues_RecyclerView);
        queueList.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(MyApplication.context);
        queueList.setLayoutManager(manager);
        adapter = new MyAdapter();
        queueList.setAdapter(adapter);

        // Listeners
        adapter.setOnClickListener(new OnItemClickListener()
        {
            @Override
            public void onClick(int position){}

            @Override
            public void onCancelClick(int position) {
                Queue queue = queuesListViewModel.list.get(position);
                if(!queue.isQueueAvailable())
                    openCancelDialog(position);
            }

            @Override
            public void onDeleteClick(int position) {
                Queue queue = queuesListViewModel.list.get(position);
                queue.setDeleted(true);
                queue.setQueueAvailable(false);
                queue.setUserId("");
                Model.instance.saveQueue(queue,()->adapter.notifyItemRemoved(position));
            }
        });

        plusBtn.setOnClickListener(v->{
            if(!(Model.instance.getUser().isBarbershop()))
                Navigation.findNavController(v).navigate(R.id.nav_barbershops_list_Fragment);
            else
                openNewQueueDialog();
        });

        swipeRefreshLayout.setOnRefreshListener(()->queuesListViewModel.refresh());
        setUpProgressListener();


        return view;
    }

    private void setUpProgressListener() {
        Model.instance.loadingState.observe(getViewLifecycleOwner(),(state)->{
            switch(state){
                case loaded:
                    pb.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case loading:
                    pb.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(true);
                    break;
                case error:
                    //TODO: display error message
            }
        });

    }

    private void openNewQueueDialog() {
        //Dialog settings:
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.barbershop_add_queue_dialog);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.popup_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.popup_dialog_animation;
        dialog.show();
        //Params:
        EditText time = dialog.findViewById(R.id.barbershop_add_queue_time);
        Button addBtn = dialog.findViewById(R.id.barbershop_add_queue_add_btn);
        Button backBtn = dialog.findViewById(R.id.barbershop_add_queue_back_btn);
        //Listeners:
        addBtn.setOnClickListener(v->addQueue(time));
        backBtn.setOnClickListener(v->dialog.dismiss());
    }

    private void addQueue(EditText time){
        Barbershop barbershop=null;
        List<Barbershop> tempList = queuesListViewModel.getBarbershopsList().getValue();
        if(!(time.getText().toString().equals("")) && tempList!=null){

            for (Barbershop b: tempList)
            {
                if(b.getOwner().equals(Model.instance.getUser().getId()))
                {
                    barbershop = b;
                    break;
                }
            }
            Queue queue = new Queue(Math.random() + "_" + barbershop.getOwner(),"",
                    Model.instance.getUser().getId(),barbershop.getName(),
                    date,time.getText().toString(),barbershop.getAddress(),
                    true, false);

            Model.instance.saveQueue(queue,()->dialog.dismiss());
        }
    }

    private void openCancelDialog(int position) {
        //Dialog settings
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_dialog_delete_queue);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.popup_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.popup_dialog_animation;
        dialog.show();

        //Initialise Params
        deleteDialogBtn = dialog.findViewById(R.id.popup_dialod_delete_btn);
        backDialogBtn = dialog.findViewById(R.id.popup_dialod_cancel_btn);

        //Listeners
        backDialogBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        deleteDialogBtn.setOnClickListener(v -> {
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
        });
    }


    static class MyViewHolder extends RecyclerView.ViewHolder{
        //Params:
        OnItemClickListener listener;
        TextView nameTv;
        TextView dateTv;
        TextView timeTv;
        TextView addressTv;
        Button cancelBtn;
        TextView isAvailable;
        TextView titleName;
        ImageButton deleteBtn;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            //Initialize params:
            nameTv = itemView.findViewById(R.id.queuesListrow_barbershopName_textView);
            titleName = itemView.findViewById((R.id.queuesListrow_Name_textView));
            dateTv = itemView.findViewById(R.id.queuesListrow_queueDate_textView);
            timeTv = itemView.findViewById(R.id.queuesListrow_queueTime_textView);
            addressTv = itemView.findViewById(R.id.queuesListrow_queueAddress_textView);
            cancelBtn = itemView.findViewById(R.id.queuesListrow_cancel_btn);
            isAvailable = itemView.findViewById(R.id.queuesListrow_isAvilable_tv);
            deleteBtn = itemView.findViewById(R.id.queuesListrow_delete_imgBtn);
            //After we created the listener we connect it to this row:
            this.listener=listener;
            itemView.setOnClickListener(v -> {
                if(listener!=null){
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onClick(position);
                    }
                }
            });
            cancelBtn.setOnClickListener(v -> {
                if(listener!=null){
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onCancelClick(position);
                    }
                }
            });
            deleteBtn.setOnClickListener(v -> {
                if(listener!=null){
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onDeleteClick(position);
                    }
                }
            });
        }

        public void bind(Queue queue) {
            if (!(Model.instance.getUser().isBarbershop())){
                titleName.setText("Barbershop:");
                nameTv.setText(queue.getBarbershopName());
                deleteBtn.setVisibility(View.INVISIBLE);
            }
            else {
                titleName.setText("Client Name:");
                String userName = queuesListViewModel.getUserName(queue.getUserId());
                nameTv.setText(userName);
                if(!queue.isQueueAvailable())
                    isAvailable.setText("Catch");
                else
                    isAvailable.setText("");
            }
            dateTv.setText(queue.getQueueDate());
            timeTv.setText(queue.getQueueTime());
            addressTv.setText(queue.getQueueAddress());
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
        void onCancelClick(int position);
        void onDeleteClick(int position);
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