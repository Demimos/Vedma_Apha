package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Invoker implements Serializable {
    @SerializedName("Id")
    @Expose
    public int Id;
    @SerializedName("Sources")
    @Expose
    public List<Integer> Sources;
    @SerializedName("Targets")
    @Expose
    public List<Integer> Targets;
    @SerializedName("Parameters")
    @Expose
    public List<DataItem> Parameters;
    @SerializedName("Shares")
    @Expose
    public List<Integer> Shares;

    public Invoker()
    {

        Sources = new ArrayList<>();

        Targets = new ArrayList<>();
        Parameters = new ArrayList<>();
        Shares = new ArrayList<>();
    }
}
