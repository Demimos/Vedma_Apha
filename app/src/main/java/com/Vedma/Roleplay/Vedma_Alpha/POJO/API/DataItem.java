package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import android.support.annotation.Nullable;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.PropertyType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataItem implements Serializable {
    @Nullable
    @SerializedName("Description")
    @Expose
    public String Description;
    @SerializedName("Name")
    @Expose
    public String Name;
    @SerializedName("DataType")
    @Expose
    public int DataType;
    @Nullable
    @SerializedName("StringValue")
    @Expose
    public String StringValue;
    @Nullable
    @SerializedName("StringArrayValue")
    @Expose
    public List<String> StringArrayValue;
    @Nullable
    @SerializedName("NumericValue")
    @Expose
    public double NumericValue;
    @Nullable
    @SerializedName("IdentityId")
    @Expose
    public Integer IdentityId;

    public PropertyType getDataType() {
        switch (DataType)
        {
            case 0:return PropertyType.Text;
            case 1:return PropertyType.TextArray;
            case 2:return PropertyType.Number;
            case 3:return PropertyType.Identity;
            default: throw new EnumConstantNotPresentException(PropertyType.class, String.valueOf(DataType));
        }
    }
}
