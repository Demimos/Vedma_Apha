package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.ArticleView;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.FileUtils;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.ProgressRequestBodyImg;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.ProgressRequestBodyVideo;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.UploadCallbacks;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.app.NotificationCompat.PRIORITY_LOW;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.sendNotification;

public class ActivityNewArticle extends AppCompatActivity{// implements UploadCallbacks {
    private static final int REQUEST_GALLERY_VIDEO = 3;
    public static final String PUBLISHER ="PUBLISHER_ID" ;
    public static final String VIDEO_ALLOWED = "VIDEO_ALLOWED";
    private Context context;
    private ImageView imageView;
    private VideoView videoView;
    EditText Title;
    EditText Preview;
    EditText Body;
    Point size;
    private Uri imageUri;
    private Uri videoUri;
    private int PublisherId;
    private  boolean VideoAllowed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_article);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        Intent intent = getIntent();
        if (intent.hasExtra(PUBLISHER))
        {
            PublisherId = intent.getIntExtra(PUBLISHER,0);
            VideoAllowed = intent.getBooleanExtra(VIDEO_ALLOWED,false);
            if (PublisherId<1){
                Toast.makeText(context, "Error PublisherId "+PublisherId+" is Wrong", Toast.LENGTH_SHORT).show();
                finish();
            }
        }else {
            finish();

        }
        Title=findViewById(R.id.Title);
        Preview = findViewById(R.id.Preview);
        Body= findViewById(R.id.Body);
        imageView=findViewById(R.id.test_img);
        videoView = findViewById(R.id.video_preview);
        context=this;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chooser_menu, menu);
        return true;
    }

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    public void TakePhoto(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }
    public void TakeVideo(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_GALLERY_VIDEO);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA:

                break;
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    try {
                        imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(selectedImage);
                        imageView.setVisibility(View.VISIBLE);

                    } catch (FileNotFoundException e) {
                        e.getMessage();
                    }
                }
                break;
            case REQUEST_GALLERY_VIDEO:
                if (resultCode == RESULT_OK) {
                    try {
                        videoUri=data.getData();
                        MediaController controller = new MediaController(context);
                        controller.setAnchorView(videoView);
                        controller.setMediaPlayer(videoView);
                        controller.setAnchorView(videoView);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setMediaController(controller);
                        videoView.requestFocus(0);
                        videoView.setVideoURI(videoUri);
                        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
                        params.width = size.x;
                        params.height = size.y/2;
                        params.leftMargin = 8;
                        params.rightMargin=8;
                        videoView.setLayoutParams(params);
                        videoView.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.clear:
                AlertDialog cancel = (new AlertDialog.Builder(this)).setCancelable(true)
                        .setMessage("Вы уверены, что хотите прекратить публикацию?")
                        .setTitle("Выход")
                        .setPositiveButton("Прекратить и выйти", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                cancel.show();
                break;
            case R.id.news:
                final AlertDialog next = (new AlertDialog.Builder(this)).setCancelable(true)
                        .setMessage("Всё готово?")
                        .setTitle("Продолжить")
                        .setPositiveButton("Опубликовать", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Title.getText().length()>1&&Preview.getText().length()>1&&Body.getText().length()>1)
                                    Publish();
                                else
                                    Toast.makeText(context, "Напишите статью", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                next.show();
                break;
        }
        return true;
    }

    private void Publish() {
        MultipartBody.Part imgBody=null;
        MultipartBody.Part videoBody=null;

        final List<MultipartBody.Part> files = new ArrayList<>();
        if (videoUri!=null) {
            File file = FileUtils.getFile(this, videoUri);
            if (file.length()>10000000)
            {
                Toast.makeText(context, "Файл слишком большой", Toast.LENGTH_SHORT).show();
                return;
            }
            // Создаем RequestBody
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("video/*"), file);

            // MultipartBody.Part используется, чтобы передать имя файла
          //  MultipartBody.Part fileBody = new MultipartBody.Part(file, this);
            videoBody = MultipartBody.Part.createFormData("video", file.getName(), requestFile);
            files.add(videoBody);




        }
        if (imageUri!=null) {
            File file = FileUtils.getFile(this, imageUri);

            // Создаем RequestBody
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/jpeg"), file);
     //       ProgressRequestBodyImg fileBody = new ProgressRequestBodyImg(file, this);
            // MultipartBody.Part используется, чтобы передать имя файла
            imgBody = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            files.add(imgBody);
        }
        if (imageUri==null&&videoUri==null)
        {
            File externalAppDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName());
            if (!externalAppDir.exists()) {
                externalAppDir.mkdir();
            }

            File file = new File(externalAppDir , "FileName.txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("text/txt"), file);
       //     ProgressRequestBodyImg fileBody = new ProgressRequestBodyImg(file, this);
            // MultipartBody.Part используется, чтобы передать имя файла
            imgBody = MultipartBody.Part.createFormData("txt", file.getName(), requestFile);
            files.add(imgBody);
        }
        VedmaExecutor.getInstance(this).DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                VedmaExecutor.getInstance(context).getJSONApi().postArticle(getCharId(context),
                        PublisherId,
                        "vs",
                        "vs",
                        "vs",
                        files).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.code() == 200) {
                            ArticleView av = new ArticleView();
                            av.Title=Title.getText().toString();
                            av.Preview=Preview.getText().toString();
                            av.Body=Body.getText().toString();
                            VedmaExecutor.getInstance(context).getJSONApi().postArticle2(getCharId(context),
                                    response.body(),
                                    av).enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(context, "Опубликовано", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {
                                    Log.e("Vedma.Error", t.getMessage());
                                    Toast.makeText(context, "Неуданая попытка публикации попробуйте снова", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.e("Vedma.Error", t.getMessage());
                        Toast.makeText(context, "Неуданая попытка публикации попробуйте снова", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Toast.makeText(context, "Публикация запланирована", Toast.LENGTH_LONG).show();
        finish();
    }
//    @Override
//    public void onProgressUpdate(int percentage, boolean img) {
//        if (img)
//            informProgress(percentage, 1000);
//        else
//            informProgress(percentage,1001);
//    }

    private void informProgress(int p, int num) {
        //String channelId = getString(R.string.default_notification_channel_id);
      //  Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel channel;
            channel = new NotificationChannel("Вспомогательный канал",
                    "Вспомогательный канал",
                    NotificationManager.IMPORTANCE_MIN);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "Вспомогательный канал")
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("Загрузка статьи")
                        .setProgress(100,p,true)
                        .setAutoCancel(true);



        // Since android Oreo notification channel is needed.


            notificationBuilder.setPriority(PRIORITY_LOW);

        Objects.requireNonNull(notificationManager).notify(num/* ID of notification */, notificationBuilder.build());
        if (p>=99)
            notificationManager.cancel(num);

    }

    public void clearInput(View view) {
        videoView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        imageUri=null;
        videoUri=null;
    }
}
