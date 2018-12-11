package in.togethersolutions.logiangle.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

import in.togethersolutions.logiangle.activities.MainActivity;
import in.togethersolutions.logiangle.fragments.Notification;

public class CheckAllServicesWorking extends Service{


    Timer timer = new Timer();
    //String riderLoggedInSessionID =null;
    private final int TIME_INTERVAL = 10000;
    @Override
    public void onCreate() {
        super.onCreate();
                if (Build.VERSION.SDK_INT >= 26) {
                    String CHANNEL_ID = "my_channel_01";
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_DEFAULT);

                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

                    android.app.Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle("")
                            .setContentText("").build();

                    startForeground(1, notification);
                }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkAllService();
        return START_STICKY;
    }

    private void checkAllService() {
        // check for the location service
        final Handler handler = new Handler();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(isServiceRunning(LocationService.class))
                        {
                            System.out.println("Location service is working");
                        }
                        else
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ContextCompat.startForegroundService(CheckAllServicesWorking.this, new Intent(getApplicationContext(), LocationService.class));
                                ContextCompat.startForegroundService(CheckAllServicesWorking.this, new Intent(getApplicationContext(), NotificationService.class));
                               // ContextCompat.startForegroundService(CheckAllServicesWorking.this, new Intent(getApplicationContext(), CheckAllServicesWorking.class));
                               /* Intent i = new Intent(getApplicationContext(), CheckAllServicesWorking.class);
                                CheckAllServicesWorking.enqueueWork(getApplicationContext(),i);*/
                            }
                            else {
                                Intent i2 = new Intent(CheckAllServicesWorking.this, NotificationService.class);
                                startService(i2);
                                Intent i3 = new Intent(CheckAllServicesWorking.this, LocationService.class);
                                startService(i3);

                            }

                        }

                        //check for the Notification service
                        if(isServiceRunning(NotificationService.class))
                        {
                            System.out.println("Notification service is workings");
                        }
                        else
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                               // ContextCompat.startForegroundService(CheckAllServicesWorking.this, new Intent(getApplicationContext(), LocationService.class));
                              //  ContextCompat.startForegroundService(CheckAllServicesWorking.this, new Intent(getApplicationContext(), NotificationService.class));
                                // ContextCompat.startForegroundService(CheckAllServicesWorking.this, new Intent(getApplicationContext(), CheckAllServicesWorking.class));
                            }
                            else {
                                Intent i2 = new Intent(CheckAllServicesWorking.this, NotificationService.class);
                                startService(i2);
                                Intent i3 = new Intent(CheckAllServicesWorking.this, LocationService.class);
                                startService(i3);

                            }


                        }
                       /* if(isServiceRunning(BackGroundTimer.class))
                        {

                        }
                        else
                        {
                            Intent serviceBackgroundTimer = new Intent(getApplicationContext(), BackGroundTimer.class);
                            startService(serviceBackgroundTimer);
                        }*/
                    }
                };
                handler.postDelayed(runnable, 15000);
            }
        },0,TIME_INTERVAL);

    }
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // isRunning = false;
        timer.purge();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        else
        {
            stopSelf();
        }
        if(timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
    }
}
