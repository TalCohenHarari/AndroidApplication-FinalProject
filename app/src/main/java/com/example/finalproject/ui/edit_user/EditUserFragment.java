package com.example.finalproject.ui.edit_user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.finalproject.R;
import com.example.finalproject.Utilities;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.User;
import com.example.finalproject.ui.login.LoginFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditUserFragment extends Fragment {

    CircleImageView imageV;
    Bitmap imageBitmap;
    ImageButton galleryImgB;
    ImageButton cameraImgB;
    View view;
    EditText nameEt;
    EditText emailEt;
    EditText phoneEt;
    EditText passwordEt;
    Button backBtn;
    Button saveBtn;
    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialise Params
        view = inflater.inflate(R.layout.fragment_edit_user, container, false);
        nameEt = view.findViewById(R.id.editUser_name_et);
        emailEt = view.findViewById(R.id.editUser_email_et);
        phoneEt = view.findViewById(R.id.editUser_phone_et);
        passwordEt = view.findViewById(R.id.editUser_password_et);
        backBtn = view.findViewById(R.id.editUser_back_btn);
        saveBtn = view.findViewById(R.id.editUser_save_btn);
        imageV = view.findViewById(R.id.editUser_iamgev);
        galleryImgB =view.findViewById(R.id.editUser_imageGalleryBtn);
        cameraImgB=view.findViewById(R.id.editUser_imageCameraBtn);
        pb = view.findViewById(R.id.editUser_progressBar);
        pb.setVisibility(View.INVISIBLE);

        //Args from bundle
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

        //Listeners
        nameEt.setOnKeyListener((v,keyCode,event)->{Utilities.offValidationUserName(nameEt);return false;});
        passwordEt.setOnKeyListener((v,keyCode,event)->{Utilities.offValidationPassword(passwordEt);return false;});
        backBtn.setOnClickListener(v->Navigation.findNavController(v).navigateUp());
        cameraImgB.setOnClickListener(v->takePicture());
        galleryImgB.setOnClickListener(v->takePictureFromGallery());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEt.getText().toString().length() >= 6 &&
                        !(nameEt.getText().toString().isEmpty()) &&
                        !(nameEt.getText().toString().matches(".*\\s.*"))) {
                  save();
                }
                else
                    Utilities.validationOn(passwordEt,nameEt);
            }
        });

        return view;
    }

    //----------------------------------Save------------------------------------------------
    void save()
    {
        saveBtn.setEnabled(false);
        cameraImgB.setEnabled(false);
        galleryImgB.setEnabled(false);

        if(imageBitmap!=null)
        {
            Model.instance.uploadImage(imageBitmap,Model.instance.getUser().getId(),
                    "user", new Model.UpLoadImageListener() {
                        @Override
                        public void onComplete(String url) {
                            saveUser(url);
                        }
                    });
        }
        else
            saveUser(null);

    }

    void saveUser(String url)
    {
        pb.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);

        User user = Model.instance.getUser();

        String actionOnSave="";
        if(user.getName().equals(nameEt.getText().toString()))
            actionOnSave="update";
        else
            actionOnSave="updateEmail";

        user.setName(nameEt.getText().toString());
        user.setEmail(emailEt.getText().toString());
        user.setPhone(phoneEt.getText().toString());
        user.setPassword(passwordEt.getText().toString());


        if(url!=null)
            user.setAvatar(url);
        else if(user.getAvatar()==null || user.getAvatar().equals(""))
            user.setAvatar("");


        Model.instance.saveUser(user, actionOnSave, () -> {
            Navigation.findNavController(view).navigateUp();
        });

    }
    //---------------------------------Image-------------------------------------------------
    //This is a sign for us to know from where activity we back to onActivityResult function:
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int GALLERY_IMAGE = 0;
    final static int RESULT_SUCCESS = -1;
    void takePicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        //start the activity camera:
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
    void takePictureFromGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_IMAGE);
    }

    //GETTING THE RESULT FROM THE OTHER ACTIVITY:
    // (data is a faw parameters and the image we take).
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If we back from camera:
        if(requestCode==REQUEST_IMAGE_CAPTURE)
        {
            if(resultCode == RESULT_SUCCESS)
            {
                //Bundle is some map with key & value.
                Bundle extras = data.getExtras();
                // Under the key data we have the image (BUT IN LOW QUALITY).
                // Bitmap is pixels of the image.
                imageBitmap = (Bitmap) extras.get("data");
                imageV.setImageBitmap(imageBitmap);
            }
        }
        else if(requestCode == GALLERY_IMAGE)
        {
            if(resultCode == RESULT_SUCCESS)
            {
                Uri selectedImage = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                } catch (IOException e) {}
                imageV.setImageBitmap(imageBitmap);
            }
        }
    }
}