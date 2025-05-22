package com.codekroy.tap2winbtc.model;
public class fetch_model {
    String email;
    String points;
   int status;
   String time;
  double btc;
    public fetch_model(String email,  String points, int status,String time,double btc ) {
        this.email = email;
        this.points = points;
        this.status = status;
        this.time=time;
        this.btc=btc;
    }
    public String getEmail() {
        return email;
    }

    public String getPoints() {
        return points;
    }
    public int getStatus() {
        return status;
    }
    public String gettime() {
        return time;
    }
    public double getbtc() {
        return btc;
    }
}
