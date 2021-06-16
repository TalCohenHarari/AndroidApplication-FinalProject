package com.example.finalproject.ui.edit_user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.finalproject.R;
import com.example.finalproject.Utilities;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.User;
import com.example.finalproject.ui.login.LoginFragment;
import com.squareup.picasso.Picasso;


public class EditUserFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);
        EditText nameEt = view.findViewById(R.id.editUser_name_et);
        EditText emailEt = view.findViewById(R.id.editUser_email_et);
        EditText phoneEt = view.findViewById(R.id.editUser_phone_et);
        EditText passwordEt = view.findViewById(R.id.editUser_password_et);
        Button backBtn = view.findViewById(R.id.editUser_back_btn);
        Button saveBtn = view.findViewById(R.id.editUser_save_btn);
        ImageView imageV = view.findViewById(R.id.editUser_iamgev);

        ProgressBar pb = view.findViewById(R.id.editUser_progressBar);
        pb.setVisibility(View.INVISIBLE);

        String userName = EditUserFragmentArgs.fromBundle(getArguments()).getUserName();
        String phone = EditUserFragmentArgs.fromBundle(getArguments()).getPhone();
        String email = EditUserFragmentArgs.fromBundle(getArguments()).getEmail();
        String password = EditUserFragmentArgs.fromBundle(getArguments()).getPassword();

        nameEt.setText(userName);
        phoneEt.setText(phone);
        emailEt.setText(email);
        passwordEt.setText(password);
        passwordEt.setEnabled(false);

        if(Model.instance.getUser().getAvatar()!=null && !(Model.instance.getUser().getAvatar().equals("")))
            Picasso.get().load(Model.instance.getUser().avatar).into(imageV);


        nameEt.setOnKeyListener((v,keyCode,event)->{Utilities.offValidationUserName(nameEt);return false;});
        passwordEt.setOnKeyListener((v,keyCode,event)->{Utilities.offValidationPassword(passwordEt);return false;});


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEt.getText().toString().length() >= 6 &&
                        !(nameEt.getText().toString().isEmpty()) &&
                        !(nameEt.getText().toString().matches(".*\\s.*"))) {
                    pb.setVisibility(View.VISIBLE);
                    saveBtn.setEnabled(false);

                    User user = Model.instance.getUser();
                    user.setName(nameEt.getText().toString());
                    user.setEmail(emailEt.getText().toString());
                    user.setPhone(phoneEt.getText().toString());
                    user.setPassword(passwordEt.getText().toString());

                    Model.instance.saveUser(user, "updateEmail", () -> {
                        Navigation.findNavController(v).navigateUp();
                    });
                }
                else
                    Utilities.validationOn(passwordEt,nameEt);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        return view;
    }
}