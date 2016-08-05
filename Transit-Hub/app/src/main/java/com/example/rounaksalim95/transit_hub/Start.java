package com.example.rounaksalim95.transit_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import cz.msebera.android.httpclient.Header;

public class Start extends AppCompatActivity {

    // Holds reference to the LinearLayout inside the ScrollView to add buttons dynamically
    private LinearLayout mLinearLayout;

    // Reference for WebSocket Client
    private WebSocketClient mWebSocketClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Cache the LinearLayout
        mLinearLayout = (LinearLayout) findViewById(R.id.garageView);

        // Make the WebSocket connection
        try {
            connectWebSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            getGarageInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Store our shared preferences
        SharedPreferences sp = getSharedPreferences("ACTIVE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(this.getLocalClassName(), true);
        editor.apply();
    }


    @Override
    protected void onStop() {
        super.onStop();

        // Store our shared preferences
        SharedPreferences sp = getSharedPreferences("ACTIVE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(this.getLocalClassName(), false);
        editor.apply();
    }


    /**
     * Method that displays button pertaining to the garages
     * @param garageJson JSONArray that has all the data about garages
     * @throws JSONException
     */
    public void displayGarages(final JSONArray garageJson, JSONArray data) throws JSONException {

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
                        intent.putExtra("garages", data.toString());
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

        TransitRestClient.get("api/devices/ch1/ParkingData", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {

                // Parse the JSON data
                JSONArray garages = parseJson(timeline);

                try {
                    // Call the method that displays the buttons for the garages
                    displayGarages(garages, timeline);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Processes data from all documents and puts it in one JSONArray
     * @param data JSON data about parking garages
     * @return Returns JSONArray containing garage data from all documents
     */
    public static JSONArray parseJson(JSONArray data) {
        JSONArray allEvents = data;

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

        return garages;
    }


    public void connectWebSocket() throws URISyntaxException {
        URI uri;
        // Connect to the local host at port 9000
        uri = new URI("ws://10.0.2.2:9000");

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("WebSocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(() -> {
                    // Testing
                    SharedPreferences sp = getSharedPreferences("ACTIVE", MODE_PRIVATE);
                    JSONArray holder = null;
                    try {
                        holder = new JSONArray(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("WebSocket", "Closed" + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.i("WebSocket", "Error" + ex.getMessage());
            }
        };
        mWebSocketClient.connect();
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
