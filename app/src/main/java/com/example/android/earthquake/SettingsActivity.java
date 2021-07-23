package com.example.android.earthquake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * settings activity where the user can update the preferences and criteria for sorting the
 * earthquakes in some particular order
 */
public class SettingsActivity extends AppCompatActivity {

    private Button decMin;
    private Button incMin;
    private Button decMax;
    private Button incMax;
    private TextView minMagTextView;
    private TextView maxMagTextView;
    private Spinner orderSpinner;
    private Toolbar mToolbar;
    private Context mContext;

    /**
     * initialise the views, adapter and set up click listeners for the buttons
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        orderSpinner = (Spinner)findViewById(R.id.orderBy);
        // Create an ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.orderBy, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appear
        mToolbar = findViewById(R.id.toolbar_settings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        decMin = findViewById(R.id.decMinMagBtn);
        incMin = findViewById(R.id.incMinMagBtn);
        decMax = findViewById(R.id.decMaxMagBtn);
        incMax = findViewById(R.id.incMaxMagBtn);
        minMagTextView = findViewById(R.id.minMagView);
        maxMagTextView = findViewById(R.id.maxMagView);
        // get the context
        mContext = this;
        // Apply the adapter to the spinner
        orderSpinner.setAdapter(adapter);
        decMin = findViewById(R.id.decMinMagBtn);
        SharedPreferences sharedPreferences =
                this.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // set the value from the saved preferences to the text views
        minMagTextView.setText(String.valueOf(sharedPreferences.
                getInt(getString(R.string.minimumMagnitude),0)));
        maxMagTextView.setText(String.valueOf(sharedPreferences.
                getInt(getString(R.string.maximumMagnitude),0)));
        String chosenValue = sharedPreferences.getString
                (getString(R.string.orderBy),"Descending Time");
        int spinnerPosition = adapter.getPosition(chosenValue);
        orderSpinner.setSelection(spinnerPosition);
        //set up the click listeners for the buttons
        decMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minMagValue = Integer.parseInt(minMagTextView.getText().toString());
                if(minMagValue == 0){
                    // display a toast
                    Toast.makeText(mContext,"Cannot go below 0",Toast.LENGTH_SHORT).show();
                }
                else{
                    --minMagValue;
                }
                minMagTextView.setText(String.valueOf(minMagValue));
                editor.putInt(getString(R.string.minimumMagnitude),minMagValue);
                editor.apply();
            }
        });
        incMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxMagValue = Integer.parseInt(maxMagTextView.getText().toString());
                int minMagValue = Integer.parseInt(minMagTextView.getText().toString());
                if(minMagValue == maxMagValue){
                    // display a toast\
                    Toast.makeText(mContext,"Cannot go above maximum magnitude",Toast.LENGTH_SHORT).show();
                }
                else{
                    ++minMagValue;
                }
                minMagTextView.setText(String.valueOf(minMagValue));
                editor.putInt(getString(R.string.minimumMagnitude),minMagValue);
                editor.apply();
            }
        });
        decMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minMagValue = Integer.parseInt(minMagTextView.getText().toString());
                int maxMagValue = Integer.parseInt(maxMagTextView.getText().toString());
                if(maxMagValue == minMagValue){
                    // display a toast
                    Toast.makeText(mContext,"Cannot go below minimum magnitude",Toast.LENGTH_SHORT).show();
                }
                else{
                    --maxMagValue;
                }
                maxMagTextView.setText(String.valueOf(maxMagValue));
                editor.putInt(getString(R.string.maximumMagnitude),maxMagValue);
                editor.apply();
            }
        });
        incMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxMagValue = Integer.parseInt(maxMagTextView.getText().toString());
                if(maxMagValue == 10){
                    // display a toast
                    Toast.makeText(mContext,"Cannot go beyond 10",Toast.LENGTH_SHORT).show();
                }
                else{
                    ++maxMagValue;
                }
                maxMagTextView.setText(String.valueOf(maxMagValue));
                editor.putInt(getString(R.string.maximumMagnitude),maxMagValue);
                editor.apply();
            }
        });
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String orderBy = (String)parent.getItemAtPosition(position);
                editor.putString(getString(R.string.orderBy),orderBy);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String orderBy = "Descending Time";
                editor.putString(getString(R.string.orderBy),orderBy);
                editor.apply();
            }
        });
        // set up click listener for back button on toolbar and resume the main activity
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,EarthquakeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
}