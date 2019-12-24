package com.Vedma.Roleplay.Vedma_Alpha.SERVICE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AlarmReceiver.AlARM_BROADCAST;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.sendPushNotification;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static final String PUSH_UPDATE = "com.Vedma.Roleplay.Vedma_Alpha.pushAction";
    private static final String TOKEN_JOB = "com.Vedma.Roleplay.Vedma_Alpha.Token.Job";
    String body = null;
    String title = null;
    public static FirebaseJobDispatcher dispatcher;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(this, s);
    }

    public static void sendRegistrationToServer(final Context context, final String token) {

        VedmaExecutor.getInstance(context).getJSONApi().setPushToken(token).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.code() == 200) {
                    if (dispatcher != null)
                        dispatcher.cancel(TOKEN_JOB);
                } else {
                    ScheduleJob(token, context);
                    Log.d("Vedma.TokenFCM", "Update, delivery failed. Starting a job");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("Vedma.error", "Token update, delivery failed " + t.getMessage() + " Starting a job");
                ScheduleJob(token, context);
            }
        });
        Log.d("Vedma.TokenFCM", token);
    }

    private class TokenDeliveryService extends JobService {
        @Override
        public boolean onStartJob(JobParameters job) {
            sendRegistrationToServer(this, Objects.requireNonNull(job.getExtras()).getString("Token"));
            return false;
        }

        @Override
        public boolean onStopJob(JobParameters job) {
            return true;
        }
    }

    public static void ScheduleJob(String token, Context context) {
        if (dispatcher == null)
            dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Bundle tokenBundle = new Bundle();
        tokenBundle.putString("Token", token);
        //dispatcher.cancel(getString(R.string.vedma_tag));
        Job myJob = dispatcher.newJobBuilder()
                .setExtras(tokenBundle)
                // the JobService that will be called
                .setService(TokenDeliveryService.class)
                // uniquely identifies the job
                .setTag(TOKEN_JOB)
                //  cycled job
                .setRecurring(true)
                // persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(0, 60))
                //  overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        dispatcher.mustSchedule(myJob);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("FCMFCM", "Messagedata" + remoteMessage.getData());


            // Handle message within 10 seconds
            Map<String, String> data = remoteMessage.getData();
            handleNow(data);
        }
        updateNow();

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            title = remoteMessage.getNotification().getTitle();
            //TODO log
            if (remoteMessage.getData().get("update") != null)
                sendPushNotification(this, title, body, 22, remoteMessage.getData().get("update"));//TODO id
            else
                sendPushNotification(this, title, body, 22, "default");

        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void updateNow() {

            Intent update = new Intent(PUSH_UPDATE);
            sendBroadcast(update);
            //   AsyncService.sendNotification(this,"silent","data",9999,"default");

    }
    // [END receive_message]


    private void handleNow(Map<String, String> data) {
         if (data.get("Tick") != null) {
            Tick();
        }
        Log.d(TAG, "Short lived task is done.");

    }

    void Tick() {
        sendBroadcast((new Intent(AlARM_BROADCAST)).addFlags(Intent.FLAG_RECEIVER_FOREGROUND).addFlags(Intent.FLAG_RECEIVER_NO_ABORT));
    }
}
