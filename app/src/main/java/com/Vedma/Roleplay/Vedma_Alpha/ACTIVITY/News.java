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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.NewsAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.NewsCapture;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.LogError;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startCONFIG;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startDIARY;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startEVENTS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAIN;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAPS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startOBJECT;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyFirebaseMessagingService.PUSH_UPDATE;

public class News extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView news;
    View header;
    Context context;
    Intent intent;
    ProgressBar bar;
    private BroadcastReceiver pushReceiver;
    {
        pushReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

             getUpdates();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_news);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            bar = findViewById(R.id.bar);
            news = findViewById(R.id.news);
            news.setHasFixedSize(true);
            context = this;
            Toolbar toolbar = findViewById(R.id.toolbar_news);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout_news);
            final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            intent = getIntent();
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            header = navigationView.getHeaderView(0);
            getUpdates();
        }
        catch (Exception e){

            LogError(this, e);
        }
    }

    private void getUpdates() {
        VedmaExecutor.getInstance(this).getJSONApi().getNews(getCharId(this)).enqueue(new Callback<List<NewsCapture>>() {
            @Override
            public void onResponse(@NonNull Call<List<NewsCapture>> call, @NonNull Response<List<NewsCapture>> response) {
                if (response.code()!=200)
                    return;
                bar.setVisibility(View.GONE);
                try {
                    List<NewsCapture> newsList = new ArrayList<>();
                    if (response.body() != null) {
                        newsList.addAll(response.body());
                    }
                    Collections.reverse(newsList);
                    if (intent.hasExtra("Publisher")) {
                        int id = intent.getIntExtra("Publisher", 0);
                        List<NewsCapture> sortedList = new ArrayList<>();
                        if (id != 0) {
                            for (int i = 0; i < newsList.size(); i++) {
                                if (newsList.get(i).getPublisherId() == id)
                                    sortedList.add(newsList.get(i));
                            }
                        }
                        newsList = sortedList;
                    }
                    NewsAdapter adapter = new NewsAdapter(News.this, newsList);
                    RecyclerView.LayoutManager viewManager = new LinearLayoutManager(News.this);
                    if (news==null)
                        return;
                    news.setLayoutManager(viewManager);
                    news.hasFixedSize();
                    news.setAdapter(adapter);
                }
                catch (Exception e){
                    LogError(context, e);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<NewsCapture>> call, @NonNull Throwable t) {
                try {
                    Toast.makeText(News.this, "Соединение с сервером отсутствует", Toast.LENGTH_SHORT).show();
                    Log.d("Vedma.error", t.getMessage());
                }
                catch (Exception e){
                    LogError(context, e);
                }
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_diary) {
            startDIARY(this);
        }else if (id==R.id.nav_contacts){
            startActivity(new Intent(this,ContactsActivity.class));
        } else if (id == R.id.nav_main) {
            startMAIN(this);
        } else if (id == R.id.nav_map) {
            startMAPS(this);
        }    else if (id == R.id.nav_object) {
            startOBJECT(this);
     /*   }  else if (id == R.id.nav_profile) {
            startCONFIG(this);*/
        } else if (id == R.id.nav_events) {
            startEVENTS(this);
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout_news);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter pushFilter = new IntentFilter(PUSH_UPDATE);
        registerReceiver(pushReceiver, pushFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(pushReceiver);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_menu_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        OnPublishersClick();
        return true;
    }
    public void OnPublishersClick()
    {
        Intent intent = new Intent(this,ActivityPublishers.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
