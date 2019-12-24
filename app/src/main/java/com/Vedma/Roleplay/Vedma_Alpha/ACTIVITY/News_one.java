package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.DiaryAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Comment;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.NewsCapture;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.IMessage;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.DownloadImageTask;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.sendNotification;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startCONFIG;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyFirebaseMessagingService.PUSH_UPDATE;


public class News_one extends AppCompatActivity {
    int newsNum;
    TextView Title;
    TextView Text;
    TextView Preview;
    TextView Author;
    TextView Publisher;
    VideoView videoView;
    ImageView img;
    Point size;
    RecyclerView Recycler;
    RecyclerView Recycler2;
    Button addButton;
    AlertDialog alertDialog;
    ProgressBar progressBar;
    Context context;

    Call<List<Comment>> commentCall;
    Call<NewsCapture> newsCall;
    BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           UpdateComments();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(pushReceiver, new IntentFilter(PUSH_UPDATE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(pushReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newsCall!=null)
            newsCall.cancel();
        if (commentCall!=null)
            commentCall.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_one);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (!getIntent().hasExtra("newsNum"))
        {
            startActivity(new Intent(this, News.class));
            return;
        }
        context = this;

        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        progressBar = findViewById(R.id.progressBar4);
        newsNum= Objects.requireNonNull(getIntent().getExtras()).getInt("newsNum");
        Recycler=findViewById(R.id.comments);
        Recycler2 = findViewById(R.id.comments2);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        Recycler.setLayoutManager(lm);
        Recycler.setHasFixedSize(true);
        Recycler2.setHasFixedSize(true);
        lm = new LinearLayoutManager(this);
        Recycler2.setLayoutManager(lm);
        Recycler.setNestedScrollingEnabled(true);
        Title = findViewById(R.id.NewsTitle);
        Preview =findViewById(R.id.Preview);
        Text = findViewById(R.id.NewsText);
        addButton = findViewById(R.id.add_comment);
        addButton.setActivated(false);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddClick(v);
            }
        });
        Publisher =findViewById(R.id.Publisher);
        Author = findViewById(R.id.Author);
        videoView =findViewById(R.id.videoView);
        img= findViewById(R.id.image);
        newsCall = VedmaExecutor.getInstance(this).getJSONApi().getArticle(getCharId(this),newsNum);
                newsCall.enqueue(new Callback<NewsCapture>() {
            @Override
            public void onResponse(@NonNull Call<NewsCapture> call, @NonNull Response<NewsCapture> response) {
                NewsCapture art = response.body();
                if (art==null)
                {
                    Log.d("Vedma.error",response.toString() + " "+response.code()  );
                    startActivity(new Intent(News_one.this, News.class));
                    return;
                }
                if (Title==null)
                    return;
                Title.setText(art.getTitle());
                Preview.setText(art.getPreview());
                Text.setText(art.getBody());
                Publisher.setText(art.getPublisherName());
                Author.setText(art.getAuthor());
                if (art.getIMG()!=null&&!art.getIMG().equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    img.setVisibility(View.VISIBLE);
                    new DownloadImageTask(img, progressBar)
                            .execute(art.getIMG());
                }
                else{
                    img.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
                if (art.getVideoUrl()!=null && !art.getVideoUrl().equals("")){
                    videoView.setVisibility(View.VISIBLE);
                    MediaController controller = new MediaController(News_one.this);
                    controller.setAnchorView(videoView);
                    controller.setMediaPlayer(videoView);
                    controller.setAnchorView(videoView);
                    videoView.setMediaController(controller);
                    videoView.requestFocus(1);
                    videoView.setVideoPath(art.getVideoUrl());
                    android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
                    params.width = (int)(size.x*0.8);
                    params.height = (int)(size.y*0.5);
                    params.leftMargin = 16;
                    params.rightMargin=16;
                    params.gravity= Gravity.CENTER;
                    videoView.setLayoutParams(params);
                    videoView.start();
                }
                UpdateComments();
            }

            @Override
            public void onFailure(@NonNull Call<NewsCapture> call, @NonNull Throwable t) {

            }
        });

    }
    public void AddClick(View v)
    {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.data_item_alert_layout,null);
        TextView tv = view.findViewById(R.id.label);
        final EditText et = view.findViewById(R.id.dataitem);
        tv.setText("");
        et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        addButton.setActivated(false);
        alertDialog =(new AlertDialog.Builder(this)).setView(view).setTitle("Введите текст комментария")
                .setPositiveButton("Опубликовать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addButton.setActivated(true);
                        if (et.getText().toString().equals(""))
                        {
                            Toast.makeText(News_one.this, "Напишите комментарий!", Toast.LENGTH_SHORT).show();
                            et.setHighlightColor(Color.RED);
                        } else {
                            VedmaExecutor.getInstance(News_one.this).getJSONApi().comment(getCharId(News_one.this),newsNum,et.getText().toString())
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                            if (response.code()==200){
                                                if (Objects.requireNonNull(response.body()).equals("ok")) {
                                                    Toast.makeText(context, "Комментарий отправлен", Toast.LENGTH_SHORT).show();
                                                    UpdateComments();
                                                }
                                                else
                                                    Toast.makeText(News_one.this, "Комментировать можно не чаще. чем раз в 10 секунд", Toast.LENGTH_SHORT).show();

                                            }
                                            alertDialog.dismiss();
                                            addButton.setActivated(true);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                            addButton.setActivated(true);
                                            Toast.makeText(context, "Не удалось, попробуйте позже", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }).create();
        alertDialog.show();
        addButton.setActivated(false);

    }

    private void UpdateComments() {

        if (commentCall!=null)
            commentCall.cancel();
        commentCall = VedmaExecutor.getInstance(this).getJSONApi().getComments(getCharId(this),newsNum);
        commentCall.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                if (response.code()==200){
                    List<IMessage> commentList = new ArrayList<>((List<? extends IMessage>) Objects.requireNonNull(response.body()));
                    if (commentList.size()<10)
                    {
                        Recycler.setHasFixedSize(true);
                        Recycler.setNestedScrollingEnabled(true);
                        Recycler.setAdapter(new DiaryAdapter(News_one.this,commentList));
                        Recycler.setVisibility(View.VISIBLE);
                        Recycler2.setVisibility(View.GONE);
                    } else{
                        Recycler2.setAdapter(new DiaryAdapter(News_one.this,commentList));
                        Recycler2.setVisibility(View.VISIBLE);
                        Recycler.setVisibility(View.GONE);
                    }

                   addButton.setActivated(true);
            }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable t) {

            }
        });
    }
}
