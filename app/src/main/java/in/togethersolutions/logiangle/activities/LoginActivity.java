package in.togethersolutions.logiangle.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.javaClass.SendMail;
import in.togethersolutions.logiangle.receivers.BootCompletedIntentReceiver;
import in.togethersolutions.logiangle.receivers.NetworkChangeReceiver;
import in.togethersolutions.logiangle.services.CheckAllServicesWorking;
import in.togethersolutions.logiangle.services.GPSTracker;
import in.togethersolutions.logiangle.services.LocationService;
import in.togethersolutions.logiangle.services.NotificationService;
import in.togethersolutions.logiangle.session.SessionManagement;

import static android.widget.Toast.LENGTH_LONG;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static EditText editTextUserName;
    private EditText editTextPassword;
    private Button btnLogin;
    private String token;
    private String date;
    GPSTracker gps;
    ProgressDialog loading;
    JSONObject jsonObject,jsonObject1;
    JSONArray jsonArray;

    Timer timer = new Timer();
    //String riderLoggedInSessionID =null;
    private final int TIME_INTERVAL = 60000;


    String batteryStatus = null;
    public String lat = null;
    public String longi = null;
    BroadcastReceiver mNetworkReceiver;
    static AlertDialog alertDialog;
    String username = null;
    private TextView txtForgotPassword;

    /*Remember me */

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private CheckBox checkBoxRememberMe;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        mNetworkReceiver = new BootCompletedIntentReceiver();
      //  mNetworkReceiver = new NetworkChangeReceiver();
       // registerNetworkBroadcastForNougat();
        alertDialog  = new AlertDialog.Builder(LoginActivity.this).create();
//        checkConnection();
        //checkInternetConnection();
        checkVersion();
        checkGPSIsOn();

        inItComponent();
        inItListner();

        checkLogin();
        checkPermission();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        df.setTimeZone(utc);
    }

    private void checkVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            final String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            StringRequest stringRequest  = new StringRequest(Request.Method.POST, AllURL.checkApplicationVersion,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(version.equals(response.toString()))
                            {

                            }
                            else
                            {
                                android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                                //setting dialog title
                                alertDialog.setTitle("New update available");
                                //setting dialog message
                                alertDialog.setCancelable(false);
                                alertDialog.setIcon(R.drawable.update_version);
                                alertDialog.setMessage("Please, update the app to new version from play store");
                                alertDialog.setPositiveButton("update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                    }
                                });

                                alertDialog.show();
                                Intent i2 = new Intent(LoginActivity.this, NotificationService.class);
                                stopService(i2);
                                Intent i3 = new Intent(LoginActivity.this, LocationService.class);
                                stopService(i3);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                      //  loading.dismiss();
                        System.out.println("time out and noConnection...................." + error);
                        int duration = Toast.LENGTH_SHORT;
                        Toast.makeText(LoginActivity.this, "No internet connection", duration).show();
                    }
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> param  = new HashMap<String, String>();
                    param.put("versionName",version);
                    return param;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            requestQueue.add(stringRequest);
        } catch (PackageManager.NameNotFoundException e) {
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
    }

   /* public static void dialog(boolean value){

        if(value){
            *//*editTextUserName.setText("");

            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                  editTextUserName.setText("");
                }
            };
            handler.postDelayed(delayrunnable, 3000);*//*
            alertDialog.dismiss();
        }else {
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
            alertDialog.setIcon(R.drawable.no_internet);
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }*/


    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        //MyApplication.getInstance().setConnectivityListener(this);
    }
    /*Check GPS Location Is On*/
    private void checkGPSIsOn() {
        gps = new GPSTracker(LoginActivity.this);
        if (!gps.canGetLocation) {
            gps.showSettingsAlert();
        }
    }

    /*Check Permission*/
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.CAMERA,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.RECEIVE_BOOT_COMPLETED

                        },
                        AllConstant.MY_CAMERA_REQUEST_CODE);

            }


        }

    }


    /*Check User Already Logged in*/
    private void checkLogin() {
        if (SessionManagement.getUserLoggedInStatus(this)) {

            username = SessionManagement.getLoggedInUserName(getApplicationContext());
            if(isServiceRunning(LocationService.class)||isServiceRunning(NotificationService.class))
            {

            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(LoginActivity.this, new Intent(getApplicationContext(), LocationService.class));
                    ContextCompat.startForegroundService(LoginActivity.this, new Intent(getApplicationContext(), NotificationService.class));
                    ContextCompat.startForegroundService(LoginActivity.this, new Intent(getApplicationContext(), CheckAllServicesWorking.class));
                } else {
                    Intent i2 = new Intent(LoginActivity.this, NotificationService.class);
                    startService(i2);
                    Intent i3 = new Intent(LoginActivity.this, LocationService.class);
                    startService(i3);
                    Intent i4 = new Intent(LoginActivity.this, CheckAllServicesWorking.class);
                    startService(i4);
                }
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
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
    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    /*Create New Login Session ID*/
    private void createLoginSession() {
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final String android_id = TelephonyMgr.getDeviceId();
        username = editTextUserName.getText().toString().trim();
        SessionManagement.setUserLoggedInStatus(this,true);
        SessionManagement.setLoggedInUserName(this,username);
        token = FirebaseInstanceId.getInstance().getToken();
        SessionManagement.setFCMToken(this,token);
       // System.out.println(AllURL.updateURL);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        String riderLoginID = response.trim();
                        //Toast.makeText(LoginActivity.this,riderLoginID,Toast.LENGTH_LONG).show();
                        SessionManagement.setLoggedInSessionID(LoginActivity.this,riderLoginID);

                     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ContextCompat.startForegroundService(LoginActivity.this, new Intent(getApplicationContext(), LocationService.class));
                            ContextCompat.startForegroundService(LoginActivity.this, new Intent(getApplicationContext(), NotificationService.class));
                            ContextCompat.startForegroundService(LoginActivity.this, new Intent(getApplicationContext(), CheckAllServicesWorking.class));
                        }
                        else {
                            Intent i2 = new Intent(LoginActivity.this, NotificationService.class);
                            startService(i2);
                            Intent i3 = new Intent(LoginActivity.this, LocationService.class);
                            startService(i3);
                            Intent i4 = new Intent(LoginActivity.this, CheckAllServicesWorking.class);
                            startService(i4);
                        }*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }){
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName",username);
                params.put("loginTime",getUTCTime());
                params.put("mobileDevice",android_id);
                params.put("updateCode", String.valueOf(AllConstant.loginSession));
                return params;
            }
        }; stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }
    /*Initialize UI Component*/
    private void inItComponent() {
        editTextUserName = (EditText)findViewById(R.id.editTextUserName);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        btnLogin = (Button)findViewById(R.id.buttonSignIn);
        txtForgotPassword = (TextView)findViewById(R.id.txtForgotPassword);
        checkBoxRememberMe = (CheckBox)findViewById(R.id.chkRememberMe);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin == true) {
            editTextUserName.setText(loginPreferences.getString("rmUserName", ""));
            editTextPassword.setText(loginPreferences.getString("rmPassword", ""));
            checkBoxRememberMe.setChecked(true);
        }
        // final ProgressBar simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
    }
    /*Initialize Event Listner */
    private void inItListner() {
        btnLogin.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
        checkBoxRememberMe.setOnClickListener(this);
    }

    /*On Click Event*/
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.buttonSignIn:

                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    final String version = pInfo.versionName;
                    int verCode = pInfo.versionCode;
                //    System.out.println(version+" == Application version name & "+ verCode+"  Code" );
                    StringRequest stringRequest  = new StringRequest(Request.Method.POST, AllURL.checkApplicationVersion,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println("Response for the Check verison"+response);
                                    if(version.equals(response.toString()))
                                    {
                                        validateUser();
                                    }
                                    else
                                    {
                                        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                                        //setting dialog title
                                        alertDialog.setTitle("New update available");
                                        //setting dialog message
                                        alertDialog.setCancelable(false);
                                        alertDialog.setIcon(R.drawable.update_version);
                                        alertDialog.setMessage("Please, update the app to new version from play store");
                                        alertDialog.setPositiveButton("update", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            }
                                        });

                                        alertDialog.show();
                                        Intent i2 = new Intent(LoginActivity.this, NotificationService.class);
                                        stopService(i2);
                                        Intent i3 = new Intent(LoginActivity.this, LocationService.class);
                                        stopService(i3);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                //  loading.dismiss();
                                System.out.println("time out and noConnection...................." + error);
                                int duration = Toast.LENGTH_SHORT;
                                Toast.makeText(LoginActivity.this, "No internet connection", duration).show();
                            }
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> param  = new HashMap<String, String>();
                            param.put("versionName",version);
                            return param;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                    requestQueue.add(stringRequest);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.txtForgotPassword:
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    final String version = pInfo.versionName;
                    int verCode = pInfo.versionCode;
                  //  System.out.println(version+" == Application version name & "+ verCode+"  Code" );
                    StringRequest stringRequest  = new StringRequest(Request.Method.POST, AllURL.checkApplicationVersion,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println("Response for the Check verison"+response);
                                    if(version.equals(response.toString()))
                                    {
                                        forgotPassword();
                                    }
                                    else
                                    {
                                        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                                        //setting dialog title
                                        alertDialog.setTitle("New update available");
                                        //setting dialog message
                                        alertDialog.setCancelable(false);
                                        alertDialog.setIcon(R.drawable.update_version);
                                        alertDialog.setMessage("Please, update the app to new version from play store");
                                        alertDialog.setPositiveButton("update", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            }
                                        });
                                        alertDialog.show();

                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                //  loading.dismiss();
                                System.out.println("time out and noConnection...................." + error);
                                int duration = Toast.LENGTH_SHORT;
                                Toast.makeText(LoginActivity.this, "No internet connection", duration).show();
                            }
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> param  = new HashMap<String, String>();
                            param.put("versionName",version);
                            return param;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                    requestQueue.add(stringRequest);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.chkRememberMe:
                checkRememberMe();
                break;
        }
    }
    //
    private void checkRememberMe() {
        boolean isChecked = checkBoxRememberMe.isChecked();

        String rmUserName,rmPassword;
        rmUserName = editTextUserName.getText().toString();
        rmPassword = editTextPassword.getText().toString();

        if(isChecked)
        {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("rmUserName", rmUserName);
            loginPrefsEditor.putString("rmPassword", rmPassword);
            loginPrefsEditor.commit();
        }
        else
        {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
    }

    /* Forgot Password functionality start */
    private void forgotPassword() {
        LayoutInflater inflater = getLayoutInflater();
        View forgotPasswordView = inflater.inflate(R.layout.forgot_password_dailog,null);
        final EditText txtRegisterEmailID = (EditText) forgotPasswordView.findViewById(R.id.txtRegisterEmail);
        final EditText txtUserName =    (EditText)forgotPasswordView.findViewById(R.id.txtUserName);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(forgotPasswordView);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LoginActivity.this, "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Toast.makeText(LoginActivity.this, "Send", Toast.LENGTH_SHORT).show();
                String registerUserName = txtUserName.getText().toString().trim();
                String registerEmail = txtRegisterEmailID.getText().toString().trim();
                checkValidateUser(registerUserName,registerEmail);
            }
        });
        alert.show();
    }
    // check login credentials for forget password
    private void checkValidateUser(final String registerUserName, final String registerEmail) {


        if(registerUserName==null || registerEmail==null)
        {
            Toast.makeText(this, "Enter username or registered email id", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Toast.makeText(this, "Enter username or register email id"+registerUserName+"Password"+registerEmail, Toast.LENGTH_SHORT).show();
            StringRequest stringRequestCheckEmailIDRegister = new StringRequest(Request.Method.POST, AllURL.checkEmailIdIsRegisterOrNot,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            loading.dismiss();
                            System.out.println(response);
                            try {
                                jsonObject = new JSONObject(response);
                                jsonArray = jsonObject.getJSONArray("result");
                                for(int i = 0; i<jsonArray.length();i++)
                                {
                                    jsonObject1 = jsonArray.getJSONObject(i);
                                    if(jsonObject1.getString("status").equals("SUCCESS"))
                                    {
                                        sendEmail(jsonObject1.getString("FirstName").trim(),registerEmail,registerUserName);
                                    }
                                    else if(jsonObject1.getString("status").equals("FAIL"))
                                    {
                                        Toast.makeText(LoginActivity.this, R.string.Forgot_Password_Check_Fail, Toast.LENGTH_SHORT).show();
                                    }
                                    else if(jsonObject1.getString("status").equals("RIDERONLINE"))
                                    {
                                        Toast.makeText(LoginActivity.this, R.string.Already_logged_in, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   // loading.dismiss();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> param = new HashMap<String, String>();
                    param.put("userName",registerUserName);
                    param.put("email",registerEmail);
                    return param;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequestCheckEmailIDRegister);
        }

    }


    // send email to the rider
    private void sendEmail(String firstName, String registerEmail, String registerUserName) {
        int digits = 8;
        int passwordInt = nDigitRandomNo(digits);
        String password = "<b><u>"+passwordInt+"</u></b>";

        String subject = "Request for password reset";
        String message = "Hello "+firstName+"\n\n"+"We have received your password reset request your password is "+ Html.fromHtml(password)+". This is your temporary password. If you ignore this message, your password will not be changed. If you have not requested for password reset let us know at info@logiangle.com\n\nThanks & Regards,\nLogiAngle Team.";

        //Creating SendMail object
        SendMail sm = new SendMail(this, registerEmail, subject, message);
        //Executing sendmail to send email
        sm.execute();
        changePassword(passwordInt,registerUserName);
    }

    private int nDigitRandomNo(int digits){
        int max = (int) Math.pow(10,(digits)) - 1; //for digits =7, max will be 9999999
        int min = (int) Math.pow(10, digits-1); //for digits = 7, min will be 1000000
        int range = max-min; //This is 8999999
        Random r = new Random();
        int x = r.nextInt(range);// This will generate random integers in range 0 - 8999999
        int nDigitRandomNo = x+min; //Our random rumber will be any random number x + min
        return nDigitRandomNo;
    }

    //change password
    private void changePassword(final int passwordInt, final String registerUserName) {
        StringRequest stringRequestUpdatePassword = new StringRequest(Request.Method.POST, AllURL.changeForgotPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.trim().equals("SUCCESS"))
                        {
                            Intent intent = new Intent(LoginActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
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
                param.put("password",String.valueOf(passwordInt));
                param.put("userName",registerUserName);
                param.put("updateDateTime",getUTCTime());
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequestUpdatePassword);
    }

    /* Forgot Password functionality end */


    /*Logged In Validate*/
    private void validateUser() {
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final String android_id = TelephonyMgr.getDeviceId();


        loading = ProgressDialog.show(LoginActivity.this, "Please Wait", "Checking Credential", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                if (response.equals("Success"))
                {
                    username = editTextUserName.getText().toString().trim();
                    loading.dismiss();
                    createLoginSession();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(LoginActivity.this, new Intent(getApplicationContext(), LocationService.class));
                        ContextCompat.startForegroundService(LoginActivity.this, new Intent(getApplicationContext(), NotificationService.class));
                        ContextCompat.startForegroundService(LoginActivity.this, new Intent(getApplicationContext(), CheckAllServicesWorking.class));
                    }
                    else {
                        Intent i2 = new Intent(LoginActivity.this, NotificationService.class);
                        startService(i2);
                        Intent i3 = new Intent(LoginActivity.this, LocationService.class);
                        startService(i3);
                        Intent i4 = new Intent(LoginActivity.this, CheckAllServicesWorking.class);
                        startService(i4);
                    }

                }
                else if(response.equals("PasswordChange"))
                {
                    loading.dismiss();
                  //  Toast.makeText(LoginActivity.this,"Password Change Activity",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,PasswordChange.class);
                    intent.putExtra("userName",editTextUserName.getText().toString().trim());
                    startActivity(intent);

                }
                else if(response.equals("NotActive"))
                {
                    loading.dismiss();
                    Toast.makeText(LoginActivity.this,"Rider not yet working", LENGTH_LONG).show();
                }
                else if(response.equals("AllReadyLogin"))
                {
                    loading.dismiss();
                    Toast.makeText(LoginActivity.this,"Already logged in on other device",Toast.LENGTH_SHORT).show();
                }
                else if(response.equals("Failure"))
                {
                    loading.dismiss();
                    Toast.makeText(LoginActivity.this,"Please enter valid credential", LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    loading.dismiss();
                    System.out.println("time out and noConnection...................." + error);
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(LoginActivity.this, "No internet connection", duration).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    loading.dismiss();
                    System.out.println("AuthFailureError.........................." + error);
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(LoginActivity.this, "Something wrong", duration).show();
                } else if (error instanceof ServerError) {
                    System.out.println("Server Error" + error);
                    loading.dismiss();
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(LoginActivity.this, "Something wrong", duration).show();
                    //TODO
                } else if (error instanceof NetworkError) {
                    loading.dismiss();
                    System.out.println("NetworkError........................." + error);
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(LoginActivity.this, "Something wrong", duration).show();
                    //TODO
                } else if (error instanceof ParseError) {
                    loading.dismiss();
                    System.out.println("parseError............................." + error);
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(LoginActivity.this, "Something wrong", duration).show();
                    //TODO
                }
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //String username = editTextUserName.getText().toString().trim();
                params.put("username", editTextUserName.getText().toString().trim());
                params.put("password", editTextPassword.getText().toString().trim());
                params.put("loginTime",getUTCTime());
                params.put("mobileDevice",android_id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == AllConstant.MY_CAMERA_REQUEST_CODE && requestCode == AllConstant.MY_READY_PHONE_STATE &&requestCode == AllConstant.MY_LOCATION_REQUEST_CODE && requestCode == AllConstant.MY_CALL_PHONE ) {

            switch (requestCode) {
                case AllConstant.MY_CAMERA_REQUEST_CODE:

                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 10000);

                    } else {
                        finish();
                    }
                    return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }


    //get UTC date time
    private String getUTCTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        df.setTimeZone(utc);
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }
}