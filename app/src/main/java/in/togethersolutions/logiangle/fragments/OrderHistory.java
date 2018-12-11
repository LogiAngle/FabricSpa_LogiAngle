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
import in.togethersolutions.logiangle.adapters.OrderHistoryAdapter;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.javaClass.logoutInterface;
import in.togethersolutions.logiangle.modals.ListItem;
import in.togethersolutions.logiangle.modals.NewListItems;
import in.togethersolutions.logiangle.session.SessionManagement;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHistory extends Fragment{


    public OrderHistory() {
        // Required empty public constructor
    }

    boolean isLoggedIn = true;
    logoutInterface logoutInterface;
    /*RecyclerView Declaration*/
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    ProgressBar progressBar;
    private String date;
    String userName = null;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    public List<ListItem> listItems;
    public List<NewListItems> sortedListItem=new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    LinearLayout linearLayout,linearLayoutEmptyCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_order_history,container,false);
//        coordinatorLayout = (CoordinatorLayout)view.findViewById(R.id.error);
        userName = SessionManagement.getLoggedInUserName(getActivity());
        progressBar = (ProgressBar)view.findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewOrderHistory);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        linearLayout = (LinearLayout)view.findViewById(R.id.linearLayoutOrderHistory);
        listItems = new ArrayList<>();
        recyclerView.setNestedScrollingEnabled(false);
        linearLayoutEmptyCard = (LinearLayout) view.findViewById(R.id.linearLayoutEmptyCard);
       // System.out.println(AllConstant.checkRiderLogin(getContext(),userName)+"Order History");
        if(checkRiderLogin(userName)) {
            loadData();
        }
        else
        {
            /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
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

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.orderHistoryDetail,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try
                        {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            System.out.println("History "+response);
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject o = jsonArray.getJSONObject(i);
                                ListItem item = new ListItem(
                                        o.getString("OrderNumber"),
                                        o.getString("FirstName"),
                                        o.getString("MobileNumber"),
                                        o.getString("Status"),
                                        o.getString("CRMID"),
                                        o.getString("TaskTypeID")
                                );
                                listItems.add(item);
                            }
                            adapter = new OrderHistoryAdapter(listItems,getContext());
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();/*
                            if(jsonObject.getString("result").equals("SUCCESS"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("array");

                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject o = jsonArray.getJSONObject(i);
                                    ListItem item = new ListItem(
                                            o.getString("OrderNumber"),
                                            o.getString("FirstName"),
                                            o.getString("MobileNumber"),
                                            o.getString("Status"),
                                            o.getString("CRMID")
                                    );
                                    listItems.add(item);
                                }
                                adapter = new OrderHistoryAdapter(listItems,getContext());
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                linearLayout.setVisibility(View.VISIBLE);
                                linearLayoutEmptyCard.setVisibility(View.GONE);

                            }
                            else if(jsonObject.getString("result").equals("FAIL"))
                            {
                                linearLayoutEmptyCard.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility(View.GONE);
                            }*/
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      //  progressBar.setVisibility(View.GONE);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            System.out.println("time out and noConnection...................." + error);
                            int duration = Toast.LENGTH_SHORT;
                           /* snackbar = Snackbar.make(coordinatorLayout, "No internet connection", Snackbar.LENGTH_LONG);

                            snackbar.show();*/
                           progressBar.setVisibility(View.GONE);
                        }
                    }
                }
        ){
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", userName);
                params.put("date",getDate());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Order History");
    }


    public String getDate()
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        df.setTimeZone(utc);
        date = df.format(Calendar.getInstance().getTime());
        return date;
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

                                    isLoggedIn =true;
                                }
                                else
                                {
                                    isLoggedIn =false;
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        return isLoggedIn;
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
