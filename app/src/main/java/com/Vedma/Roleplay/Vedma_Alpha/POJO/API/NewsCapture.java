package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewsCapture {
    @SerializedName("Id")
    @Expose
    private int Id;
    @SerializedName("PublisherId")
    @Expose
    private int PublisherId;
    @SerializedName("PublisherName")
    @Expose
    private String PublisherName;
    @SerializedName("Title")
    @Expose
    private String Title;
    @SerializedName("Preview")
    @Expose
    private String Preview;
    @SerializedName("Body")
    @Expose
    private String Body;
    @SerializedName("TitleImg")
    @Expose
    private String IMG;
    @SerializedName("VideoUrl")
    @Expose
    String VideoUrl;
    @SerializedName("Author")

    String Author;
    @SerializedName("DateTime")
    @Expose
    private String DateTime;

    public String getPublisherName() {
        return PublisherName;
    }

    public String getIMG() {
        return IMG;
    }

    public String getTitle() {
        return Title;
    }

    public int getPublisherId() {
        return PublisherId;
    }

    public String getBody() {
        return Body;
    }

    public String getPreview() {
        return Preview;
    }

    public int getId() {
        return Id;
    }

    public String getAuthor() {
        return Author;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public Date getDateTime(){ Date d;
    String buff=DateTime;
    buff = buff.replace("T"," ");
    buff = buff.replace("."," ");
    SimpleDateFormat current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
        try {
        return current.parse(buff);
    } catch (ParseException e) {
        e.printStackTrace();
        return null;
    }

    }
    public static String GetTime(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM HH:mm", new Locale("rus"));
        return formatter.format(d);
    }

    public static String GetTimePast(Date d)
    {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar date = Calendar.getInstance();
        date.setTime(d);
        if(now.get(Calendar.YEAR) - date.get(Calendar.YEAR)>0)
            return "более года назад";
        if (now.get(Calendar.MONTH) - date.get(Calendar.MONTH) > 1)
        {

            if (now.get(Calendar.MONTH) - date.get(Calendar.MONTH) < 5)
                return now.get(Calendar.MONTH) - date.get(Calendar.MONTH)+" месяца назад";
            return now.get(Calendar.MONTH) - date.get(Calendar.MONTH) + " месяцев назад";
        }
        if (now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH) > 0)
        {
            if (now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH)  == 1)
                return "вчера";
            if (now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH) < 5)
                return now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH) + " дня назад";
            if (now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH) < 21)
                return now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH) + " дней назад";
            if (now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH)== 21
                    || now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH)  == 31)
                return now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH)  + " день назад";
            if (now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH)  < 25)
                return now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH)  + " дня назад";
            return now.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH)  + " дней назад";
        }
        if (now.get(Calendar.HOUR) - date.get(Calendar.HOUR)  > 0)
        {
            if (now.get(Calendar.HOUR) - date.get(Calendar.HOUR)  == 1)
                return "час назад";
            if (now.get(Calendar.HOUR) - date.get(Calendar.HOUR)  < 5)
                return now.get(Calendar.HOUR) - date.get(Calendar.HOUR)  + " часа назад";
            if (now.get(Calendar.HOUR) - date.get(Calendar.HOUR)  < 21)
                return now.get(Calendar.HOUR) - date.get(Calendar.HOUR) + " часов назад";
            if (now.get(Calendar.HOUR) - date.get(Calendar.HOUR)  == 21)
                return now.get(Calendar.HOUR) - date.get(Calendar.HOUR) + " час назад";

            return now.get(Calendar.HOUR) - date.get(Calendar.HOUR)  + " часа назад";
        }
        if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) > 0)
        {
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) == 1)
                return "минуту назад";
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) < 5)
                return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минуты назад";
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) < 21)
                return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минут назад";
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) == 21 || now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) == 31
                    || now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) == 41 || now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) == 51)
                return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минуту назад";
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) < 25)
                return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минуты назад";
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) < 31)
                return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минут назад";
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) < 35)
                return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минуты назад";
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) < 41)
                return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минут назад";
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) < 45)
                return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минуты назад";
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) < 51)
                return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минут назад";
            if (now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) < 55)
                return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минуты назад";
            return now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE) + " минут назад";
        }
        return "менее минуты назад";
    }
}
