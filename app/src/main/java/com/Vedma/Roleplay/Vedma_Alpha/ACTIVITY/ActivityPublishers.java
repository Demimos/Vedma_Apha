package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.PublisherAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Publisher;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;

public class ActivityPublishers extends AppCompatActivity {
    ListView lv;
    Call<List<Publisher>> call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publishers);
        lv=findViewById(R.id.listview);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UpdateList();
    }

    private void UpdateList() {
        final Context context = this;
        if (call!=null)
            call.cancel();
        call = VedmaExecutor.getInstance(context).getJSONApi().getPublishers(getCharId(this));
        call.enqueue(new Callback<List<Publisher>>() {
            @Override
            public void onResponse(@NonNull Call<List<Publisher>> call, @NonNull Response<List<Publisher>> response) {
                if (response.code()==200)
                {
                    if (lv==null)
                        return;
                    lv.setAdapter(new PublisherAdapter(context, response.body()));
                    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent =new Intent(context, News.class );
                            intent.putExtra("Publisher",(int) view.getTag());
                            startActivity(intent);
                            return false;
                        }
                    });
                }
            }
                @Override
                public void onFailure(@NonNull Call<List<Publisher>> call, @NonNull Throwable t) {
                    Log.e("Vedma.error", t.getMessage());
                    Toast.makeText(context, "Соединение отсутствует", Toast.LENGTH_SHORT).show();
                }
            });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.publishers_menu_drawer, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        OnNewsClick();
//        return true;
//    }

    private void OnNewsClick() {
        Intent intent = new Intent(this,News.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call!=null)
            call.cancel();
    }
}
