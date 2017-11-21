package itp341.pai.sonali.finalprojectfrontend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import itp341.pai.sonali.finalprojectfrontend.AddToiletActivity;
import itp341.pai.sonali.finalprojectfrontend.R;
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
        final Toilet t = (Toilet)i.getSerializableExtra("toilet");
        addCommentButton = (Button) findViewById(R.id.addCommentButton);
        comment = (EditText)findViewById(R.id.commentText);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = comment.getText().toString();
                t.addComments(commentText);

                OkHttpClient client = new OkHttpClient();
                int userId = Integer.parseInt(MainActivity.USERID); //the current user adding comment

                RequestBody formBody = new FormEncodingBuilder().add("bathroomid", Long.toString(t.getBathroomId())).build();
                Request request = new Request.Builder()
                        .url("http://ec2-54-86-4-0.compute-1.amazonaws.com:8080/bathroom")
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if(!response.isSuccessful()) {
                            }else {
                                String userJsonValidate = response.body().string();

                                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                                i.putExtra("guest", false);
                                startActivity(i);
                            }
                        }
                    }
                });
                setResult(Activity.RESULT_OK);
                finish();

            }
        });
    }
}
