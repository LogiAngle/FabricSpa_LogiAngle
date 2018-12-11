package in.togethersolutions.logiangle.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import in.togethersolutions.logiangle.fragments.Notification;
import in.togethersolutions.logiangle.services.CheckAllServicesWorking;
import in.togethersolutions.logiangle.services.LocationService;
import in.togethersolutions.logiangle.services.NotificationService;

import static in.togethersolutions.logiangle.activities.MainActivity.dialog;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            try
            {
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            if (isOnline(context)) {
                dialog(true);
               // Log.e("keshav", "Online Connect Intenet ");
            } else {
                dialog(false);
            //    Log.e("keshav", "Conectivity Failure !!! ");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(netInfo.isConnectedOrConnecting())
            {
                return true;
            }
            else
            {
                NetworkInfo networkInfo  = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if(networkInfo.isConnectedOrConnecting())
                {
                    return true;
                }
                return false;
            }

            //should check null because in airplane mode it will be null
          /*  boolean isWiFi = netInfo.getType() == ConnectivityManager.TYPE_WIFI;
            if(isWiFi)
            {

            }
            return (netInfo != null && netInfo.isConnectedOrConnecting()&& netInfo.isAvailable());*/
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}

