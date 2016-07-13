package com.example.rounaksalim95.transit_hub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Start extends AppCompatActivity {

    // Holds reference to the LinearLayout inside the ScrollView to add buttons dynamically
    private LinearLayout mLinearLayout;

    // Holds the Json objects for the garages
    private JSONArray garageHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Cache the LinearLayout
        mLinearLayout = (LinearLayout) findViewById(R.id.garageView);

        try {
            getGarageInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void test(final JSONArray garageJson) throws JSONException {

        JSONObject garage;

        String garageName;

        if (garageJson != null) {

            // Loop through all the garages in the database and display buttons for them
            for (int i = 0; i < garageJson.length(); ++i) {

                Button button = new Button(this);

                // Get the name of the garage from the JsonArray's JsonObject
                garage = garageJson.getJSONObject(i);
                garageName = garage.getString("garageName");

                button.setText("This is : " + garageName);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("garage", garageJson.toString());
                        startActivity(intent);
                    }
                });

                mLinearLayout.addView(button);

            }
        } else {

            // Create a TextView to display message if no garages present
            TextView textView = new TextView(this);
            textView.setText("There are no garages to display. Please check back later. Thanks!");
            textView.setTextSize(30);
            mLinearLayout.addView(textView);
        }

    }


    /**
     * Method that gets the Json data using the REST API and processes it
     */
    public void getGarageInfo() throws JSONException {

        TransitRestClient.get("readings/", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                System.out.println("Wow, we're here!!!");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                JSONObject firstEvent = null;

                JSONArray holder;
                try {
                    firstEvent = (JSONObject) timeline.get(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(firstEvent.toString());
                /*ParkingGarage parkingGarage = gson.fromJson(firstEvent.toString(), ParkingGarage.class);
                System.out.println("This is the json to java object: " + parkingGarage + "\n");

                String result = null, lhsSpots = null, rhsSpots = null, floors = null;
                try {
                    result = firstEvent.getString("garageName");
                    lhsSpots = firstEvent.getString("lhsSpots");
                    rhsSpots = firstEvent.getString("rhsSpots");
                    floors = firstEvent.getString("floors");
                } catch (JSONException e) {
                    e.printStackTrace();
                } */

                try {
                    holder = firstEvent.getJSONArray("garages");
                    System.out.println(holder);

                    JSONObject jsonGarage = holder.getJSONObject(0);
                    System.out.println(jsonGarage);
                    JSONArray jsonFloors = jsonGarage.getJSONArray("floors");
                    System.out.println(jsonFloors);

                    test(holder);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
