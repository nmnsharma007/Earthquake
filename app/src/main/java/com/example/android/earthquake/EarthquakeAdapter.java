package com.example.android.earthquake;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.graphics.drawable.GradientDrawable;

/**
 *  Create the basic adapter extending from RecyclerView.Adapter
 *  we specify the custom ViewHolder which gives us access to our views
 */
public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.ViewHolder> {

    // store a member variable for the earthquakes
    private final ArrayList<Earthquake> mEarthquakes;
    private final Context mContext;
    private ItemClickListener clickListener;
    //pass in the earthquakes array list into the constructor
    public EarthquakeAdapter(Context context,ArrayList<Earthquake> earthquakes) {
        super();
        mEarthquakes = earthquakes;
        mContext = context;
    }
    /**
    * Return the formatted date string from a date object
     */

    private String formatDate(Date dateObj) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return  simpleDateFormat.format(dateObj);
    }

    /**
    * Return the formatted time string from a date object
     */
    private String formatTime(Date dateObj) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        return simpleDateFormat.format(dateObj);
    }

    private String offset(String location) {
        // finding occurrence of ',' to check if offset exists already
        int index = location.indexOf(",");
        if(index == -1){
            return "Near the";
        }
        else{
            return location.substring(0,index);
        }
    }

    /**
     * get the primary location name for the fetched location information
     */

    private String primaryLocation(String location) {
        // finding the length of the string
        int len = location.length();
        // finding occurrence of ',' to check if offset exists already
        int index = location.indexOf(",");
        if(index == -1){
            return location;
        }
        else{
            return location.substring(index+1,len);
        }
    }

    /**
     * get the color of the circle based on the magnitude
     */
    private int getMagnitudeColor(double magnitude) {
        if(magnitude <= 2){
            return ContextCompat.getColor(mContext,R.color.magnitude1);
        }
        if(magnitude <= 3){
            return ContextCompat.getColor(mContext,R.color.magnitude2);
        }
        if(magnitude <= 4){
            return ContextCompat.getColor(mContext,R.color.magnitude3);
        }
        if(magnitude <= 5){
            return ContextCompat.getColor(mContext,R.color.magnitude4);
        }
        if(magnitude <= 6){
            return ContextCompat.getColor(mContext,R.color.magnitude5);
        }
        if(magnitude <= 7){
            return ContextCompat.getColor(mContext,R.color.magnitude6);
        }
        if(magnitude <= 8){
            return ContextCompat.getColor(mContext,R.color.magnitude7);
        }
        if(magnitude <= 9){
            return ContextCompat.getColor(mContext,R.color.magnitude8);
        }
        if(magnitude <= 10){
            return ContextCompat.getColor(mContext,R.color.magnitude9);
        }
        return ContextCompat.getColor(mContext,R.color.magnitude10plus);
    }

    /**
     * override the needed methods for our custom adapter to work properly
     */
    @Override
    public EarthquakeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    // involves populating data into item through holder
    @Override
    public void onBindViewHolder(EarthquakeAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Earthquake earthquake = mEarthquakes.get(position);

        // formatting the magnitude to show only one digit after 0
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String output = decimalFormat.format(earthquake.getMagnitude());
        // set the proper background on the magnitude circle
        // fetch the background from the TextView which is gradient drawable
        GradientDrawable magnitudeCircle = (GradientDrawable)holder.magTextView.getBackground();
        // get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(earthquake.getMagnitude());

        // set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        // set the item views based on data model
        TextView magView = holder.magTextView;
        magView.setText(output);

        TextView offsetView = holder.offsetTextView;
        offsetView.setText(offset(earthquake.getLocation()));

        TextView locationView = holder.locationTextView;
        locationView.setText(primaryLocation(earthquake.getLocation()));

        // create a date object from the time in milliseconds
        Date dateObj = new Date(earthquake.getDateTime());
        String date = formatDate(dateObj);
        String time = formatTime(dateObj);

        TextView dateView = holder.dateTextView;
        dateView.setText(date);

        TextView timeView = holder.timeTextView;
        timeView.setText(time);

    }

    /**
     * Returns the total count of items in the array list
     */
    @Override
    public int getItemCount() {
        return mEarthquakes.size();
    }

    /**
     * setup a click listener for the recycler view
     */
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView magTextView;
        private TextView offsetTextView;
        private TextView locationTextView;
        private TextView dateTextView;
        private TextView timeTextView;

        /**
         * create a constructor that accepts entire list_item
         *          and does the view lookup to find each subview
         */
        public ViewHolder(View itemView) {
            // stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance
            super(itemView);
            magTextView = itemView.findViewById(R.id.magnitude);
            offsetTextView = itemView.findViewById(R.id.offset);
            locationTextView = itemView.findViewById(R.id.location);
            dateTextView = itemView.findViewById(R.id.date);
            timeTextView = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener != null) {
                        clickListener.onClick(v,getAbsoluteAdapterPosition());
                    }
                }
            });// bind the listener
        }
    }
}


