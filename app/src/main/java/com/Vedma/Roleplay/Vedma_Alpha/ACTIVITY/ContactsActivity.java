package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.EntityAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GameCharacter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.IEntity;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.LogOff;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startDIARY;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startEVENTS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAPS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startNEWS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startOBJECT;

public class ContactsActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    View header;
    ProgressBar progressBar;
    Call<List<GameCharacter>> call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar_contacts);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progressBar5);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_contacts);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        recyclerView = findViewById(R.id.contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
         if (id == R.id.nav_main) {
             startActivity(new Intent(this, MainActivity.class));
         } else if (id == R.id.nav_diary) {
            startDIARY(this);
        } else if (id == R.id.nav_map) {
            startMAPS(this);
        }  else if (id == R.id.nav_news) {
            startNEWS(this);
        }   else if (id == R.id.nav_object) {
            startOBJECT(this);
        } else if (id == R.id.nav_events) {
            startEVENTS(this);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout_contacts);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        final Context context=this;
        if (call!=null)
            call.cancel();

        call = VedmaExecutor.getInstance(this).getJSONApi().getContacts(getCharId(this));
        call.enqueue(new Callback<List<GameCharacter>>() {
            @Override
            public void onResponse(Call<List<GameCharacter>> call, Response<List<GameCharacter>> response) {
                if (response.code()==200)
                {
                    if (response.body() != null) {
                        //InBlock,unchecked
                        if (recyclerView==null)
                            return;
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setAdapter(new EntityAdapter(context, (List<IEntity>)(List<? extends IEntity>) response.body(), false,true));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GameCharacter>> call, Throwable t) {
                Log.e("Vedma.Error", t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call!=null)
            call.cancel();
    }
}
