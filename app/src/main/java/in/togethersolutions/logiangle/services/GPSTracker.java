package in.togethersolutions.logiangle.services;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import java.util.Timer;

@SuppressWarnings("MissingPermission")

public class GPSTracker extends Service implements LocationListener {

    //Timer timer = new Timer();
    private final Context mContext;
    //flag for gps status
    boolean isGPSEnabled = false;
    //flag for Network Enabled
    boolean isNetworkEnabled = false;
    //flag for GetLocation
    public boolean canGetLocation = false;
    //Location object
    Location location;
    //    String batteryStatus =null;
    public String userName = null;

    //latitude and longitude variable declaration
    double latitude;
    String batteryStatus="100";
    double longitude;
    public String longitude1;
    public String latitude1;


    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //10 meter
    //The minimum time between updates i10021003    n milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; //1 minute
    //Declaring LocationMAnager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {

        this.mContext = context;
        getLocation();

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public Location getLocation() {
        try {

            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //getting Network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                //No Network Provider is enable
            } else {
                this.canGetLocation = true;
                //fist get Location from Network Provider
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            //  sendLocationToDB();

                        }
                    }
                }

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getMessage();
        }
        return location;
    }
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        isGPSEnabled = false;
        if(locationManager != null){
            isGPSEnabled = false;
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /*
     * Function get Latitude
     * */
    public double getLatitude()
    {
        if(location != null)
        {
            latitude = location.getLatitude();
        }
        return latitude;
    }
    /*
     * Function get Longitude
     * */
    public double getLongitude()
    {
        if(location != null)
        {
            longitude = location.getLongitude();
        }
        return longitude;
    }
    /*
     * functon to check GPS/wifi enabled
     * @return boolean value*/
    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     * */
    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        //setting dialog title
        alertDialog.setTitle("GPS is settings");
        //setting dialog message
        alertDialog.setMessage("GPS is not enabled. ");

        // On pressing Cancel button
        alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.show();

    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
