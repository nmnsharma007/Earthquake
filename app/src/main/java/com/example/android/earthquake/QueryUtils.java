package com.example.android.earthquake;

import android.util.Log;

import com.example.android.earthquake.Earthquake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {


    public static final String LOG_TAG = "QueryUtils";

    /**
     * create a private constructor so that no instance of the class is created
     */
    private QueryUtils() {

    }

    /**
     * Query the USGS dataset and return an Earthquake object to represent an earthquake
     */
    public static ArrayList<Earthquake> fetchEarthquakeData(String requestUrl) {
        // create URL object
        URL url = createURL(requestUrl);
        // Perform HTTP Request and receives a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e) {
            Log.e(LOG_TAG,"Error closing input stream",e);
        }
        return extractFeaturesFromJson(jsonResponse);
    }

    /**
     *  Return new URL object from the given string URL
     */

    public static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        }
        catch (MalformedURLException m) {
            Log.e(LOG_TAG,"Error creating URL",m);
        }
        return url;
    }

    /**
     * Make a HTTP request for fetching the data from the USGS website
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // if url null, return
        if(url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection= (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000/*milliseconds*/);
            urlConnection.setConnectTimeout(15000/*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.v(LOG_TAG,"Network request made");

            // if the request was successful(response code 200)
            //then read the input stream and parse the output
            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else{
                Log.e(LOG_TAG,"Error Response code: " + urlConnection.getResponseCode());
            }
        }
        catch (IOException e) {
            Log.e(LOG_TAG,"Problem retrieving the earthquake JSON results",e);
        }
        finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws  IOException{
        StringBuilder stringBuilder = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while(line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */

    private static ArrayList<Earthquake> extractFeaturesFromJson(String jsonResponse) {
        if(jsonResponse.isEmpty()) {
            return  null;
        }
        ArrayList<Earthquake> earthquakes = new ArrayList<Earthquake>();
        try {
            // make a json object from the given string
            JSONObject jsonObject = new JSONObject(jsonResponse);
            // get the features array from the json object
            JSONArray jsonArray = jsonObject.getJSONArray("features");
            Log.v(LOG_TAG,"Size of data: " + jsonArray.length());
            for(int i = 0; i < jsonArray.length();++i) {
                // build up a list of Earthquake objects with the corresponding data.
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                // get properties json object
                JSONObject jsonObject2 = jsonObject1.getJSONObject("properties");
                double magnitude = jsonObject2.getDouble("mag");
                String place = jsonObject2.getString("place");
                // get the milliseconds
                Long dateTime = jsonObject2.getLong("time");
                // get the url for more information
                String url = jsonObject2.getString("url");
                Earthquake earthquake = new Earthquake(magnitude, place, dateTime, url);
                earthquakes.add(earthquake);
            }
        }
        catch (JSONException e) {
            Log.e(LOG_TAG,"Invalid Json",e);
        }
        return earthquakes;
    }

}