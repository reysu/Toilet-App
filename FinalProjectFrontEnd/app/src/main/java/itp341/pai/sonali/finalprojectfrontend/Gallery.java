package itp341.pai.sonali.finalprojectfrontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import itp341.pai.sonali.finalprojectfrontend.model.Photo;
import itp341.pai.sonali.finalprojectfrontend.model.SquaredImageView;
import itp341.pai.sonali.finalprojectfrontend.model.Toilet;
import itp341.pai.sonali.finalprojectfrontend.model.User;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.squareup.picasso.Picasso;
import static android.widget.ImageView.ScaleType.CENTER_CROP;

/**
 * Created by reysu on 11/17/17.
 */

public class Gallery extends AppCompatActivity {
    private int imageCounter = 0;
    private Toilet t = null;
    private List<Photo> photos;
    private List<String> imgUrls = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        Intent i = getIntent();
        t = (Toilet) i.getSerializableExtra("toilet");

        OkHttpClient client = new OkHttpClient();
        String url = "http://ec2-54-86-4-0.compute-1.amazonaws.com:8080/bathroom/" + t.getBathroomId() + "/photo";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String photoJson = response.body().string();
                        Gson gson = new Gson();
                        Type targetClassType = new TypeToken<ArrayList<Photo>>(){}.getType();
                        List<Photo> photostemp = gson.fromJson(photoJson,targetClassType);
                        for(int j=0;j<photostemp.size();j++){
                            imgUrls.add("http://ec2-54-86-4-0.compute-1.amazonaws.com:8080/" + photostemp.get(j).getUrl());

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                GridView gridview = (GridView) findViewById(R.id.gallery);
                                gridview.setAdapter(new ImageAdapter(Gallery.this));

                            }
                        });
                    } else {

                    }
                }
            }
        });



    }


    private class ImageAdapter extends BaseAdapter{
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imgUrls.size();
        }

        public Object getItem(int position) {
            return imgUrls.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            SquaredImageView view = (SquaredImageView) convertView;
            if (view == null) {
                view = new SquaredImageView(mContext);
                view.setScaleType(CENTER_CROP);
            }

            // Get the image URL for the current position.
            String url = (String)getItem(position);
            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(mContext) //
                    .load(url) //
                    .fit() //
                    .into(view);

            return view;

//            ImageView imageView;
//            if (convertView == null) {
//                // if it's not recycled, initialize some attributes
//                imageView = new ImageView(mContext);
//                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setPadding(8, 8, 8, 8);
//            } else {
//                imageView = (ImageView) convertView;
//            }
//            if (imageCounter < imgUrls.size())
//            {
//                new DownloadImageTask(imageView).execute("https://www.blog.google/static/blog/images/google-200x200.7714256da16f.png");//);
//               // System.out.println("ImageURL::::::::::: " + imgUrls.get(imageCounter))   imgUrls.get(imageCounter);
//                //imageView.setImageResource(imgUrls.get(imageCounter));
//                ++imageCounter;
//            }
//            return imageView;
         }
        }
        // references to our images
        // private String[] mThumbIds


//
//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//
//
//        }
//    }
}
