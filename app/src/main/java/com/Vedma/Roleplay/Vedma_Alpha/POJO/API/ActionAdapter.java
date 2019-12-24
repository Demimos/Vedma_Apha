package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.MyIntentType;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.ObjectType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ActionAdapter implements Serializable {
    @SerializedName("Id")
    @Expose
    private int Id;
    @SerializedName("IntentType")
    @Expose
    private int IntentType;

    @SerializedName("NeedSource")
    @Expose
    private boolean NeedSource;
    @SerializedName("NeedTarget")
    @Expose
    private boolean NeedTarget;
    @SerializedName("SourceByTag")
    @Expose
    private boolean SourceByTag;
    @SerializedName("TargetByTag")
    @Expose
    private boolean TargetByTag;

    @SerializedName("MultiSource")
    @Expose
    private boolean MultiSource;

    @SerializedName("MultiTarget")
    @Expose
    private boolean MultiTarget;

    @SerializedName("Target")
    @Expose
    private int Target;

    @SerializedName("TargetDescription")
    @Expose
    private String TargetDescription;
    @SerializedName("SourceDescription")
    @Expose
    private String SourceDescription;

    @SerializedName("Parameters")
    @Expose
    private List<DataItem> Parameters;

    @SerializedName("Shares")
    @Expose
    private int Shares;
    @SerializedName("ShareDescription")
    @Expose
    private String ShareDescription;

    @SerializedName("Characters")
    @Expose
    private List<GameCharacter> Characters;
    @SerializedName("GeoPositions")
    @Expose
    private List<GeoPosition> GeoPositions;

    public MyIntentType getIntentType() {
        switch (IntentType)
        {
            case 0: return MyIntentType.change;
            case 1: return MyIntentType.insight;
            case 2: return MyIntentType.share;
            default: throw new EnumConstantNotPresentException(MyIntentType.class, String.valueOf(IntentType));
        }
    }

    public int getId() {
        return Id;
    }

    public String getShareDescription() {
        return ShareDescription;
    }

    public String getTargetDescription() {
        return TargetDescription;
    }

    public String getSourceDescription() {
        return SourceDescription;
    }

    public List<GeoPosition> getGeoPositions() {
        return GeoPositions;
    }

    public List<GameCharacter> getCharacters() {
        List<GameCharacter> characters = new ArrayList<>();
        for (GameCharacter character:Characters) {
            if (character.ReflectedProperties!=null && character.ReflectedProperties.size()!=0)
                characters.add(character);
        }
        return characters;
    }

    public List<DataItem> getParameters() {
        return Parameters;
    }

    public boolean isMultiTarget() {
        return MultiTarget;
    }
    public boolean isMultiSource() {
        return MultiSource;
    }

    public boolean isTargetByTag() {
        return TargetByTag;
    }
    public boolean isSourceByTag() {
        return SourceByTag;
    }
    public boolean isNeedTarget() {
        return NeedTarget;
    }
    public boolean isNeedSource() {
        return NeedSource;
    }
    public ObjectType getTarget() {
        switch (Target)
        {
            case 0: return ObjectType.character;
            case 1: return ObjectType.geo;
            default: throw new EnumConstantNotPresentException(ObjectType.class, String.valueOf(Target));
        }
    }

    public ObjectType getShares() {
        switch (Shares)
        {
            case 0: return ObjectType.character;
            case 1: return ObjectType.geo;
            default: throw new EnumConstantNotPresentException(ObjectType.class, String.valueOf(Shares));
        }
    }

    public List<GeoPosition> getGeoObjects() {
        List<GeoPosition> geos = new ArrayList<>();
        for (GeoPosition geo:GeoPositions) {
            if (geo.getProperties()!=null && geo.getProperties().size()!=0)
                geos.add(geo);
        }
        return geos;
    }
}

