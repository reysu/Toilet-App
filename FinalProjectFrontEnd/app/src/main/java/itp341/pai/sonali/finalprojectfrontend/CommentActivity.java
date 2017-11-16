package itp341.pai.sonali.finalprojectfrontend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import itp341.pai.sonali.finalprojectfrontend.AddToiletActivity;
import itp341.pai.sonali.finalprojectfrontend.R;
import itp341.pai.sonali.finalprojectfrontend.model.Toilet;

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
                //TODO: SEND TO DATABASE
                setResult(Activity.RESULT_OK);
                finish();

            }
        });
    }
}
