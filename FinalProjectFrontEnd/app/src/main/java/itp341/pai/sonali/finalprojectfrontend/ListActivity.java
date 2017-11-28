package itp341.pai.sonali.finalprojectfrontend;

/**
 * Created by Sonali Pai on 11/10/2017.
 */

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

//import javax.xml.stream.Location;

import itp341.pai.sonali.finalprojectfrontend.model.GET_HTTP;
import itp341.pai.sonali.finalprojectfrontend.model.Toilet;
import itp341.pai.sonali.finalprojectfrontend.model.User;
//import sun.applet.Main;


public class ListActivity extends AppCompatActivity  {
    //declare widgets
    private ListView toiletList;
    private Button addButton;
    private ArrayAdapter<Toilet> adapter;
    private Toilet toilet;
    private List<Toilet> toilets;
    static final int DETAIL_INTENT_CONSTANT = 0;
    static final int ADD_TOILET_INTENT_CONSTANT = 1;
    FloatingActionButton fabButton;
    private boolean isGuest;
    private ImageView pinPageButton;

    public ListActivity() {
        // Required empty public constructor

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        SearchView searchView;

        MenuItem searchItem = menu.findItem(R.id.searchButton);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        MenuItem.OnActionExpandListener expandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                refresh();
                return false;
            }
        };

        return super.onCreateOptionsMenu(menu);
    }

    //listener to navigate to the pins page, and the sorting functions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.location_sort:
                //make a call to the server to get a list of toilets sorted by location
                //toilets = WHATEVER SERVER GIVES US;
                refresh();
                return true;
            case R.id.points_sort:
                //make a call to the server to get a list of toilets sorted by points
                //toilets = WHATEVER SERVER GIVES US;
                refresh();
                return true;
            case R.id.pin:
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_list_fragment);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        SearchView searchView;

        Window window = this.getWindow();

        //change color of status bar
        window.setStatusBarColor(getResources().getColor(android.R.color.black));
        Intent mainPgIntent = getIntent();
        isGuest = mainPgIntent.getBooleanExtra("guest",false);

        toilets = new ArrayList<Toilet>();
        //find views
       // addButton = (Button) findViewById(R.id.button_add);
        toiletList = (ListView)findViewById(R.id.toiletList);
        Toilet toilet = new Toilet("Cardinal Gardens","3131 Mcclintock Avenue",true,false);
       // Toilet toilet2 = new Toilet("Leavey Library","3131 123123123 Avenue",false,true);
        //Toilet toilet3 = new Toilet("Cafe 84","3131 Mcclintock Avenue",true,false);
        //Toilet toilet4 = new Toilet("Starbucks","3131 Mcclintock Avenue",true,true);
        //Toilet toilet5 = new Toilet("USC Village","3131 Mcclintock Avenue",false,false);
        toilet.setLatitude(34.018740);
        toilet.setLongitude(-118.291156);


        toilets.add(toilet);
       // toilets.add(toilet2);
        //toilets.add(toilet3);
        //toilets.add(toilet4);
        //toilets.add(toilet5);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://ec2-54-86-4-0.compute-1.amazonaws.com:8080/bathroom")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response  response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if(!response.isSuccessful()) {

                    }else {
                        String toiletJson = response.body().string();
                        Gson gson = new Gson();
                        Type targetClassType = new TypeToken<ArrayList<Toilet>>(){}.getType();
                        List<Toilet> toiletstemp = gson.fromJson(toiletJson,targetClassType);
//                        System.out.println(toiletstemp.get(0).getNameOfLocation());
                        for(int i=0;i<toiletstemp.size();i++){
                            toilets.add(toiletstemp.get(i));
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new ToiletListAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,toilets);
                                toiletList.setAdapter(adapter);
                            }
                        });


//                        adapter = new ToiletListAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,toilets);

                    }
                }
            }
        });

     //   adapter = new ToiletListAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,toilets);
     //   toiletList.setAdapter(adapter);
        fabButton = (FloatingActionButton) findViewById(R.id.listFab);
        fabButton.show();
        if(isGuest){
            fabButton.setVisibility(View.GONE);
        }else{
            fabButton.show();
        }
        //floating action button listener
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddToiletActivity.class);
                startActivityForResult(i, 0);
            }
        });
        toiletList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toilet t = toilets.get(position);
                        Intent i = new Intent(getApplicationContext(),DetailActivity.class);
                        i.putExtra("guest",isGuest);
                        i.putExtra("toilet",t);
                        startActivity(i);
                    }
                });

            }
        });
    }

//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.activity_toilet_list_fragment, container, false);
//      //  final Context context = v.getContext();
//        toilets = new ArrayList<Toilet>();
//        //find views
//        toiletList = (ListView)v.findViewById(R.id.toiletList);
//        Toilet toilet = new Toilet("Cardinal Gardens","3131 Mcclintock Avenue",true,true);
//        toilets.add(toilet);
//        adapter = new ToiletListAdapter(this, android.R.layout.simple_list_item_1,toilets);
//        //get all toilets close to location and load into the adapter
////        Loction location = CURRENT LOCATION OF USER USING GOOGLE MAPS API
////       Location location = null; //comment this line out
////        try {
////            URL url = new URL("http://localhost:8080/FinalProject/BathroomGPSServlet?location=" + location);
////            GET_HTTP get_http = new GET_HTTP(url);
////            String toiletList = get_http.getResponse();
////            Gson gson = new Gson();
////            toilets = (List<Toilet>) gson.fromJson(toiletList, Toilet.class);
////            adapter = new ToiletListAdapter(v.getContext(), R.layout.activity_listtoilets, toilets);
////        }
////        catch(IOException ioe){
////            System.out.println("ioe in list activity: " + ioe.getMessage());
////        }
//     //toiletList.setAdapter(adapter);
//
//        return v;
//    }


    private class ToiletListAdapter extends ArrayAdapter<Toilet> {
        int pos = 0;

        //default constructor
        public ToiletListAdapter(Context c, int resId, List<Toilet> movies) {
            super(c, resId, movies);
        }

        //getview generates A SINGLE ROW
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //step 1 -- inflate view (row) if necessary
            if (convertView == null) {      //this means row has NEVER been created
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.activity_listtoilets,
                        null
                );
            }
            //step 2 -- get references to XML views in the row
            TextView toiletName = (TextView) convertView.findViewById(R.id.list_toiletName);
            final TextView toilet_points = (TextView) convertView.findViewById(R.id.toilet_points);
            ImageView toiletImage = (ImageView) convertView.findViewById(R.id.toiletImage);
            ImageView accessibleImage = (ImageView) convertView.findViewById(R.id.accesibleIconList);
            ImageView keyImage = (ImageView) convertView.findViewById(R.id.keyIconList);
            final ImageView upImage = (ImageView) convertView.findViewById(R.id.upArrow);
            final ImageView downImage = (ImageView) convertView.findViewById(R.id.downArrow);

            boolean accessibleBool = false;
            boolean keyBool = false;
            //step 3 -- get the new itp341.pai.sonali.a9.model data
            toilet = getItem(position);      //getting the specified movie FROM the adapter
            upImage.setTag(position);
            downImage.setTag(position);
            accessibleBool = toilet.isHasDisabilityAccomodations();
            keyBool = toilet.isRequiresKey();
            if(accessibleBool){
                accessibleImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            }else{
                accessibleImage.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            }
            if(keyBool){
                keyImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            }else{
                keyImage.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            }
            //step 4 -- load the data from the itp341.pai.sonali.a9.model to the view
            toiletName.setText(toilet.getNameOfLocation());
            //SET THE IMAGE TO AN IMAGE FROM GOOGLE MAPS
            //toiletImage.setImageResource(IMAGE RESOURCE);
            toilet_points.setText(toilet.getPoints()+ " POINTS");
            if(isGuest){

                upImage.setVisibility(View.GONE);
                downImage.setVisibility(View.GONE);
            }
            upImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = (Integer)upImage.getTag();
                    toilet = toilets.get(i);
                    if(toilet.isUpArrowSelected()){ //if already selected
                        toilet.setPoints(toilet.getPoints()-1); //add points
                        toilet_points.setText(toilet.getPoints() + " POINTS");

                        upImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                        toilet.setUpArrowSelected(false);
                    }else {
                        if(toilet.isDownArrowSelected()){
                            toilet.setPoints(toilet.getPoints() + 1); //add points
                        }
                        toilet.setPoints(toilet.getPoints() + 1); //add points
                        toilet_points.setText(toilet.getPoints() + " POINTS");

                        upImage.setColorFilter(Color.rgb(255, 86, 0), PorterDuff.Mode.SRC_ATOP);
                        downImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                        toilet.setUpArrowSelected(true);
                        toilet.setDownArrowSelected(false);
                    }
                }
            });
            downImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = (Integer)downImage.getTag();
                    toilet = toilets.get(i);
                    if(toilet.isDownArrowSelected()){ //if already selected

                        toilet.setPoints(toilet.getPoints() + 1); //add points
                        toilet_points.setText(toilet.getPoints() + " POINTS");

                        downImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                        toilet.setDownArrowSelected(false);

                    }else{
                        if(toilet.isUpArrowSelected()){
                            toilet.setPoints(toilet.getPoints() - 1); //add points
                        }
                        toilet.setPoints(toilet.getPoints() - 1); //decrement points
                        toilet_points.setText(toilet.getPoints() + " POINTS");

                        downImage.setColorFilter(Color.rgb(147, 145, 255), PorterDuff.Mode.SRC_ATOP);
                        upImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                        toilet.setDownArrowSelected(true);
                        toilet.setUpArrowSelected(false);
                    }
                }
            });

            return convertView;
        }
    }

    //refresh method
    public void refresh(){
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });
        super.onActivityResult(requestCode, resultCode, data);
    }
}






















