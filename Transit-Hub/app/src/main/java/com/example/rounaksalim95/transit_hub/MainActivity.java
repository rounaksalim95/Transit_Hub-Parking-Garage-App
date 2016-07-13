package com.example.rounaksalim95.transit_hub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.*;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private TableLayout mTableLayout;

    // JSONArray that holds the different garage Json objects
    private JSONArray garageHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTableLayout = (TableLayout) findViewById(R.id.mainTable);

        // Get the JsonArray String data present in the intent
        Intent intent = getIntent();
        String jsonString = intent.getDataString();


        try {
            test();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.firstFloor) {
            return true;
        }
        if (id == R.id.secondFloor) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void test() throws JSONException {

        for (int i = 1; i < 10; i += 2) {
            // Create a table row to add to the TableLayout
            TableRow row = new TableRow(this);

            // Declare the layoutParams for the table row and textviews
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            TableRow.LayoutParams tvlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            TextView lhs = new TextView(this);
            lhs.setId(i);
            lhs.setTextSize(30);
            lhs.setText("My id is: " + Integer.toString(lhs.getId()));
            lhs.setLayoutParams(tvlp);
            row.addView(lhs);


            TextView rhs = new TextView(this);
            rhs.setId(i + 1);
            rhs.setTextSize(30);
            rhs.setText("My id is: " + Integer.toString(rhs.getId()));
            rhs.setLayoutParams(tvlp);
            row.addView(rhs);

            mTableLayout.addView(row);

        }

        getGarageInfo();

    }


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
                    garageHolder = firstEvent.getJSONArray("garages");
                    System.out.println(garageHolder);

                    JSONObject jsonGarage = garageHolder.getJSONObject(0);
                    System.out.println(jsonGarage);
                    JSONArray jsonFloors = jsonGarage.getJSONArray("floors");
                    System.out.println(jsonFloors);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
