package com.Vedma.Roleplay.Vedma_Alpha.SERVICE;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MainActivity;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoLocation;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoPosition;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT;
import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AlarmReceiver.AlARM_BROADCAST;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AlarmReceiver.AlARM_BROADCAST_GLOBAL;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getToken;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.sendNotification;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class MyService extends Service {// implements ServiceConnection {


    private double Lat;
    private double Lng;
    private double Acc;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    NotificationManager notificationManager;
    Notification notification;
    private AlarmManager am;
    private PendingIntent pendingIntent;
    private static final String NOTIFICATION_CHANNEL_ID = "Ведьмин канал";
    @SuppressWarnings("FieldCanBeLocal")
    private final long Interval = 60000*4 ;
    private long time;
    public LatLng latLng;
    String charId;
    AlarmManager am2;
    FirebaseJobDispatcher dispatcher;

    boolean gps_on;

    BroadcastReceiver alarmBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            run();
        }
    };

    public void startNotification()// неубиваемое сообщение с сылкой на приложение. Вариации для печеньки и для ранних
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//это для печеньки
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Ведьма работает", NotificationManager.IMPORTANCE_LOW);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(null);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            notificationChannel.setShowBadge(false);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Ведьма")
                    .setSound(null)
                    .setVibrate(null)
                    .setOngoing(true)
                    .setContentText("Вы в игре");
            notification = builder.build();

            notificationManager.notify(9999999, notification);
            startForeground(9999999, notification);
        } else { //это для остальных
            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentIntent(contentIntent)
                    .setOngoing(true)   //Can't be swiped out
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Ведьма")
                    .setContentText("Вы в игре");

            notification = builder.build();

            Objects.requireNonNull(notificationManager).notify(9999999, builder.build());
            startForeground(9999999, notification);
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startNotification();// поставить вывеску
        if (am != null)
            am.cancel(pendingIntent);
        if (am2 != null)
            am2.cancel(pendingIntent);
        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
       // charId = getCharId(this);

        registerReceiver(alarmBroadcastReceiver, new IntentFilter(AlARM_BROADCAST));
        run(); //поставить цикл запроса геолокации
        Log.d("onStart", "onStart");
        setTimer(); //поставить будильник TODO TIMER
        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Vedma.Service", "onDestroy");
        unregisterReceiver(alarmBroadcastReceiver);
        //TODO
    //    postMessage(this, "/system/msg/", "device_id=" + PID() + "&title= Сервис Завершает Работу&text=Геолокация отключена, Выход из игры?", "ALARM", "POST");
        if (fusedLocationClient != null)
            stopLocationUpdates();
        if (dispatcher!=null)
            dispatcher.cancel(getString(R.string.vedma_tag));
        if (am != null)
            am.cancel(pendingIntent);
        notificationManager.cancel(9999999);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }




    @Override
    public void onCreate() {
        super.onCreate();
        gps_on = true;
    }

    void run() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Log.d("run", "run");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult); // why? this. is. retarded. Android.
                if (locationResult == null) {
                    return;
                }
                Location currentLocation = locationResult.getLastLocation();
                if (currentLocation.getAccuracy() != 0) {
                    Lat = currentLocation.getLatitude();
                    Lng = currentLocation.getLongitude();
                    Acc = currentLocation.getAccuracy();
                    latLng = new LatLng(Lat, Lng);
                    time = Calendar.getInstance().getTimeInMillis();
                    Log.d("Vedma.server", String.format("Current position: lat %f, lng %f, accuracy %f",Lat, Lng, Acc));
                    if (currentLocation.isFromMockProvider()&&gpsStatus() ) {
                        latLng = new LatLng(42.0, 42.0);
                        Lat=42.0;
                        Lng=42.0;
                    }
                    charId=getCharId(MyService.this);
                    if (charId ==null|| charId.equals(""))
                    {
                        stopSelf();
                    }
                    if (!gpsStatus()) {
                        latLng = new LatLng(0, 0);
                    }
                   // UpdateGeoFenceObjects();
                    UpdatePositionStatus();
                    stopLocationUpdates();
                }
                Log.e("Vedma.error", "location is null");
            }
        };
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(2000);
        locationRequest.setMaxWaitTime(4000);
        locationRequest.setNumUpdates(2);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        sendNotification(this,
                "Отсутствует критическое разрешение",
                "Пожалуйста. дайте программе необходимые разрешения",
                512,
                AsyncService.NotificationChannelID.Error);
              //TODO
           // postMessage(this, "/system/msg/", "device_id=" + PID + "&title=GPS&text=GPS отозвано разрешение", "ALARM", "POST");
            gps_on = false;
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.myLooper());
        if (!gpsStatus()) {
        //     if (gps_on) TODO
          //      postMessage(this, "/system/msg/", "device_id=" + PID() + "&title=GPS&text=GPS отключён", "ALARM", "POST");
            gps_on = false;
            int NOPOSITION = 3;

        } else {
          //  if (!gps_on)TODO
          //      postMessage(this, "/system/msg/", "device_id=" + PID() + "&title=GPS&text=GPS включён", "ALARM", "POST");
            gps_on = true;
        }
    }

    void stopLocationUpdates() {
        Log.d("service_vedma", "stop up");
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void setTimer() {
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        //dispatcher.cancel(getString(R.string.vedma_tag));
        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(MyJobService.class)
                // uniquely identifies the job
                .setTag(getString(R.string.vedma_tag))
                //  cycled job
                .setRecurring(true)
                // persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(30 * 60, 40 * 60))
                //  overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        Constraint.ON_ANY_NETWORK
                        // Constraint.DEVICE_IDLE
                )

                .build();
//        dispatcher.mustSchedule(myJob);
//        Intent sum = new Intent(AlARM_BROADCAST_GLOBAL);
//        sum.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//        sum.addFlags(Intent.FLAG_RECEIVER_NO_ABORT);
//        sum.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
//        sum.putExtra("force", true);
//        pendingIntent = PendingIntent.getBroadcast(this, 9191, sum, PendingIntent.FLAG_UPDATE_CURRENT);
//        am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        am2 = (AlarmManager) getSystemService(ALARM_SERVICE);
//        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    SystemClock.elapsedRealtime() + 7*60000, pendingIntent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            am2.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + Interval*2, null), pendingIntent);
//        }
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private boolean gpsStatus() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else
            return false;
    }

    private void UpdateGeoFenceObjects() {

        final Context context = this;
        VedmaExecutor.getInstance(this).getJSONApi().getGeoObjects(getCharId(this)).enqueue(new Callback<List<GeoPosition>>() {
            @Override
            public void onResponse(@NonNull Call<List<GeoPosition>> call, @NonNull Response<List<GeoPosition>> response) {
                if (response.code() == 200) {
                    if (response.body() == null) {
                        Log.e("Vedma.error.service", "GeoObject response has empty body");
                        return;
                    }
                    Log.d("Vedma.service", "Updating objects. Array Size:" + response.body().size());
                    //UpdatePositionStatus(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<GeoPosition>> call, @NonNull Throwable t) {
                Log.e("Vedma.error", t.getMessage());
            }
        });
    }

    private void UpdatePositionStatus(/*List<GeoPosition> geoPositions*/) {
//        JSONArray inObj = new JSONArray();
//        LatLng cLatLng;
//        double radius;
//        if (latLng != null) {
//            for (int i = 0; i < geoPositions.size(); i++) {
//
//                GeoPosition geoPosition = geoPositions.get(i);
//                cLatLng = geoPosition.getLatLng();
//                radius = geoPosition.getRad();
//                if (SphericalUtil.computeDistanceBetween(latLng, cLatLng) < radius)
//                    inObj.put(geoPosition.getReflectionId());
//            }
//TODO geofence
//            postMessage(this, "/characters/" + charId + "/position/", "device_id=" + PID() + "&time=" + time +
//                    "&lat=" + latLng.latitude + "&lng=" + latLng.longitude + "&acc=" + Acc + "&inobjects=" + inObj.toString(), "my_location", "POST");
      //  }
     //   Log.d("Vedma.geoFence", inObj.toString());
        VedmaExecutor.getInstance(this).getJSONApi().locationPost(getCharId(this), new GeoLocation(Lat,Lng,Acc,time)).enqueue((new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Vedma.Service", "background"+response.code());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Vedma.Service", "background error"+t.getMessage());
            }
        }));
    }


}
