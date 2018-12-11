package in.togethersolutions.logiangle.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.adapters.BreachedAdapter;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.javaClass.logoutInterface;
import in.togethersolutions.logiangle.modals.BreachedListItem;
import in.togethersolutions.logiangle.session.SessionManagement;

public class BreachedOrder extends Fragment {

    public BreachedOrder() {
        // Required empty public constructor
    }

    ProgressBar progressBar;

    public String userName;
    logoutInterface logoutInterface;
    private String date;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    boolean isLoggedIn[] = {true};

    /* TextView declaration */
    private TextView textViewName;
    private TextView textViewUser;



    /*Recycler View*/
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<BreachedListItem> listItems;



    LinearLayout linearLayout;
    LinearLayout linearLayoutBreachedEmptyCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view  = inflater.inflate(R.layout.fragment_breached_order,container,false);
//        coordinatorLayout = (CoordinatorLayout)view.findViewById(R.id.error);
        userName = SessionManagement.getLoggedInUserName(getActivity());
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewBreachedOrder);
        linearLayoutBreachedEmptyCard = (LinearLayout)view.findViewById(R.id.linearLayoutBreachedEmptyCard);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        linearLayout = (LinearLayout)view.findViewById(R.id.linearLayoutBreachedOrder);
        listItems = new ArrayList<>();
        progressBar = (ProgressBar)view.findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        System.out.println("Rider checkout time"+checkRiderLogin(userName));
       // loadData();
        if(checkRiderLogin(userName)){
        loadData();}

        else
        {
           // Toast.makeText(getActivity(), "Get Text", Toast.LENGTH_SHORT).show();
           /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            //setting dialog title
            alertDialog.setTitle("Logout");
            //setting dialog message
            alertDialog.setIcon(R.drawable.logout);
            alertDialog.setCancelable(false);

            alertDialog.setMessage("You have been logged out\n" +
                    "Please try again or contact Administrator ");

            // On pressing Settings button
            alertDialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SessionManagement.clearLoggedInEmailAddress(getActivity());
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    getActivity().startActivity(intent);
                }
            });
            alertDialog.show();*/
        }
        return view ;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Breached Orders");
    }

    private void loadData() {

        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.breachedData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = null;
                        JSONArray jsonArray = null;
                        try {
                            jsonObject = new JSONObject(response);
                            jsonArray = jsonObject.getJSONArray("result");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject o = jsonArray.getJSONObject(i);
                                BreachedListItem item = new BreachedListItem(
                                        o.getString("OrderNumber"),
                                        o.getString("Status"),
                                        o.getString("StatusName"),
                                        o.getString("Type"),
                                        o.getString("TypeName"),
                                        o.getString("TotalAmount"),
                                        o.getString("CRMID"),
                                        o.getString("FirstName"),
                                        o.getString("MobileNumber"),
                                        o.getString("Address"),
                                        o.getString("Latitude"),
                                        o.getString("Longitude"),
                                        o.getString("DateTime"),
                                        o.getInt("PD"),
                                        o.getString("PickUpFirstName"),
                                        o.getString("PickUpAddress"),
                                        o.getString("PickUpMobileNumber"),
                                        o.getString("PickUpLatitude"),
                                        o.getString("PickUpLongitude"),
                                        o.getString("DeliveryFirstName"),
                                        o.getString("DeliveryAddress"),
                                        o.getString("DeliveryMobileNumber"),
                                        o.getString("DeliveryLatitude"),
                                        o.getString("DeliveryLongitude"),
                                        o.getString("PickUpDateTime"),
                                        o.getString("DeliveryDateTime"),
                                        o.getString("Notes"),
                                        o.getString("TimeSlot")
                                );

                                listItems.add(item);
                            }
                            linearLayout.setVisibility(View.VISIBLE);
                            linearLayoutBreachedEmptyCard.setVisibility(View.GONE);
                            adapter = new BreachedAdapter(listItems,getContext());
                            recyclerView.setAdapter(adapter);
                            /*if(jsonObject.getString("result").equals("SUCCESS"))
                            {
                                jsonArray = jsonObject.getJSONArray("array");
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject o = jsonArray.getJSONObject(i);
                                    BreachedListItem item = new BreachedListItem(
                                            o.getString("OrderNumber"),
                                            o.getString("Status"),
                                            o.getString("StatusName"),
                                            o.getString("Type"),
                                            o.getString("TypeName"),
                                            o.getString("TotalAmount"),
                                            o.getString("CRMID"),
                                            o.getString("FirstName"),
                                            o.getString("MobileNumber"),
                                            o.getString("Address"),
                                            o.getString("Latitude"),
                                            o.getString("Longitude"),
                                            o.getString("DateTime"),
                                            o.getInt("PD"),
                                            o.getString("PickUpFirstName"),
                                            o.getString("PickUpAddress"),
                                            o.getString("PickUpMobileNumber"),
                                            o.getString("PickUpLatitude"),
                                            o.getString("PickUpLongitude"),
                                            o.getString("DeliveryFirstName"),
                                            o.getString("DeliveryAddress"),
                                            o.getString("DeliveryMobileNumber"),
                                            o.getString("DeliveryLatitude"),
                                            o.getString("DeliveryLongitude"),
                                            o.getString("PickUpDateTime"),
                                            o.getString("DeliveryDateTime")
                                    );

                                    listItems.add(item);
                                }
                                linearLayout.setVisibility(View.VISIBLE);
                                linearLayoutBreachedEmptyCard.setVisibility(View.GONE);
                                adapter = new BreachedAdapter(listItems,getContext());
                                recyclerView.setAdapter(adapter);
                            }
                            else if(jsonObject.getString("result").equals("FAIL"))
                            {
                               *//* recyclerView.setVisibility(View.INVISIBLE);
                                txtEmpty.setVisibility(View.VISIBLE);*//*
                               linearLayout.setVisibility(View.GONE);
                               linearLayoutBreachedEmptyCard.setVisibility(View.VISIBLE);

                                *//*adapter = new BreachedAdapter(listItems,getContext());
                                recyclerView.setAdapter(adapter);*//*
                            }*/

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                                    /*snackbar = Snackbar.make(coordinatorLayout, "No internet connection", Snackbar.LENGTH_INDEFINITE);

                                    snackbar.show();*/


                        }
                    }
                }
        ){
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                TimeZone utc = TimeZone.getTimeZone("UTC");
                df.setTimeZone(utc);
                final String date = df.format(Calendar.getInstance().getTime());
                ///System.out.println(date);
                params.put("date",date);
                params.put("userName", userName);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);


    }

    public  boolean checkRiderLogin(final String userName) {

        final boolean[] isLogin ={false};
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


                                    isLogin[0] =true;

                                    //System.out.println(isLoggedIn[0]+"sanrosh");
                                }
                                else
                                {
                                    isLogin[0] =false;
//                                    //Toast.makeText(this, "You have been logged out\nPlease try again or contact Administrator", Toast.LENGTH_SHORT).show();
                                    logoutInterface.logoutFromFragment();
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
                param.put("loginSessionID",SessionManagement.getLoggedInSessionID(getActivity()));
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        return isLogin[0];
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try
        {
            logoutInterface = (logoutInterface) getActivity();
        }
        catch (ClassCastException cce)
        {
            cce.printStackTrace();
        }
    }

}
