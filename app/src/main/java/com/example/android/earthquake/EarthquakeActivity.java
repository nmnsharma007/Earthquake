package com.example.android.earthquake;

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
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        mEmptyView = findViewById(R.id.empty_view);
        // get the progress bar to indicate loading
        mProgressBar = findViewById(R.id.loading_bar);
        // set a view model provider for the current activity
        mViewModelProvider = new ViewModelProvider(this);
        // get the view model for the class
        mMyModel = mViewModelProvider.get(MyModel.class);
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
        Handler handler = new Handler(Looper.getMainLooper());
        // setup network listener for when some network connection gets active
        mConnectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(Network network) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mEmptyView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        fetchData();
                    }
                });
            }
        });
        Log.e(LOG_TAG,"Visibility changed");
    }

    private void fetchData(){
        // fetch the list of earthquakes
        mMyModel.getmMutableLiveData().observe(EarthquakeActivity.this, new Observer<ArrayList<Earthquake>>() {
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

    private void searchWeb(String url) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY,url);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

//https://guides.codepath.com/android/using-the-recyclerview