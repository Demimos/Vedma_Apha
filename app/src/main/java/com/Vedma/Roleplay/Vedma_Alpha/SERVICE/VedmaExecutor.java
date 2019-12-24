package com.Vedma.Roleplay.Vedma_Alpha.SERVICE;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.v4.content.ContextCompat.createDeviceProtectedStorageContext;
import static android.support.v4.content.ContextCompat.startActivity;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.ActLikeInactive;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.ExitCurrentGame;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.LogOff;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getToken;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.sendNotification;

public class VedmaExecutor {
    private static VedmaExecutor sInstance;
    private static final Object LOCK= new Object();
    private final Executor DiskIO;
    private Context context;
    private final Executor MainThread;
    private static final String BASE_URL = "https://vedma-proj.ru";
    private static final String TEST_URL = "http://localhost:49397";
    private Retrofit retrofit;
    public static VedmaExecutor getInstance(Context context) {
        if (sInstance==null)
        {
            synchronized (LOCK){
                sInstance= new VedmaExecutor(context,Executors.newSingleThreadExecutor(), new MainThreadExecutor()/*,Executors.newFixedThreadPool(3)*/);
            }
        }
        String token = getToken(context);
        if (token!=null&&!token.equals("")&&
                (MyServiceInterceptor.getInstance().getSessionToken()==null||MyServiceInterceptor.getInstance().getSessionToken().equals("")))
        {
            MyServiceInterceptor.getInstance().setSessionToken(token);
        }

        return sInstance;
    }

    private VedmaExecutor(final Context context, Executor DiskIO, Executor MainThread/*, Executor NetWorkIO*/) {
        this.DiskIO = DiskIO;
        this.MainThread = MainThread;
       // this.NetworkIO = NetWorkIO;
        this.context=context;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        MyServiceInterceptor tokenInterceptor = MyServiceInterceptor.getInstance();

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(tokenInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        okhttp3.Response response = chain.proceed(request);
                        if (response.code() == 404) {
                            ExitCurrentGame(context);
                            return response;
                        } else if (response.code()==401){
                            LogOff(context);
                        } else if (response.code()==205){
                            ActLikeInactive(context);
                        }
                        return response;
                    }
                });
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
    }
    public ApiService getJSONApi() {
            return retrofit.create(ApiService.class);
        }

    public Executor DiskIO() {
        return DiskIO;
    }

    public Executor MainThread() {
        return MainThread;
    }

//    public Executor NetworkIO() {
//        return NetworkIO;
//    }

    public static class MainThreadExecutor implements Executor
    {
        private Handler mainThreadHandler= new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
