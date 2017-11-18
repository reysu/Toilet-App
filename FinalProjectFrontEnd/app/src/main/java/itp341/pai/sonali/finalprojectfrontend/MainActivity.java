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

    //declare widgets
    private EditText usernameInput;
    private EditText passwordInput;
    private Button signUpButton;
    private Button signInButton;

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
        username = "";
        password = "";

        //listener for sign In button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ListActivity.class);
                startActivity(i);
//                username = usernameInput.getText().toString();
//                password = passwordInput.getText().toString();
//                //TO DO: send these to data base to verify if user exists
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            // get connection to the "SignIn" Servlet
//                            URL url = new URL("http://localhost:8080/FinalProject/SignInServlet");
//                            POST_HTTP post_http = new POST_HTTP(url);
//                            post_http.addParameters("username", username);
//                            post_http.addParameters("password", password);
//                            post_http.connect();
//
//                            // return user JSON as the response Text. If the user does not exist, or
//                            //if the user's credentials are not correct - return an "ERROR" string
//
//
//                            String userJson = post_http.getResponse();
//                            int userId = Integer.parseInt(userJson);
//
//
//                            if (userId==-1)
//                            {
//                                //To-Do: show error messages
//                                Toast.makeText(getApplicationContext(), "Username and/or password are incorrect", Toast.LENGTH_LONG).show();
//                            }
//                            else
//                            {
//                                URL url2 = new URL("http://localhost:8080/FinalProject/User?userId="+userId);
//                                GET_HTTP get_http = new GET_HTTP(url2);
//                                String userJsonValidate = get_http.getResponse();
//                                Gson gson = new Gson();
//                                User user = gson.fromJson(userJsonValidate, User.class);
//                                if(user == null){
//                                    Toast.makeText(getApplicationContext(), "ERROR THAT SHOULD NEVER HAPPEN", Toast.LENGTH_LONG).show();
//                                }
//                                else {
//                                    SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
//                                    editor.putString(USERNAME, username);
//                                    editor.putInt(USERID, userId);
//                                    Intent i = new Intent(getApplicationContext(), ListActivity.class);
//                                }
//                            }
//
//                        }catch (Exception e)
//                        {
//                            Log.d("Exception", e.toString());
//                        }
//                    }
//                });
//
//





                //if exists, launch an intent to the listview page of all toilets

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
                    URL url = new URL("http://localhost:8080/FinalProject/UserServlet");
                    POST_HTTP post_http = new POST_HTTP(url);
                    post_http.addParameters("username", username);
                    post_http.addParameters("password", password);
                    post_http.connect();

                    String response = post_http.getResponse();
                    if(response.equals("ERROR")){
                        //To-Do: show error messages
                        Toast.makeText(getApplicationContext(), "Choose a different username", Toast.LENGTH_LONG).show();
                    }
                    else{

                    }
                //get response and check id
                URL url2 = new URL("http://localhost:8080/FinalProject/SignInServlet");
                POST_HTTP post_http2 = new POST_HTTP(url);
                post_http2.addParameters("username", username);
                post_http2.addParameters("password", password);
                post_http2.connect();

                // return user JSON as the response Text. If the user does not exist, or
                //if the user's credentials are not correct - return an "ERROR" string


                String userJson = post_http2.getResponse();
                int userId = Integer.parseInt(userJson);
                    if (userId==-1)
                    {
                        //To-Do: show error messages
                        Toast.makeText(getApplicationContext(), "Username and/or password are incorrect", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        URL url3 = new URL("http://localhost:8080/FinalProject/User?userId="+userId);
                        GET_HTTP get_http = new GET_HTTP(url3);
                        String userJsonValidate = get_http.getResponse();
                        Gson gson = new Gson();
                        User newUser = gson.fromJson(userJsonValidate, User.class);
                        if(newUser == null){
                            Toast.makeText(getApplicationContext(), "ERROR THAT SHOULD NEVER HAPPEN", Toast.LENGTH_LONG).show();
                        }
                        else {
                            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                            editor.putString(USERNAME, username);
                            editor.putInt(USERID, userId);
                            Intent i = new Intent(getApplicationContext(), ListActivity.class);
                        }
                    }


                }
                catch(Exception e){
                    System.out.println("Exception in sign up button click: " + e.getMessage());
                }
            }
        });


    }
}