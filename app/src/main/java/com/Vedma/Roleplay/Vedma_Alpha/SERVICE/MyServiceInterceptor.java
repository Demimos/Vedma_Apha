package com.Vedma.Roleplay.Vedma_Alpha.SERVICE;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MyServiceInterceptor implements Interceptor {
    private String sessionToken;
    private static final Object LOCK= new Object();
    private static MyServiceInterceptor sInstance;
    private MyServiceInterceptor() {
    }
    public static MyServiceInterceptor getInstance() {
        if (sInstance==null)
        {
            synchronized (LOCK){
                sInstance= new MyServiceInterceptor();
            }
        }
        return sInstance;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder requestBuilder = request.newBuilder();
        if (sessionToken!=null &&!sessionToken.equals(""))
            requestBuilder.addHeader("Authorization", sessionToken);
        return chain.proceed(requestBuilder.build());
    }
}