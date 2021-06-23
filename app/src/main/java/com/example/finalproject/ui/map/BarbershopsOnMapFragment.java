package com.example.finalproject.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.finalproject.R;
import com.example.finalproject.model.Barbershop;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class BarbershopsOnMapFragment extends Fragment implements OnMapReadyCallback {


    View view;
    MapViewModel mapViewModel;
    boolean isPermissionGranted;
    FusedLocationProviderClient client;
    GoogleMap mGoogleMap;
    FloatingActionButton zoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_barbershops_on_map, container, false);
        zoom = view.findViewById(R.id.barbershopsOnMap_floatingActionButton);

        mapViewModel  = new ViewModelProvider(this).
                get(MapViewModel.class);
        mapViewModel.getData().observe(getViewLifecycleOwner(), (data)-> {});

        checkPermission();

        initMap();

        client = new FusedLocationProviderClient(getActivity());

        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentFocus();
            }
        });

        return view;
    }

    private void checkPermission() {
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
                initMap();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getContext().getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    private void getCurrentFocus() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                goToLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void goToLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
//        LatLng latLng = new LatLng(32.013733,34.765637);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(30) // radius in meters
                .fillColor(0x8800CCFF) //this is a half transparent blue, change "88" for the transparency
                .strokeColor(Color.BLUE) //The stroke (border) is blue
                .strokeWidth(2);
        mGoogleMap.addCircle(circleOptions);
    }

    private void initMap() {
        if (isPermissionGranted) {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.barbershopsOnMap_map);
            supportMapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                int barbershopId= mapViewModel.getBarbershopPosition(marker.getTitle());
                BarbershopsOnMapFragmentDirections.ActionBarbershopsOnMapFragmentToNavBarbershopDetailsFragment
                        action = BarbershopsOnMapFragmentDirections.actionBarbershopsOnMapFragmentToNavBarbershopDetailsFragment(barbershopId);
                Navigation.findNavController(view).navigate(action);
            }
        });
        drawAllBarbershopsMarks();
    }

    private void drawAllBarbershopsMarks(){
        if(isPermissionGranted) {
            for (Barbershop b : mapViewModel.getData().getValue()) {
                LatLng latLng = new LatLng(b.latitude, b.longitude);
                mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(b.getName() + " (Barbershop)"));
            }
        }
    }

}



