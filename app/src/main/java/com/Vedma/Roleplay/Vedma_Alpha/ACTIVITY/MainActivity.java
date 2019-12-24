package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.Manifest;
import android.app.ActionBar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.PropertiesAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.PropertyItem;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.StatusResponse;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.FragmentView;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AlarmReceiver;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyService;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyServiceInterceptor;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.widget.Toast.LENGTH_LONG;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.dropCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getToken;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.isMyServiceRunning;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startCONFIG;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startDIARY;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startEVENTS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAPS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startNEWS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startOBJECT;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyFirebaseMessagingService.PUSH_UPDATE;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String CONTENT = "Content";
    private static final String STATUS_MESSAGE = "STATUS_MESSAGE";
    public static final String MAIN_RESPONSE_ACTION ="Vedma.Main.Response";
    public static final String GAME_NAME = "Current_Game_Name";
    Call<StatusResponse> call;
    Call<List<FragmentView>> call2;
    View header;
    Toolbar toolbar;
    TextView gameName;
    private ProgressBar progressBar;
    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CheckStatus();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call!=null)
            call.cancel();
        if (call2!=null)
            call2.cancel();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        CheckStatus();
}

    private void CheckStatus() {
        String charId = getCharId(this);
        String token = getToken(this);
        if (charId==null||charId.equals("")||getToken(this).equals(""))
        {
            startActivity(new Intent(this, Account.class));
            return;
        }
        MyServiceInterceptor.getInstance().setSessionToken(token);
        VedmaExecutor v = VedmaExecutor.getInstance(this);

        call = v.getJSONApi().getStatus(charId);
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.code()==200){
                    if (response.body().isActive()){
                        gameName.setVisibility(View.GONE);
                       starting();
                    } else {
                        if (gameName!=null)
                            gameName.setText(response.body().getMessage());
                        gameName.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Log.e("Vedma.error", t.getMessage());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ManageChannels();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
         FirebaseApp.initializeApp(this);
         progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (getCharId(this)==null||getCharId(this).equals(""))
        {
            startActivity(new Intent(this, Account.class));
            return;
        }

        gameName = findViewById(R.id.game_name);
        try {

            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            header = navigationView.getHeaderView(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent markIntent=getIntent();
        if (markIntent.hasExtra("update"))
        {
            switch (markIntent.getStringExtra("update")){
                case "map":
                    markIntent = new Intent(this, MapsActivity.class);
                    break;
                case "news":
                    markIntent = new Intent(this, News.class);
                    break;
                case "events":
                    markIntent = new Intent(this, Abilities.class);
                    break;
                case "status":
                    markIntent=null;
                    break;
                default:
                    markIntent=null;
            }
        }

        final ComponentName myService = new ComponentName(getApplication().getPackageName(), MyService.class.getName());
        if(getPackageManager().getComponentEnabledSetting(myService) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            getPackageManager().setComponentEnabledSetting(myService,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
        final ComponentName myReceiver = new ComponentName(getApplication().getPackageName(), AlarmReceiver.class.getName());
        if(getPackageManager().getComponentEnabledSetting(myReceiver) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            getPackageManager().setComponentEnabledSetting(myReceiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
    }

    private void ManageChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager==null)
                return;
            NotificationChannel channel;

            if (notificationManager.getNotificationChannel(getString(R.string.action_channel))==null){
                channel = new NotificationChannel("Канал взаимодействия",
                        getString(R.string.action_channel),
                        IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            if (notificationManager.getNotificationChannel(getString(R.string.error_channel))==null) {
                channel = new NotificationChannel("Канал ошибок",
                        getString(R.string.error_channel),
                        IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            if ( notificationManager.getNotificationChannel(getString(R.string.default_notification_channel_id))==null) {
                channel = new NotificationChannel("Общий канад",
                        getString(R.string.default_notification_channel_id),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(pushReceiver, new IntentFilter(PUSH_UPDATE));
        CheckStatus();

    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(pushReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actvity_main_header,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.user)
        {
            startCONFIG(this);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();

         if (id == R.id.nav_diary) {
            startDIARY(this);
         } else if (id == R.id.nav_contacts) {
             startActivity(new Intent(this, ContactsActivity.class));
        } else if (id == R.id.nav_map) {
            startMAPS(this);
        }  else if (id == R.id.nav_news) {
            startNEWS(this);
        }   else if (id == R.id.nav_object) {
            startOBJECT(this);
        } else if (id == R.id.nav_events) {
            startEVENTS(this);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
    private void starting() {


        call2 = VedmaExecutor.getInstance(this).getJSONApi().getOuterMain(getCharId(this));
        call2.enqueue(new Callback<List<FragmentView>>(){
            @Override
            public void onResponse(@NonNull Call<List<FragmentView>> call, @NonNull Response<List<FragmentView>> response) {
                if (!isMyServiceRunning(MainActivity.this))
                {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.FOREGROUND_SERVICE)!=PackageManager.PERMISSION_GRANTED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, 1);
                        }
                    }
                    if (ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BROADCAST_STICKY}, 0);
                } else {
                        StartService();
                  }
                }
                List<FragmentView> f = response.body();
                final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),f);
                final ViewPager mViewPager = findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                if (progressBar!=null)
                progressBar.setVisibility(View.GONE);
                if (toolbar!=null)
                    toolbar.setTitle(getGameName());
            }
            @Override
            public void onFailure(@NonNull Call<List<FragmentView>> call, @NonNull Throwable t) {
                Log.e("Vedma.error", t.getMessage());
            }
        });
        setInterface();
    }

    private String getGameName() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(GAME_NAME,"");
    }

    private void StartService() {
        Intent run = new Intent(MainActivity.this, MyService.class);
        run.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        run.addFlags(Intent.FLAG_RECEIVER_NO_ABORT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(run);
        } else
            startService(run);
    }

    private void setInterface()
    {

    }

    public void RefreshClick(View v)
    {
        Intent intent = new Intent(this, QRTag.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isMyServiceRunning(this))
                        StartService();
                } else {
                    Toast.makeText(this,"Ведение игры невозможно",LENGTH_LONG).show();
                    dropCharId(this);
                    startActivity(new Intent(this, Account.class));
                }
                break;
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BROADCAST_STICKY}, 0);
                    } else {
                        StartService();
                    }
                } else {
                    Toast.makeText(this,"Использование приложения невозможно",LENGTH_LONG).show();
                    dropCharId(this);
                    startActivity(new Intent(this, Account.class));
                }
                break;
        }
    }
}
