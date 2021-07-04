package com.example.android.earthquake;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyModel extends ViewModel {

    private static final String LOG_TAG = "MyModel";
    private MutableLiveData<ArrayList<Earthquake>> mMutableLiveData;

    public MutableLiveData<ArrayList<Earthquake>> getmMutableLiveData() {
        if(mMutableLiveData == null) {
            // call the API
            mMutableLiveData = new MutableLiveData<>();
            init();
        }
        return mMutableLiveData;
    }

    public void init() {
        // perform the network request on separate thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // create array list of earthquakes
                mMutableLiveData.postValue(QueryUtils.fetchEarthquakeData("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2021-06-01&limit=300"));
            }
        });
        executorService.shutdown();

    }

    // fetching the earthquakes mutable list without calling the init method
    public MutableLiveData<ArrayList<Earthquake>> getEarthquakes() {
        if(mMutableLiveData == null) {
            Log.v(LOG_TAG,"The mutable list is null");
        }
        return mMutableLiveData;
    }


}//https://blog.mindorks.com/understanding-livedata-in-android
// https://tudip.com/blog-post/livedata-android/
