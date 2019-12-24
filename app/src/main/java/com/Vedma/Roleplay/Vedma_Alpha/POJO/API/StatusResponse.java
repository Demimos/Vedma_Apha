package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusResponse {
    @SerializedName("active")
    @Expose
    public boolean active;
    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public boolean isActive() {
        return active;
    }
}
