package com.example.finalproject.ui.login;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.Utilities;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.User;


public class LoginFragment extends Fragment {

    public static Dialog dialog;
    public LoginViewModel loginViewModel;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //ViewModel:
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getData().observe(getViewLifecycleOwner(), (data)->{});

        view = inflater.inflate(R.layout.fragment_login, container, false);

        popupLoadingDialog();

        //Disable return toolBar Btn:
        if( MainActivity.actionBar!=null)
            MainActivity.actionBar.setDisplayHomeAsUpEnabled(false);

        //Initialize params:
        EditText userName = view.findViewById(R.id.login_userName_txtE);
        EditText password = view.findViewById(R.id.login_password_txtE);
        Button login=view.findViewById(R.id.login_login_btn);
        TextView signUp=view.findViewById(R.id.login_signUp_txt);
        TextView isExistTv = view.findViewById(R.id.login_validationText_tv);
        signUp.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.nav_signUpFragment));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().toString().isEmpty() && password.getText().toString().isEmpty())
                   isExistTv.setText("Please enter a username and password");
                else if(LoginViewModel.isUserExist(userName.getText().toString(), password.getText().toString()))
                {
                    dialog.show();
                    isExistTv.setText("");
                    Model.instance.login(userName.getText().toString(),
                            password.getText().toString(),
                            () -> {

                                //Pop up login page after we connected:
                                Navigation.findNavController(v).popBackStack();
                                dialog.dismiss();
                                if (Model.instance.getUser().isBarbershop()){
                                    Navigation.findNavController(v).
                                            navigate(R.id.nav_barbershopCalendarFragment);
                                    MainActivity.navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setVisible(false);
                                }
                                else{
                                    Navigation.findNavController(v).
                                            navigate(R.id.nav_barbershops_list_Fragment);
                                    MainActivity.navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setVisible(true);
                                }
                            });
                }
                else
                    isExistTv.setText("The username or password is incorrect");
            }
        });

        return view;
    }

    private void popupLoadingDialog() {

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_dialog_loading);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.popup_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.popup_dialog_animation;
        ProgressBar pb = dialog.findViewById(R.id.loading_progressBar_pb);
        pb.setVisibility(View.VISIBLE);
    }

}