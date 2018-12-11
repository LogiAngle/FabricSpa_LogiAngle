package in.togethersolutions.logiangle.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String TAG = "MyFirebaseMessagingService";
    String title;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String myCustomKey = data.get("my_custom_key");
        if(remoteMessage.getNotification().getBody().toString().equals(""))
        {
            SendNote(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }
        else
        {
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }
        if(remoteMessage.getNotification() != null)
        {
            title = remoteMessage.getNotification().getTitle();
        }
    }
    private void SendNote(String body, String title) {
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);
        Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.icon);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(rawBitmap)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
    private int getNotificationIcon() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
           // System.out.print("dfss");
            return  R.drawable.logo;
        }
        return R.mipmap.logo;
    }
    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
    private void sendNotification(String body, String ti) {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.icon);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(rawBitmap)
                .setContentTitle(ti)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                ;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(generateRandom() /* ID of notification */, notificationBuilder.build());
    }
}
