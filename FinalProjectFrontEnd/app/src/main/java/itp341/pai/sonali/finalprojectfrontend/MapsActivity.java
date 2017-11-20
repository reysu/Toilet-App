package itp341.pai.sonali.finalprojectfrontend;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import java.util.ArrayList;
import java.util.HashMap;

import itp341.pai.sonali.finalprojectfrontend.model.Toilet;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<String, Toilet> markerIdToiletMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Window window = this.getWindow();
        //change color of status bar
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(USC));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()   {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Toilet m = markerIdToiletMap.get(marker.getId());
                Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                i.putExtra("toilet",m);
                startActivity(i);
            }
        });
    }

}
