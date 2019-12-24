package com.Vedma.Roleplay.Vedma_Alpha.SERVICE;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Abilities;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Account;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.InGameConfig;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MainActivity;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MapsActivity;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.News;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.BearerItem;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Game;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.UserCreditals;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT;
import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;
import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Account.AuthFailed;
import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Account.AuthOk;
import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MainActivity.GAME_NAME;


public class AsyncService extends IntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_NOTIFY= "com.Vedma.Roleplay.Vedma_Alpha.action.NOTIFY";
    private static final String ACTION_POST = "com.Vedma.Roleplay.Vedma_Alpha.action.POST";
    private static final String ACTION_GEO_POST = "com.Vedma.Roleplay.Vedma_Alpha.action.GEO_POST";

    private static final String EXTRA_PARAM1 = "com.Vedma.Roleplay.Vedma_Alpha.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.Vedma.Roleplay.Vedma_Alpha.extra.PARAM2";
    private static final String EXTRA_PARAM3 = "com.Vedma.Roleplay.Vedma_Alpha.extra.PARAM3";
    private static final String EXTRA_PARAM4 = "com.Vedma.Roleplay.Vedma_Alpha.extra.PARAM4";
    private static final String ACTION_NOTIFY_PUSH = "com.Vedma.Roleplay.v.action.NOTIFY_PUSH";
    private static final String CHAR_ID ="CharacterCurrent" ;
    private static final String GAME_ID ="GameId";
    private static final String TOKEN = "TOKEN" ;
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    public static  Intent newsIntent;


    public AsyncService() {
        super("AsyncService");
    }



    public static boolean Contains(LatLng latLng, Circle circle)//проверка наличия точки в круге
    {
        return SphericalUtil.computeDistanceBetween(latLng, circle.getCenter()) < circle.getRadius();
    }
    public static boolean Contains(Circle circle1, Circle circle2)//проверка наличия кругв в круге
    {
        return SphericalUtil.computeDistanceBetween(circle1.getCenter(), circle2.getCenter()) < Math.abs(circle1.getRadius() - circle2.getRadius());
    }
    public static boolean Contains(LatLng latLng,  List<LatLng> polygon)//проверка наличия точки в полигоне
    {
        if (CheckPoly(polygon)&&PolyUtil.containsLocation(latLng, polygon,true))
                return true;
        return false;
    }
    public static boolean Contains(Circle circle, List<LatLng> polygon)//проверка наличия круга в полигоне
    {
        if (CheckPoly(polygon)){
            if (PolyUtil.containsLocation(circle.getCenter(), polygon,true))
                return !PolyUtil.isLocationOnEdge(circle.getCenter(), polygon, true, circle.getRadius());
        }
        else
            Log.d("error","polygon is not closed");
        return false;
    }
    public static boolean Contains(LatLng latLng,  List<LatLng> polyline, double tolerance)//проверка наличия точки на растоянии tolerance от маршрута
    {
        return PolyUtil.isLocationOnPath(latLng, polyline, true, tolerance);

    }
    public static boolean CheckPoly(java.util.List<LatLng> polygon)// проверка замкнут ли полигон
    {
        return PolyUtil.isClosedPolygon(polygon);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void sendNotification(Context context, String Title, String Text, int id,NotificationChannelID channelID) {
        Intent intent = new Intent(context, AsyncService.class);
        intent.setAction(ACTION_NOTIFY);
        intent.putExtra(EXTRA_PARAM1, Title);
        intent.putExtra(EXTRA_PARAM2, Text);
        intent.putExtra(EXTRA_PARAM3, id);
        intent.putExtra(EXTRA_PARAM4, channelID);
        context.startService(intent);
    }
    public static void sendPushNotification(Context context, String Title, String Text, int id, String activity) {
        Intent intent = new Intent(context, AsyncService.class);
        intent.setAction(ACTION_NOTIFY_PUSH);
        intent.putExtra(EXTRA_PARAM1, Title);
        intent.putExtra(EXTRA_PARAM2, Text);
        intent.putExtra(EXTRA_PARAM3, id);
        intent.putExtra(EXTRA_PARAM4, activity);
        context.startService(intent);
    }




    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NOTIFY.equals(action)) {
                final String Title = intent.getStringExtra(EXTRA_PARAM1);
                final String Text = intent.getStringExtra(EXTRA_PARAM2);
                final int id = intent.getIntExtra(EXTRA_PARAM3,0);
                final NotificationChannelID channelID = (NotificationChannelID) intent.getSerializableExtra(EXTRA_PARAM4);
                handleSendNotification(Title,Text,id,channelID);
            } else if (ACTION_NOTIFY_PUSH.equals(action)) {
                final String Title = intent.getStringExtra(EXTRA_PARAM1);
                final String Text = intent.getStringExtra(EXTRA_PARAM2);
                final int id = intent.getIntExtra(EXTRA_PARAM3,0);
                final String activity = intent.getStringExtra(EXTRA_PARAM4);
                handlePushNotification(Title,Text,id,activity);
            }

        }
    }

    public enum NotificationChannelID{
        Error,
        Action,
        Base
    }
    private String getString(NotificationChannelID c)
    {
        switch (c)
        {
            case Base:return getString(R.string.default_notification_channel_id);
            case Error:return getString(R.string.error_channel);
            case Action:return  getString(R.string.action_channel);
            default:throw new EnumConstantNotPresentException(c.getClass(),"");
        }
    }
    private void handleSendNotification(String Title, String messageBody,int id,NotificationChannelID channelId) {
        Intent intent = new Intent(AsyncService.this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(AsyncService.this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(AsyncService.this, getString(channelId))
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle(Title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel;
            switch (channelId)
            {
                case Action:
                    channel = notificationManager.getNotificationChannel(getString(R.string.action_channel));
                    if (channel==null){
                        channel = new NotificationChannel(getString(channelId),
                            getString(R.string.action_channel),
                            IMPORTANCE_HIGH);
                        Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
                    }
                                      break;
                case Error:
                    channel = notificationManager.getNotificationChannel(getString(R.string.error_channel));
                    if (channel==null){
                        channel = new NotificationChannel(getString(channelId),
                            getString(R.string.error_channel),
                            IMPORTANCE_HIGH);
                        Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
                    }
                                       break;
                case Base:
                    channel = notificationManager.getNotificationChannel(getString(R.string.default_notification_channel_id));
                    if (channel==null){
                        channel = new NotificationChannel(getString(channelId),
                            getString(R.string.default_notification_channel_id),
                            NotificationManager.IMPORTANCE_DEFAULT);
                        Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                 break;
                    default:throw new EnumConstantNotPresentException(channelId.getClass(),String.valueOf(channelId.name()+ " " +String.valueOf(channelId)));
            }
        }else {
            if (channelId==NotificationChannelID.Action||channelId==NotificationChannelID.Error)
                notificationBuilder.setPriority(PRIORITY_HIGH);
            else
                notificationBuilder.setPriority(PRIORITY_DEFAULT);
        }

        Objects.requireNonNull(notificationManager).notify(id/* ID of notification */, notificationBuilder.build());

    }

    private void handlePushNotification(String Title, String messageBody,int id,String activity) {
        Intent intent;
        switch (activity)
        {
            case "map":
                intent = new Intent(AsyncService.this, MapsActivity.class);
                break;
            case "news":
                intent = new Intent(AsyncService.this, News.class);
                break;
            case "events":
                intent = new Intent(AsyncService.this, Abilities.class);
                break;
            case "status":
                intent = new Intent(AsyncService.this, MainActivity.class);
                break;
            default:
                    intent = new Intent(AsyncService.this, MainActivity.class);
        }
       // intent = new Intent(AsyncService.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(AsyncService.this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(AsyncService.this, getString(NotificationChannelID.Action))
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle(Title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    NotificationChannel channel = new NotificationChannel(getString(NotificationChannelID.Action),
                            "Канал взаимодействия",
                            IMPORTANCE_HIGH);
                    Objects.requireNonNull(notificationManager).createNotificationChannel(channel);


        }else {
                notificationBuilder.setPriority(PRIORITY_HIGH);
        }

        Objects.requireNonNull(notificationManager).notify(id/* ID of notification */, notificationBuilder.build());

    }

    public static UserCreditals getUserCredentials(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username =sharedPreferences.getString(USERNAME,"");
        String password = sharedPreferences.getString(PASSWORD,"");
        if (!username.equals("")&&!password.equals("")) {
            return new UserCreditals(username, password);
        } else {
            return null;
        }
    }

    public static String getToken(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(TOKEN,"");
    }
    public static String getCharId(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(CHAR_ID,"");
    }
    public static void setToken(Context context, String Token)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN, Token);
        editor.apply();
    }
    public static void setCharId(Context context, String CharID)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CHAR_ID, CharID);
        editor.apply();
    }
    public static void setGameId(Context context, Game game)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GAME_ID, game.getId());
        editor.putString(GAME_NAME, game.getName());
        editor.apply();
    }

    public static String getGameId(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(GAME_ID,"");
    }
    public static void dropGameId(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GAME_ID, "");
        editor.putString(GAME_NAME, "");
        editor.apply();
    }
    public static void dropCharId(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CHAR_ID, "");
        editor.apply();
    }
    public static void LogOff(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN, "");
        editor.putString(CHAR_ID, "");
        editor.apply();
        MyServiceInterceptor.getInstance().setSessionToken(null);
        if (isMyServiceRunning(context))
            context.stopService(new Intent(context, MyService.class));
        Intent intent =new Intent(context, Account.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static void ActLikeInactive(Context context){
        Intent intent =new Intent(context, MainActivity.class);
        if (isMyServiceRunning(context))
            context.stopService(new Intent(context, MyService.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void LogError(Context context, Throwable e){
        StringBuilder error = new StringBuilder();
        e.printStackTrace();
        error.append(e.getMessage()).append("\n");
        for (int i=0;i<e.getStackTrace().length;i++)
        {
            error.append(e.getStackTrace()[i]).append("\n");
        }
        VedmaExecutor.getInstance(context).getJSONApi().ErrorPost(getCharId(context),error.toString()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public static void dropUserCredentials(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, "");
        editor.putString(PASSWORD, "");
        editor.apply();
    }

    public static boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (MyService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
    public static void ExitCurrentGame(Context context){
        context.stopService(new Intent(context, MyService.class));
        dropCharId(context);
        final String topic = getGameId(context);
        if (topic.length()>0)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Log.e("Vedma.error", "subscribe failed to topic "+topic);//TODO schedule a job
                            }
                            //TODO LOG errors
                        }
                    });
        dropGameId(context);
        if (isMyServiceRunning(context))
            context.stopService(new Intent(context, MyService.class));
    }
}
