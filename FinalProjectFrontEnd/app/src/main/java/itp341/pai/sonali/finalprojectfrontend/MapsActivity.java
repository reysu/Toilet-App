package itp341.pai.sonali.finalprojectfrontend;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import itp341.pai.sonali.finalprojectfrontend.model.PermissionUtils;
import itp341.pai.sonali.finalprojectfrontend.model.Toilet;

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, PermissionUtils.PermissionResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private HashMap<String, Toilet> markerIdToiletMap;
    private Location currentLocation;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Window window = this.getWindow();
        //change color of status bar
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        ArrayList<LatLng> pins = new ArrayList<LatLng>();
        LatLng USC = new LatLng(34.0224, -118.2851);
        pins.add(new LatLng(34.026030, -118.287440));
        pins.add(new LatLng(33.827903, -117.987440));
        pins.add(new LatLng(34.226030, -118.135540));
        pins.add(new LatLng(34.426030, -118.587440));
        pins.add(new LatLng(33.426030, -118.687440));
        String id = null;
        //maybe we get coordinates from Toilet objects - then we will have a list of Toilet objects.
        // we will pass that toilet instance to the next intent page.
        // then we will use the same index i for the Toilet object and the corresponding LatLng
        // right now just hardcoding it using one toilet:

        Toilet toilet = new Toilet("Cardinal Gardens","3131 Mcclintock Avenue",true,false);

        markerIdToiletMap = new HashMap<String,Toilet>();
        for (int i = 0; i < pins.size(); ++i)
        {
            Marker k = mMap.addMarker(new MarkerOptions().position(pins.get(i)).title("Toilet" + i));
            markerIdToiletMap.put(k.getId(), toilet);
        }
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                USC, 15);
        mMap.animateCamera(location);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()   {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Toilet m = markerIdToiletMap.get(marker.getId());
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putExtra("toilet",m);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        ArrayList<String> permissions=new ArrayList<>();
        PermissionUtils permissionUtils;

        permissionUtils=new PermissionUtils(this);

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);

        try {


            if (checkPlayServices())
            {
                currentLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if (currentLocation != null)
                {
                    double lat = currentLocation.getLatitude();
                    double lng = currentLocation.getLongitude();
                    LatLng coordinate = new LatLng(lat, lng); //Store these lat lng values somewhere. These should be constant.
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                            coordinate, 15);
                    mMap.animateCamera(location);
                }
            }

        }catch (SecurityException se)
        {
            Toast.makeText(this, "Pooper cannot get your current location.", Toast.LENGTH_SHORT).show();
        }

        // Return false so that we don't consume the event and the default behavior still occurs
        return false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "+ result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location


    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this,resultCode,
                        34).show();//34 was originally PLAY_SERVICES_REQUEST
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }

        return true;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }


    public Address getAddress(double latitude, double longitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {

    }

    @Override
    public void PermissionGranted(int request_code) {

    }

    @Override
    public void NeverAskAgain(int request_code) {

    }

    @Override
    public void PermissionDenied(int request_code) {

//        return super.onOptionsItemSelected();
    }

}
