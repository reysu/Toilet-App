package itp341.pai.sonali.finalprojectfrontend;

import android.content.DialogInterface;
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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import static android.media.MediaRecorder.VideoSource.CAMERA;


/**
 * Created by Sonali Pai on 11/10/2017.
 */

public class DetailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, PermissionUtils.PermissionResultCallback {

    private long bathroomId = -1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String url = "http://ec2-54-86-4-0.compute-1.amazonaws.com:8080";
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private TextView bathroomNameView;
    private TextView bathroomDescView;
    private TextView addressView;
    private ListView commentsView;
    private Toilet t = null;
    private ImageView disabledIcon;
    private ImageView keyIcon;
    private URI mImageUri;
    FloatingActionButton fabButton;
    FloatingActionButton fabImage;
    FloatingActionButton fabComment;
    private ArrayAdapter<String> adap;
    private boolean isFABOpen;
    private Location currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "DetailActivity";
    private ImageView goToGallery;

    private boolean isGuest;
    private Toolbar mTopToolbar;

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, 2003);
    }

    private void takePhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){            Toast.makeText(DetailActivity.this, "Camera Permission Not Granted!", Toast.LENGTH_SHORT).show();
            return;}
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == 2003) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //String path = saveImage(bitmap);
                    uploadImage(bitmap);
                    Toast.makeText(DetailActivity.this, "Request Sent!", Toast.LENGTH_SHORT).show();
                  //  imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DetailActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
           // imageview.setImageBitmap(thumbnail);
            uploadImage(thumbnail);
            Toast.makeText(DetailActivity.this, "Request Sent!", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImage(Bitmap myBitmap)
    {
        try {

            String endpointUrl = url + "/" + bathroomId + "/photo";
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
            File filesDir = getApplicationContext().getFilesDir();
            File imageFile = new File(filesDir, bathroomId +".png" );
            OutputStream os = new FileOutputStream(imageFile);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();

            RequestBody req = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("file", bathroomId+"_pic", RequestBody.create(MEDIA_TYPE_PNG, imageFile)).build();
            Request request = new Request.Builder()
                    .url("url")
                    .post(req)
                    .build();
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
        }catch (Exception e )
        {
            Log.e(TAG, e.getLocalizedMessage());
        }

    }
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "image/");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_toilet);

        //toolbar
        mTopToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        goToGallery = (ImageView) findViewById(R.id.goToGallery);
        if(mTopToolbar != null){
            mTopToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            mTopToolbar.setTitle("Toilet Information");
            mTopToolbar.setBackgroundColor(Color.parseColor("#000000"));
        }

        goToGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
      //          Intent i = new Intent(getApplicationContext(), Gallery.class);
//                i.putExtra("toilet", t);
        //        startActivity(i);
                showPictureDialog();

            }
        });

        Window window = this.getWindow();

        //change color of status bar
        window.setStatusBarColor(getResources().getColor(android.R.color.black));


        Intent i = getIntent();
        t = (Toilet) i.getSerializableExtra("toilet");
        isGuest = i.getBooleanExtra("guest",false);
        bathroomId = t.getBathroomId();

       // bathroomId = getIntent().getIntExtra("bathroomId", -1);
//
//        try {
//            URL url_getBa
//
//
// throom = new URL("http://localhost:8080/BathroomServlet?bathroomId=" + bathroomId);
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
        if(isGuest){
            fabButton.setVisibility(View.GONE);
            fabImage.setVisibility(View.GONE);
            fabComment.setVisibility(View.GONE);
            goToGallery.setVisibility(View.GONE);
        }
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
                Uri cameraImageUri = getOutputMediaFileUri(1);
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
        LatLng coordinate = new LatLng(lat,lng);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 16);
        mMap.animateCamera(location);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Toilet"));
    }


    @Override
    public boolean onMyLocationButtonClick() {
       // Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "+ result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location


    }


    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        Date date = new java.util.Date();
        File mediaFile;
        if(type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + ".jpg");
        }
        else {
            return null;
        }
        return mediaFile;
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

//        return super.onOptionsItemSelected();
    }
}
