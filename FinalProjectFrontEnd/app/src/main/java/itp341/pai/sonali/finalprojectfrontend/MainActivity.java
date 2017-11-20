package itp341.pai.sonali.finalprojectfrontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URL;

import itp341.pai.sonali.finalprojectfrontend.model.GET_HTTP;
import itp341.pai.sonali.finalprojectfrontend.model.POST_HTTP;
import itp341.pai.sonali.finalprojectfrontend.model.User;

public class MainActivity extends AppCompatActivity {

    //final variables
    public static final String USERNAME = "";
    public static final String USERID = "";
    private static final String ENDPOINT = "http://ec2-54-86-4-0.compute-1.amazonaws.com:8080/";
    //declare widgets
    private EditText usernameInput;
    private EditText passwordInput;
    private Button signUpButton;
    private Button signInButton;
    private Button guestButton;
    //private data members
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        Window window = this.getWindow();
        window.setTitle("Pooper");
        //change color of status bar
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        usernameInput = (EditText) findViewById(R.id.usernameInputField);
        passwordInput = (EditText) findViewById(R.id.passwordInputField);
        signInButton = (Button) findViewById(R.id.signInButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);

        guestButton = (Button) findViewById(R.id.guestButton);
        username = "";
        password = "";

        //listener for sign In button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameInput.getText().toString();
                password = passwordInput.getText().toString();
                //TO DO: send these to data base to verify if user exists
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // get connection to the "SignIn" Servlet
                            URL url = new URL(ENDPOINT + "login?username="+username+"&password="+password);
                            POST_HTTP post_http = new POST_HTTP(url);

                            post_http.connect();

                            // return user JSON as the response Text. If the user does not exist, or
                            //if the user's credentials are not correct - return an "ERROR" string

                            String userJson = post_http.getResponse();
                            if (userJson.equals("fail"))
                            {
                                Toast.makeText(getApplicationContext(), "Username and/or password are incorrect", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Gson gson = new Gson();
                                User user = gson.fromJson(userJson, User.class);

                                if(user == null){
                                    Toast.makeText(getApplicationContext(), "ERROR THAT SHOULD NEVER HAPPEN", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    int userId = user.getId();
                                    SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                                    editor.putString(USERNAME, username);
                                    editor.putInt(USERID, userId);
                                    Intent i = new Intent(getApplicationContext(), ListActivity.class);
                                    i.putExtra("guest",false);
                                    startActivity(i);
                                    }
                            }

                        }catch (Exception e)
                        {
                            Log.d("Exception", e.toString());
                        }
                    }
                });

                //if exists, launch an intent to the listview page of all toilets

            }
        });
        //listener for guest button
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(),ListActivity.class);
                    i.putExtra("guest",true);
                    startActivity(i);

            }
        });

        //listener for sign Up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameInput.getText().toString();
                password = passwordInput.getText().toString();
                User user = new User(username, password);
                //send this user to database
                //do post
                try {
                    URL url = new URL(ENDPOINT + "register?username="+username+"&password="+password);
                    POST_HTTP post_http = new POST_HTTP(url);
                    post_http.connect();
                    String response = post_http.getResponse();
                    if(response.equals("fail")){
                        //To-Do: show error messages
                        Toast.makeText(getApplicationContext(), "Choose a different username", Toast.LENGTH_LONG).show();
                    }
                    else{
                        //get response and check id
                        URL url3 = new URL(ENDPOINT + "login?username="+username+"&password="+password);
                        POST_HTTP post_http3 = new POST_HTTP(url3);
                        post_http3.connect();
                        String userJson = post_http3.getResponse();
                        if (userJson.equals("fail"))
                        {
                            Toast.makeText(getApplicationContext(), "Username and/or password are incorrect", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            String userJsonValidate = post_http3.getResponse();
                            Gson gson = new Gson();
                            User newUser = gson.fromJson(userJsonValidate, User.class);
                            if(newUser == null){
                                Toast.makeText(getApplicationContext(), "ERROR THAT SHOULD NEVER HAPPEN", Toast.LENGTH_LONG).show();
                            }
                            else {
                                int userId = newUser.getId();
                                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                                editor.putString(USERNAME, username);
                                editor.putInt(USERID, userId);
                                Toast.makeText(getApplicationContext(), "Username and/or password are incorrect", Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                                i.putExtra("guest",false);
                                startActivity(i);
                            }
                        }
                    }

                // return user JSON as the response Text. If the user does not exist, or
                //if the user's credentials are not correct - return an "ERROR" string



                 }
                catch(Exception e){
                    System.out.println("Exception in sign up button click: " + e.getMessage());
                }
            }
        });


    }
}