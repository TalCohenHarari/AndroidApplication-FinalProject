package com.example.finalproject;

import android.text.InputType;
import android.widget.EditText;

public class Utilities {

    static int redTxtColor=MyApplication.context.getResources().getColor(R.color.design_default_color_error);
    static int whiteTextColor = MyApplication.context.getResources().getColor(R.color.myHint);

    public static void validationOn(EditText password, EditText username) {

        if(password.length()<6 &&  (username.getText().toString().isEmpty() || username.getText().toString().matches(".*\\s.*"))) {
            onValidationPassword(password);
            onValidationUserName(username);
        }
        else if(password.length()<6)
            onValidationPassword(password);
        else
            onValidationUserName(username);
    }

    public static void onValidationPassword(EditText password)
    {
        password.setBackground(MyApplication.context.getResources().getDrawable(R.drawable.input_bg_validation));
        password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.security_icon_validation, 0, 0, 0);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        password.setText("");
        password.setHint("Password must be least 6 characters");
        password.setHintTextColor(redTxtColor);
    }


    public static void offValidationPassword(EditText password) {
        if(password.getText().toString().length()>=6){
            password.setBackground(MyApplication.context.getResources().getDrawable(R.drawable.input_bg));
            password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.custom_security_icon, 0, 0, 0);
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setHint("Password");
            password.setHintTextColor(whiteTextColor);
        }
    }
    public static void onValidationUserName(EditText userName){
        userName.setBackground(MyApplication.context.getResources().getDrawable(R.drawable.input_bg_validation));
        userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.person_icon_validation, 0, 0, 0);
        if(userName.getText().toString().matches(".*\\s.*")) {
            userName.setText("");
            userName.setHint("Username must be non-spaces");
        }
        else {
            userName.setText("");
            userName.setHint("Missing Username");
        }
        userName.setHintTextColor(redTxtColor);
    }
    public static void offValidationUserName(EditText userName) {
        if(!(userName.getText().toString().isEmpty())){
            userName.setHint("Username");
            userName.setHintTextColor(whiteTextColor);
            userName.setBackground(MyApplication.context.getResources().getDrawable(R.drawable.input_bg));
            userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.custom_person_icon,0,0,0);
        }
    }
    public static void userNameExist(EditText userName){
        userName.setBackground(MyApplication.context.getResources().getDrawable(R.drawable.input_bg_validation));
        userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.person_icon_validation, 0, 0, 0);
        userName.setText("");
        userName.setHint("This Username already exist");
        userName.setHintTextColor(redTxtColor);
    }
}
