package com.example.finalproject.ui.signUp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.Utilities;
import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;
import com.example.finalproject.model.User;
import com.example.finalproject.ui.queues_list.QueuesListViewModel;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class SignUpFragment extends Fragment {

    //General:
    View view;
    Dialog dialog;
    TextView haveAccount;
    Button signUp;
    ImageButton addBarbershop;
    ImageView imageCameraImgV;
    ImageView imageGalleryImgV;
    SignUpViewModel signUpViewModel;
    //User params:
    User newUser;
    EditText password;
    EditText userName;
    EditText email;
    EditText phone;
    ImageView imageV;
    Bitmap imageBitmap;

    //Barbershop Params:
    Dialog dialogBarbershop;
    EditText barbershopNameEt;
    EditText barbershopAddressEt;
    EditText barbershopPhoneEt;
    ImageButton barbershopImageCameraBtn;
    ImageButton barbershopImageGalleryBtn;
    ImageView barbershopImageV;
    Bitmap barbershopImageBitmap;
    Button barbershopAddBtn;
    Button barbershopBackBtn;
    Barbershop barbershop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        //Disable return toolBar Btn:
        if( MainActivity.actionBar!=null)
            MainActivity.actionBar.setDisplayHomeAsUpEnabled(false);

        //The fragment viewModel:
        signUpViewModel  = new ViewModelProvider(this).get(SignUpViewModel.class);

        //Initialize the params:
        haveAccount = view.findViewById(R.id.signUp_already_tv);
        signUp = view.findViewById(R.id.signup_signup_btn);
        addBarbershop = view.findViewById(R.id.signUp_add_Barbershop_imgBtn);
        userName = view.findViewById(R.id.signUp_userName_txtE);
        email = view.findViewById(R.id.signUp_email_txtE);
        phone = view.findViewById(R.id.signUp_phone_txtE);
        password = view.findViewById(R.id.signUp_password_txtE);
        imageCameraImgV = view.findViewById(R.id.signUp_camera_imgV);
        imageGalleryImgV = view.findViewById(R.id.signUp_gallery_imgV);
        imageV = view.findViewById(R.id.signUp_userImage_imgV);

        //Listeners:
        addBarbershop.setOnClickListener(v->openDialog());
        imageCameraImgV.setOnClickListener(v->takePicture("user"));
        imageGalleryImgV.setOnClickListener(v->takePictureFromGallery("user"));
        haveAccount.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_login));
        userName.setOnKeyListener((v,keyCode,event)->{Utilities.offValidationUserName(userName);return false;});
        password.setOnKeyListener((v,keyCode,event)->{Utilities.offValidationPassword(password);return false;});
        popupLoadingDialog();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(signUpViewModel.isUserNameExist(userName.getText().toString()))
                    Utilities.userNameExist(userName);
                else if(password.getText().toString().length()>=6 &&
                        !(userName.getText().toString().isEmpty()) &&
                        !(userName.getText().toString().matches(".*\\s.*"))){
                        dialog.show();
                        signUp.setEnabled(false);
                        save();
                }
                else
                    Utilities.validationOn(password,userName);

            }
        });

        return view;
    }

    private void save() {

        imageCameraImgV.setEnabled(false);
        newUser = new User();
        newUser.setName(userName.getText().toString());
        newUser.setEmail(email.getText().toString());
        newUser.setPhone(phone.getText().toString());
        newUser.setPassword(password.getText().toString());
        newUser.setAvatar("");
        newUser.setBarbershop(false);
        newUser.setAvailable(true);

        //Regular user with no image:
        if(imageBitmap==null && barbershop==null)
        {
            Model.instance.saveUser(newUser, "signUp", ()->{
            dialog.dismiss();
            Navigation.findNavController(view).navigate(R.id.nav_barbershops_list_Fragment);
            });
        }
        //Regular user with image:
        else if(imageBitmap!=null && barbershop==null)
        {
            //Saving the user:
            Model.instance.saveUser(newUser, "signUp",()->
            {
                //Saving the image:
                Model.instance.uploadImage(imageBitmap, newUser.getId(), "user", (url)->
                {
                    //Updating the user image field:
                    newUser.setAvatar(url);
                    Model.instance.saveUser(newUser, "update", ()->
                    {
                        dialog.dismiss();
                        Navigation.findNavController(view).navigate(R.id.nav_barbershops_list_Fragment);
                    });
                });
            });
        }
        //User wih no image but he is a Barbershop:
        else if(imageBitmap==null && barbershop!=null)
        {
            //Saving the user:
            newUser.setBarbershop(true);
            Model.instance.saveUser(newUser, "signUp",()->
            {
                barbershop.setOwner(newUser.getId());
                if(barbershopImageBitmap!=null)
                    Model.instance.uploadImage(barbershopImageBitmap, newUser.getId(),"barbershop", (barbershopUrl)-> saveBarbershop(barbershopUrl));
                else
                    saveBarbershop(null);
            });
        }
        //User wih image and he is a Barbershop:
        else if(imageBitmap!=null && barbershop!=null)
        {
            //Saving the user:
            Model.instance.saveUser(newUser, "signUp",()->
            {
                //Saving the user image:
                Model.instance.uploadImage(imageBitmap, newUser.getId(), "user", (url)->
                {
                    //Updating the user image fields (image & isBarbershop)
                    newUser.setBarbershop(true);
                    newUser.setAvatar(url);
                    Model.instance.saveUser(newUser, "update", ()->
                    {
                            //Saving the barbershop:
                            barbershop.setOwner(newUser.getId());
                            //If there is an image to the barbershop saving his image and then saving himself:
                            if(barbershopImageBitmap!=null)
                                Model.instance.uploadImage(barbershopImageBitmap, newUser.getId(), "barbershop",(barbershopUrl)-> saveBarbershop(barbershopUrl));
                            //If there is no any image saving only the barbershop:
                            else
                                saveBarbershop(null);
                    });
                });
            });
        }
    }


    private void openDialog() {

        dialogBarbershop = new Dialog(getContext());
        dialogBarbershop.setContentView(R.layout.fragment_new_baebershop);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialogBarbershop.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.popup_dialog_background));
        dialogBarbershop.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialogBarbershop.setCancelable(true);
        dialogBarbershop.getWindow().getAttributes().windowAnimations = R.style.popup_dialog_animation;

        barbershopNameEt = dialogBarbershop.findViewById(R.id.newBarbershop_name);
        barbershopAddressEt = dialogBarbershop.findViewById(R.id.newBarbershop_address);
        barbershopPhoneEt = dialogBarbershop.findViewById(R.id.newBarbershop_phone);
        barbershopImageV = dialogBarbershop.findViewById(R.id.newBarbershop_imgV);
        barbershopAddBtn = dialogBarbershop.findViewById(R.id.newBarbershop_save_btn);
        barbershopBackBtn = dialogBarbershop.findViewById(R.id.newBarbershop_back_btn);
        barbershopImageCameraBtn = dialogBarbershop.findViewById(R.id.newBarbershop_imageCameraBtn);
        barbershopImageGalleryBtn = dialogBarbershop.findViewById(R.id.newBarbershop_imageGalleryBtn);

        barbershopImageCameraBtn.setOnClickListener(v->takePicture("barbershop"));
        barbershopImageGalleryBtn.setOnClickListener(v->takePictureFromGallery("barbershop"));
        barbershopBackBtn.setOnClickListener(v->{barbershop=null; dialogBarbershop.dismiss();});
        barbershopAddBtn.setOnClickListener(v->{
            barbershop = new Barbershop();
            barbershop.setName(barbershopNameEt.getText().toString());
            barbershop.setAddress(barbershopAddressEt.getText().toString());
            barbershop.setDeleted(false);
            barbershop.setAvatar("");
            barbershop.setPhone(barbershopPhoneEt.getText().toString());
            dialogBarbershop.dismiss();
        });

        dialogBarbershop.show();
    }

    void saveBarbershop(String url) {

        if(url!=null)
            barbershop.setAvatar(url);
        else
            barbershop.setAvatar("");

        Model.instance.saveBarbershop(barbershop, ()->createCalendar());
    }

    private void createCalendar() {

        List<String> hours = new LinkedList<>();
        hours.add("08:00 - 09:00");
        hours.add("09:00 - 10:00");
        hours.add("10:00 - 11:00");
        hours.add("11:00 - 12:00");
        hours.add("12:00 - 13:00");
        hours.add("13:00 - 14:00");
        hours.add("14:00 - 15:00");
        hours.add("15:00 - 16:00");
        hours.add("16:00 - 17:00");
        hours.add("17:00 - 18:00");
        hours.add("18:00 - 19:00");
        hours.add("19:00 - 20:00");
        hours.add("20:00 - 21:00");


        List<Queue> queuesList = new LinkedList<>();
        // creating on month queues for this barbershop:
        for (int j = 1; j < 2; j++)
        {
            for (int i = 0; i < hours.size(); i++)
            {
                Queue queue = new Queue();
                queue.barbershopId = barbershop.owner;
                queue.userId="";
                queue.id=""+i + "_" + barbershop.owner;
                queue.isQueueAvailable=true;
                queue.barbershopName = barbershop.name;
                queue.queueTime= hours.get(i);
                queue.queueDate = j + "/" + 6 + "/2021";
                queue.queueAddress=barbershop.address;
                queuesList.add(queue);
            }
        }
        Model.instance.createCalendar(queuesList,()->{});

        dialog.dismiss();
        MainActivity.navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setVisible(false);
        Navigation.findNavController(view).navigate(R.id.nav_barbershopCalendarFragment);

    }



    //----------------------------------------- Loading Dialog -----------------------------------------
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

    //-------------------------------------- take picture --------------------------------------
    //This is a sign for us to know from where activity we back to onActivityResult function:
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE_BARBERSHOP = 2;

    static final int GALLERY_IMAGE_BARBERSHOP = 3;
    static final int GALLERY_IMAGE = 0;
    final static int RESULT_SUCCESS = -1;
    void takePicture(String who)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        //start the activity camera:
        if(who.equals("user"))
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        else
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_BARBERSHOP);
    }

    void takePictureFromGallery(String who){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        if(who.equals("user"))
            startActivityForResult(photoPickerIntent, GALLERY_IMAGE);
        else
            startActivityForResult(photoPickerIntent, GALLERY_IMAGE_BARBERSHOP);
    }

    //GETTING THE RESULT FROM THE OTHER ACTIVITY:
    // (data is a faw parameters and the image we take).
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If we back from camera:
        if(requestCode==REQUEST_IMAGE_CAPTURE || requestCode==REQUEST_IMAGE_CAPTURE_BARBERSHOP)
        {
            if(resultCode == RESULT_SUCCESS)
            {
                //Bundle is some map with key & value.
                Bundle extras = data.getExtras();
                // Under the key data we have the image (BUT IN LOW QUALITY).
                // Bitmap is pixels of the image.
                //If it's the user image:
                if(requestCode==REQUEST_IMAGE_CAPTURE)
                {
                    imageBitmap = (Bitmap) extras.get("data");
                    imageV.setImageBitmap(imageBitmap);
                } //If it's the barbershop image:
                else if(requestCode==REQUEST_IMAGE_CAPTURE_BARBERSHOP)
                {
                    barbershopImageBitmap = (Bitmap) extras.get("data");
                    barbershopImageV.setImageBitmap(barbershopImageBitmap);
                }
            }
        }
        else if(requestCode == GALLERY_IMAGE || requestCode == GALLERY_IMAGE_BARBERSHOP)
        {
            if(resultCode == RESULT_SUCCESS)
            {
                Uri selectedImage = data.getData();
                try
                {

                    if(requestCode == GALLERY_IMAGE)
                         imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    else if(requestCode == GALLERY_IMAGE_BARBERSHOP)
                        barbershopImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);


                } catch (IOException e) {}

                if(requestCode == GALLERY_IMAGE)
                    imageV.setImageBitmap(imageBitmap);
                else if(requestCode == GALLERY_IMAGE_BARBERSHOP)
                    barbershopImageV.setImageBitmap(barbershopImageBitmap);
            }
        }
    }

}