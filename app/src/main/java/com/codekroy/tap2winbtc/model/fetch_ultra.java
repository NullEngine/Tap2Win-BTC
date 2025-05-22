package com.codekroy.tap2winbtc.model;

public class fetch_ultra {
    String points;

    int points_id;
    public  fetch_ultra(String points, int points_id ) {

        this.points = points;
        this.points_id = points_id;
    }


    public String getPoints() {
        return points;
    }
    public int getID() {
        return points_id;
    }
}

