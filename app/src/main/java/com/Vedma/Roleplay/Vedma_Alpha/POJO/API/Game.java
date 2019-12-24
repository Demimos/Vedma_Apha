package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Game {
//    @SerializedName("Key")
//    @Expose
//    private String Key;
    @SerializedName("Id")
    @Expose
    private String Id;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("Characters")
    @Expose
    private List<GameCharacter> Characters;

    public String getName() {
        return Name;
    }
    public int getCharacterCount()
    {
        return Characters.size();
    }

    public String getId() {
        return Id;
    }

    public List<GameCharacter> getCharacters() {
        return Characters;
    }
}
