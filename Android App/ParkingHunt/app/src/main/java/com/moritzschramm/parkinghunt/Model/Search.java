package com.moritzschramm.parkinghunt.Model;

/**
 * Created by Moritz on 30.08.2016.
 */
public class Search {

    private String id;

    private double lat, lng;
    private String city, streetName;

    public Search() {}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getStreetName() {
        return streetName;
    }
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
}
