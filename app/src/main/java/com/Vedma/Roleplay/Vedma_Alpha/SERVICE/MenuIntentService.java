package com.Vedma.Roleplay.Vedma_Alpha.SERVICE;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Diary;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.InGameConfig;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MainActivity;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MapsActivity;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.News;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Objects;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Abilities;


public class MenuIntentService extends IntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_MAINS= "com.Vedma.Roleplay.witch_20.action.MAINS";
    private static final String ACTION_MAPS = "com.Vedma.Roleplay.witch_20.action.MAPS";
    private static final String ACTION_CONFIG= "com.Vedma.Roleplay.witch_20.action.CONFIG";
    private static final String ACTION_NEWS = "com.Vedma.Roleplay.witch_20.action.NEWS";
    private static final String ACTION_OBJECTS= "com.Vedma.Roleplay.witch_20.action.OBJECTS";
    private static final String ACTION_DIARY = "com.Vedma.Roleplay.witch_20.action.DIARY";
    private static final String ACTION_EVENTS = "com.Vedma.Roleplay.witch_20.action.EVENTS";
    private static final String ACTION_EVENTS_NEW_TASK ="com.Vedma.Roleplay.witch_20.action.EVENTS_NEW_TASK" ;
    private static Intent ObjIntent;

    public MenuIntentService() {
        super("MenuIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startMAIN(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void startEVENTS(Context context) {
        context.startActivity(new Intent(context, Abilities.class));
    }

    public static void startCONFIG(Context context) {
        context.startActivity(new Intent(context, InGameConfig.class));
    }

    public static void startNEWS(Context context) {
        context.startActivity(new Intent(context, News.class));
    }

    public static void startMAPS(Context context) {
        context.startActivity(new Intent(context, MapsActivity.class));
    }

    public static void startOBJECT(Context context) {
        context.startActivity(new Intent(context, Objects.class));
    }

    public static void startDIARY(Context context) {
        context.startActivity(new Intent(context, Diary.class));
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


}
