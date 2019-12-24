package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.MessageView;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;


import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MainActivity.CONTENT;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startCONFIG;


public class QRTag extends AppCompatActivity {
    public static  final String TAG_RESPONSE_ACTION="Vedma.Main.Tag.Response";
   // private static final long TIMER_IN = 30000;
  //  TextView timerView;
    ImageView imageView;
    Button qrButton;
    Timer timer;
    Call<MessageView> messageViewCall;
    TextView tagText;
    int quota;
    Call<Void> postCall;
    TextView inMessage;
    EditText outMessage;
    ProgressBar progressBar;
    boolean QRState =false;
    private BroadcastReceiver responseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                bindInterface(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error reading status. Please contact system administrator", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void bindInterface(Intent intent) {
        if (intent.hasExtra(CONTENT))
        {
            tagText.setVisibility(View.VISIBLE);
            String result = (intent.getStringExtra(CONTENT)).replace("\"","");
          //  if (result.length()!=4)
            //    throw new Exception("Not a Code");
            tagText.setText(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrtag);
       // getActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        qrButton = findViewById(R.id.qrbutton);
        imageView = findViewById(R.id.imageQr);
        imageView.setClickable(true);
        imageView.setOnClickListener(v -> {
            if (quota>0&&outMessage.getText().toString().length()>0) {
                if (postCall!=null)
                    postCall.cancel();
                imageView.setClickable(false);
                postCall = VedmaExecutor.getInstance(QRTag.this)
                        .getJSONApi().postMessage(getCharId(QRTag.this), outMessage.getText().toString());
                postCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200)
                        Toast.makeText(QRTag.this, "Отправлено", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(QRTag.this, "Не удалось", Toast.LENGTH_SHORT).show();
                    }
                    imageView.setClickable(true);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(QRTag.this, "Не удалось", Toast.LENGTH_SHORT).show();
                    imageView.setClickable(true);
                }
            });
            }
        });
        tagText = findViewById(R.id.TagText);
        outMessage = findViewById(R.id.out_message);
        progressBar = findViewById(R.id.progressTag);
        inMessage = findViewById(R.id.Message);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (messageViewCall!=null)
                    messageViewCall.cancel();
                messageViewCall = VedmaExecutor.getInstance(QRTag.this).getJSONApi().getBroadcast(getCharId(QRTag.this));
                messageViewCall.enqueue(new Callback<MessageView>() {
                    @Override
                    public void onResponse(Call<MessageView> call, Response<MessageView> response) {
                        if (response.code()==200)
                           if (inMessage!=null){

                               inMessage.setText(response.body().getMessage());
                               if (response.body().getQuota()!=null)
                                   quota=response.body().getQuota();
                               else
                                   quota=0;
                               if (quota>0) {
                               outMessage.setVisibility(View.VISIBLE);
                               outMessage.setHint("Ваша квота равна "+String.valueOf(quota));
                               } else {
                               outMessage.setVisibility(View.GONE);
                               }

                           }
                    }

                    @Override
                    public void onFailure(Call<MessageView> call, Throwable t) {

                    }
                });
            }
        },10000);
        showWitch();
    }
    public void onClickQR(View view)
    {
        if (QRState)
            showWitch();
        else
            showTag();
    }

    private void showWitch()
    {
        QRState =false;
      //  timerView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        qrButton.setText("Показать метку");
        tagText.setVisibility(View.INVISIBLE);
        if (timer!=null)
            timer.cancel();
        imageView.setImageResource(R.drawable.vedma);


    }
    private void showTag()
    {
       // timerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        QRState =true;
        qrButton.setText("Показать Ведьму");

        imageView.setVisibility(View.GONE);
        VedmaExecutor.getInstance(this).getJSONApi().getTag(getCharId(this)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String tag = response.body();
                if (tagText==null)
                    return;
                tagText.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                tagText.setText(tag);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(QRTag.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Vedma.error", t.getMessage());
            }
        });

     //   QRCodeWriter writer = new QRCodeWriter();

//        try {
//            BitMatrix bitMatrix = writer.encode(getMyTag(), BarcodeFormat.QR_CODE, 512, 512);
//            int width = bitMatrix.getWidth();
//            int height = bitMatrix.getHeight();
//            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Account.RGB_565);
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
//                }
//            }
//            ((ImageView) findViewById(R.id.imageQr)).setImageBitmap(bmp);
//
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
//        timer = new MyTimer(TIMER_IN, 1000);
//        timerView = findViewById(R.id.timerID);
//        timerView.setText(String.valueOf(TIMER_IN/1000));
//
//        timer.start();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (messageViewCall!=null)
            messageViewCall.cancel();
        if (postCall!=null)
            postCall.cancel();
        if (timer!=null)
            timer.cancel();
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(TAG_RESPONSE_ACTION);
        registerReceiver(responseReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(responseReceiver);
    }
}
