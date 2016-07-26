package com.example.rounaksalim95.transit_hub;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Parking_Space_Activity extends AppCompatActivity {

    private TableLayout mTableLayout;

    // JSONObject that holds the Json data for the floor
    private JSONObject floor;

    // JSONArray that holds the Json data for the parking slots on the floor
    private JSONArray parkingSlots;

    // Number of parking slots on the floor
    private int numberOfSlots;

    // Drawables set to available and unavailable images
    private Drawable available;
    private Drawable unavailable;

    // Preserves a link to the menu
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_space);

        mTableLayout= (TableLayout) findViewById(R.id.mainTable);

        Intent intent = getIntent();
        // System.out.println("This is the floor Json: " + intent.getStringExtra("floor"));

        // Get the resources from the drawable
        available = ResourcesCompat.getDrawable(getResources(), R.drawable.available, null);
        unavailable = ResourcesCompat.getDrawable(getResources(), R.drawable.unavailable, null);

        // Set bounds for the drawables
        available.setBounds(0, 0, 135, 135);
        unavailable.setBounds(0, 0, 135, 135);

        try {
            floor = new JSONObject(intent.getStringExtra("floor"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            displayParkingSlots();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Method that displays all the parking slots and their availability status
     * @throws JSONException
     */
    private void displayParkingSlots() throws JSONException {

        // Get the Json data for the parking slots
        parkingSlots = floor.getJSONArray("parkingSpots");

        // Place holders for the text that has to go into the TextViews
        String lhsString, rhsString;

        if (parkingSlots != null) {

            // Loop through all the garages in the database and display buttons for them
            numberOfSlots = parkingSlots.length();
            for (int i = 1; i <= numberOfSlots; i+=2) {

                // Create a table row to add to the TableLayout
                TableRow row = new TableRow(this);

                // Declare the layoutParams for the table row and textviews
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                TableRow.LayoutParams tvlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView lhs = new TextView(this);
                TextView rhs = new TextView(this);
                lhs.setLayoutParams(tvlp);
                rhs.setLayoutParams(tvlp);

                // Set the size of the text in the text field
                lhs.setTextSize(22);
                rhs.setTextSize(22);

                // Check whether there are an odd number of parking slots
                // so that only the left side is printed out
                if (numberOfSlots % 2 != 0 && i >= numberOfSlots) {

                    lhsString = "Parking spot " + i + " : ";
                    lhs.setText(lhsString);

                    // Set the drawable to the right of the text
                    lhs.setCompoundDrawables(null, null,
                            getDrawable(parkingSlots.getJSONObject(i - 1)), null);

                } else {
                    lhsString = "Parking spot " + i + " : ";
                    rhsString = "Parking spot " + (i + 1) + " : ";
                    lhs.setText(lhsString);
                    rhs.setText(rhsString);

                    // Set the drawable to the right of the text
                    lhs.setCompoundDrawables(null, null,
                            getDrawable(parkingSlots.getJSONObject(i - 1)), null);

                    rhs.setCompoundDrawables(null, null,
                            getDrawable(parkingSlots.getJSONObject(i)), null);
                }

                // Add the views to the row
                row.addView(lhs);
                row.addView(rhs);

                // Add the row to the TableLayout
                mTableLayout.addView(row);
            }
        } else {

            // Throw a toast indicating that there are not parking slots on this floor
            // (if that ever happens)
            Toast.makeText(this, "Surprisingly there are no parking slots on this floor",
                    Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Sets the drawable to the appropriate image bases on the availability of the parking slots
     * @param slot The JSONObject that contains information about the parking slot
     * @return Returns the appropriate drawable
     */
    private Drawable getDrawable(JSONObject slot) throws JSONException {
        if (slot.getString("Availability").equals("Available")) {
            return available;
        } else {
            return unavailable;
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

}
