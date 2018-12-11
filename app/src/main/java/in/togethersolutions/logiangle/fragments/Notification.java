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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.adapters.NotificationAdapter;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.javaClass.logoutInterface;
import in.togethersolutions.logiangle.modals.NotificationListItem;
import in.togethersolutions.logiangle.session.SessionManagement;

public class Notification extends Fragment {

    String userName = null;
    private String date;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<NotificationListItem> listItems;
    boolean isLoggedIn = true;
    ProgressBar progressBar;
    logoutInterface logoutInterface;


    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    LinearLayout linearLayout,linearLayoutEmptyCart;

    public Notification() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_notification,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewNotification);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        linearLayout = (LinearLayout)view.findViewById(R.id.linearLayoutNotification);
        linearLayoutEmptyCart = (LinearLayout)view.findViewById(R.id.linearLayoutEmptyCard);
        recyclerView.setNestedScrollingEnabled(false);
        userName= SessionManagement.getLoggedInUserName(getContext());
        listItems = new ArrayList<>();
        progressBar = (ProgressBar)view.findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();


        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if(checkRiderLogin(userName)){
            loadData();
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            //setting dialog title
            alertDialog.setTitle("Logout");
            //setting dialog message
            alertDialog.setCancelable(false);
            alertDialog.setIcon(R.drawable.logout);

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
            alertDialog.show();
        }

        return view ;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Notification");
    }


    private void loadData()
    {

        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("Notification Response "+response);
                            progressBar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject o = jsonArray.getJSONObject(i);
                                NotificationListItem notificationListItem = new NotificationListItem(
                                        o.getString("NotificationTitle"),
                                        o.getString("NotificationMessage"),
                                        o.getInt("NotificationIsSeen"),
                                        o.getString("NotificationDateTime")
                                );
                                listItems.add(notificationListItem);
                            }
                            adapter = new NotificationAdapter(listItems,getContext());
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            /*if(jsonObject.getString("result").equals("SUCCESS")){
                                JSONArray jsonArray = jsonObject.getJSONArray("array");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject o = jsonArray.getJSONObject(i);
                                    NotificationListItem notificationListItem = new NotificationListItem(
                                            o.getString("NotificationTitle"),
                                            o.getString("NotificationMessage"),
                                            o.getInt("NotificationIsSeen")
                                    );
                                    listItems.add(notificationListItem);
                                }
                                adapter = new NotificationAdapter(listItems,getContext());
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                linearLayoutEmptyCart.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                            else if(jsonObject.getString("result").equals("FAIL"))
                            {
                                linearLayoutEmptyCart.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility(View.GONE);
                            }*/



                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    int duration = Toast.LENGTH_SHORT;
                    /*snackbar = Snackbar.make(linearLayout, "No internet connection", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();*/
                }
            }
        }){
            protected Map<String,String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("updateCode", String.valueOf(AllConstant.notificationRecord));
                params.put("userName",userName);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
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
