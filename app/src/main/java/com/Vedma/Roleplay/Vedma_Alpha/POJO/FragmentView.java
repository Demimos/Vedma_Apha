package com.Vedma.Roleplay.Vedma_Alpha.POJO;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.PropertyItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FragmentView {
    @SerializedName("Name")
    @Expose
    public String Name;
    @SerializedName("Properties")
    @Expose
    public List<PropertyItem> Properties;

    public ArrayList<PropertyItem> getProperties() {
        return new ArrayList<>(Properties);
    }
}
