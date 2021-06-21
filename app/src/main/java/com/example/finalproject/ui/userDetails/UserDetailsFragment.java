package com.example.finalproject.ui.userDetails;

import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.User;
import com.example.finalproject.ui.barbershopDetails.BarbershopDetailsViewModel;
import com.example.finalproject.ui.login.LoginFragment;
import com.example.finalproject.ui.queues_list.QueuesListViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserDetailsFragment extends Fragment {


    UserDetailsViewModel userDetailsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        ImageButton editImgBtn = view.findViewById(R.id.userDetails_edit_imageButton);
        ImageView barberIcon = view.findViewById(R.id.userDetails_barberIcon_imgV);
        TextView userName = view.findViewById(R.id.userDetails_username_tv);
        TextView phone = view.findViewById(R.id.userDetails_phone_tv);
        TextView email = view.findViewById(R.id.userDetails_email_tv);
        TextView password = view.findViewById(R.id.userDetails_password_tv);
        Button backBtn = view.findViewById(R.id.userDetails_back_btn);
        ImageView deleteBtn = view.findViewById(R.id.userDetails_delete_btn);
        CircleImageView image = view.findViewById(R.id.userDetails_image_imgV);

        userDetailsViewModel  = new ViewModelProvider(this).
                get(UserDetailsViewModel.class);

        userName.setText(Model.instance.getUser().name);
        password.setText(Model.instance.getUser().password);
        phone.setText(Model.instance.getUser().phone);
        email.setText(Model.instance.getUser().email);
        image.setImageResource(R.drawable.avatar);
        if(Model.instance.getUser().getAvatar()!=null && !(Model.instance.getUser().getAvatar().equals("")))
            Picasso.get().load(Model.instance.getUser().avatar).into(image);



        if(!(Model.instance.getUser().isBarbershop()))
            barberIcon.setVisibility(View.INVISIBLE);

        barberIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer position = userDetailsViewModel.geFilterByPosition(Model.instance.getUser().id);
                UserDetailsFragmentDirections.ActionNavUserDetailsFragmentToNavBarbershopDetailsFragment2
                        action = UserDetailsFragmentDirections.actionNavUserDetailsFragmentToNavBarbershopDetailsFragment2(position);
                Navigation.findNavController(v).navigate(action);
            }
        });

        editImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserDetailsFragmentDirections.ActionNavUserDetailsFragmentToEditUserFragment action = UserDetailsFragmentDirections.
                        actionNavUserDetailsFragmentToEditUserFragment
                                (userName.getText().toString(),email.getText().toString(),
                                phone.getText().toString(),password.getText().toString());

                Navigation.findNavController(v).navigate(action);
            }
        });

        deleteBtn.setOnClickListener(v->{
            if(!(Model.instance.getUser().isBarbershop()))
            {
                Model.instance.getUser().setAvailable(false);
                Model.instance.saveUser(Model.instance.getUser(),"delete",()->{
                    Model.instance.signOut();
                    while(MainActivity.navController.popBackStack());
                    Navigation.findNavController(v).navigate(R.id.nav_login);
                });
            }
        });
        backBtn.setOnClickListener(v->Navigation.findNavController(v).navigateUp());

        return view;
    }
}