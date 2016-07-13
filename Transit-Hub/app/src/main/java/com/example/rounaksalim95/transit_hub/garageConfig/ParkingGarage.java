/**
 * Created by rounaksalim95 on 6/29/16.
 */

package com.example.rounaksalim95.transit_hub.garageConfig;

import com.example.rounaksalim95.transit_hub.garageConfig.ParkingFloor;
import com.example.rounaksalim95.transit_hub.garageConfig.Turn;

import java.util.List;

public class ParkingGarage {

    private String garageName;
    private List<ParkingFloor> floors;
    private List<Turn> turns;

    public ParkingGarage(String name, List<ParkingFloor> floor, List<Turn> turn) {
        this.garageName = name;
        this.floors = floor;
        this.turns = turn;
    }

    @Override
    public String toString() {
        return "Garage [name=" + garageName + ", floors=" + floors + ", turns=" + turns + "]";
    }
}
