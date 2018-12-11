package in.togethersolutions.logiangle.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.receivers.BootCompletedIntentReceiver;
import in.togethersolutions.logiangle.session.SessionManagement;

public class LocationService extends Service {
    private static final String TAG = "Hello Location Service";
    private String date;
    public String lat = null;
    public String longi = null;
    String batteryStatus =null;
    public String userName = null;
    Timer timer = new Timer();
    //String riderLoggedInSessionID =null;
    private final int TIME_INTERVAL = 10000;
    boolean isLoggedIn[] ={true};

    RequestQueue requestQueue;
    private boolean isRunning  = false;
    GPSTracker gps ;

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }

        gps= new GPSTracker(LocationService.this);

        isRunning = true;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
       // System.out.println("Logout the session check"+checkRiderLogin(userName, timer));
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        if(checkRiderLogin(userName, timer)) {
            location();
        }
        //In this example we are just looping and waits for 1000 milliseconds in each loop.
        return START_STICKY;
    }

    private void location() {
        userName= SessionManagement.getLoggedInUserName(getApplicationContext());
        //requestQueueNotification = Volley.newRequestQueue(getApplicationContext());
        timer.scheduleAtFixedRate(new TimerTask() {

            final Handler handler = new Handler();
            @SuppressLint("DefaultLocale")
            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void run() {
                Runnable runnable = new Runnable() {
                    public void run()
                    {
                        // calculate result1
                        if(checkRiderLogin(userName,timer)) {
                        gps= new GPSTracker(LocationService.this);
                        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
                            public void onReceive(Context context, Intent intent) {
                                try {

                                    context.unregisterReceiver(this);
                                    int rawlevel = intent.getIntExtra("level", -1);
                                    int scale = intent.getIntExtra("scale", -1);
                                    int level = -1;
                                    if (rawlevel >= 0 && scale > 0) {
                                        level = (rawlevel * 100) / scale;
                                        batteryStatus = String.valueOf(level);
                                    }

                                }catch(Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        };
                        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
                        if (!gps.canGetLocation) {
                          //  final String sendURL = "http://gm.logiangle.com/GajananAndroid/mobilecalls2/Api1.php";
                            final String body="";
                            final String token = SessionManagement.getFCMToken(LocationService.this);
                            final String title = "Please enable GPS Location";
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.sendURL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {

                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("msg1", body);
                                    params.put("token",token);
                                    params.put("title",title);
                                    return params;
                                }

                            };

                            requestQueue.add(stringRequest);
                        }

                        if (gps.canGetLocation()) {

                            double latitude = gps.getLatitude();
                            double longitude = gps.getLongitude();
                            lat = Double.toString(latitude);
                            longi = Double.toString(longitude);
                            /*System.out.println("GPS="+gps.isGPSEnabled);
                            System.out.println("Lat="+lat);*/
                            if(latitude!=0.0 && longitude!=0.0)
                            {
                                final String riderLoginDetailID = SessionManagement.getLoggedInSessionID(getApplicationContext());
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                    System.out.println("time out and noConnection.............." + error);
                                                    int duration = Toast.LENGTH_SHORT;
                                                    //  Toast.makeText(getApplicationContext(), "No internet connection", duration).show();
                                                }
                                            }
                                        }
                                ) {
                                    protected Map<String, String> getParams() {
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        TimeZone utc = TimeZone.getTimeZone("UTC");
                                        df.setTimeZone(utc);
                                        date = df.format(Calendar.getInstance().getTime());
                                        Map<String, String> params = new HashMap<>();
                                        params.put("latitude", lat);
                                        params.put("longitude", longi);
                                        params.put("date", date);
                                        params.put("userName", userName);
                                        params.put("riderLoginDetailID", riderLoginDetailID);
                                        params.put("batteryStatus", batteryStatus);
                                        params.put("updateCode", String.valueOf(AllConstant.sendLatLong));
                                        return params;
                                    }
                                };

                                requestQueue.add(stringRequest);
                            }
                            else
                            {
                               // final String sendURL = "http://gm.logiangle.com/GajananAndroid/mobilecalls2/Api1.php";
                                final String body="Location getting 0.0";
                                final String token = SessionManagement.getFCMToken(LocationService.this);
                                final String title = "Please Check GPS Location";
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.sendURL,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {

                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("msg1", body);
                                        params.put("token",token);
                                        params.put("title",title);
                                        return params;
                                    }

                                };

                                requestQueue.add(stringRequest);
                            }
                        }
                    }
                    }
                };
                handler.postDelayed(runnable, 3000);

            }
        }, 0, TIME_INTERVAL);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {

        isRunning = false;
        timer.purge();
        if(timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, BootCompletedIntentReceiver.class);
        this.sendBroadcast(broadcastIntent);
        Log.i(TAG, "Service onDestroy");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        else
        {
            stopSelf();
        }
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
                                    isRunning = false;
                                    timer.purge();
                                    if(timer != null) {
                                        timer.cancel();
                                        timer.purge();
                                        //timer = null;
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        stopForeground(true); //true will remove notification
                                    }
                                    else
                                    {
                                        stopSelf();
                                    }
                                    isLoggedIn[0] =false;

                                    SessionManagement.clearLoggedInEmailAddress(getApplicationContext());
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplication().startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "You have been logged out\n" +
                                            "Please try again or contact Administrator", Toast.LENGTH_SHORT).show();

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

        requestQueue.add(stringRequest);
        return isLoggedIn[0];

    }
}

