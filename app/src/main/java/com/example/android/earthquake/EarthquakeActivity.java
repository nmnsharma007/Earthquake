package com.example.android.earthquake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
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

public class EarthquakeActivity extends AppCompatActivity{
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
    private SharedPreferences sharedPreferences;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        // get the shared preferences file
        sharedPreferences = this.getSharedPreferences(
                getString(R.string.settingsFile),Context.MODE_PRIVATE);
        // Find the toolbar view inside the activity layout
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        minMag = -1;
        maxMag = -1;
        orderBy = "random";// random string to trigger fetching of data on creation of activity
        mEmptyView = findViewById(R.id.empty_view);
        // get the progress bar to indicate loading
        mProgressBar = findViewById(R.id.loading_bar);
        // set a view model provider for the current activity
        mViewModelProvider = new ViewModelProvider(this);
        // get the view model for the class
        mMyModel = mViewModelProvider.get(MyModel.class);
        // find a reference to the RecyclerView in the layout
        mRecyclerView = findViewById(R.id.earthquakes);
        mHandler = new Handler(Looper.getMainLooper());
        mConnectivityManager = getSystemService(ConnectivityManager.class);
        mConnectivityManager.registerDefaultNetworkCallback(
                new ConnectivityManager.NetworkCallback(){
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

    /**
     * checking if preferences were updated whenever activity is resumed, if yes
     * make a new network request based on updated preferences
     */

    @Override
    protected void onResume() {
        super.onResume();
        // reading from shared preferences
        int tempMinMag = sharedPreferences.getInt(getString(R.string.minimumMagnitude),0);
        int tempMaxMag = sharedPreferences.getInt(getString(R.string.maximumMagnitude),0);
        String tempOrderBy = sharedPreferences.
                getString(getString(R.string.orderBy),"Descending Time");
        if(tempMaxMag != maxMag || tempMinMag != minMag || !findBack(orderBy).equals(tempOrderBy)){
            minMag = tempMinMag;
            maxMag = tempMaxMag;
            orderBy = tempOrderBy;
            // generate the parameter corresponding to the order chosen
            orderBy = findValue(orderBy);
            mMyModel.nullifyMutableLiveData();
            mRecyclerView.setVisibility(View.GONE);
            init();
        }
    }

    /**
     * check if internet connection is available to , if yes make an API call else
     * tell the user about the missing internet connectivity
     */

    private void init() {
        if(mConnectivityManager.getActiveNetwork() == null && mMyModel.getEarthquakes() == null) {
            mEmptyView.setText(R.string.no_internet);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.VISIBLE);
            fetchData();
        }
    }

    /**
     * fetch the list of earthquakes and observe the changes in the data and screen configuration
     * set up recycler view with this data, this will work even if the device is rotated
     */
    private void fetchData(){
        mMyModel.getMutableLiveData(minMag,maxMag,orderBy).observe(EarthquakeActivity.this,
                new Observer<ArrayList<Earthquake>>() {
                    @Override
                    public void onChanged(ArrayList<Earthquake> earthquakes) {
                        Log.v(LOG_TAG,"fetching the data");
                        setUpRecyclerView(earthquakes);
                    }
                });
    }

    /**
     * inflating the options menu to show settings
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    /**
     * setup the the recycler view with the list or tell the user if no matching earthquakes were
     * found
     */

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
            mEarthquakeAdapter.setClickListener((view, position) ->
                    searchWeb(earthquakes.get(position).getUrl()));
        }
    }

    /**
     * for handling selection from the options menu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(EarthquakeActivity.this,SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    /**
     * if user clicks an earthquake on screen, direct him to the website for more information on
     * that earthquake
     */

    private void searchWeb(String url) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY,url);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * helper function to get the string corresponding to the user preference in spinner object so
     * that suitable network request can be made
     */
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

    /**
     * helper function to get string corresponding to the url query parameter and convert it into
     * string in spinner object
     */
    private String findBack(String temp) {
        switch (temp) {
            case "time":
                return "Descending Time";
            case "time-asc":
                return "Ascending Time";
            case "magnitude":
                return "Descending Magnitude";
            case "magnitude-asc":
                return "Ascending Magnitude";
        }
        return "Descending Time";
    }
}

