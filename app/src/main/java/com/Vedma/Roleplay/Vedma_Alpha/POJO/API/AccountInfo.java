package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountInfo {
    @SerializedName("Name")
    @Expose
    public String Name;
    @SerializedName("Email")
    @Expose
    public String Email;
    @SerializedName("Password")
    @Expose
    public String Password;
    @SerializedName("Phone")
    @Expose
    public String Phone;
    @SerializedName("EmailSignal")
    @Expose
    public boolean EmailSignal;

}
