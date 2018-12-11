package in.togethersolutions.logiangle.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.session.SessionManagement;

public class BackGroundTimer extends Service {


    GPSTracker gps;
    private String date;

    String batteryStatus ="100";
    public String lat = null;
    public String longi = null;
    String userName =null;

    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;


    private static final String TAG = "HelloService";

    private boolean isRunning  = false;
    boolean setFlag = false;
    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        //In this example we are just looping and waits for 1000 milliseconds in each loop.
      //  startTimer();
        setFlag = true;


        return START_STICKY;

    }

    private void yourMethod() {
        System.out.println("In your Method");
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        //initializeTimerTask();

        //schedule the timer, to wake up every 1 minute
        timer.schedule(timerTask, 1000, 60000); //
    }


    public void initializeTimerTask() {

        userName= SessionManagement.getLoggedInUserName(getApplicationContext());

        timerTask = new TimerTask() {
            public void run() {

                System.out.println("Hello Location");
               /* BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        context.unregisterReceiver(this);
                        int rawlevel = intent.getIntExtra("level", -1);
                        int scale = intent.getIntExtra("scale", -1);
                        int level = -1;
                        if (rawlevel >= 0 && scale > 0) {
                            level = (rawlevel * 100) / scale;
                            batteryStatus = String.valueOf(level);
                            System.out.println("Battery Level" + level);

                        }


                    }
                };
                IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                registerReceiver(batteryLevelReceiver, batteryLevelFilter);*/
                gps=new GPSTracker(getApplicationContext());
                /*
                 */
                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    lat = Double.toString(latitude);
                    longi = Double.toString(longitude);

                    final String riderLoginDetailID = SessionManagement.getLoggedInSessionID(getApplicationContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println(response + "location");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {

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

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
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

        Log.i(TAG, "Service onDestroy");
    }
}

