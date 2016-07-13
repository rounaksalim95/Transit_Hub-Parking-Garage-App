/**
 * Created by rounaksalim95 on 6/29/16.
 */


package com.example.rounaksalim95.transit_hub.garageConfig;

import java.util.List;

public class Camera_View {
    private int floorNumber;
    private List<Integer> lhsSpots;
    private List<Integer> rhsSpots;

    // Use these in the future
	/* private int x_location;
	private int y_location; */
    private String identifier;

    public Camera_View(String id, int floorNum, List<Integer> lhsSpots, List<Integer> rhsSpots) {
        this.identifier = id;
        this.floorNumber = floorNum;
        this.lhsSpots = lhsSpots;
        this.rhsSpots = rhsSpots;
    }
}