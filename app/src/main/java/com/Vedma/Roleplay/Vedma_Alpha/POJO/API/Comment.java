package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.IMessage;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static com.Vedma.Roleplay.Vedma_Alpha.POJO.API.NewsCapture.GetTimePast;

public class Comment implements IMessage {
    @SerializedName("Body")
    @Expose
    public String Body;
    @SerializedName("Author")
    @Expose
    public String Author;
    @SerializedName("DateTime")
    @Expose
    public String DateTime;
    @SerializedName("Likes")
    @Expose
    public int Likes;
    @SerializedName("DisLikes")
    @Expose
    public int DisLikes;
    @SerializedName("Liked")
    @Expose
    public boolean Liked;
    @SerializedName("DisLiked")
    @Expose
    public boolean DisLiked;
    public String getTitle() {
       return Author;
    }

    @Override
    public String getBody() {
        return Body;
    }

    @Override
    public String getTime() {
        Date d;
        String buff=DateTime;
        if (DateTime==null)
            return null;
        buff = buff.replace("T"," ");
        buff = buff.replace("."," ");
        SimpleDateFormat current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            d= current.parse(buff);
             SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM HH:mm", new Locale("rus"));
            return formatter.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
