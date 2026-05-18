package com.example.task7p_s225138694;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.task7p_s225138694.data.DatabaseHelper;
import com.example.task7p_s225138694.data.ItemDataModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseHelper dbhelper;
    private ArrayList<ItemDataModel> itms;

    private static final String TAG = "MapsActivity";
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private boolean mapReady = false;

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maps);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbhelper = new DatabaseHelper(this);
        itms = dbhelper.getAllItems();

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "Map fragment not found!");
            Toast.makeText(this, "Error initializing map.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mapReady = true;
        Log.d(TAG, "Map is ready.");

        // Map Settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // Info window click listener
        mMap.setOnInfoWindowClickListener(marker ->
                Toast.makeText(this, "Info: " + marker.getTitle(), Toast.LENGTH_SHORT).show());

        // Check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        mMap.setMyLocationEnabled(true);

        // Get last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && mapReady && mMap != null) {
                LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.addMarker(new MarkerOptions()
                        .position(myLoc)
                        .title("You are here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));

                // show Lost and Found items < 5km
                showLostFoundItems(location);

            } else {
                Toast.makeText(MapsActivity.this, "Location unavailable", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get location: ", e);
            Toast.makeText(this, "Failed to retrieve location", Toast.LENGTH_SHORT).show();
        });

    }

    private void showLostFoundItems(Location userLocation) {

        for (ItemDataModel itm : itms) {

            // skip invalid coordinates
            if (itm.getLat() == 0.0 && itm.getLong() == 0.0) {
                continue;
            }

            float[] result = new float[1];

            Location.distanceBetween(
                    userLocation.getLatitude(),
                    userLocation.getLongitude(),
                    itm.getLat(),
                    itm.getLong(),
                    result
            );

            // meters -> KM
            double distanceKm = result[0] / 1000.0;

            // Radius based search
            if (distanceKm <= 5) {

                LatLng itemLocation = new LatLng(
                        itm.getLat(),
                        itm.getLong()
                );

                float markerColor;

                // Lost = red
                // Found = blue
                if (itm.getType().equalsIgnoreCase("Lost")) {

                    markerColor =
                            BitmapDescriptorFactory.HUE_RED;

                } else {

                    markerColor =
                            BitmapDescriptorFactory.HUE_BLUE;
                }

                mMap.addMarker(
                        new MarkerOptions()
                                .position(itemLocation)
                                .title(itm.getName())
                                .snippet(
                                        itm.getType()
                                                + " • "
                                                + String.format("%.2f km away", distanceKm)
                                )
                                .icon(
                                        BitmapDescriptorFactory
                                                .defaultMarker(markerColor)
                                )
                );
            }
        }
    }
}