package com.example.finalproject.ui.new_barbershop;

import android.app.Dialog;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.finalproject.R;
import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.example.finalproject.model.Queue;
import com.example.finalproject.model.User;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class NewBarbershopFragment extends Fragment {

    EditText nameEt;
    EditText addressEt;
    EditText phoneEt;
    ImageButton imageCameraBtn;
    ImageButton imageGalleryBtn;
    ImageView imageV;
    Bitmap imageBitmap;
    Button saveBtn;
    Button backBtn;
    Dialog dialog;
    ProgressBar pb;
    View view;
    User user;
    Barbershop barbershop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_new_baebershop, container, false);
         nameEt = view.findViewById(R.id.newBarbershop_name);
         addressEt = view.findViewById(R.id.newBarbershop_address);
         phoneEt = view.findViewById(R.id.newBarbershop_phone);
         imageV = view.findViewById(R.id.newBarbershop_imgV);
         saveBtn = view.findViewById(R.id.newBarbershop_save_btn);
        backBtn = view.findViewById(R.id.newBarbershop_back_btn);
        imageCameraBtn = view.findViewById(R.id.newBarbershop_imageCameraBtn);
        imageGalleryBtn = view.findViewById(R.id.newBarbershop_imageGalleryBtn);
//        pb = view.findViewById(R.id.newBarbershop_progressBar);
//         pb.setVisibility(View.INVISIBLE);

        //Get all data from signUp fragment:
        String userName = NewBarbershopFragmentArgs.fromBundle(getArguments()).
                getUserName();
        String email = NewBarbershopFragmentArgs.fromBundle(getArguments()).
                getEmail();
        String phone = NewBarbershopFragmentArgs.fromBundle(getArguments()).
                getPhone();
        String password = NewBarbershopFragmentArgs.fromBundle(getArguments()).
                getPassword();

        user = new User();
        //Getting an ID from firebase
        user.setName(userName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);
        user.setAvailable(true);
        user.setBarbershop(true);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.instance.saveUser(user,"signUp",()->{
                    save();
                });

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Navigation.findNavController(v).navigateUp();
            }
        });

        imageCameraBtn.setOnClickListener(v->takePicture());
        imageGalleryBtn.setOnClickListener(v->takePictureFromGallery());

        return view;
    }

    void save()
    {
        popupLoadingDialog();
        saveBtn.setEnabled(false);
        imageCameraBtn.setEnabled(false);

        if(imageBitmap!=null)
        {
            Model.instance.uploadImage(imageBitmap, user.getId(),
                    "barbershop", new Model.UpLoadImageListener() {
                        @Override
                        public void onComplete(String url) {
                            saveBarbershop(url);
                        }
                    });
        }
        else
            saveBarbershop(null);

    }

    void saveBarbershop(String url)
    {

        barbershop = new Barbershop();
        if(url!=null)
            barbershop.setAvatar(url);
        else
            barbershop.setAvatar("");

        barbershop.setOwner(user.getId());
        barbershop.setName(nameEt.getText().toString());
        barbershop.setAddress(addressEt.getText().toString());
        barbershop.setAvailable(true);
        barbershop.setPhone(phoneEt.getText().toString());

        Model.instance.saveBarbershop(barbershop, ()->{
            createCalendar();
        });

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
        Navigation.findNavController(view).navigate(R.id.nav_barbershopCalendarFragment);

    }

    private void popupLoadingDialog() {

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_dialog_loading);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.popup_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.popup_dialog_animation;
        pb = dialog.findViewById(R.id.loading_progressBar_pb);

        pb.setVisibility(View.VISIBLE);
        dialog.show();

    }

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