package in.togethersolutions.logiangle.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.fragments.BreachedOrder;
import in.togethersolutions.logiangle.fragments.Notification;
import in.togethersolutions.logiangle.fragments.OrderHistory;
import in.togethersolutions.logiangle.fragments.OrderManagement;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.javaClass.logoutInterface;
import in.togethersolutions.logiangle.receivers.ConnectivityReceiver;
import in.togethersolutions.logiangle.services.CheckAllServicesWorking;
import in.togethersolutions.logiangle.services.GPSTracker;
import in.togethersolutions.logiangle.services.LocationService;
import in.togethersolutions.logiangle.services.NotificationService;
import in.togethersolutions.logiangle.session.SessionManagement;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,logoutInterface {

    String userName;
    private String date;
    private Snackbar snackbar;
    ProgressBar progressBar;
    GPSTracker gps;
    private TextView textViewName;
    private TextView textViewUser;
    BroadcastReceiver mNetworkReceiver;
    static AlertDialog alertDialog;

    private static CoordinatorLayout coordinatorLayout;

    TextView datePickerTextView;
    TextView title;
    boolean isLoggedIn[] = {true};
    private AppBarLayout appBarLayout;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", /*Locale.getDefault()*/Locale.ENGLISH);

    private CompactCalendarView compactCalendarView;

    ImageView arrow;
    private boolean isExpanded = false;
    RelativeLayout datePickerButton;

    Fragment fragment = null;
    Event event = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //checkConnection();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        textViewName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textViewName);
        textViewUser =(TextView)navigationView.getHeaderView(0).findViewById(R.id.textViewUser);
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.VISIBLE);
        gps=new GPSTracker(MainActivity.this);
        /*mNetworkReceiver = new NetworkChangeReceiver();*/
       // registerNetworkBroadcastForNougat();
        alertDialog  = new AlertDialog.Builder(MainActivity.this).create();
        userName= SessionManagement.getLoggedInUserName(MainActivity.this);


        riderInfo();

        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorlayout);

            //riderInfo();
            displayView(0);
       /* }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            //setting dialog title
            alertDialog.setTitle("Logout");
            //setting dialog message
            alertDialog.setIcon(R.drawable.logout);
            alertDialog.setCancelable(false);
            alertDialog.setMessage("You have been logged out\n" +
                    "Please try again or contact Administrator");

            // On pressing Settings button
            alertDialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SessionManagement.clearLoggedInEmailAddress(MainActivity.this);
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            });
            alertDialog.show();
        }*/
        inItComponent();
    }
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        dialog(isConnected);
    }
    private void inItComponent() {


        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView
        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        // Force English
        compactCalendarView.setLocale(TimeZone.getDefault(), /*Locale.getDefault()*/Locale.ENGLISH);

        compactCalendarView.setShouldDrawDaysHeader(true);
        getOrderAsEvent(compactCalendarView);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setSubtitle(dateFormat.format(dateClicked));

                Bundle bundle = new Bundle();

                OrderManagement orderManagement = new OrderManagement();
                bundle.putString("edttext", dateFormat.format(dateClicked));
                orderManagement.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.screen_area,orderManagement);
                fragmentTransaction.commit();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));
                Bundle bundle = new Bundle();
                OrderManagement orderManagement = new OrderManagement();
                bundle.putString("edttext", dateFormat.format(firstDayOfNewMonth));
                orderManagement.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.screen_area,orderManagement);
                fragmentTransaction.commit();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        // Set current date to today
        setCurrentDate(new Date());

        arrow = (ImageView) findViewById(R.id.date_picker_arrow);

         datePickerButton = (RelativeLayout) findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rotation = isExpanded ? 0 : 180;
                ViewCompat.animate(arrow).rotation(rotation).start();

                isExpanded = !isExpanded;
                appBarLayout.setExpanded(isExpanded, true);
            }
        });
    }
    private void getOrderAsEvent(final CompactCalendarView compactCalendarView) {
        StringRequest stringRequestAsEvent = new StringRequest(Request.Method.POST, AllURL.getOrderDate,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("result").equals("SUCCESS"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("array");
                                JSONObject jsonObject1 = null;
                                for(int i=0; i< jsonArray.length();i++)
                                {
                                    jsonObject1=jsonArray.getJSONObject(i);
                                    event  = new Event(Color.WHITE,getDateInMillis(jsonObject1.getString("DateTime")),"Orders");
                                    compactCalendarView.addEvent(event);
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
                Map<String,String> params = new HashMap<String,String>();
                params.put("userName", userName);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequestAsEvent);
    }

    private void setCurrentDate(Date date) {
        progressBar.setVisibility(View.INVISIBLE);
        setSubtitle(dateFormat.format(date));
        if (compactCalendarView != null) {
            compactCalendarView.setCurrentDate(date);
        }

    }
    private void setSubtitle(String subtitle) {
        datePickerTextView = (TextView) findViewById(R.id.date_picker_text_view);
        title = (TextView)findViewById(R.id.title);
        if (datePickerTextView != null) {
            datePickerTextView.setText(convertDate(subtitle));
            Bundle bundle = new Bundle();
            OrderManagement orderManagement = new OrderManagement();
            bundle.putString("edttext", subtitle);
            orderManagement.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_area,orderManagement);
            fragmentTransaction.commit();
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void riderInfo() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                JSONArray jsonArray =null;
                try {
                    jsonObject = new JSONObject(response);
                    jsonArray = jsonObject.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        String name = o.getString("Name");
                        String email = o.getString("Email");
                        SessionManagement.setRiderEmail(MainActivity.this,email);
                        SessionManagement.setRiderName(MainActivity.this,name);

                        textViewName.setText(SessionManagement.getRiderName(MainActivity.this));
                        textViewUser.setText(SessionManagement.getRiderEmail(MainActivity.this));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("RiderInfo Error"+error);
            }
        }){
            protected Map<String,String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("updateCode", String.valueOf(AllConstant.riderInfo));
                params.put("riderUserName",userName);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            new AlertDialog.Builder(MainActivity.this).setTitle("Logout").setMessage("would you like to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //progressDialog.show();
                            logOutUpdate();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }

    @SuppressLint("ResourceAsColor")
    public static void dialog(boolean value){

        if(value){
            alertDialog.dismiss();
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Back online", Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(R.color.enable_thumb_green);

            snackbar.show();
        }else {
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
            alertDialog.setIcon(R.drawable.no_internet);
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterNetworkChanges();
    }


    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        else
        {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(MainActivity.this,OrdersOnMap.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ordermanagement) {

            setCurrentDate(new Date());
            Bundle bundle = new Bundle();
            OrderManagement orderManagement = new OrderManagement();

            bundle.putString("edttext", dateFormat.format(new Date()));
            // set Fragmentclass Arguments
            orderManagement.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_area,orderManagement);
            fragmentTransaction.commit();
            arrow.setVisibility(View.VISIBLE);
            compactCalendarView.setVisibility(View.VISIBLE);
            appBarLayout.setExpanded(isExpanded, true);
            datePickerTextView.setVisibility(View.VISIBLE);
            datePickerButton.setEnabled(true);
            title.setText("Order Management");

        } else if (id == R.id.nav_logout) {

            logOutUpdate();
            Intent intent = new Intent(MainActivity.this, LocationService.class);
            stopService(intent);
            Intent i2 = new Intent(MainActivity.this,NotificationService.class);
            stopService(i2);


        } else if(id == R.id.nav_orderhistory) {
            fragment = new OrderHistory();
            arrow.setVisibility(View.INVISIBLE);
            compactCalendarView.setVisibility(View.INVISIBLE);
            //appBarLayout.setExpanded(isExpanded, false);
            appBarLayout.setExpanded(false);
            appBarLayout.setEnabled(false);
            datePickerButton.setEnabled(false);
            datePickerTextView.setText("");
            title.setText("Order History");
            datePickerTextView.setText("");
        }
        else if(id == R.id.nav_notification){
            fragment = new Notification();
            arrow.setVisibility(View.INVISIBLE);
            compactCalendarView.setVisibility(View.INVISIBLE);
            //appBarLayout.setExpanded(isExpanded, false);
            appBarLayout.setExpanded(false);
            appBarLayout.setEnabled(false);
            datePickerButton.setEnabled(false);
            datePickerTextView.setText("");
            title.setText("Notification");

        }
        else if (id == R.id.nav_breached)
        {
            fragment = new BreachedOrder();
            arrow.setVisibility(View.INVISIBLE);
            compactCalendarView.setVisibility(View.INVISIBLE);
            //appBarLayout.setExpanded(isExpanded, false);
            appBarLayout.setExpanded(false);
            appBarLayout.setEnabled(false);
            datePickerButton.setEnabled(false);
            datePickerTextView.setText("");
            title.setText("Breached Order");

        }

        displayView(0);
        if (fragment != null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_area,fragment);
            fragmentTransaction.commit();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void logOutUpdate() {
        // progressDialog.show();
     //   progressBar.setVisibility(View.VISIBLE);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        df.setTimeZone(utc);
        date = df.format(Calendar.getInstance().getTime());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();

                        logOut();
                        finish();

                       // progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    // progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //hasInternet = false;
                            snackbar = Snackbar.make(coordinatorLayout, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //    makeRequest();
                                            Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            snackbar.show();
                        }
                    });
                }
            }
        }){
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<String,String>();
                params.put("userName",userName);
                params.put("logoutDateTime",date);
                params.put("updateCode", String.valueOf(AllConstant.logout));
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
        SharedPreferences SM = getSharedPreferences(AllConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = SM.edit();
        edit.putBoolean(AllConstant.LOGGEDIN_SHARED_PREF, false);
        edit.clear();
        edit.commit();
    }

    /*Logout function*/
    private void logOut() {

        // System.out.println("Santosh");
        SessionManagement.clearLoggedInEmailAddress(this);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent i2 = new Intent(MainActivity.this,NotificationService.class);
        stopService(i2);
        Intent i3= new Intent(MainActivity.this,LocationService.class);
        stopService(i3);
        startActivity(intent);
        gps.stopUsingGPS();
        finish();
    }


    private void displayView(int position) {
        Fragment fragment = null;
        String fragmentTags = "";
        switch (position) {
            case 0:
                /*Intent intent = new Intent(MainActivity.this, in.togethersolutions.logiangle.activities.OrderManagement.class);
                startActivity(intent);*/
                Bundle bundle = new Bundle();
                OrderManagement orderManagement = new OrderManagement();

                bundle.putString("edttext", dateFormat.format(new Date()));
                // set Fragmentclass Arguments
                orderManagement.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.screen_area,orderManagement);
                fragmentTransaction.commit();
                /*fragment = new in.togethersolutions.logiangle.fragments.OrderManagement();*/
                break;

            default:
                break;
        }

        if(fragment != null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_area,fragment);
            fragmentTransaction.commit();
            // getSupportFragmentManager().beginTransaction().replace(R.id.screen_area, fragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(MainActivity.this, new Intent(getApplicationContext(), LocationService.class));
            ContextCompat.startForegroundService(MainActivity.this, new Intent(getApplicationContext(), NotificationService.class));
            ContextCompat.startForegroundService(MainActivity.this, new Intent(getApplicationContext(), CheckAllServicesWorking.class));
        }
        else {
            Intent i2 = new Intent(MainActivity.this, NotificationService.class);
            startService(i2);
            Intent i3 = new Intent(MainActivity.this, LocationService.class);
            startService(i3);
            Intent i4 = new Intent(MainActivity.this, CheckAllServicesWorking.class);
            startService(i4);
        }*/
    }

    public  boolean checkRiderLogin(final String userName) {


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
                                System.out.println(jsonObject1.getBoolean("isLoggedIn"));
                                if(jsonObject1.getBoolean("isLoggedIn")){

                                    isLoggedIn[0] =true;
                                }
                                else
                                {

                                    SessionManagement.clearLoggedInEmailAddress(MainActivity.this);
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Intent i2 = new Intent(MainActivity.this,NotificationService.class);
                                    stopService(i2);
                                    Intent i3= new Intent(MainActivity.this,LocationService.class);
                                   /* Toast.makeText(MainActivity.this, "You have been logged out\n" +
                                            "Please try again or contact Administrator", Toast.LENGTH_SHORT).show();*/
                                    stopService(i3);
                                    startActivity(intent);
                                    gps.stopUsingGPS();
                                    //isLoggedIn[0] =false;


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
                param.put("loginSessionID",SessionManagement.getLoggedInSessionID(MainActivity.this));
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
        return isLoggedIn[0];

    }

    @Override
    public void logoutFromFragment() {
        /*logOut();*/
     //   Toast.makeText(this, "You have been logged out\nPlease try again or contact Administrator", Toast.LENGTH_SHORT).show();
        SessionManagement.clearLoggedInEmailAddress(this);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent i2 = new Intent(MainActivity.this,NotificationService.class);
        stopService(i2);
        Intent i3= new Intent(MainActivity.this,LocationService.class);
        stopService(i3);
        Intent i4= new Intent(MainActivity.this,CheckAllServicesWorking.class);
        stopService(i4);
        startActivity(intent);
        gps.stopUsingGPS();
        finish();
    }
    public String convertDate(String datetime)
    {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy" );
        Date date;
        String changeDateFormatted=null;
        try {
            date = originalFormat.parse(datetime);
            //System.out.println("Old Format Santosh :   " + originalFormat.format(date));
            //System.out.println("New Format Santosh :   " + targetFormat.format(date));
            changeDateFormatted = targetFormat.format(date);
        } catch (ParseException ex) {
            // Handle Exception.
        }
        return changeDateFormatted;
    }

    private long getDateInMillis(String inputDate)
    {
        System.out.println(inputDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dateConvertInIST(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();
        return millis;
    }
    private String dateConvertInIST(String inputDate)
    {
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df2.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date2 = null;
        String formattedDeliverDate=null;
        try {
            date2 = df2.parse(inputDate);
            df2.setTimeZone(TimeZone.getDefault());
            formattedDeliverDate = df2.format(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDeliverDate;
    }
}
