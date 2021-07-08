package com.example.android.earthquake;

import android.view.View;

/**
 * interface for setting up a click listener for recycler view
 * The Activity class will implement an interface for onClick event,
 * this interface will be passed to the RecyclerView Adapter class,
 * then the ViewHolder class in the RecyclerView will call onClick
 * method defined in the interface, which will pass the view and position of the clicked item in the
 * Activity class
 */

public interface ItemClickListener {
    void onClick(View view,int position);
}
