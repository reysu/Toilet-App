package itp341.pai.sonali.finalprojectfrontend;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import itp341.pai.sonali.finalprojectfrontend.model.POST_HTTP;
import itp341.pai.sonali.finalprojectfrontend.model.Toilet;

import static android.R.attr.id;
import static android.R.attr.name;

public class AddToiletActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtoilet);

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
                double longitude = 20;
                double latitude = 20;
                Toilet t = new Toilet(locName, address, disabledAccessbool, requiresKeybool);
                t.setLatitude(latitude);
                t.setLongitude(longitude);
                OkHttpClient client = new OkHttpClient();
                String json = new GsonBuilder().create().toJson(t, Toilet.class);
                try {
                    RequestBody formBody = new FormEncodingBuilder().add("toilet", json).build();

                    Request request = new Request.Builder()
                            .url("http://ec2-54-86-4-0.compute-1.amazonaws.com:8080/bathroom")
                            .post(formBody)
                            .build();

                    Response response = client.newCall(request).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        });



    }


}