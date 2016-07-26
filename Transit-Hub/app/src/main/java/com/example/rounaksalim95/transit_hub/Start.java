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

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Start extends AppCompatActivity {

    // Holds reference to the LinearLayout inside the ScrollView to add buttons dynamically
    private LinearLayout mLinearLayout;


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


    /**
     * Method that displays button pertaining to the garages
     * @param garageJson JSONArray that has all the data about garages
     * @throws JSONException
     */
    public void displayGarages(final JSONArray garageJson) throws JSONException {

        JSONObject garage;

        String garageName;

        if (garageJson != null) {

            // Loop through all the garages in the database and display buttons for them
            for (int i = 0; i < garageJson.length(); ++i) {

                Button button = new Button(this);

                // Get the name of the garage from the JsonArray's JsonObject
                garage = garageJson.getJSONObject(i);
                garageName = garage.getString("garageName");

                button.setText(garageName);

                button.setId(i);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("garages", garageJson.toString());
                        intent.putExtra("id", view.getId());
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
                JSONArray allEvents = timeline;

                // JSONArray that holds garages
                JSONArray holder;

                // JSONArray that has all the garages
                JSONArray garages = new JSONArray();

                // Store all the JSON data in the garages JSONArray
                for (int i = 0; i < allEvents.length(); ++i) {
                    try {
                        holder = allEvents.getJSONObject(i).getJSONArray("garages");

                        for (int j = 0; j < holder.length(); ++j) {
                            garages.put(holder.get(j));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    // Call the method that displays the buttons for the garages
                    displayGarages(garages);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                // Restart the activity
                finish();
                startActivity(getIntent());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
