package com.jotamarti.golocal.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.CustomToast;
import com.jotamarti.golocal.ViewModels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private final static int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    private final String TAG = "MapsFragment";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private MainActivityViewModel model;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            mMap = googleMap;
            //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
            LatLng valencia = new LatLng(39.47427, -0.37548);
            LatLng sydney = new LatLng(-34, 151);

            //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            //CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(valencia, 16);
            //googleMap.animateCamera(yourLocation);

            List<Shop> shopList = model.getShopsList().getValue();

            int height = 50;
            int width = 50;
            BitmapDrawable bitmapdraw = (BitmapDrawable) ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.go_local_house, null);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            List<Marker> markers = new ArrayList<>();

            for(int i = 0; i < shopList.size(); i++){
                Shop currentShop = shopList.get(i);
                Marker marker = googleMap.addMarker(new MarkerOptions().position(shopList.get(i).getCoordinates()).title(currentShop.getShopName()).snippet(currentShop.getAddress()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                marker.setTag(currentShop.getUserUid());
                marker.showInfoWindow();
                markers.add(marker);
                Log.d(TAG, "Cordenadas de la tienda: " + "Latitud: " + shopList.get(i).getCoordinates().latitude + " Longitud: " + shopList.get(i).getCoordinates().longitude);
            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d(TAG, "He pulsado en el marcador con el TAG: " + marker.getTag());
                    return false;
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.d(TAG, "He pulsado en el infoWindow de el marker con el TAG: " + marker.getTag());
                }
            });

            if (!checkHaveLocationPermission()) {
                requestPermission();
            } else {
                googleMap.setMyLocationEnabled(true);
                navigateToUserLocation();
            }
        }
    };



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        model.setTitle("Explore Shops");

        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    public void requestPermission() {
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!checkHaveLocationPermission()) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            CustomToast.showToast(requireActivity(), "Has clickado el mapa", CustomToast.mode.LONGER);
                        }
                    });
                    navigateToUserLocation();
                } else {
                    CustomToast.showToast(getActivity(), "Permission not granted", CustomToast.mode.LONGER);
                }
            }
        }
    }

    private void navigateToUserLocation() {
        if (!checkHaveLocationPermission()) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(null, null);
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    LatLng cordsUser = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(cordsUser, 16);
                    mMap.animateCamera(yourLocation);
                }
            }
        });
    }

    private Boolean checkHaveLocationPermission(){
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }
}