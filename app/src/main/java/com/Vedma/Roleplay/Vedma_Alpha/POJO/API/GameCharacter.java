package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import android.service.autofill.RegexValidator;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.IEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GameCharacter implements Serializable, IEntity {
    @SerializedName("Id")
    @Expose
    public int Id;
    @SerializedName("CharId")
    @Expose
    public int CharId;
    @SerializedName("ReflectedProperties")
    @Expose
    public List<PropertyItem> ReflectedProperties;

    public int getReflectionId() {
        return Id;
    }

    @Override
    public int getReflectedId() {
        return getCharId();
    }

    public int getCharId() {
        return CharId;
    }

    public String getName() {
        for (PropertyItem i:
             ReflectedProperties) {
            if (i.getTitle().equalsIgnoreCase("Имя")||i.getTitle().equalsIgnoreCase("Name"))
            {
                return i.getBody();
            }
        }
        return "";
}

    @Override
    public String getSecondary() {
       return getPhone();
    }

    public String getPhone() {
        for (PropertyItem i:
                ReflectedProperties) {
            if (i.getTitle().equalsIgnoreCase("Телефон")||i.getTitle().equalsIgnoreCase("Phone"))
            {
                return i.getBody();
            }
        }
        return "";
    }
}
