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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.AbilitiesAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Ability;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.ActionAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Invoker;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.LogOff;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.sendNotification;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startDIARY;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAIN;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAPS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startNEWS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startOBJECT;

public class Abilities extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static final String ACTION_ADAPTER = "action_adapter";
    static final String ACTION_ABILITY ="action_ability";
    private String charId;
    ListView listView;
    AbilitiesAdapter adapter;
    ProgressBar progressBar;
    final Context context = this;
    Call<List<Ability>> call;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateList();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter("com.Vedma.Roleplay.witch_20.ACTIVITY.Abilities");
        setContentView(R.layout.activity_abilities);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar_abilities);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_abilities);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        registerReceiver(broadcastReceiver,intentFilter);
        charId = getCharId(this);
        listView = findViewById(R.id.abilities_list);


    }

    @Override
    protected void onStart() {
        super.onStart();
       UpdateList();
    }

    private void UpdateList() {

        call = VedmaExecutor.getInstance(this).getJSONApi().getAbilities(charId);
        call.enqueue(new Callback<List<Ability>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ability>> call, @NonNull Response<List<Ability>> response) {
                if (response.code()==200)
                {
                    adapter = new AbilitiesAdapter(context, response.body(), false);
                    if (listView!=null){
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                TryInvoke((Ability) view.getTag());
                            }
                        });
                    }
                    listView.setAdapter(adapter);
                    if (progressBar!=null)
                    progressBar.setVisibility(View.GONE);

                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Ability>> call, @NonNull Throwable t) {
                Log.e("Vedma.error", t.getMessage());
            }
        });
    }

    private void TryInvoke(final Ability ability) {
        listView.setOnItemClickListener(null);
        VedmaExecutor.getInstance(this).getJSONApi().getAbilityAdapter(charId,
                ability.getID(),
                ability.getPresetId(),
                ability.getChainId()).enqueue(new Callback<ArrayList<ActionAdapter>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ActionAdapter>> call, @NonNull Response<ArrayList<ActionAdapter>> response) {

                if (response.code()==200)
                {
                    if (Objects.requireNonNull(response.body()).size()==0)
                    {
                        InvokeNow(ability,new ArrayList<Invoker>());
                    }
                    Intent intent = new Intent(context, ChooserActivity.class);
                    intent.putExtra(ACTION_ADAPTER, response.body());
                    intent.putExtra(ACTION_ABILITY, ability);
                    context.startActivity(intent);
                    return;
                }
              else
                {
                    Toast.makeText(context, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                }
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TryInvoke((Ability) view.getTag());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ActionAdapter>> call, @NonNull Throwable t) {
                Log.e("Vedma.Error", t.getMessage());
            }
        });
    }

    private void InvokeNow(final Ability ability, ArrayList<Invoker> invokers) {
        VedmaExecutor.getInstance(this).getJSONApi().invokeAbility(getCharId(this),
                ability.getID(),
                ability.getPresetId(),
                ability.getChainId(),
                invokers).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
             if (response.code()==200){
                 if (!response.body().equals(""))
                     sendNotification(context,ability.getTitle(),response.body(),ability.getID(), AsyncService.NotificationChannelID.Action);
             }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("Vedma.Error", t.getMessage());
            }
        });
}

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (broadcastReceiver!=null)
            unregisterReceiver(broadcastReceiver);
        if (call!=null)
            call.cancel();


    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_diary) {
            startDIARY(this);
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
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout_abilities);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
