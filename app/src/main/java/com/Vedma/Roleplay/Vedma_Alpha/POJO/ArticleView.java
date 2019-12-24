package com.Vedma.Roleplay.Vedma_Alpha.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArticleView {
    @SerializedName("Title")
    @Expose
    public String Title;
    @SerializedName("Preview")
    @Expose
    public String Preview;
    @SerializedName("Body")
    @Expose
    public String Body;
}
