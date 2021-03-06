package com.example.rounaksalim95.transit_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    // Holds name of garage
    private String garageName;

    // Default value for SharedPreferences int
    private final int DEFAULT_VALUE = -1;


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
        editor.putBoolean(getLocalClassName(), true);
        editor.putBoolean("Floor_Activity", false);
        editor.putBoolean("Parking_Space_Activity", false);
        System.out.println(this.getLocalClassName());
        editor.apply();
    }


    @Override
    protected void onStop() {
        super.onStop();

        /*// Store our shared preferences
        SharedPreferences sp = getSharedPreferences("ACTIVE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(this.getLocalClassName(), false);
        editor.apply();*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

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

        if (garageJson != null) {

            // Loop through all the garages in the database and display buttons for them
            for (int i = 0; i < garageJson.length(); ++i) {

                Button button = new Button(this);

                // Get the name of the garage from the JsonArray's JsonObject
                garage = garageJson.getJSONObject(i);
                garageName = garage.getString("garageName");

                System.out.println("THIS IS THE GARAGE NAME : " + garageName);

                button.setText(garageName);

                button.setTag(garageName);

                button.setId(i);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), Floor_Activity.class);
                        /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                        intent.putExtra("garages", data.toString());
                        intent.putExtra("garageName", view.getTag().toString());
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
                garages.put(allEvents.get(i));
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
                    JSONArray holder = null;
                    try {
                        holder = new JSONArray(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    System.out.println("WebSockets information : " + holder);
                    //Intent intent;
                    SharedPreferences sp = getSharedPreferences("ACTIVE", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    if (sp.getBoolean("Start", false)) {
                        Intent intent = new Intent();
                        intent = new Intent(getApplicationContext(), Start.class);
                        /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                        System.out.println("Going to start new Start activity");
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),
                                "Parking information has been updated!", Toast.LENGTH_SHORT).show();

                    } else if (sp.getBoolean("Floor_Activity", false)) {
                        Intent intent = new Intent();
                        System.out.println(sp.getBoolean("Floor_Activity", false));
                        int id = sp.getInt("garageID", DEFAULT_VALUE);
                        String garageName = sp.getString("garageName", "DEFAULT");
                        intent = new Intent(getApplicationContext(), Floor_Activity.class);
                        /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                        intent.putExtra("garages", holder.toString());
                        intent.putExtra("id", id);
                        intent.putExtra("garageName", garageName);
                        System.out.println("Going to start new Floor_Activity activity : " + garageName);
                        System.out.println("This is holder : " + holder);
                        // Set SharedPreference to true
                        editor.putBoolean("Floor_Activity", true);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),
                                "Parking information has been updated!", Toast.LENGTH_SHORT).show();

                    } else if (sp.getBoolean("Parking_Space_Activity", false)) {
                        Intent intent = new Intent();
                        intent = new Intent(getApplicationContext(), Parking_Space_Activity.class);
                        /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                        int garageID = sp.getInt("garageID", DEFAULT_VALUE);
                        int floorID = sp.getInt("floorID", DEFAULT_VALUE);
                        int floorName = sp.getInt("floorName", DEFAULT_VALUE);
                        String garageName = sp.getString("garageName", "DEFAULT");
                        intent.putExtra("garageID", garageID);
                        intent.putExtra("floorID", floorID);
                        intent.putExtra("data", holder.toString());
                        intent.putExtra("garageName", garageName);
                        intent.putExtra("floorName", floorName);
                        System.out.println("Going to start new Parking_Space_Activity activity");
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),
                                "Parking information has been updated!", Toast.LENGTH_SHORT).show();

                    } else {
                        System.out.println("NOTHING WAS TRUE?");
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
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }
}
