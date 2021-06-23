package com.example.finalproject.ui.edit_Barbershop;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.finalproject.R;
import com.example.finalproject.model.Barbershop;
import com.example.finalproject.model.Model;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditBarbershopFragment extends Fragment {
    EditBarbershopViewModel editBarbershopViewModel;
    View view;
    ImageButton galleryImgB;
    ImageButton cameraImgB;
    ImageView deleteImgV;
    Button backBtn;
    Button saveBtn;
    EditText nameEt;
    EditText addressEt;
    EditText phoneEt;
    Barbershop barbershop;
    Dialog dialog;
    ProgressBar pb;
    CircleImageView imageV;
    Bitmap imageBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_edit_barbershop, container, false);
        backBtn = view.findViewById(R.id.editBaebershop_back_btn);
        saveBtn = view.findViewById(R.id.editBaebershop_save_btn);
        nameEt = view.findViewById(R.id.editBaebershop_name_et);
        addressEt = view.findViewById(R.id.editBaebershop_address_et);
        phoneEt = view.findViewById(R.id.editBaebershop_phone_et);
        galleryImgB= view.findViewById(R.id.editBaebershop_imageGallery_Btn);
        cameraImgB = view.findViewById(R.id.editBaebershop_imageCamera_Btn);
        imageV = view.findViewById(R.id.editBaebershop_image_imgV);
        deleteImgV = view.findViewById(R.id.editBaebershop_delete_imgV);
        editBarbershopViewModel = new ViewModelProvider(this).
                get(EditBarbershopViewModel.class);

        editBarbershopViewModel.getData().observe(getViewLifecycleOwner(), (data)->{

            barbershop = editBarbershopViewModel.getCurrentBarbershop(Model.instance.getUser().getId());
            nameEt.setText(barbershop.getName());
            addressEt.setText(barbershop.getAddress());
            phoneEt.setText(barbershop.getPhone());
            if(barbershop.getAvatar()!=null && !(barbershop.getAvatar().equals("")))
                Picasso.get().load(barbershop.getAvatar()).into(imageV);
        });


        backBtn.setOnClickListener(v->Navigation.findNavController(v).navigateUp());
        saveBtn.setOnClickListener(v->save());
        cameraImgB.setOnClickListener(v->takePicture());
        galleryImgB.setOnClickListener(v->takePictureFromGallery());

        //Delete Barbershop:
        deleteImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.instance.getUser().setBarbershop(false);
                barbershop.setDeleted(true);
                //TODO: set unavailable on his all queues...
                Model.instance.saveBarbershop(barbershop,()->{
                    Model.instance.saveUser(Model.instance.getUser(),"update",
                                    ()->{
                        Navigation.findNavController(v).navigate(R.id.nav_barbershops_list_Fragment);

                    });
                });
            }
        });

        return view;
    }



    void save()
    {
        popupLoadingDialog();
        saveBtn.setEnabled(false);
        cameraImgB.setEnabled(false);
        galleryImgB.setEnabled(false);

        if(imageBitmap!=null)
        {
            Model.instance.uploadImage(imageBitmap,Model.instance.getUser().getId(),
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


        if(url!=null)
            barbershop.setAvatar(url);
        else if(barbershop.getAvatar()==null || barbershop.getAvatar().equals(""))
            barbershop.setAvatar("");


        barbershop.setName(nameEt.getText().toString());
        barbershop.setAddress(addressEt.getText().toString());
        barbershop.setPhone(phoneEt.getText().toString());
        barbershop.setDeleted(false);
        barbershop.setOwner(Model.instance.getUser().getId());

        Model.instance.saveBarbershop(barbershop, ()->{
            dialog.dismiss();
            Navigation.findNavController(view).navigateUp();
        });

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