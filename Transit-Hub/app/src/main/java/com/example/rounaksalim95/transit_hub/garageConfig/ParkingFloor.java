/**
 * Created by rounaksalim95 on 6/29/16.
 */


package com.example.rounaksalim95.transit_hub.garageConfig;

import com.example.rounaksalim95.transit_hub.garageConfig.Camera_View;

import java.util.List;

public class ParkingFloor {
    private int floorNumber;
    private int lhsSpots;
    private int rhsSpots;
    private List<Camera_View> cameras;

    public ParkingFloor(int floorNum, int lhsNum, int rhsNum, List<Camera_View> cameras) {
        this.floorNumber = floorNum;
        this.lhsSpots = lhsNum;
        this.rhsSpots = rhsNum;
        this.cameras = cameras;
    }
}
