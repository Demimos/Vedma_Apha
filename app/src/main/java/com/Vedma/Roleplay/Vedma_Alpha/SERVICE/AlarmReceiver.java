package com.Vedma.Roleplay.Vedma_Alpha.SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.isMyServiceRunning;


public class AlarmReceiver extends BroadcastReceiver {
    public static final String AlARM_BROADCAST = "com.Vedma.Roleplay.Vedma_Alpha.alarm";
    public static  final  String AlARM_BROADCAST_GLOBAL ="com.Vedma.Roleplay.Vedma_Alpha.alarm_global";
    @Override
    public void onReceive(Context context, Intent intent)
    {
//        if (!isMyServiceRunning(context)) {
            try {
                Intent sum = new Intent(context, MyService.class);
                sum.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                sum.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
                sum.addFlags(Intent.FLAG_RECEIVER_NO_ABORT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    context.startForegroundService(sum);
                else
                    context.startService(sum);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
//        } else {
//            Intent sum = new Intent(AlARM_BROADCAST);
//            sum.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//            sum.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
//            //sum.addFlags(Intent.FLAG_RECEIVER_NO_ABORT);
//            context.sendBroadcast(sum);
//            Log.d("Vedma.AlarmReceiver","service_on");
//        }
    }

}
