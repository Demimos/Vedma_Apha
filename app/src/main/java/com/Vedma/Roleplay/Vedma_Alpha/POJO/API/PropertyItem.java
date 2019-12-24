package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.PropertyType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PropertyItem  implements Serializable {
    @SerializedName("Id")
    @Expose
    public int Id;
    @SerializedName("Title")
    @Expose
    private String Title;
    @SerializedName("Body")
    @Expose
    private String Body;
    @SerializedName("Value")
    @Expose
    private double Value;
    @SerializedName("PropertyType")
    @Expose
    private int Type;
    @SerializedName("Visual")
    @Expose
    private int VisualType;
    @SerializedName("NumericType")
    @Expose
    private int NumericType;
    @SerializedName("UpperBound")
    @Expose
    private int Upper;
    @SerializedName("LowerBound")
    @Expose
    private int Lower;

    public PropertyItem(String Title, String Body, int Type)
    {
        this.Title=Title;
        this.Body=Body;
        this.Type = Type;

    }

    public int getNumericType() {
        return NumericType;
    }

    public int getLower() {
        return Lower;
    }

    public int getUpper() {
        return Upper;
    }

    public int getVisualType() {
        return VisualType;
    }

    public void setValue(double value) {
        Value = value;
    }

    public double getValue() {
        return Value;
    }

    public PropertyType getType() {
        switch (Type)
        {
            case 0: return PropertyType.Text;
            case 1: return PropertyType.TextArray;
            case 2: return PropertyType.Number;
            case 3: return  PropertyType.Identity;
            default: throw new EnumConstantNotPresentException(PropertyType.class, String.valueOf(Type));
        }
    }

    public void setType(int type) { Type = type; }


    public String getBody() {
        return Body;
    }

    public String getTitle() {
        return Title;
    }


}
