package com.example.android.earthquake;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyModel extends ViewModel {

    private static final String LOG_TAG = "MyModel";
    public MutableLiveData<ArrayList<Earthquake>> mMutableLiveData;
    public ArrayList<Earthquake> mEarthquakes;

    public MutableLiveData<ArrayList<Earthquake>> getEarthquakes() {
        mMutableLiveData = new MutableLiveData<>();
        // call the API
        init();
        return mMutableLiveData;
    }

    public void init() {
        // perform the network request on separate thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // create array list of earthquakes
                mEarthquakes = QueryUtils.fetchEarthquakeData("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2021-06-01&limit=300");
                mMutableLiveData.postValue(mEarthquakes);
            }
        });
        executorService.shutdown();

    }


}//https://blog.mindorks.com/understanding-livedata-in-android
