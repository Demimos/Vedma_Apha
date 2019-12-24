package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageView {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("quota")
    @Expose
    private Integer quota;

    public String getMessage() {
        return message;
    }

    public Integer getQuota() {
        return quota;
    }
}
