package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeoLocation {
    @SerializedName("Lat")
    @Expose
    private double Lat ;
    @SerializedName("Lng")
    @Expose
    private double Lng ;
    @SerializedName("Acc")
    @Expose
    private double Acc ;
    @SerializedName("Time")
    @Expose
    private long Time ;
    public GeoLocation(double Lat, double Lng, double Acc, long Time)
    {
        this.Lat=Lat;
        this.Lng=Lng;
        this.Acc=Acc;
        this.Time=Time;
    }
}
