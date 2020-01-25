/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.particulatematterapp.model;

import javax.persistence.*;

@Entity
@Table(name = "data_2020_01")
public class DataRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String time;
    private double pm2_5;
    private double pm10;
    private double temp;
    private double humidity;
    private double pressure;
    private double gps_lat;
    private double gps_lng;
    private double gps_alt;
    private String note;

    public DataRecord() {}

    public DataRecord(String time, double pm2_5, double pm10, double temp, double humidity, double pressure, double gps_lat, double gps_lng, double gps_alt, String note) {
        this.time = time;
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
        this.temp = temp;
        this.humidity = humidity;
        this.pressure = pressure;
        this.gps_lat = gps_lat;
        this.gps_lng = gps_lng;
        this.gps_alt = gps_alt;
        this.note = note;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public double getP1() {
        return pm2_5;
    }
    public void setP1(double pm2_5) {
        this.pm2_5 = pm2_5;
    }

    public double getP2() {
        return pm10;
    }
    public void setP2(double pm10) {
        this.pm10 = pm10;
    }

    public double getTemp() {
        return temp;
    }
    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getHumidity() {
        return humidity;
    }
    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }
    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getGpsLat() {
        return gps_lat;
    }
    public void setGpsLat(double gps_lat) {
        this.gps_lat = gps_lat;
    }

    public double getGpsLng() {
        return gps_lng;
    }
    public void setGpsLng(double gps_lng) {
        this.gps_lng = gps_lng;
    }

    public double getGpsAlt() {
        return gps_alt;
    }
    public void setGpsAlt(double gps_alt) {
        this.gps_alt = gps_alt;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}