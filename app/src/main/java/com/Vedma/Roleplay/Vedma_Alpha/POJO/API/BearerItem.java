package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BearerItem {
    @SerializedName("access_token")
    @Expose
    private String access_token;
    @SerializedName("token_type")
    @Expose
    private String token_type;
    @SerializedName("expires_in")
    @Expose
    private String expires_in;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName(".issued")
    @Expose
    private String issued;
    @SerializedName(".expires")
    @Expose
    private String expires;

    public String getAccess_token() {
        return access_token;
    }
}
