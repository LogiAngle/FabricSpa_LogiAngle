package in.togethersolutions.logiangle.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.adapters.MapPagerAdapter;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.modals.OrderMapDAO;
import in.togethersolutions.logiangle.services.GPSTracker;
import in.togethersolutions.logiangle.session.SessionManagement;

public class OrdersOnMap extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Marker customMarker, customMarker1;
    private LatLng markerLatLng;
    String lat;
    String longi;
    double latitudeCurrentPosition,longitudeCurrentPosition,latitudeOrderPostion,longitudeOrderPosition,distance= 0.0;
    double latitude = 0;
    double longitude = 0;
    String k,l;
    ViewPager mViewPager;
    FragmentPagerAdapter adapterViewPager;
    MapPagerAdapter mapPagerAdapter;
    GPSTracker gps;
    String userName;
    View marker;
    TextView numTxt;

    List<LatLng> routeArray;
    LatLngBounds.Builder builder;
    LatLng L1,latLng;
    ArrayList<JSONObject> array;

    JSONArray jsonArray,jsArray;
    JSONObject jsonObject1,jsonObjectPager;

    private List<OrderMapDAO> myDealsList = new ArrayList<OrderMapDAO>();
    private Map<String, OrderMapDAO> mDealMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_mp);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gps = new GPSTracker(this);
        //   coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorlayout);

    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            Toast.makeText(this, "Map not Loaded", Toast.LENGTH_LONG).show();
        } else {
            // setUpMap();
            getOrderMapRecord();
        }
    }

    public void getOrderMapRecord() {
        userName = SessionManagement.getLoggedInUserName(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.riderAllOrderLocation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        setUpMap(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }
        ){
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", userName);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(OrdersOnMap.this);
        requestQueue.add(stringRequest);;
    }

    public void setUpMap(String response) {
        getCurrentPositionOnMap();
        try {
            latitudeCurrentPosition = gps.getLatitude();
            longitudeCurrentPosition = gps.getLongitude();
            JSONObject jsonObject = new JSONObject(response);
            jsonArray = jsonObject.getJSONArray("result");
            for(int i = 0; i<jsonArray.length();i++)
            {
                //get json array to object
                jsonObject1 = jsonArray.getJSONObject(i);
                latitudeOrderPostion =jsonObject1.getDouble("Latitude");
                longitudeOrderPosition = jsonObject1.getDouble("Longitude");
                //Distance calculation
                distance = getDistance(latitudeCurrentPosition,longitudeCurrentPosition,latitudeOrderPostion,longitudeOrderPosition);
                jsonObject1.put("Distance",distance);
            }

            JSONArray jsonArray1 = jsonObject.getJSONArray("result");
            array = new ArrayList<JSONObject>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    array.add(jsonArray1.getJSONObject(i));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Collections.sort(array, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {
                    // TODO Auto-generated method stub
                    try {
                        return (lhs.getString("Distance").compareTo(rhs.getString("Distance")));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return 0;
                    }

                }
            });

            jsArray = new JSONArray(array);
            routeArray = new ArrayList<LatLng>();
            builder = new LatLngBounds.Builder();
            L1 = new LatLng(gps.getLatitude(),gps.getLongitude());
            builder.include(L1);
            routeArray.add(L1);
            for(int i=0;i<jsArray.length();i++)
            {
                int j = i + 1;
                k = Integer.toString(j);
                final JSONObject c = jsArray.getJSONObject(i);
                OrderMapDAO orderMapDAOItem = new OrderMapDAO(
                        c.getString("OrderID"),
                        c.getString("FullName"),
                        c.getString("Latitude"),
                        c.getString("Longitude"),
                        c.getString("OrderStatus"),
                        c.getString("OrderScheduleDate")
                );

                myDealsList.add(orderMapDAOItem);
                latitude = c.getDouble("Latitude");
                longitude = c.getDouble("Longitude");
                lat = c.getString("Latitude");
                longi = c.getString("Longitude");

                mDealMap.put(myDealsList.get(i).getLatitude(), myDealsList.get(i));

                //set position on map
                markerLatLng = new LatLng(latitude, longitude);

                /*create marker view*/
                marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.custom_map_view, null);
                TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);

                //add text on marker
                numTxt.setText(k);
                if(i==0) {
                    //generate custom marker on map
                    customMarker = mMap.addMarker(new MarkerOptions()
                            .position(markerLatLng)
                            .title(c.getString("FullName"))
                            .snippet(c.getString("OrderID"))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

                    );
                }
                else
                {
                    customMarker1 = mMap.addMarker(new MarkerOptions()
                            .position(markerLatLng)
                            .title(c.getString("FullName"))
                            .snippet(c.getString("OrderID"))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }

                /*Create polyline on map*/
                latLng = new LatLng(Double.parseDouble(lat.trim()), Double.parseDouble(longi.trim()));
                builder.include(latLng);
                if (!routeArray.contains(latLng)) {
                    routeArray.add(latLng);
                }
                drawLine(routeArray);
            }
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
            mViewPager = (ViewPager) findViewById(R.id.vp_details);
            mViewPager.setPadding(16, 0, 16, 0);
            mViewPager.setClipToPadding(false);
            mViewPager.setPageMargin(8);
            adapterViewPager = new MapPagerAdapter(myDealsList,getSupportFragmentManager(),OrdersOnMap.this);
            mViewPager.setAdapter(adapterViewPager);

            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public int tabPosition;
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }
                @Override
                public void onPageSelected(final int position) {
                    this.tabPosition = position;

                    markerAnimate(tabPosition,jsArray);
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
//            mapPagerAdapter.notifyDataSetChanged();
            customMarker1.remove();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawLine(List<LatLng> routeArray) {
        if (routeArray == null) {
            Log.e("Draw Line", "got null as parameters");
            return;
        }
        Polyline line = mMap.addPolyline(new PolylineOptions().width(5).color(Color.BLACK));
        line.setPoints(routeArray);
    }

    /*public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }*/

    private void getCurrentPositionOnMap() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
            }
        } else {

            mMap.setMyLocationEnabled(true);
        }
    }

    private double getDistance(double startLat,double startLon,double endLat, double endLon) {
        double distance=0.0;
        //Source Location
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(startLat);
        startPoint.setLongitude(startLon);

        //Destination Location
        Location endPoint=new Location("locationB");
        endPoint.setLatitude(endLat);
        endPoint.setLongitude(endLon);

        //Calculation distance
        distance=startPoint.distanceTo(endPoint);
        distance=distance*0.001;

        return distance;
    }

    private void markerAnimate(int tabPosition, JSONArray jsArray) {
        mMap.clear();
        for(int i = 0; i< jsArray.length(); i++)
        {
            try {
                jsonObjectPager = jsArray.getJSONObject(i);
                if(i==tabPosition) {
                    customMarker1 = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(jsonObjectPager.getDouble("Latitude"),jsonObjectPager.getDouble("Longitude")))
                            .title(jsonObjectPager.getString("FullName"))
                            .snippet(jsonObjectPager.getString("OrderID"))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(jsonObjectPager.getDouble("Latitude"),jsonObjectPager.getDouble("Longitude"))));


                }
                else{

                    customMarker1 = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(jsonObjectPager.getDouble("Latitude"),jsonObjectPager.getDouble("Longitude")))
                            .title(jsonObjectPager.getString("FullName"))
                            .snippet(jsonObjectPager.getString("OrderID"))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(jsonObjectPager.getDouble("Latitude"),jsonObjectPager.getDouble("Longitude"))));

                }
                latLng = new LatLng(Double.parseDouble(lat.trim()), Double.parseDouble(longi.trim()));
                builder.include(latLng);
                if (!routeArray.contains(latLng)) {
                    routeArray.add(latLng);
                }
                drawLine(routeArray);
                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMapIfNeeded();
    }

    @Override
    public void onLocationChanged(Location location) {
        markerLatLng = new LatLng(location.getLongitude(),location.getLatitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}


