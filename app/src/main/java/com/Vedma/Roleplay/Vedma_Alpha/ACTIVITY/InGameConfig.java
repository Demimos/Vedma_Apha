package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyService;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.ExitCurrentGame;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.LogOff;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.dropCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.dropGameId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getGameId;


public class InGameConfig extends AppCompatActivity {
Intent intentto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game_config);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    public void onClickResume(View view)
    {
        intentto = new Intent(this, MainActivity.class);
        startActivity(intentto);
    }
    public void onClickExit(View view)
    {
        exitDialog();
    }
    void exitDialog()
    {
        AlertDialog.Builder ad;
        ad=new AlertDialog.Builder(InGameConfig.this);
        ad.setTitle("Внимание");  // заголовок
        ad.setMessage("Вы перестаните получать уведомления о игровых событиях. Вы действительно хотите покинуть игру?"); // сообщение
        ad.setCancelable(false);
        ad.setPositiveButton("Всё равно выйти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(InGameConfig.this, "Вы вышли из игры", Toast.LENGTH_SHORT).show();
                ExitCurrentGame(InGameConfig.this);
                startActivity(new Intent(InGameConfig.this, Account.class));
            }
        });
        ad.setNegativeButton("Вернуться в игру", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(InGameConfig.this, MainActivity.class));
            }
        });
        ad.show();
    }
}
