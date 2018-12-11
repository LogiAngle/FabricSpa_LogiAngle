package in.togethersolutions.logiangle.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.widget.Toast;


import in.togethersolutions.logiangle.services.CheckAllServicesWorking;
import in.togethersolutions.logiangle.services.LocationService;


public class BootCompletedIntentReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
        //    Toast.makeText(context, "Application Restart logiangle", Toast.LENGTH_SHORT).show();
            //Snackbar.make(context,"Application Restart ",Snackbar.LENGTH_INDEFINITE).show();

            System.out.println("Application Restarted");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                Intent pushIntent = new Intent(context, CheckAllServicesWorking.class);
                pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startForegroundService(pushIntent);
                //context.startForegroundService(new Intent(context, ServedService.class));
            } else {
                Intent pushIntent = new Intent(context, CheckAllServicesWorking.class);
                pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(pushIntent);
            }

        }
    }
}
