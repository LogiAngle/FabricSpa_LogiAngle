package in.togethersolutions.logiangle.adapters;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.modals.OrderMapDAO;
import in.togethersolutions.logiangle.services.GPSTracker;

public class MapAdapter extends Fragment {
    int page_Position;
    OrderMapDAO orderMapDAO;
    TextView tvOrderNumber, tvOrderStatus, tvCustomerName, tvScheduleDateTime, tvSpeed, tvDistanceInKm;
    GPSTracker gps;
    Button btnStart;
    String lat,lng;
    double currentLatitude,currentLongitude;
    double destinationLatitude,destinationLongitude;
    float distance;
    @SuppressLint("ValidFragment")
    public MapAdapter(int position, OrderMapDAO orderMapDAO) {
        this.page_Position = position;
        this.orderMapDAO = orderMapDAO;

    }
    public MapAdapter() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.deal_card_layout, container, false);
        gps = new GPSTracker(getContext());

        try
        {
            tvOrderNumber = (TextView)rootView.findViewById(R.id.tv_order_number);
            tvOrderStatus = (TextView)rootView.findViewById(R.id.tv_order_status);
            tvCustomerName = (TextView)rootView.findViewById(R.id.tv_customer_name);
            tvScheduleDateTime = (TextView)rootView.findViewById(R.id.tv_schedule_datetime);
            tvSpeed = (TextView)rootView.findViewById(R.id.tv_speed);
            tvDistanceInKm = (TextView)rootView.findViewById(R.id.tv_distanceinkm);
            tvOrderNumber.setText(orderMapDAO.getOrderNumber());
            tvOrderStatus.setText(orderMapDAO.getOrderStatus());
            tvCustomerName.setText(orderMapDAO.getCustomerName());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.println("Default Time zone "+ TimeZone.getDefault());
            String dateStr = orderMapDAO.getOrderScheduleDateTime();
            String formattedDate=null;
            Date date = null;
            try {
                date = df.parse(dateStr);
                df.setTimeZone(TimeZone.getDefault());
                formattedDate = df.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvScheduleDateTime.setText(formattedDate);
            lat = orderMapDAO.getLatitude();
            lng = orderMapDAO.getLongitude();
            destinationLatitude = Double.parseDouble(lat);
            destinationLongitude = Double.parseDouble(lng);

            currentLatitude = gps.getLatitude();
            currentLongitude = gps.getLongitude();

            Location locationA = new Location("A");
            locationA.setLatitude(currentLatitude);
            locationA.setLongitude(currentLongitude);

            Location locationB = new Location("B");
            locationB.setLatitude(destinationLatitude);
            locationB.setLongitude(destinationLongitude);

            distance = locationA.distanceTo(locationB)/1000;

            DecimalFormat decimalFormat = new DecimalFormat("000.00");
            String dist = decimalFormat.format(distance);
            //distance = Float.valueOf();

            double d = distance(currentLatitude,currentLongitude,destinationLatitude,destinationLongitude);
            System.out.println("Disat"+d);


            tvDistanceInKm.setText(dist);

            int speedIs1KmMinute =30;
            float estimatedDriveTimeInMinutes = distance / speedIs1KmMinute;

            String eta = String.valueOf(estimatedDriveTimeInMinutes);

            double finalBuildTime = Double.parseDouble(eta);
            int hours = (int) finalBuildTime;
            int minutes = (int) (finalBuildTime * 60) % 60;
            int seconds = (int) (finalBuildTime * (60*60)) % 60;

            DecimalFormat df1 = new DecimalFormat("00");
            String hr = df1.format(hours);
            String sec = df1.format(seconds);
            String min = df1.format(minutes);

            System.out.println(String.format("%s(h) %s(m) %s(s)", hours, minutes, seconds));
            tvSpeed.setText(hr+":"+min+":"+sec);
            return rootView;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("PlacesMapFragment:" + e.getStackTrace());
        }

    }
    public double distance(double lat1, double lon1,
                           double lat2, double lon2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return dist;
    }
    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    public double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    private static double distPerLng(double lat){
        return 0.0003121092* Math.pow(lat, 4)
                +0.0101182384* Math.pow(lat, 3)
                -17.2385140059*lat*lat
                +5.5485277537*lat+111301.967182595;
    }

    private static double distPerLat(double lat){
        return -0.000000487305676* Math.pow(lat, 4)
                -0.0033668574* Math.pow(lat, 3)
                +0.4601181791*lat*lat
                -1.4558127346*lat+110579.25662316;
    }
}
