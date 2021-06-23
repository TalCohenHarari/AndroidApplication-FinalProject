package com.example.finalproject.ui.userDetails;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.finalproject.R;
import com.example.finalproject.model.Model;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserDetailsFragment extends Fragment {

    View view;
    UserDetailsViewModel userDetailsViewModel;
    Dialog dialog;
    ImageButton editImgBtn;
    ImageView barberIcon;
    TextView userName;
    TextView phone;
    TextView email;
    TextView password;
    Button backBtn;
    ImageView deleteBtn;
    CircleImageView image;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialise Params
        view = inflater.inflate(R.layout.fragment_user_details, container, false);
        editImgBtn = view.findViewById(R.id.userDetails_edit_imageButton);
        barberIcon = view.findViewById(R.id.userDetails_barberIcon_imgV);
        userName = view.findViewById(R.id.userDetails_username_tv);
        phone = view.findViewById(R.id.userDetails_phone_tv);
        email = view.findViewById(R.id.userDetails_email_tv);
        password = view.findViewById(R.id.userDetails_password_tv);
        backBtn = view.findViewById(R.id.userDetails_back_btn);
        deleteBtn = view.findViewById(R.id.userDetails_delete_btn);
        image = view.findViewById(R.id.userDetails_image_imgV);

        //ViewModel
        userDetailsViewModel  = new ViewModelProvider(this).
                get(UserDetailsViewModel.class);
        userDetailsViewModel.getData().observe(getViewLifecycleOwner(), (data)->{});

        //Set visibility
        userName.setText(Model.instance.getUser().name);
        password.setText(Model.instance.getUser().password);
        phone.setText(Model.instance.getUser().phone);
        email.setText(Model.instance.getUser().email);
        image.setImageResource(R.drawable.avatar);
        if(Model.instance.getUser().getAvatar()!=null && !(Model.instance.getUser().getAvatar().equals("")))
            Picasso.get().load(Model.instance.getUser().avatar).into(image);

        if(!(Model.instance.getUser().isBarbershop()))
            barberIcon.setVisibility(View.INVISIBLE);

        //Listeners
        deleteBtn.setOnClickListener(v->deleteUserDialog());
        backBtn.setOnClickListener(v->Navigation.findNavController(v).navigateUp());
        barberIcon.setOnClickListener(v -> {
            Integer position = userDetailsViewModel.geFilterByPosition(Model.instance.getUser().id);
            UserDetailsFragmentDirections.ActionNavUserDetailsFragmentToNavBarbershopDetailsFragment2
                    action = UserDetailsFragmentDirections.actionNavUserDetailsFragmentToNavBarbershopDetailsFragment2(position);
            Navigation.findNavController(v).navigate(action);
        });

        editImgBtn.setOnClickListener(v -> {
            UserDetailsFragmentDirections.ActionNavUserDetailsFragmentToEditUserFragment
                    action = UserDetailsFragmentDirections.actionNavUserDetailsFragmentToEditUserFragment
                            (userName.getText().toString(),email.getText().toString(),
                            phone.getText().toString(),password.getText().toString());
            Navigation.findNavController(v).navigate(action);
        });

        return view;
    }

    private void deleteUserDialog() {
        //Dialog settings:
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_dialog_delete_user);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.popup_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.popup_dialog_animation;
        dialog.show();
        //Initialise Params
        Button deleteDialogBtn = dialog.findViewById(R.id.popup_dialod_delete_user_btn);
        Button backDialogBtn = dialog.findViewById(R.id.popup_dialod_back_user_btn);
        //Listeners:
        backDialogBtn.setOnClickListener(v->{
                Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
        });
        deleteDialogBtn.setOnClickListener(v->deleteUser());
    }

    public void deleteUser(){
        if(!(Model.instance.getUser().isBarbershop()))
        {
            Model.instance.getUser().setAvailable(false);
            Model.instance.saveUser(Model.instance.getUser(),"delete",()->{
                dialog.dismiss();
                Model.instance.signOut();
                while(Navigation.findNavController(view).popBackStack());
                Navigation.findNavController(view).navigate(R.id.nav_login);
            });
        }
    }
}