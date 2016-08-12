package com.example.rounaksalim95.transit_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;


public class Floor_Activity extends AppCompatActivity {

    private LinearLayout mLinearLayout;

    // JSONArray that holds the different garage Json objects
    private JSONArray garageHolder,data;

    // JSONObject that holds the Json data for the appropriate garage
    private JSONObject garage;

    // JSONArray that holds the Json data for the floors in the garage
    private JSONArray floors;

    // JSONObject that holds each individual floor
    private JSONObject floor;

    // Name of the garage
    private String garageName;

    // Name of floor (number)
    private int floorName;

    // Id of the garage
    private int id;

    // Default value used while extracting id from intent
    private final int DEFAULT_VALUE = -1;

    // Preserves a link to the menu
    private Menu optionsMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

        mLinearLayout = (LinearLayout) findViewById(R.id.floorView);
        Intent intent = getIntent();

        garageName = intent.getStringExtra("garageName");

        // Get the JsonArray String data present in the intent
        try {
            data = new JSONArray(intent.getStringExtra("garages"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        garageHolder = parseGarageData(data);

        // Get the Json data for the appropriate garage
        try {
            garage = getCorrectGarage(garageHolder, garageName);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // In case the garage whose floor we are in is removed
        if (garage != null) {
            try {
                displayFloors();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            this.finish();
            startActivity(new Intent(this, Start.class));
            Toast.makeText(this, "This garage has been removed!", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        // Store our shared preferences
        SharedPreferences sp = getSharedPreferences("ACTIVE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(this.getLocalClassName(), true);
        editor.putBoolean("Start", false);
        editor.putBoolean("Parking_Space_Activity", false);
        editor.putString("garageName", garageName);
        editor.apply();
    }


    @Override
    protected void onStop() {
        System.out.println("ONSTOP FOR FLOOR HAS BEEN CALLED");
        super.onStop();

        // Store our shared preferences
        /*SharedPreferences sp = getSharedPreferences("ACTIVE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(this.getLocalClassName(), false);
        editor.apply();*/
    }


    @Override
    protected void onDestroy() {
        System.out.println("ONDESTROY FOR FLOOR HAS BEEN CALLED");
        super.onDestroy();

        // Store our shared preferences
        SharedPreferences sp = getSharedPreferences("ACTIVE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(this.getLocalClassName(), false);
        editor.apply();
    }


    /**
     * Displays buttons for all the floors in the garage
     * @throws JSONException
     */
    public void displayFloors() throws JSONException {

        floors = garage.getJSONArray("floors");

        if (floors != null) {

            // Setup UI
            for (int i = 0; i < floors.length(); ++i) {
                Button button = new Button(this);
                button.setText("Floor Number: " + (i + 1));

                // Set ID to floor number
                button.setId(i + 1);

                // Set tag to garage name
                button.setTag(garageName);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), Parking_Space_Activity.class);
                        intent.putExtra("data", data.toString());
                        intent.putExtra("garageName", view.getTag().toString());
                        intent.putExtra("floorName", view.getId());
                        startActivity(intent);
                    }
                });

                mLinearLayout.addView(button);

            }
        } else {

            // Create a TextView to display message if no garages present
            TextView textView = new TextView(this);
            textView.setText("There are no floors to display. Please check back later. Thanks!");
            textView.setTextSize(30);
            mLinearLayout.addView(textView);
        }

    }


    /**
     * Gets the correct garage matching by name
     * @param garages JSONArray that contains all garages
     * @return JSONObject of appropriate garage
     */
    public static JSONObject getCorrectGarage(JSONArray garages, String garageName) throws JSONException {
        System.out.println(garageName);
        for (int i = 0; i < garages.length(); ++i) {
            System.out.println(garages.getJSONObject(i).getString("garageName"));
            if (garages.getJSONObject(i).getString("garageName").equals(garageName)) {
                return garages.getJSONObject(i);
            }
        }
        return null;
    }


    /**
     * Parses JSON data received so that floors can be displayed
     * @param data JSONArray that has the data from the database
     * @return Returns processed JSON data
     */
    public static JSONArray parseGarageData(JSONArray data) {
        System.out.println("THIS IS DATA : " + data);
        return Start.parseJson(data);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Start.class));
    }
}
