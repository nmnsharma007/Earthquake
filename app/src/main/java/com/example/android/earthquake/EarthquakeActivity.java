package com.example.android.earthquake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EarthquakeActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private MyModel mMyModel;
    private ViewModelProvider mViewModelProvider;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private EarthquakeAdapter mEarthquakeAdapter;
    private ProgressBar mProgressBar;
    private ConnectivityManager mConnectivityManager;
    private int minMag;
    private int maxMag;
    private String orderBy;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mEmptyView = findViewById(R.id.empty_view);
        // get the progress bar to indicate loading
        mProgressBar = findViewById(R.id.loading_bar);
        // set a view model provider for the current activity
        mViewModelProvider = new ViewModelProvider(this);
        // get the view model for the class
        mMyModel = mViewModelProvider.get(MyModel.class);
        minMag = 0;
        maxMag = 10;
        orderBy = "time";
        // get the shared preferences file
        SharedPreferences preferences = getSharedPreferences("preferences",Context.MODE_PRIVATE);
        // reading from shared preferences
        minMag = preferences.getInt("Minimum Magnitude",0);
        maxMag = preferences.getInt("Maximum Magnitude",0);
        orderBy = preferences.getString("orderBy","Descending Time");
        // generate the parameter corresponding to the order chosen
        orderBy = findValue(orderBy);
        // find a reference to the {@link RecyclerView} in the layout
        mRecyclerView = findViewById(R.id.earthquakes);
        mConnectivityManager = getSystemService(ConnectivityManager.class);
        if(mConnectivityManager.getActiveNetwork() == null && mMyModel.getEarthquakes() == null) {
            mEmptyView.setText("No internet available");
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.VISIBLE);
            fetchData();
        }
        mHandler = new Handler(Looper.getMainLooper());
        fetchEarthquakes();
        Log.e(LOG_TAG,"Visibility changed");
    }

    public void fetchEarthquakes() {
        // setup network listener for when some network connection gets active
        mConnectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(Network network) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mEmptyView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        fetchData();
                    }
                });
            }
        });
    }

    // inflating the options menu to show settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    private void fetchData(){
        // fetch the list of earthquakes
        mMyModel.getMutableLiveData(minMag,maxMag,orderBy).observe(EarthquakeActivity.this, new Observer<ArrayList<Earthquake>>() {
            @Override
            public void onChanged(ArrayList<Earthquake> earthquakes) {
                Log.v(LOG_TAG,"fetching the data");
                // set up recycler view with this data, this will work even if you rotate the device
                setUpRecyclerView(earthquakes);
            }
        });
    }

    private void setUpRecyclerView(ArrayList<Earthquake> earthquakes) {
        if(earthquakes == null || earthquakes.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setText(R.string.no_data_available);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            // create adapter passing in the earthquake data
            mEarthquakeAdapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);
            // attach the adapter to the recyclerView to populate the items
            mRecyclerView.setAdapter(mEarthquakeAdapter);
            // set the layout manager to position the items
            mRecyclerView.setLayoutManager(new LinearLayoutManager(EarthquakeActivity.this));
            Log.e(LOG_TAG,"Recycler view about to be setup");
            // click listener for when an item is clicked
            mEarthquakeAdapter.setClickListener((view, position) -> searchWeb(earthquakes.get(position).getUrl()));
        }
    }

    // for handling selection from the options menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(EarthquakeActivity.this,SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    private void searchWeb(String url) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY,url);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String findValue(String orderBy) {
        switch (orderBy) {
            case "Descending Time":
                return "time";
            case "Ascending Time":
                return "time-asc";
            case "Ascending Magnitude":
                return "magnitude-asc";
            case "Descending Magnitude":
                return "magnitude";
        }
        return "time";
    }
}

//https://guides.codepath.com/android/using-the-recyclerview
// https://guides.codepath.com/android/Storing-and-Accessing-SharedPreferences
// https://guides.codepath.com/android/using-the-app-toolbar