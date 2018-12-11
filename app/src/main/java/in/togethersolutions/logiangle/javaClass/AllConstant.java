package in.togethersolutions.logiangle.javaClass;

import android.content.Context;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AllConstant {

    /*Constant For Session Management*/
    //Keys for Sharedpreferences
    public static final String KEY_USERNAME= "username";
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";
    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
    public static final String KEY_LOGIN_ID = "loginid";
    public static final String KEY_RIDER_NAME= "riderName";
    public static final String KEY_RIDER_EMAIL= "riderEmail";
    public static final String KEY_FCM_TOKEN_ID = "FCMtoken";

    public static final int MY_CAMERA_REQUEST_CODE = 100;

    public static final int MY_LOCATION_REQUEST_CODE = 100;
    public static final int MY_READY_PHONE_STATE = 100;
    public static final int MY_CALL_PHONE = 100;
    /*Constant For GPS Tracking*/
    // The minimum distance to change Updates in meters
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;//10 meter
    // The minimum time between updates in milliseconds
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    public static final long TIME_INTERVEL = 20000;
    private static boolean[] isLoggedIn = {false};
    private final int TIME_INTERVAL = 20000;

    /*---------URL Constant----------*/
    public static final int passwordChange = 1;
    public static final int loginSession = 2;
    public static final int sendLatLong = 3;
    public static final int logout = 4;
    public static final int orderStatus = 5;
    public static final int completeOrder =6;
    public static final int completeOrderCash =7;
    public static final int completeOrderImage =8;
    public static final int riderStartMessage = 9;
    public static final int riderCompleteMessage = 10;
    public static final int riderInfo = 11;
    public static final int orderhistoryInfo = 12;
    public static final int checkOrderCompleteOrNot = 13;
    public static final int notificationRecord = 17;
    public static final int reschduleFetchRecord = 18;
    public static final int orderCount = 19;
    public static final int prodUpdate = 20;
    public static final int checkReschdule = 21;
    public static final String emailID = "info@logiangle.com";
    public static final String fcmServerID = "AAAAaxgZ5eg:APA91bE20lmbBgpbw1dKhFYfFB9nbSiCIiPdpji236XgTM0C_KBra2PJn1OBdatJh4hCI8H9cIt3ibf7eKhPMUDzAL0WQ3gOq08FT_qSgbIZJ9No2CT-Ei7xxetQzPK7vXRCL78pO3Ku";

    // convert the date format yyyy-MM-dd HH:mm:ss to dd-MM-yyyy HH:mm:ss
    public static String convertDateFormate(String datetime)
    {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy" );
        Date date;
        String changeDateFormatted=null;
        try {
            date = originalFormat.parse(datetime);
            System.out.println("Old Format :   " + originalFormat.format(date));
            System.out.println("New Format :   " + targetFormat.format(date));
            changeDateFormatted = targetFormat.format(date);
        } catch (ParseException ex) {
            // Handle Exception.
        }
        return changeDateFormatted;
    }

    public static boolean checkRiderLogin(Context context, final String userName) {


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
                                    isLoggedIn[0] =false;
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
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        return isLoggedIn[0];

    }

}
