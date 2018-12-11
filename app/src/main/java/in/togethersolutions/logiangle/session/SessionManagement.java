package in.togethersolutions.logiangle.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import in.togethersolutions.logiangle.javaClass.AllConstant;

public class SessionManagement {


    public static SharedPreferences getSharedPreferences(Context ctx)
    {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLoggedInUserName(Context ctx, String userName)
    {   //System.out.println("Session username"+userName);
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(AllConstant.KEY_USERNAME, userName);
        editor.commit();
        editor.apply();
    }

    public static String getLoggedInUserName(Context ctx)
    {

        return getSharedPreferences(ctx).getString(AllConstant.KEY_USERNAME, "");
    }

    public static void setLoggedInSessionID(Context ctx,String logInID)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(AllConstant.KEY_LOGIN_ID,logInID);
        editor.apply();
        editor.commit();
    }
    public static String getLoggedInSessionID(Context ctx)
    {
        return getSharedPreferences(ctx).getString(AllConstant.KEY_LOGIN_ID,"");
    }
    public static void setFCMToken(Context ctx,String FCMTokenID)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(AllConstant.KEY_FCM_TOKEN_ID,FCMTokenID);
        editor.apply();
        editor.commit();
    }
    public static String getFCMToken(Context ctx)
    {
        return getSharedPreferences(ctx).getString(AllConstant.KEY_FCM_TOKEN_ID,"");
    }

    public static void setUserLoggedInStatus(Context ctx, boolean status)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(AllConstant.LOGGEDIN_SHARED_PREF, status);
        editor.commit();
    }

    public static boolean getUserLoggedInStatus(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(AllConstant.LOGGEDIN_SHARED_PREF, false);
    }

    public static void clearLoggedInEmailAddress(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(AllConstant.KEY_USERNAME);
        editor.remove(AllConstant.KEY_LOGIN_ID);
        editor.remove(AllConstant.LOGGEDIN_SHARED_PREF);
        editor.remove(AllConstant.KEY_FCM_TOKEN_ID);
        editor.remove(AllConstant.KEY_RIDER_NAME);
        editor.remove(AllConstant.KEY_RIDER_EMAIL);
        editor.commit();
        editor.apply();
    }

    public static void setRiderName(Context context,String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(AllConstant.KEY_RIDER_NAME,userName);
        editor.apply();
        editor.commit();
    }
    public static String getRiderName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(AllConstant.KEY_RIDER_NAME, "");
    }


    public static void setRiderEmail(Context context,String email)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(AllConstant.KEY_RIDER_EMAIL,email);
        editor.apply();
        editor.commit();
    }

    public static String getRiderEmail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(AllConstant.KEY_RIDER_EMAIL, "");
    }





}
