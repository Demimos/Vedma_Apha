package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.IMessage;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryPage implements IMessage {
    @SerializedName("Id")
    @Expose
    private int Id;
    @SerializedName("Title")
    @Expose
    private String Title;
    @SerializedName("Body")
    @Expose
    private String Body;
    @SerializedName("DateTime")
    @Expose
    private String DateTime;
    @SerializedName("DiaryPageType")
    @Expose
    private int DiaryPageType;

    public DiaryPage(String title, String body) {
        Title = title;
        Body=body;
        DateTime = "2019-03-12T21:26:35.393";
    }


    public String getTitle() {
        return Title;
    }

    private String getDateTime() {
        return DateTime;
    }

    public int getDiaryPageType() {
        return DiaryPageType;
    }

    public String getBody() {
        return Body;
    }

    @Override
    public String getTime() {
        Date d;
        String buff=getDateTime();
        buff = buff.replace("T"," ");
        buff = buff.replace("."," ");
        SimpleDateFormat current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
        try {
            d= current.parse(buff);

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM HH:mm",new Locale("rus"));
            return formatter.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
