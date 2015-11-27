package com.example.android.newapp.model;

/**
 * Created by admin on 7/10/15.
 */
public class Coordinate
{
   /* private double lat;
    private double lng;

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
    }*/


    String type;
    double[] coordinates;

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setCoordinates(double[] coordinate)
    {
        this.coordinates = coordinate;
    }

    public double[] getCoordinates()
    {
        return coordinates;
    }

}
