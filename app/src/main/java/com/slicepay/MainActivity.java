package com.slicepay;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.slicepay.models.AddToAdapter;
import com.slicepay.models.Model;
import com.slicepay.models.OnBottomReachedListener;
import com.slicepay.models.OuterModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    RecyclerView recyclerView;
    GridLayoutManager staggeredGridLayoutManager;
    GridAdapter gridApdapter;
    ArrayList<AddToAdapter> addToAdapters;
    ProgressBar progressBar;
    ProgressBar progressbottom;
    int span=2; // for controlling no. of row and column in portait and lanscape mode.
    int pageNo=1; //for pagination loading 6 item from each page
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        progressbottom=(ProgressBar)findViewById(R.id.progressbottom);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        staggeredGridLayoutManager = new GridLayoutManager(this, span);
        staggeredGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        addToAdapters=new ArrayList<>();
        if(!isNetworkAvailable())
        {
            Toast.makeText(this, R.string.no_network,Toast.LENGTH_LONG).show();
        }
        else {
            getData();
        }

    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        if(pageNo>1)
        {
            progressBar.setVisibility(View.GONE);
            progressbottom.setVisibility(View.VISIBLE);
        }
        String url="https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=9f89151d82e427401680cd48dd2d5cf5&format=json&nojsoncallback=1&per_page=6&page="+pageNo;
        JsonObjectRequest jsonRequest=new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                progressbottom.setVisibility(View.GONE);
                if(response==null)
                    return;
                 parse(response);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                progressbottom.setVisibility(View.GONE);
            }
        });

        AppController.getInstance(this).addtoRequestQueue(jsonRequest);
    }

    private void parse(JSONObject response) {
        OuterModel outerModel = new Gson().fromJson(response.toString(), OuterModel.class);
        List<Model> photolist;
        if(outerModel!=null && outerModel.getPhotos()!=null)
        {
            photolist=outerModel.getPhotos().getPhoto();
            generateUrlList(photolist);
            if(gridApdapter==null) {
                gridApdapter = new GridAdapter(MainActivity.this, addToAdapters);
                recyclerView.setAdapter(gridApdapter);
            }
            else
            {
                gridApdapter.notifyDataSetChanged();
            }
            if(gridApdapter!=null)
            {
                gridApdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                    @Override
                    public void onBottomReached(int position) {
                        if(pageNo<5) {     // for contolling to only 5 page as we have to disply only 30 items
                            pageNo = pageNo + 1;
                            getData();
                        }

                    }
                });
            }
        }
        else
        {
            Toast.makeText(this, R.string.no_data_available,Toast.LENGTH_LONG).show();
        }

    }

    private void generateUrlList(List<Model> photolist) {
        if(photolist!=null && photolist.size()>0) {
            for (int i = 0; i < photolist.size(); i++) {
              String url="https://farm"+photolist.get(i).getFarm()+".staticflickr.com/"+photolist.get(i).getServer()+"/"+photolist.get(i).getId()+"_" +photolist.get(i).getSecret()+"_c.jpg";
                AddToAdapter data=new AddToAdapter();
                data.setUrl(url);
                data.setName(photolist.get(i).getTitle());
                addToAdapters.add(data);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem menuItem =menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText=newText.toLowerCase();
        ArrayList<AddToAdapter> al=new ArrayList<AddToAdapter>();
        for(AddToAdapter data: addToAdapters)
        {
            String name=data.getName().toLowerCase().trim();
            if(name.contains(newText))
            {
                al.add(data);
            }
        }
        if(gridApdapter!=null) {
            gridApdapter.setFilter(al);
        }
        return true;
    }

    //configuration changes
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT)
        {
            span=2;
            staggeredGridLayoutManager = new GridLayoutManager(this, span);
            staggeredGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
        }
        else if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            span=3;
            staggeredGridLayoutManager = new GridLayoutManager(this, span);
            staggeredGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
        }
    }
}
