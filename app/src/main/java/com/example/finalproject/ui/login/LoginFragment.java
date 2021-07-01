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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.model.Model;


public class LoginFragment extends Fragment {

    Dialog dialog;
    LoginViewModel loginViewModel;
    View view;
    EditText userName;
    EditText password;
    Button loginBtn;
    TextView signUp;
    TextView isExistTv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Initialize params:
        view = inflater.inflate(R.layout.fragment_login, container, false);
        userName = view.findViewById(R.id.login_userName_txtE);
        password = view.findViewById(R.id.login_password_txtE);
        loginBtn=view.findViewById(R.id.login_login_btn);
        signUp=view.findViewById(R.id.login_signUp_txt);
        isExistTv = view.findViewById(R.id.login_validationText_tv);
        popupLoadingDialog();
        setUpProgressListener();
        isLoggedIn();

        //ViewModel:
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getData().observe(getViewLifecycleOwner(), (data)->{});

        //Listeners
        signUp.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_signUpFragment));
        loginBtn.setOnClickListener(v->login());


        return view;
    }

    private void isLoggedIn(){
        //If the user is still logged in:
        Model.instance.isLoggedIn(()->{
            //Pop the last login page to start from main page for connected users:
            Navigation.findNavController(view).popBackStack();

            if(Model.instance.getUser().isBarbershop) {
                Navigation.findNavController(view).navigate(R.id.nav_barbershopCalendarFragment);
                MainActivity.navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setVisible(false);
            }else{
                Navigation.findNavController(view).navigate(R.id.nav_barbershops_list_Fragment);
                MainActivity.navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setVisible(true);
            }
        });
    }

    private void login(){
        if(userName.getText().toString().isEmpty() && password.getText().toString().isEmpty())
            isExistTv.setText("Please enter a username and password");
        else if(loginViewModel.isUserExist(userName.getText().toString(), password.getText().toString()))
        {
            dialog.show();
            isExistTv.setText("");
            Model.instance.login(userName.getText().toString(), password.getText().toString(), () -> {

                //Pop up login page after we connected:
                while(Navigation.findNavController(view).popBackStack());
                dialog.dismiss();
                if (Model.instance.getUser().isBarbershop()){
                    Navigation.findNavController(view).
                            navigate(R.id.nav_barbershopCalendarFragment);
                    MainActivity.navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setVisible(false);
                }
                else{
                    Navigation.findNavController(view).
                            navigate(R.id.nav_barbershops_list_Fragment);
                    MainActivity.navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setVisible(true);
                }
            });
        }
        else
            isExistTv.setText("The username or password is incorrect");
    }

    private void setUpProgressListener() {
        Model.instance.loadingStateDialog.observe(getViewLifecycleOwner(),(state)->{
            switch(state){
                case loaded:
                   dialog.dismiss();
                    break;
                case loading:
                   dialog.show();
                    break;
                case error:
                    //...
            }
        });
    }
    private void popupLoadingDialog() {

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_dialog_loading);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.popup_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.popup_dialog_animation;
        ProgressBar pb = dialog.findViewById(R.id.loading_progressBar_pb);
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if( ((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        if( ((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
}