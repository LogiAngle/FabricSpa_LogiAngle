package in.togethersolutions.logiangle.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.activities.MainActivity;
import in.togethersolutions.logiangle.adapters.DashboardAdapter;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.javaClass.logoutInterface;
import in.togethersolutions.logiangle.modals.NewListItems;
import in.togethersolutions.logiangle.services.GPSTracker;
import in.togethersolutions.logiangle.services.LocationService;
import in.togethersolutions.logiangle.services.NotificationService;
import in.togethersolutions.logiangle.session.SessionManagement;

public class OrderManagement extends Fragment{

    /*RecyclerView Declaration*/
    private RecyclerView recyclerView = null;
    //private RecyclerView.Adapter adapter;
    DashboardAdapter adapter;
    //private DashboardAdapter adapter;
    public String userName = null;
    boolean[] status = {true};
    String dateTime;
    public List<NewListItems> listItems= new ArrayList<>();;
    public List<NewListItems> sortedListItem=new ArrayList<>();
    LinearLayout linearLayout,linearLayoutEmptyCart;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String SessionImage1 = "image1";
    public static final String SessionImage2 = "image2";

    boolean isLoggedIn=true;

    ProgressBar progressBar;

    Context mContext;
    public String selectedDate;

    logoutInterface logoutInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_order_management,container,false);

        mContext = getActivity().getApplicationContext();
        userName = SessionManagement.getLoggedInUserName(getActivity());
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewOrderManagement);
        linearLayout = (LinearLayout)view.findViewById(R.id.linearLayoutOrderManagement);
        progressBar = (ProgressBar)view.findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
        linearLayoutEmptyCart = (LinearLayout)view.findViewById(R.id.linearLayoutEmptyCard);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            selectedDate= bundle.getString("edttext", "");
           // System.out.println("Main Function rider check"+ checkRiderLogin(userName));
            if(checkRiderLogin(userName)) {
                loadData(mContext, selectedDate);
            }
            else
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
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
                alertDialog.show();
            }
        }
        else
        {
            Toast.makeText(mContext, "Somthing went wrong", Toast.LENGTH_SHORT).show();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SessionImage1);
        editor.remove(SessionImage2);
        editor.commit();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Order Management");
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewOrderManagement);
        linearLayout = (LinearLayout)view.findViewById(R.id.linearLayoutOrderManagement);
    }

    public void loadData(final Context context, final String selectedDate) {
        progressBar.setVisibility(View.VISIBLE);
        listItems.clear();

        userName = SessionManagement.getLoggedInUserName(context);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.orderDetail, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                    //    System.out.println("response==="+response);
                        try
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            JSONObject jsonObject = new JSONObject(response);
                            sortedListItem.clear();
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            JSONObject jsonObject1 = null;
                            //System.out.println("Response "+response);
                                /*Order wise Sorting*/
                            for(int i=0;i<jsonArray.length();i++)
                            {

                                jsonObject1=jsonArray.getJSONObject(i);
                                //System.out.println("Response "+ jsonObject1.getString("Notes"));
                                NewListItems item = new NewListItems(
                                        jsonObject1.getString("OrderNumber"),
                                        jsonObject1.getString("Status"),
                                        jsonObject1.getString("StatusName"),
                                        jsonObject1.getString("Type"),
                                        jsonObject1.getString("TypeName"),
                                        jsonObject1.getString("TotalAmount"),
                                        jsonObject1.getString("CRMID"),
                                        jsonObject1.getString("FirstName"),
                                        jsonObject1.getString("MobileNumber"),
                                        jsonObject1.getString("Address"),
                                        jsonObject1.getString("Latitude"),
                                        jsonObject1.getString("Longitude"),
                                        jsonObject1.getString("DateTime"),
                                        jsonObject1.getInt("PD"),
                                        jsonObject1.getString("PickUpFirstName"),
                                        jsonObject1.getString("PickUpAddress"),
                                        jsonObject1.getString("PickUpMobileNumber"),
                                        jsonObject1.getString("PickUpLatitude"),
                                        jsonObject1.getString("PickUpLongitude"),
                                        jsonObject1.getString("DeliveryFirstName"),
                                        jsonObject1.getString("DeliveryAddress"),
                                        jsonObject1.getString("DeliveryMobileNumber"),
                                        jsonObject1.getString("DeliveryLatitude"),
                                        jsonObject1.getString("DeliveryLongitude"),
                                        jsonObject1.getString("PickUpDateTime"),
                                        jsonObject1.getString("DeliveryDateTime"),
                                        jsonObject1.getString("RAmount"),
                                        jsonObject1.getString("OrderCompletion"),
                                        jsonObject1.getString("Notes"),
                                        jsonObject1.getString("TimeSlot")
                                );

                                listItems.add(item);

                            }

                            for(int k=0;k<listItems.size();k++)
                            {

                                int type = listItems.get(k).getType();
                          //      System.out.println("Type = "+listItems);
                                if (type == 0) {
                                    dateTime = listItems.get(k).getpDateTime();
                                } else if(type == 1) {
                                    dateTime = listItems.get(k).getPickUpDateTIme();
                                }
                                String date = getDate1(dateTime);
                                // System.out.println(date+" "+selectedDate);

                                if(date.equals(selectedDate))
                                {
                                //    System.out.println("Response Notes"+listItems.get(k).getNotes());
                                    NewListItems sortedItem = new NewListItems(
                                            listItems.get(k).getOrderNumber(),
                                            listItems.get(k).getOrderStatusID(),
                                            listItems.get(k).getOrderStatusName(),
                                            listItems.get(k).getOrderTaskTypeID(),
                                            listItems.get(k).getOrderTaskTypeName(),
                                            listItems.get(k).getTotalAmount(),
                                            listItems.get(k).getCrmID(),
                                            listItems.get(k).getFirstName(),
                                            listItems.get(k).getMobileNumber(),
                                            listItems.get(k).getAddress(),
                                            listItems.get(k).getLatitude(),
                                            listItems.get(k).getLongitude(),
                                            listItems.get(k).getpDateTime(),
                                            listItems.get(k).getType(),
                                            listItems.get(k).getPickUpClientName(),
                                            listItems.get(k).getPickUpClientAddress(),
                                            listItems.get(k).getPickUpClientMobileNumber(),
                                            listItems.get(k).getPickUpLatitude(),
                                            listItems.get(k).getPickUpLongitude(),
                                            listItems.get(k).getDeliveryClientName(),
                                            listItems.get(k).getDeliveryClientAddress(),
                                            listItems.get(k).getDeliveryClientMobileNumber(),
                                            listItems.get(k).getDeliveryLatitude(),
                                            listItems.get(k).getDeliveryLongitude(),
                                            listItems.get(k).getPickUpDateTIme(),
                                            listItems.get(k).getDeliveryDateTime(),
                                            listItems.get(k).getrAmount(),
                                            listItems.get(k).getCompletionCount(),
                                            listItems.get(k).getNotes(),
                                            listItems.get(k).getTimeSlot()
                                    );
                                    sortedListItem.add(sortedItem);

                                }


                            }

                            //  System.out.println("Sorted list size"+sortedListItem.size());
                            if(sortedListItem.size()!=0)
                            {
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                adapter = new DashboardAdapter(sortedListItem,context);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                // for the left swiping of left side to cancel the order
                                //initSwipe();
                                linearLayoutEmptyCart.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                            /*if(jsonObject.getString("result").equals("SUCCESS"))
                            {
                                sortedListItem.clear();
                                JSONArray jsonArray = jsonObject.getJSONArray("array");
                                JSONObject jsonObject1 = null;

                                *//*Order wise Sorting*//*
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    jsonObject1=jsonArray.getJSONObject(i);
                                    NewListItems item = new NewListItems(
                                            jsonObject1.getString("OrderNumber"),
                                            jsonObject1.getString("Status"),
                                            jsonObject1.getString("StatusName"),
                                            jsonObject1.getString("Type"),
                                            jsonObject1.getString("TypeName"),
                                            jsonObject1.getString("TotalAmount"),
                                            jsonObject1.getString("CRMID"),
                                            jsonObject1.getString("FirstName"),
                                            jsonObject1.getString("MobileNumber"),
                                            jsonObject1.getString("Address"),
                                            jsonObject1.getString("Latitude"),
                                            jsonObject1.getString("Longitude"),
                                            jsonObject1.getString("DateTime"),
                                            jsonObject1.getInt("PD"),
                                            jsonObject1.getString("PickUpFirstName"),
                                            jsonObject1.getString("PickUpAddress"),
                                            jsonObject1.getString("PickUpMobileNumber"),
                                            jsonObject1.getString("PickUpLatitude"),
                                            jsonObject1.getString("PickUpLongitude"),
                                            jsonObject1.getString("DeliveryFirstName"),
                                            jsonObject1.getString("DeliveryAddress"),
                                            jsonObject1.getString("DeliveryMobileNumber"),
                                            jsonObject1.getString("DeliveryLatitude"),
                                            jsonObject1.getString("DeliveryLongitude"),
                                            jsonObject1.getString("PickUpDateTime"),
                                            jsonObject1.getString("DeliveryDateTime"),
                                            jsonObject1.getString("RAmount"),
                                            jsonObject1.getString("OrderCompletion")
                                    );

                                    listItems.add(item);

                                }

                                for(int k=0;k<listItems.size();k++)
                                {

                                    int type = listItems.get(k).getType();
                                   // System.out.println("Type = "+type);
                                    if (type == 0) {
                                        dateTime = listItems.get(k).getpDateTime();
                                    } else if(type == 1) {
                                        dateTime = listItems.get(k).getPickUpDateTIme();
                                    }
                                    String date = getDate1(dateTime);
                                   // System.out.println(date+" "+selectedDate);

                                    if(date.equals(selectedDate))
                                    {
                                        NewListItems sortedItem = new NewListItems(
                                                listItems.get(k).getOrderNumber(),
                                                listItems.get(k).getOrderStatusID(),
                                                listItems.get(k).getOrderStatusName(),
                                                listItems.get(k).getOrderTaskTypeID(),
                                                listItems.get(k).getOrderTaskTypeName(),
                                                listItems.get(k).getTotalAmount(),
                                                listItems.get(k).getCrmID(),
                                                listItems.get(k).getFirstName(),
                                                listItems.get(k).getMobileNumber(),
                                                listItems.get(k).getAddress(),
                                                listItems.get(k).getLatitude(),
                                                listItems.get(k).getLongitude(),
                                                listItems.get(k).getpDateTime(),
                                                listItems.get(k).getType(),
                                                listItems.get(k).getPickUpClientName(),
                                                listItems.get(k).getPickUpClientAddress(),
                                                listItems.get(k).getPickUpClientMobileNumber(),
                                                listItems.get(k).getPickUpLatitude(),
                                                listItems.get(k).getPickUpLongitude(),
                                                listItems.get(k).getDeliveryClientName(),
                                                listItems.get(k).getDeliveryClientAddress(),
                                                listItems.get(k).getDeliveryClientMobileNumber(),
                                                listItems.get(k).getDeliveryLatitude(),
                                                listItems.get(k).getDeliveryLongitude(),
                                                listItems.get(k).getPickUpDateTIme(),
                                                listItems.get(k).getDeliveryDateTime(),
                                                listItems.get(k).getrAmount(),
                                                listItems.get(k).getCompletionCount()
                                        );
                                        sortedListItem.add(sortedItem);

                                    }


                                }

                              //  System.out.println("Sorted list size"+sortedListItem.size());
                                if(sortedListItem.size()!=0)
                                {
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    adapter = new DashboardAdapter(sortedListItem,context);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();


                                    linearLayoutEmptyCart.setVisibility(View.GONE);
                                    linearLayout.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    linearLayoutEmptyCart.setVisibility(View.VISIBLE);
                                    linearLayout.setVisibility(View.GONE);
                                }

                            }
                            else if(jsonObject.getString("result").equals("FAIl"))
                            {
                                linearLayoutEmptyCart.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility(View.GONE);
                            }*/

                        }
                        catch (JSONException e) {

                            e.printStackTrace();
                            progressBar.setVisibility(View.INVISIBLE);

                        }


                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // progressDialog.dismiss();
                        progressBar.setVisibility(View.INVISIBLE);
                       // Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            int duration = Toast.LENGTH_SHORT;

                            Toast.makeText(context, "No internet connection", duration).show();
                        }
                    }
                }
        ){
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", userName);
                params.put("date",selectedDate);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
    // for swipe the order tile
    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT){

                    adapter.removeItem(position);
                }
            }
        };
    }

    private String getDate1(String dateTime) {
        String formattedDeliverDate=null;
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df2.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = df2.parse(dateTime);
            df1.setTimeZone(TimeZone.getDefault());
            formattedDeliverDate = df1.format(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDeliverDate;
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
                                status[0] = jsonObject1.getBoolean("isLoggedIn");
                         //       System.out.println("Santosh+sss  "+status[0]);
                                if(jsonObject1.getBoolean("isLoggedIn") ==true){

                                    isLoggedIn =jsonObject1.getBoolean("isLoggedIn");
                                }
                                else
                                {

                                    logoutInterface.logoutFromFragment();
                                   // logout();
                                    isLoggedIn =jsonObject1.getBoolean("isLoggedIn");
                                 //   System.out.println(isLoggedIn+"Response Of Rider Login ");



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
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
        return isLoggedIn;
    }

    private void logout() {
        SessionManagement.clearLoggedInEmailAddress(mContext);
        Intent myIntent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivity(myIntent);
       // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    /*Intent i2 = new Intent(mContext,NotificationService.class);
                                    getActivity().getApplicationContext().stopService(i2);
                                    Intent i3= new Intent(mContext,LocationService.class);
                                    getActivity().getApplicationContext().stopService(i3);*/
       // getActivity().startActivity(intent);
        new GPSTracker(mContext).stopUsingGPS();

        Toast.makeText(getContext(), "You have been logged out\n" +
                "Please try again or contact Administrator", Toast.LENGTH_SHORT).show();
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
