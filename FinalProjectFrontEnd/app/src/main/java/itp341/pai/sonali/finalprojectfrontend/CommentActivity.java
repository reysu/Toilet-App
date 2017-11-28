package itp341.pai.sonali.finalprojectfrontend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

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

import itp341.pai.sonali.finalprojectfrontend.AddToiletActivity;
import itp341.pai.sonali.finalprojectfrontend.R;
import itp341.pai.sonali.finalprojectfrontend.model.Comment;
import itp341.pai.sonali.finalprojectfrontend.model.Toilet;
import itp341.pai.sonali.finalprojectfrontend.model.User;

/**
 * Created by reysu on 11/16/17.
 */

public class CommentActivity extends AppCompatActivity{
    Button addCommentButton;
    EditText comment;
    protected void onCreate(Bundle savedInstanceState) {
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        Window window = this.getWindow();

        //change color of status bar
        window.setStatusBarColor(getResources().getColor(android.R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_form);
        Intent i = getIntent();
        Toilet t = (Toilet)i.getSerializableExtra("toilet");
        final long bathroomId = t.getBathroomId();
        System.out.println("TOILET ID: " + t.getBathroomId());
        addCommentButton = (Button) findViewById(R.id.addCommentButton);
        comment = (EditText)findViewById(R.id.commentText);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String commentText = comment.getText().toString();
                        OkHttpClient client = new OkHttpClient();
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        int userId = 1;
                        System.out.println("USER ID: " + userId);
                        Comment newComment = new Comment("", commentText, userId, bathroomId);
                        String json = new GsonBuilder().create().toJson(newComment, Comment.class);
                        try {
                            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                            System.out.println("BATHROOM ID: " + bathroomId);
                            Request request = new Request.Builder()
                                    .url("http://ec2-54-86-4-0.compute-1.amazonaws.com:8080/bathroom/1/comment")
                                    .post(body)
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    System.out.println("failed");
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    try (ResponseBody responseBody = response.body()) {
                                        if (!response.isSuccessful()) {
                                            System.out.println("wasn't successful");
                                        } else {
                                            System.out.println("successful");
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        }
                                    }
                                }
                            });
                        } catch (Exception ie) {

                        }
                    }
                });

            }
        });
    }
}
