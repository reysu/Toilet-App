package itp341.pai.sonali.finalprojectfrontend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import itp341.pai.sonali.finalprojectfrontend.model.POST_HTTP;
import itp341.pai.sonali.finalprojectfrontend.model.PermissionUtils;
import itp341.pai.sonali.finalprojectfrontend.model.Toilet;
import itp341.pai.sonali.finalprojectfrontend.model.User;

import static android.R.attr.id;
import static android.R.attr.name;



public class AddToiletActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, PermissionUtils.PermissionResultCallback {

    //declare the widgets
    EditText locationName;
    TextView addressField;
    Button disabledAccess;
    Button requiresKey;
    Button addToilet;
    ImageView accessibleIcon;
    ImageView keyIcon;
    //private datamembers
    boolean disabledAccessbool = false;
    boolean requiresKeybool = false;

    private Location currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "DetailActivity";
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationRequest mLocationRequest;
    private double currentLat;
    private double currentLng;


    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)   //password incorrect
                Toast.makeText(getApplicationContext(),"All fields not provided", Toast.LENGTH_LONG).show();
            if(msg.arg1 == 2)   //password incorrect
                Toast.makeText(getApplicationContext(),"Toilet Exists", Toast.LENGTH_LONG).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtoilet);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        //change the color of the status bar
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        //initialize widgets
        locationName = (EditText) findViewById(R.id.locationName);
        addressField = (TextView) findViewById(R.id.addressField);
        accessibleIcon = (ImageView) findViewById(R.id.accessibleIcon);
        keyIcon = (ImageView) findViewById(R.id.keyIcon);
        disabledAccess = (Button) findViewById(R.id.disabledAccess);
        requiresKey = (Button) findViewById(R.id.requiresKey);
        addToilet = (Button) findViewById(R.id.addToiletButton);


        //listener for the disabled access button-- changes the boolean based on whether the bathroom has disabled access or not
        disabledAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disabledAccessbool = !disabledAccessbool;
                if(disabledAccessbool){
                    disabledAccess.setTextColor(Color.parseColor("#000000"));
                    accessibleIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                }else{
                    disabledAccess.setTextColor(Color.parseColor("#aaaaaa"));
                    accessibleIcon.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                }

            }
        });

        //listener for the requires key button-- changes the boolean based on whether the bathroom requries a key or not
        requiresKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requiresKeybool = !requiresKeybool;
                if(requiresKeybool){
                    requiresKey.setTextColor(Color.parseColor("#000000"));
                    keyIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                }else{
                    requiresKey.setTextColor(Color.parseColor("#aaaaaa"));
                    keyIcon.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

        //listener for adding a toilet button
        addToilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locName = locationName.getText().toString();
                String address = addressField.getText().toString();
                double longitude = currentLng;
                double latitude = currentLat;
                Toilet t = new Toilet(locName, address, disabledAccessbool, requiresKeybool);
                t.setLatitude(latitude);
                t.setLongitude(longitude);
                OkHttpClient client = new OkHttpClient();
                String json = new GsonBuilder().create().toJson(t, Toilet.class);
                try {
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

                    Request request = new Request.Builder()
                            .url("http://ec2-54-86-4-0.compute-1.amazonaws.com:8080/bathroom")
                            .post(body)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        public void onFailure(Request request, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            try (ResponseBody responseBody = response.body()) {
                                if(!response.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Adding this toilet wasn't successful", Toast.LENGTH_LONG).show();
                                }else {
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                }
                            }
                        }
                    });
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }


            }
        });


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
        } catch (SecurityException se) {
            //prompt that the location was not enabled

        }

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

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
                    Address currentAddress = getAddress(lat, lng);
                    String cAddress = "";
                    for (int n = 0; n <= currentAddress.getMaxAddressLineIndex(); n++) {
                        cAddress += currentAddress.getAddressLine(n) + ", ";
                    }
                    addressField.setText(cAddress);
                    currentLat = currentAddress.getLatitude();
                    currentLng = currentAddress.getLongitude();
                }
            }

        }catch (SecurityException se)
        {
            Toast.makeText(this, "Pooper cannot get your current location.", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onMyLocationButtonClick() {
        // Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        try {
            if (checkPlayServices())
            {
                currentLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
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
        try {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (SecurityException se) {

        }
        if (currentLocation != null)
        {
            double lat = currentLocation.getLatitude();
            double lng = currentLocation.getLongitude();
            LatLng coordinate = new LatLng(lat, lng); //Store these lat lng values somewhere. These should be constant.
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    coordinate, 15);
            mMap.animateCamera(location);
            Address currentAddress = getAddress(lat, lng);
            String cAddress = "";
            for (int n = 0; n <= currentAddress.getMaxAddressLineIndex(); n++) {
                cAddress += currentAddress.getAddressLine(n) + ", ";
            }
            addressField.setText(cAddress);
            currentLat = currentAddress.getLatitude();
            currentLng = currentAddress.getLongitude();
        }

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