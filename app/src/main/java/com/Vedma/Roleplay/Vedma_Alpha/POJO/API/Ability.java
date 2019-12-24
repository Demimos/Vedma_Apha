package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Ability implements Serializable
{
    @SerializedName("Title")
    @Expose
    private String Title;
    @SerializedName("Id")
    @Expose
    private int Id;
    @SerializedName("PresetId")
    @Expose
    private int PresetId;
    @SerializedName("ChainId")
    @Expose
    private int ChainId;

    public  Ability()
    {}
    public Ability(String Title, int Id, int ChainId)
    {
        this.Title = Title;
        this.Id = Id;
        this.ChainId = ChainId;
    }

    public int getId() {
        return Id;
    }

    public int getPresetId() {
        return PresetId;
    }

    public int getChainId() {
        return ChainId;
    }

    public int getID() {
        return Id;
    }

    public String getTitle() {
        return Title;
    }



}