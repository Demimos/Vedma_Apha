package com.Vedma.Roleplay.Vedma_Alpha.SERVICE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView bmImage;
    private ProgressBar progressBar;

    public DownloadImageTask(ImageView bmImage, ProgressBar progressBar) {
        this.bmImage = bmImage;
        this.progressBar = progressBar;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay =  urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.d("Vedma.error изображения", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        bmImage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
