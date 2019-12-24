package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import android.graphics.Color;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.IEntity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.CheckPoly;

public class GeoPosition implements Serializable,IEntity {
    @SerializedName("Id")
    @Expose
    private int Id;
    @SerializedName("ReflectionId")
    @Expose
    private int ReflectionId;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("Description")
    @Expose
    private String Description;
    @SerializedName("GeoId")
    @Expose
    private int GeoId;
    @SerializedName("Lat")
    @Expose
    private double Lat;
    @SerializedName("Lng")
    @Expose
    private double Lng;
    @SerializedName("PositionType")
    @Expose
    private int PositionType;
    @SerializedName("Rad")
    @Expose
    private double Rad;
    @SerializedName("LatLngs")
    @Expose
    private List<myLatLng> LatLngs;
    @SerializedName("Color")
    @Expose
    private int Color;
    @SerializedName("Border")
    @Expose
    private float Border;
    @SerializedName("Visible")
    @Expose
    private boolean Visible;
    @SerializedName("Transparency")
    @Expose
    private float Transparency;
    @SerializedName("Properties")
    @Expose
    private List<PropertyItem> Properties;
    @SerializedName("ONClick")
    @Expose
    private List<Ability> ONClick;
    @SerializedName("ONInteract")
    @Expose
    private List<Ability> ONInteract;
    @SerializedName("ONStep")
    @Expose
    private Ability ONStep;
    @SerializedName("ONEnter")
    @Expose
    private Ability ONEnter;
    @SerializedName("ONDwell")
    @Expose
    private Ability ONDwell;
    @SerializedName("ONExit")
    @Expose
    private Ability ONExit;

    public int getId() {
        return Id;
    }

    public float getTransparency() {
        return Transparency;
    }
    public boolean IsObject() {
        return Properties.size()!=0;
    }
    public List<PropertyItem> getProperties() {
        return Properties;
    }

    @Override
    public int getReflectionId() {
        return ReflectionId;
    }

    @Override
    public int getReflectedId() {
        return GeoId;
    }

    public enum GeoType implements Serializable{
        point,
        circle,
        area,
        route,
        geoJson
    }

    public GeoPosition() {

    }
    public enum MapMethod{
        onClick,
        onInterract
    }
    public float getBorder() {
        return Border;
    }

    public String getName() {
        return Name;
    }
    public String getDescription() {
        return Description;
    }
    @Override
    public String getSecondary() {
        return null;
    }

    public LatLng getLatLng() {
        return new LatLng(Lat,Lng);
    }
    public List<LatLng> getLatLngs() {
        List<LatLng> ll = new ArrayList<>();
        for(int i=0;i<LatLngs.size();i++)
        {
            ll.add(new LatLng(LatLngs.get(i).lat, LatLngs.get(i).lng));
        }
        return ll;
    }

    public List<Ability> getONInteract() {
        return ONInteract;
    }

    public Ability getONStep() {
        return ONStep;
    }

    public double getRad() {
        return Rad;
    }

    public List<Ability> getOnClick() {
        return ONClick;
    }

    public GeoType getType(){
        switch(PositionType)
        {
            case 0:return GeoType.point;
            case 1:return GeoType.circle;
            case 2:return GeoType.area;
            case 3:return GeoType.route;
            case 4:return GeoType.geoJson;
            default:throw new EnumConstantNotPresentException(GeoType.class, String.valueOf(PositionType));
        }
    }

    public int getColor() {
        return Color;
    }
    public int getColorRGBA() {
        String alpha = Integer.toHexString(Math.round(Transparency *255));
        if (alpha.length()<2)
            alpha="0"+alpha;
        StringBuilder color = new StringBuilder(Integer.toHexString(Color));
        while (color.length()<6)//Crazy shit
        {
            color.insert(0, "0");
        }
        return android.graphics.Color.parseColor("#"+alpha+color);
    }
    public Boolean getVisible() {
        return Visible;
    }


    public MarkerOptions getMapObjectMarker()
    {


        LatLng latLng=new LatLng(Lat,Lng);

        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        marker.title(Name);
        float[] hsv = new float[3];
       // String hexColor = "#"+Integer.toHexString(getColor());
       // int color = android.graphics.Color.parseColor(hexColor);

        android.graphics.Color.colorToHSV(getColorRGBA(),hsv);

        float hue = hsv[0];
        float sat = hsv[1];
        float val = hsv[2];

        marker.icon(BitmapDescriptorFactory.defaultMarker(hue));
       //TODO Log everything
        marker.visible(true);
        //TODO visibility
        marker.alpha(Transparency);
        return marker;
    }

    public CircleOptions getMapObjectCircle()
    {
        try {

            LatLng latLng=new LatLng(Lat,Lng);

            CircleOptions circle = new CircleOptions();
            circle.center(latLng);
            circle.radius(Rad);
            circle.strokeWidth(Border);
           // circle.strokeWidth(2f);
            circle.visible(true);//TODO
            circle.strokeColor(android.graphics.Color.BLACK);
            circle.fillColor(getColorRGBA());
            return circle;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }


    }
    public PolygonOptions getMapObjectPolygon()
    {
        if (LatLngs==null || LatLngs.size()<3)
            return null;
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.fillColor(getColorRGBA());
        polygonOptions.geodesic(true);
        if (!CheckPoly(getLatLngs()))
            LatLngs.add(LatLngs.get(0));
        polygonOptions.addAll(getLatLngs());

        return polygonOptions;
    }
    private class myLatLng implements Serializable{
        @SerializedName("lat")
        @Expose
        private double lat;
        @SerializedName("lng")
        @Expose
        private double lng;
    }
}
