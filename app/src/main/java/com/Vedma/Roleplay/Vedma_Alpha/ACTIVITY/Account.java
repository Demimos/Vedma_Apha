package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.GamesAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.SignInFragment;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.AccountInfo;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Game;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyServiceInterceptor;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.LogOff;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getToken;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyFirebaseMessagingService.PUSH_UPDATE;


public class Account extends AppCompatActivity {
    public static final String AuthFailed = "Vedma.AuthFailed.account";
    public  ArrayList<String> Games;
    public  TextView Email;
    public info info;
    public static String AuthOk = "com.Vedma.Roleplay.Vedma_Alpha.AuthOk";
    RecyclerView gameList;
    Context context;
    Timer timer;
    ProgressBar progressBar;
    Call<List<Game>> gameCall;
    Call<AccountInfo> accountCall;
    public void RegisterClick(View view) {
        startActivity(new Intent(this, RegisterNewUserActivity.class));
    }

    public class info
    {
        info(String Name, String Email, String Phone)
        {
            this.Name=Name;
            this.Email=Email;
            this.Phone=Phone;
        }
        String Name;
        String Email;
        String Phone;
    }
    BroadcastReceiver authOk = new BroadcastReceiver() {
        @Override
            public void onReceive(Context context, Intent intent) {
                ManageState();
            }
    };
    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ManageState();
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.user)
        {
            (new AlertDialog.Builder(this))
                    .setTitle("Смена пользователя")
                    .setMessage("Вы уверены что хотите сменить пользователя")
                    .setCancelable(true)
                    .setPositiveButton("Сменить пользователя", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LogOff(Account.this);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(authOk,new IntentFilter(AuthOk));
        registerReceiver(pushReceiver,new IntentFilter(PUSH_UPDATE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(authOk);
        unregisterReceiver(pushReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameCall!=null)
            gameCall.cancel();
        if (accountCall!=null)
            accountCall.cancel();
        if (timer!=null){
            timer.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actvity_main_header,menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_account);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressBar = findViewById(R.id.progressBar);
        timer = new Timer("update_timer");

        context=this;
        Games = new ArrayList<>();
        Email = findViewById(R.id.UserEmail);
        gameList = findViewById(R.id.game_list);
        ManageState();
    }

    private void ManageState(){
        String Token = getToken(this);
        if (Token.equals("") ){
            timer.cancel();
            showSignIn();
        } else {
            String token  = getToken(this);
            if (!token.equals("")) {
                progressBar.setVisibility(View.VISIBLE);
                getAccountInfo();
                getGameList();
                timer = new Timer("update_timer");
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getGameList();
                    }
                }, 30000, 30000);
            }
        }
    }

    private void getGameList() {
        if (getToken(this).equals("")) {
            timer.cancel();
            return;
        }
        if (gameCall!=null)
            gameCall.cancel();
        gameCall = VedmaExecutor.getInstance(this).getJSONApi().getGames();
        gameCall.enqueue(new Callback<List<Game>>() {
                @Override
                public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                    List<Game> games = new ArrayList<>();
                    if (response.body() != null) {
                        games.addAll(response.body());
                    }
                    GamesAdapter adapter = new GamesAdapter(Account.this, games);
                    RecyclerView.LayoutManager viewManager = new LinearLayoutManager(Account.this);
                    if(gameList==null)
                        return;
                    gameList.setLayoutManager(viewManager);
                    gameList.hasFixedSize();
                    gameList.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                    sendBroadcast(new Intent(AuthFailed));
                    Toast.makeText(Account.this, "OMG "+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void getAccountInfo() {

        if(accountCall!=null)
            accountCall.cancel();
        accountCall = VedmaExecutor.getInstance(this).getJSONApi().getAccountInfo();
        accountCall.enqueue(new Callback<AccountInfo>() {
            @Override
            public void onResponse(@NonNull Call<AccountInfo> call, @NonNull Response<AccountInfo> response) {
                if (response.body()!=null)
                {
                    info=new info(response.body().Name, response.body().Email, response.body().Phone);
                    Email.setText(response.body().Email);

                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountInfo> call, @NonNull Throwable t) {
                Toast.makeText(Account.this, t.getMessage(), Toast.LENGTH_SHORT).show();//TODO another message
                //TODO Handle this
            }
        });
    }

    public void showSignIn() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new SignInFragment();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "SignIn");

    }
    @Override
    public void onBackPressed() {
        this.finishAffinity();
        //   super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ManageState();
    }
}

