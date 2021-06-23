package com.example.finalproject.ui.barbershopDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.finalproject.R;
import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BarbershopDetailsFragment extends Fragment {

    BarbershopDetailsViewModel barbershopDetailsViewModel;
    Barbershop barbershop;
    Integer barbershopIdPosition;
    CircleImageView imageV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initialize Params
        View view = inflater.inflate(R.layout.fragment_baebershop_details, container, false);
        TextView nameTv = view.findViewById(R.id.baebershopDeails_name);
        TextView addressTv = view.findViewById(R.id.baebershopDeails_address);
        TextView phoneTv = view.findViewById(R.id.baebershopDeails_phoneNumber);
        Button addBtn = view.findViewById(R.id.baebershopDeails_add_an_appointment_btn);
        Button backBtn = view.findViewById(R.id.baebershopDeails_back_btn);
        ImageView editBtn = view.findViewById(R.id.baebershopDeails_edit);
        imageV = view.findViewById(R.id.baebershopDeails_image_imgV);

        //ViewModel
        barbershopDetailsViewModel  = new ViewModelProvider(this).
                get(BarbershopDetailsViewModel.class);
        barbershopDetailsViewModel.getData().observe(getViewLifecycleOwner(), (data)->{
            // Getting the parameters from the Bundle:
            barbershopIdPosition = BarbershopDetailsFragmentArgs.fromBundle(getArguments()).getBaebershopID();

            barbershop = data.get(barbershopIdPosition);
            nameTv.setText(barbershop.getName());
            addressTv.setText(barbershop.getAddress());
            phoneTv.setText(barbershop.getPhone());
            if(barbershop.getAvatar()!=null && !(barbershop.getAvatar().equals("")))
                Picasso.get().load(barbershop.getAvatar()).into(imageV);

            if(Model.instance.getUser().isBarbershop() && Model.instance.getUser().getId().equals(barbershop.getOwner()))
                addBtn.setVisibility(View.INVISIBLE);
            else
                editBtn.setVisibility(View.INVISIBLE);
        });

        //Listeners
        backBtn.setOnClickListener(v->Navigation.findNavController(v).navigateUp());
        editBtn.setOnClickListener(v ->Navigation.findNavController(v).navigate(R.id.nav_editBarbershopFragment));
        addBtn.setOnClickListener(v -> {
            BarbershopDetailsFragmentDirections.ActionBarbershopDetailsFragmentToNewQueueFragment
                    action = BarbershopDetailsFragmentDirections.actionBarbershopDetailsFragmentToNewQueueFragment(barbershop.owner);
            Navigation.findNavController(view).navigate(action);
       });

        return view;
    }
}