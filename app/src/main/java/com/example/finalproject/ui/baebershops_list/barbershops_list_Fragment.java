package com.example.finalproject.ui.baebershops_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.finalproject.MyApplication;
import com.example.finalproject.R;
import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.squareup.picasso.Picasso;


public class barbershops_list_Fragment extends Fragment {

    View view;
    BarbershopsListViewModel barbershopsListViewModel;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView clickMeTv;
    MyAdapter adapter;
    ProgressBar pb;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initialise Params
        view = inflater.inflate(R.layout.fragment_barbershops_list_, container, false);
        clickMeTv = view.findViewById(R.id.barbershopsList_seeAllBarbershopsOnMap_tv);
        swipeRefreshLayout = view.findViewById(R.id.barbershopsList_swipeRefreshLayout);
        pb = view.findViewById(R.id.barberShopsList_progressBar);
        pb.setVisibility(View.GONE);

        //BarbershopsList ViewModel
        barbershopsListViewModel  = new ViewModelProvider(this).
                get(BarbershopsListViewModel.class);
        barbershopsListViewModel.getData().observe(getViewLifecycleOwner(),
                (data)->{
                    barbershopsListViewModel.list=data;
                    adapter.notifyDataSetChanged();
                });

        //RecyclerView:
        RecyclerView  barbershopsList = view.findViewById(R.id.barberShopsList__RecyclerView);
        barbershopsList.setHasFixedSize(true);
//        GridLayoutManager manager = new GridLayoutManager(MyApplication.context,2,GridLayoutManager.VERTICAL,false);
        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.context);
        barbershopsList.setLayoutManager(manager);
         adapter = new MyAdapter();
        barbershopsList.setAdapter(adapter);

        //Listeners
        adapter.setOnClickListener((position) -> {
            barbershops_list_FragmentDirections.ActionBarbershopsListFragmentToBarbershopDetailsFragment
                    action = barbershops_list_FragmentDirections.actionBarbershopsListFragmentToBarbershopDetailsFragment(position);
            Navigation.findNavController(view).navigate(action);
        });
        clickMeTv.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_barbershopsOnMapFragment));
        swipeRefreshLayout.setOnRefreshListener(()->barbershopsListViewModel.refresh());
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
                    //...
            }
        });
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        OnItemClickListener listener;
        TextView nameTv;
        ImageView imageV;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            imageV = itemView.findViewById(R.id.barbershopsListrow_imagev);
            nameTv = itemView.findViewById(R.id.barbershopsListrow_Name_textView);

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
        }

        public void bind(Barbershop barbershop){
            nameTv.setText(barbershop.getName());
            imageV.setImageResource(R.drawable.avatar);
            if(barbershop.avatar!=null && !barbershop.avatar.equals("")){
                //Give us from picasso code our image and fix a lot of things:
                Picasso.get().load(barbershop.avatar).placeholder(R.drawable.avatar).into(imageV);
            }
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
            View view= getLayoutInflater().inflate(R.layout.barbershops_list_row,parent,false);
            MyViewHolder holder= new MyViewHolder(view,listener);
            return holder;
        }
        // make the variables bind to the created view from "onCreateViewHolder" function:
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Barbershop barbershop = barbershopsListViewModel.list.get(position);
            holder.bind(barbershop);
        }
        //Give me the items count:
        @Override
        public int getItemCount() {
                return barbershopsListViewModel.list.size();
        }
    }
}