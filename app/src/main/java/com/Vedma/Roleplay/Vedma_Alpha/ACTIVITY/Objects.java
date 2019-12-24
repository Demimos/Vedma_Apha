package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.Vedma.Roleplay.Vedma_Alpha.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.widget.Toast.LENGTH_LONG;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startDIARY;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startEVENTS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAIN;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAPS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startNEWS;

public class Objects extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener, NavigationView.OnNavigationItemSelectedListener {

    private QRCodeReaderView qrCodeReaderView;
    private String qrBuffer;
    int Counter=0;
    TextView title;
    private static int ENOUGH=60;
    private ProgressBar progressqr;
    Intent intent;
    View header;


    private boolean flashState;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_object_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.flashlight_drawer)
            onFlashClick();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objects);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        title = findViewById(R.id.ability_title);
        progressqr= findViewById(R.id.progressqr);
        progressqr.setMax(ENOUGH);

        Toolbar toolbar = findViewById(R.id.toolbar_objects);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_objects);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);


        if (ActivityCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(Objects.this, new String[]{CAMERA}, 4);
        } else {
            QRead();
            if (getIntent().hasExtra("action")) {
                intent=getIntent();
                String bb= intent.getStringExtra("name");
                title.setText(intent.getStringExtra("name"));
            }

        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case 4: {//get PID
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Activity activity= this;
                    activity.recreate();
                    // qrCodeReaderView.startCamera();
                } else {

                    Toast.makeText(this,"Исследование объектов невозможно",LENGTH_LONG).show();
                }
            }
        }
    }
    private void QRead()
    { qrCodeReaderView = findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the auto focus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(false);

        qrCodeReaderView.setTorchEnabled(false);
        flashState =false;
        // Use this function to set front camera preview
        //qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();

        Counter=0;
        qrCodeReaderView.startCamera();
    }
    public void onFlashClick()
    {
        if (flashState){

            qrCodeReaderView.setTorchEnabled(false);
            flashState =false;
        } else
        {

            qrCodeReaderView.setTorchEnabled(true);
            flashState =true;
        }
    }
    private void progress(String result)
    {
        if (qrBuffer==null)
            qrBuffer=result;
        else
        {
            if (result.equals(qrBuffer))
                Counter++;
            progressqr.setProgress(Counter);

        }

        qrBuffer=result;
        qrCodeReaderView.setQRDecodingEnabled(false);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);
    }



    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        // resultTextView.setText(text);
        Log.d("cameraqr",text);
        progress(text);
            if (Counter>=ENOUGH) {
            Counter=0;
            qrCodeReaderView.stopCamera();
            VedmaExecutor.getInstance(this).getJSONApi().invokeActionByTag(getCharId(this), text).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code()==200){
                        Toast.makeText(Objects.this, "Действие совершено", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Objects.this, "Неверный QR", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Vedma.error", t.getMessage());
                    Toast.makeText(Objects.this, "Соединение отсутсвует", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(Objects.this, new String[]{CAMERA}, 3);
        } else {
            // qrCodeReaderView.startCamera();
            QRead();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (qrCodeReaderView!=null)
            qrCodeReaderView.stopCamera();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_diary) {
            startDIARY(this);
        } else if (id == R.id.nav_main) {
            startMAIN(this);
        } else if (id == R.id.nav_map) {
            startMAPS(this);
        }  else if (id == R.id.nav_news) {
            startNEWS(this);
        }   else if (id == R.id.nav_object) {
            //startOBJECT(this);
      /*  }  else if (id == R.id.nav_profile) {
            startCONFIG(this);*/
        } else if (id == R.id.nav_events) {
            startEVENTS(this);
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout_objects);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
