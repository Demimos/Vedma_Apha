package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.DiaryPage;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.LogOff;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;

public class AddDiaryPageActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary_page);
        button = findViewById(R.id.Post);

    }
    public void OnPostClick(View v)
    {
        EditText title = findViewById(R.id.Title);
        EditText body = findViewById(R.id.Body);
        if (String.valueOf(title.getText()).equals("")||String.valueOf(body.getText()).equals(""))
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show();
        else {
            final Context context = this;
            DiaryPage page = new DiaryPage(String.valueOf(title.getText()),String.valueOf(body.getText()));
            button.setActivated(false);
            VedmaExecutor.getInstance(this).getJSONApi().newNote(getCharId(this),page).enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.code()!=200)
                    {
                        Toast.makeText(context, "Что-то не так", Toast.LENGTH_SHORT).show();
                        try {
                            Log.d("Vedma.error",response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        button.setActivated(true);
                        return;
                    }
                    Intent intent = new Intent(context, Diary.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    button.setActivated(true);

                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.e("Vedma.error", t.getMessage());
                    button.setActivated(true);
                }
            });
        }
    }
}
