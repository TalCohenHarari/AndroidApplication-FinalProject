package com.example.finalproject.ui.baebershops_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.MyApplication;
import com.example.finalproject.R;
import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;


public class barbershops_list_Fragment extends Fragment {
    barbershopsListViewModel barbershopsListViewModel;
    MyAdapter adapter;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        barbershopsListViewModel  = new ViewModelProvider(this).
                get(barbershopsListViewModel.class);

        barbershopsListViewModel.getData().observe(getViewLifecycleOwner(),
                (data)->{
                    adapter.notifyDataSetChanged();
                });

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_barbershops_list_, container, false);

        RecyclerView  barbershopsList = view.findViewById(R.id.barberShopsList__RecyclerView);
        // :שיפור ביצועים
        barbershopsList.setHasFixedSize(true);
        // LayoutManager:
//        GridLayoutManager manager = new GridLayoutManager(MyApplication.context,2,GridLayoutManager.VERTICAL,false);
        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.context);
        barbershopsList.setLayoutManager(manager);
        //Connect the Adapter to the RecyclerView:
         adapter = new MyAdapter();
        barbershopsList.setAdapter(adapter);
        //Add the onItemClickListener:
        adapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //row was selected and we add arguments between the fragments:
                barbershops_list_FragmentDirections.ActionBarbershopsListFragmentToBarbershopDetailsFragment
                        action = barbershops_list_FragmentDirections.actionBarbershopsListFragmentToBarbershopDetailsFragment(position);
                Navigation.findNavController(view).navigate(action);
            }

            @Override
            public void onDeleteClick(int position) {
//                Barbershop barbershop = barbershopsListViewModel.getData().getValue().get(position);
//                Model.instance.deleteBarbershop(barbershop,()->{});
//                adapter.notifyItemRemoved(position);
            }
        });



        ProgressBar pb = view.findViewById(R.id.barberShopsList_progressBar);
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
        ImageView imageV;
//        ImageView deleteBtn;
        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            imageV = itemView.findViewById(R.id.barbershopsListrow_imagev);
            nameTv = itemView.findViewById(R.id.barbershopsListrow_Name_textView);
//            deleteBtn = itemView.findViewById(R.id.barbershopsListrow_delete_btn);
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

//            deleteBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(listener!=null){
//                        int position=getAdapterPosition();
//                        if(position!=RecyclerView.NO_POSITION){
//                            listener.onDeleteClick(position);
//                        }
//                    }
//                }
//            });


        }
        public void bind(Barbershop barbershop){
            nameTv.setText(barbershop.getName());
            imageV.setImageResource(R.drawable.avatar);
            if(barbershop.avatar!=null && barbershop.avatar!=""){
                //Give us from picasso code our image and fix a lot of things:
                Picasso.get().load(barbershop.avatar).placeholder(R.drawable.avatar).into(imageV);
            }
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
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
            View view= getLayoutInflater().inflate(R.layout.barbershops_list_row,parent,false);
            MyViewHolder holder= new MyViewHolder(view,listener);
            return holder;
        }
        // make the variables bind to the created view from "onCreateViewHolder" function:
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Barbershop barbershop = barbershopsListViewModel.getData().getValue().get(position);
            holder.bind(barbershop);
        }
        //Give me the items count:
        @Override
        public int getItemCount() {
            return barbershopsListViewModel.getData().getValue().size();
        }

    }
}