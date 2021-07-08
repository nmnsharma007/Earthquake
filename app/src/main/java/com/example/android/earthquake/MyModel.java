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

    /**
     * setup mutable live data object to observe the changes in data and only reload when preferences are changed in main activity class
     */
    public MutableLiveData<ArrayList<Earthquake>> getMutableLiveData(int minMag,int maxMag,String orderBy) {
        if(mMutableLiveData == null) {
            // call the API
            mMutableLiveData = new MutableLiveData<>();
            init(minMag,maxMag,orderBy);
        }
        return mMutableLiveData;
    }

    /**
     * make a network request on background thread for the earthquakes
     */
    public void init(int minMag,int maxMag,String orderBy) {
        // perform the network request on separate thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // create array list of earthquakes
                mMutableLiveData.postValue(QueryUtils.fetchEarthquakeData("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&minmagnitude="+minMag+ "&maxmagnitude="+maxMag+"&limit=3000&orderby="+orderBy));
            }
        });
        executorService.shutdown();

    }

    /**
     * fetching the earthquakes mutable list without calling the init method
     */
    public MutableLiveData<ArrayList<Earthquake>> getEarthquakes() {
        return mMutableLiveData;
    }

    /**
     * make the mutable live data null so that a new request can be made
     */
    public void nullifyMutableLiveData() {
        mMutableLiveData = null;
    }


}
