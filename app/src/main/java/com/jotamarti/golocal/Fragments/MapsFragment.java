package com.jotamarti.golocal.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.jotamarti.golocal.Activities.ShopDetailActivity;
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
    private List<Shop> shopList;
    List<Marker> markers;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        model.setTitle(String.valueOf(getText(R.string.MapsFragment_title)));

        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            mMap = googleMap;

            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
            //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));

            shopList = model.getShopsList().getValue();
            populateMap();

            if (!checkHaveLocationPermission()) {
                requestPermission();
            } else {
                googleMap.setMyLocationEnabled(true);
                navigateToUserLocation();
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    // TODO: intentar cambiar el incono
                    Log.d(TAG, "He pulsado en el marcador con el TAG: " + marker.getTag());
                    return false;
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    //TODO: Intenta para irme al perfil de la tienda, primero tengo que sacar la tienda de la shoplist y luego pasarla
                    Shop shop = Shop.getShopByUid(shopList, marker.getTag().toString());
                    Intent intent = new Intent(getContext(), ShopDetailActivity.class);
                    intent.putExtra("shop", shop);
                    intent.putExtra("caller", "MapsFragment");
                    startActivity(intent);
                }
            });
        }
    };

    private void populateMap() {
        int height = 50;
        int width = 50;
        BitmapDrawable bitmapDrawable = (BitmapDrawable) ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.go_local_house, null);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);

        markers = new ArrayList<>();

        for(int i = 0; i < shopList.size(); i++){
            Shop currentShop = shopList.get(i);
            Marker marker = mMap.addMarker(new MarkerOptions().position(shopList.get(i).getCoordinates()).title(currentShop.getShopName()).snippet(currentShop.getAddress()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            marker.setTag(currentShop.getUserUid());
            markers.add(marker);
        }
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mContents;

        CustomInfoWindowAdapter() {
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge;

            badge = R.drawable.shop_header_mock;
            ((ImageView) view.findViewById(R.id.customInfoContent_imageView)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = (view.findViewById(R.id.customInfoContent_textView_title));
            if (title != null) {
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = (view.findViewById(R.id.customInfoContent_textView_text));
            if (snippet != null) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
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
                    navigateToUserLocation();
                } else {
                    CustomToast.showToast(getActivity(), "Permission not granted", CustomToast.mode.LONGER);
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(model.userCoordinates, 16);
                    mMap.animateCamera(yourLocation);
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