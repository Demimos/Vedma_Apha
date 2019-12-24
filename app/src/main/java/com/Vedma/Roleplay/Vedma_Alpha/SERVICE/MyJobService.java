package com.Vedma.Roleplay.Vedma_Alpha.SERVICE;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AlarmReceiver.AlARM_BROADCAST;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AlarmReceiver.AlARM_BROADCAST_GLOBAL;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.isMyServiceRunning;

public class MyJobService extends JobService {


    @Override
    public boolean onStartJob(JobParameters job) {


//            if (!isMyServiceRunning(this))
//            {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(new Intent(this, MyService.class));
//                } else {
//                    startService(new Intent(this, MyService.class));
//                }
//                return false;
//            } else {
                Intent sum = new Intent(AlARM_BROADCAST_GLOBAL);
                sum.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                sum.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
                sum.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
                sendBroadcast(sum);
                Log.d(getString(R.string.vedma_tag),"job_starts");
       //    }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
           return true;
    }

}