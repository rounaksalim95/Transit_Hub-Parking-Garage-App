/**
 * Created by rounaksalim95 on 6/29/16.
 */

package com.example.rounaksalim95.transit_hub.garageConfig;

public class Turn {

    public enum TurnDirection {
        LEFT, RIGHT
    }

    private TurnDirection turn;
    private int from;
    private int to;

    public Turn(TurnDirection turn, int from, int to) {
        this.turn = turn;
        this.from = from;
        this.to = to;
    }
}
