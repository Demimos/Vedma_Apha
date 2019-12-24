package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.DiaryAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.DiaryPage;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.IMessage;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.LogOff;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startCONFIG;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startEVENTS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAIN;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAPS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startNEWS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startOBJECT;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyFirebaseMessagingService.PUSH_UPDATE;

public class Diary extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Update();
        }
    };
    View header;
    RecyclerView lv;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        lv = findViewById(R.id.diaryList);
        lv.setHasFixedSize(true);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        Toolbar toolbar = findViewById(R.id.toolbar_diary);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_diary);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        lv.setHasFixedSize(false);

        // use a linear layout manager

        lv.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next Vedma)



    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(pushReceiver, new IntentFilter(PUSH_UPDATE));
        Update();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pushReceiver!=null)
            unregisterReceiver(pushReceiver);
    }

    private void Update() {
        final Context context = this;
        VedmaExecutor.getInstance(context).getJSONApi().getDiary(getCharId(this)).enqueue(new Callback<List<DiaryPage>>() {
            @Override
            public void onResponse(@NonNull Call<List<DiaryPage>> call, @NonNull Response<List<DiaryPage>> response) {
                if (response.code()==401)
                {
                    LogOff(context);
                    return;
                }
                List<IMessage> diaryPages = new ArrayList<>();
                if (response.body()!=null)
                    diaryPages.addAll(response.body());
                Collections.reverse(diaryPages);
                if (lv==null)
                    return;
                lv.setAdapter(new DiaryAdapter(Diary.this, diaryPages));
                progressBar = findViewById(R.id.progressBar2);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<DiaryPage>> call, @NonNull Throwable t) {
                Log.e("Vedma.error", t.getMessage());
            }
        });
    }

    public void OnAddClick(View v)
    {
        startActivity(new Intent(this, AddDiaryPageActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_diary) {
         //   startDIARY(this);
        } else if (id == R.id.nav_main) {
            startMAIN(this);
        } else if (R.id.nav_contacts==id){
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout_diary);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
