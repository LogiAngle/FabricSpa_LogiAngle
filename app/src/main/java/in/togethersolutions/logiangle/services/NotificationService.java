package in.togethersolutions.logiangle.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.session.SessionManagement;

public class NotificationService extends Service {
    boolean isLoggedIn[] ={true};

    private static final String TAG = "Hello Notification Service";

    private boolean isRunning  = false;

    String userName =null;
    String id =null;
    String isSent =null;
    private String token = null;
    RequestQueue requestQueueNotification;
    @Override

    public void onCreate() {
        Log.i(TAG, "Service onCreate");
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
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        //In this example we are just looping and waits for 1000 milliseconds in each loop.
        requestQueueNotification = Volley.newRequestQueue(getApplicationContext());
      //  startTimer();
        startTimerService();

        return START_STICKY;
    }

    private void startTimerService() {
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

// This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                getNotification();
                //System.out.println("Santosh Network check");
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 20 second
        timer.schedule(timerTask, 1000, 20000); //
    }


    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                getNotification();
            }
        };
    }
    /*Get Notification Record*/
    private void getNotification() {
   //     System.out.println("Santosh One");
        userName= SessionManagement.getLoggedInUserName(getApplicationContext());
        requestQueueNotification.getCache().clear();
        StringRequest stringRequestCheckLogin = new StringRequest(Request.Method.POST, AllURL.checkLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray;
                        JSONObject jsonObject,jsonObject1;
                        try {
                            System.out.println("Check Login Response"+response);
                            jsonObject = new JSONObject(response);
                            jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length() ; i++) {
                                jsonObject1 = jsonArray.getJSONObject(i);
                                System.out.println(jsonObject1.getBoolean("isLoggedIn"));
                                if(jsonObject1.getBoolean("isLoggedIn")){
                                    StringRequest stringRequestSendNotification = new StringRequest(Request.Method.POST, AllURL.sendNotification, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            System.out.println(response +"  Send Notification");
                                            JSONObject jsonObject = null;
                                            JSONArray jsonArray =null;
                                            try {
                                                jsonObject = new JSONObject(response);
                                                String body = null;
                                                String title = null;
                                                if(jsonObject != null) {
                                                    jsonArray = jsonObject.getJSONArray("result");

                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        JSONObject o = jsonArray.getJSONObject(i);
                                                        if(o.getString("Response").equals("SUCCESS"))
                                                        {
                                                            body = o.getString("body");
                                                            title = o.getString("title");
                                                            id = o.getString("notificationID");
                                                            isSent = o.getString("sent");
                                                            sentNotification(body, title, id, isSent,i);
                                                        }
                                                        else if(o.getString("Response").equals("FAIL"))
                                                        {
                                                            System.out.println("Notification not found yet");
                                                        }

                                                    }
                                                }
                                            }
                                            catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }){
                                        protected Map<String,String> getParams() {

                                            Map<String, String> params = new HashMap<>();
                                            params.put("userName",userName);
                                            //params.put("token",token);
                                            return params;
                                        }
                                    };

                                    requestQueueNotification.add(stringRequestSendNotification);
                                    //requestQueueNotification.removeRequestFinishedListener(stringRequestSendNotification);
                                }
                                else
                                {
                                    //isLoggedIn[0] =false;
                                    stoptimertask();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String, String>();
                param.put("userName",userName);
                param.put("loginSessionID",SessionManagement.getLoggedInSessionID(getApplicationContext()));
                return param;
            }
        };

        requestQueueNotification.add(stringRequestCheckLogin);
       


    }

    private void sentNotification(final String body, final String title, final String id1, String isSent, int i) {
        requestQueueNotification.getCache().clear();
        token = SessionManagement.getFCMToken(getApplicationContext());
        if(isSent.equals("0")) {
            addNotification(title,body,id1,i);

            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, AllURL.updateURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Id", id1);
                    params.put("updateCode", "14");
                    return params;
                }
            };
            //   RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueueNotification.add(stringRequest2);

            /*StringRequest stringRequestUpdateNotification = new StringRequest(Request.Method.POST, AllURL.sendURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println(response);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        if (success.equals("1")) {
                            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("Id", id1);
                                    params.put("updateCode", "14");
                                    return params;
                                }
                            };
                         //   RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueueNotification.add(stringRequest2);
                        }
                    } catch (JSONException e) {
                        System.out.println("Notification Not Found");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            })
            {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("msg1", body);
                    params.put("token",token);
                    params.put("title",title);
                    return params;
                }
            };
            //  AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            requestQueueNotification.add(stringRequestUpdateNotification);*/


        }
        else
        {
            System.out.println("All notification sent successfully");
        }
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }


    private void addNotification(String title, String msg, String id, int i) {
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        if(Build.VERSION.SDK_INT >= 26)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    title,
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(title).setAutoCancel(true).setSmallIcon(R.drawable.icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon))
                    .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(msg).build();

           // startForeground(0, notification);
           NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
            mNotificationManager.notify(i , notification);
        }else {
            NotificationManager mNotificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, NotificationService.class), 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this).setSmallIcon(R.drawable.icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon))
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(msg);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(i, mBuilder.build());
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        stoptimertask();
        isRunning = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        else
        {
            stopSelf();
        }
        Log.i(TAG, "Service onDestroy");
    }


    public  boolean checkRiderLogin(final String userName, final Timer timer) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.checkLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray;
                        JSONObject jsonObject,jsonObject1;

                        try {
                            jsonObject = new JSONObject(response);
                            jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length() ; i++) {
                                jsonObject1 = jsonArray.getJSONObject(i);
                                //   System.out.println(jsonObject1.getBoolean("isLoggedIn"));
                                if(jsonObject1.getBoolean("isLoggedIn")){

                                    isLoggedIn[0] =true;
                                }
                                else
                                {
                                    isLoggedIn[0] =false;

                                    SessionManagement.clearLoggedInEmailAddress(getApplicationContext());
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplication().startActivity(intent);
                                    /*Toast.makeText(getApplicationContext(), "You have been logged out\n" +
                                            "Please try again or contact Administrator", Toast.LENGTH_SHORT).show();*/
                                    stoptimertask();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // System.out.println(response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String, String>();
                param.put("userName",userName);
                param.put("loginSessionID",SessionManagement.getLoggedInSessionID(getApplicationContext()));
                return param;
            }
        };

        requestQueueNotification.add(stringRequest);
        return isLoggedIn[0];

    }

}

