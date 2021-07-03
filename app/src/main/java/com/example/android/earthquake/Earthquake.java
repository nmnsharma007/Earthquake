package com.example.android.earthquake;

public class Earthquake {

    private final double mMagnitude;// magnitude of earthquake
    private final String mLocation;// location of earthquake
    private final Long mDateTime;// date and time in milliseconds
    private final String mUrl; // url to view the additional details of the earthquake

    // constructor for initialising the earthquake information
    public Earthquake(double magnitude,String location,Long dateTime,String url){
        mMagnitude = magnitude;
        mLocation = location;
        mDateTime = dateTime;
        mUrl = url;
    }

    // get the magnitude of earthquake
    public double getMagnitude(){
        return mMagnitude;
    }

    // get the location of earthquake
    public String getLocation(){
        return mLocation;
    }

    // get the number of milliseconds
    public Long getDateTime() {
        return mDateTime;
    }

    // get the url
    public String getUrl() {
        return mUrl;
    }
}
