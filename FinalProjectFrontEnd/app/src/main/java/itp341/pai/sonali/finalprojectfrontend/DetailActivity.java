package itp341.pai.sonali.finalprojectfrontend;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import itp341.pai.sonali.finalprojectfrontend.model.Comment;
import itp341.pai.sonali.finalprojectfrontend.model.GET_HTTP;
import itp341.pai.sonali.finalprojectfrontend.model.PermissionUtils;
import itp341.pai.sonali.finalprojectfrontend.model.Toilet;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
//import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Sonali Pai on 11/10/2017.
 */

public class DetailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, PermissionUtils.PermissionResultCallback {

    //private int bathroomId = -1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private TextView bathroomNameView;
    private TextView bathroomDescView;
    private TextView addressView;
    private ListView commentsView;
    private Toilet t = null;
    private ImageView disabledIcon;
    private ImageView keyIcon;
    FloatingActionButton fabButton;
    FloatingActionButton fabImage;
    FloatingActionButton fabComment;
    private ArrayAdapter<String> adap;
    private boolean isFABOpen;
    private Location currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_toilet);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        Window window = this.getWindow();

        //change color of status bar
        window.setStatusBarColor(getResources().getColor(android.R.color.black));


        Intent i = getIntent();
        t = (Toilet) i.getSerializableExtra("toilet");
//        bathroomId = getIntent().getIntExtra("bathroomId", -1);
//
//        try {
//            URL url_getBathroom = new URL("http://localhost:8080/BathroomServlet?bathroomId=" + bathroomId);
//            GET_HTTP getBathroomHTTP = new GET_HTTP(url_getBathroom);
//            String bathroomJson = getBathroomHTTP.getResponse();
//            Gson gson = new Gson();
//            bathroom = gson.fromJson(bathroomJson, Toilet.class);
//        } catch (MalformedURLException mue) {
//            mue.getStackTrace();
//        } catch (IOException ioe) {
//            ioe.getStackTrace();
//        }

        //create an instance of the Fused Location Provider Client
       // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        commentsView = (ListView) findViewById(R.id.comments);
        bathroomNameView = (TextView) findViewById(R.id.toilet_name);
        bathroomNameView.setText(t.getNameOfLocation());
        // bathroomDescView = (TextView) findViewById(R.id.toilet_description);
        // bathroomNameView.setText(bathroom.getDescription());
        commentsView = (ListView) findViewById(R.id.comments);
        List<String> comments = t.getComments();
        disabledIcon = (ImageView) findViewById(R.id.disabledCheck);
        keyIcon = (ImageView) findViewById(R.id.keyCheck);
//        ArrayAdapter<Comment> commentsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, comments);
//        commentsView.setAdapter(commentsAdapter);
        addressView = (TextView) findViewById(R.id.address);
        addressView.setText(t.getAddress());
        t.addComments("This bathroom is pretty clean");
        t.addComments("One of my favorite locations.");
        t.addComments("Good.");
        t.addComments("This is a comment");
        t.addComments("Satisfactory.");
        adap = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, t.getComments());
        commentsView.setAdapter(adap);
        if (t.isHasDisabilityAccomodations()) {
            disabledIcon.setImageResource(R.drawable.ic_done_black_24px);
            disabledIcon.setColorFilter(Color.rgb(50, 205, 50));
        } else {
            disabledIcon.setImageResource(R.drawable.ic_close_black_24px);
            disabledIcon.setColorFilter(Color.RED);
        }
        if (t.isRequiresKey()) {
            keyIcon.setImageResource(R.drawable.ic_done_black_24px);
            keyIcon.setColorFilter(Color.rgb(50, 205, 50));
        } else {
            keyIcon.setImageResource(R.drawable.ic_close_black_24px);
            keyIcon.setColorFilter(Color.RED);
        }

        fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabImage = (FloatingActionButton) findViewById(R.id.fabImage);
        fabComment = (FloatingActionButton) findViewById(R.id.fabComment);

        //floating action button listener
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    isFABOpen = true;
                    float deg = fabButton.getRotation() + 45F;
                    fabButton.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                    fabImage.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
                    fabComment.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
                } else {
                    float deg = fabButton.getRotation() + 45F;
                    fabButton.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                    isFABOpen = false;
                    fabImage.animate().translationY(0);
                    fabComment.animate().translationY(0);
                }
            }
        });
        fabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CommentActivity.class);
                i.putExtra("toilet", t);
                startActivityForResult(i, 0);
            }
        });
        fabImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);

            }
        });

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        //Google Map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.setOnMyLocationButtonClickListener(this);
            enableMyLocation();
        } catch (SecurityException se) {
            //prompt that the location was not enabled

        }
        //double lat = 34.0224, lng = 118.2851; //lat and lng of USC
        double lat = t.getLatitude();
        double lng = t.getLongitude();

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Toilet"));
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        ArrayList<String> permissions=new ArrayList<>();
        PermissionUtils permissionUtils;

        permissionUtils=new PermissionUtils(this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

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

//    @Override
//    public void onMyLocationClick(@NonNull Location location) {
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
//    }

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
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

    }
}
