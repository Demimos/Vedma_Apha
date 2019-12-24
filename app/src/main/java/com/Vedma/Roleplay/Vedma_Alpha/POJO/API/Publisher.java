package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Publisher {
    @SerializedName("Id")
    @Expose
    private int Id;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("Address")
    @Expose
    private String Address;
    @SerializedName("PhoneNumber")
    @Expose
    private String PhoneNumber;
    @SerializedName("Email")
    @Expose
    private String Email;
    @SerializedName("AllowVideo")
    @Expose
    private boolean AllowVideo;
    @SerializedName("Tickets")
    @Expose
    private int Tickets;
    @SerializedName("CoverImg")
    @Expose
    private String CoverImg;
    @SerializedName("Articles")
    @Expose
    private List<NewsCapture> Articles;

    public boolean isVideoAllowed() {
        return AllowVideo;
    }

    public String getName() {
        return Name;
    }

    public int getTickets() {
        return Tickets;
    }

    public List<NewsCapture> getArticles() {
        return Articles;
    }

    public String getAddress() {
        return Address;
    }

    public String getCoverImg() {
        return CoverImg;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public int getId() {
        return Id;
    }
}
