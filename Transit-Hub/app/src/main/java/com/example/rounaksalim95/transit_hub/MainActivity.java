package com.example.rounaksalim95.transit_hub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.*;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mLinearLayout;

    // JSONArray that holds the different garage Json objects
    private JSONArray garageHolder;

    // JSONObject that holds the Json data for the appropriate garage
    private JSONObject garage;

    // JSONArray that holds the Json data for the floors in the garage
    private JSONArray floors;

    // JSONObject that holds each individual floor
    private JSONObject floor;

    // Name of the garage
    private String garageName;

    // Id of the garage
    private int id;

    // Default value used while extracting id from intent
    private final int DEFAULT_VALUE = -1;

    // Preserves a link to the menu
    private Menu optionsMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLinearLayout = (LinearLayout) findViewById(R.id.floorView);
        Intent intent = getIntent();

        // Get the id of the garage
        id = intent.getIntExtra("id", DEFAULT_VALUE);

        // Get the JsonArray String data present in the intent
        try {
            garageHolder = new JSONArray(intent.getStringExtra("garages"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            displayFloors();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Cache the menu
        optionsMenu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                // Get back to the Start activity
                finish();
                startActivity(new Intent(this, Start.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Displays buttons for all the floors in the garage
     * @throws JSONException
     */
    public void displayFloors() throws JSONException {

        // Get the Json data for the appropriate garage
        garage = garageHolder.getJSONObject(id);

        floors = garage.getJSONArray("floors");

        int floorNumber;

        if (floors != null) {

            // Loop through all the garages in the database and display buttons for them
            for (int i = 0; i < floors.length(); ++i) {

                Button button = new Button(this);

                // Get the floor number from the JSONArray
                floor = floors.getJSONObject(i);
                floorNumber = floor.getInt("floorNumber");

                button.setText("Floor Number: " + floorNumber);

                button.setId(i);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), Parking_Space_Activity.class);
                        try {
                            intent.putExtra("floor", floors.getJSONObject(view.getId()).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

}
