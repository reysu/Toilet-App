package itp341.pai.sonali.finalprojectfrontend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import itp341.pai.sonali.finalprojectfrontend.model.Comment;
import itp341.pai.sonali.finalprojectfrontend.model.GET_HTTP;
import itp341.pai.sonali.finalprojectfrontend.model.Toilet;

/**
 * Created by Sonali Pai on 11/10/2017.
 */

public class DetailActivity extends AppCompatActivity {

    private int bathroomId = -1;
    private TextView bathroomNameView;
    private TextView bathroomDescView;
    private TextView addressView;
    private ListView commentsView;
    private Toilet bathroom;
    private ImageView disabledIcon;
    private ImageView keyIcon;
    FloatingActionButton fabButton;
    FloatingActionButton fabImage;
    FloatingActionButton fabComment;
    private ArrayAdapter<String> adap;
    private boolean isFABOpen;
    private boolean isGuest;
    private Toolbar mTopToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_toilet);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        mTopToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        //setSupportActionBar(mTopToolbar);
        mTopToolbar.setNavigationIcon(R.drawable.ic_image_white_24px);



        Window window = this.getWindow();

        //change color of status bar
        window.setStatusBarColor(getResources().getColor(android.R.color.black));


        Intent i = getIntent();
        final Toilet t = (Toilet)i.getSerializableExtra("toilet");
        isGuest = i.getBooleanExtra("guest",false);

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
        adap = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,t.getComments());
        commentsView.setAdapter(adap);
        if(t.isHasDisabilityAccomodations()){
            disabledIcon.setImageResource(R.drawable.ic_done_black_24px);
            disabledIcon.setColorFilter(Color.rgb(50,205,50));
        }else{
            disabledIcon.setImageResource(R.drawable.ic_close_black_24px);
            disabledIcon.setColorFilter(Color.RED);
        }
        if(t.isRequiresKey()){
            keyIcon.setImageResource(R.drawable.ic_done_black_24px);
            keyIcon.setColorFilter(Color.rgb(50,205,50));
        }else{
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
        }
        //floating action button listener
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    isFABOpen=true;
                    float deg = fabButton.getRotation() + 45F;
                    fabButton.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                    fabImage.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
                    fabComment.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
                }else{
                    float deg = fabButton.getRotation() + 45F;
                    fabButton.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                    isFABOpen=false;
                    fabImage.animate().translationY(0);
                    fabComment.animate().translationY(0);
                }
            }
        });
        fabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),CommentActivity.class);
                i.putExtra("toilet",t);
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

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_favorite) {
//           // Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
