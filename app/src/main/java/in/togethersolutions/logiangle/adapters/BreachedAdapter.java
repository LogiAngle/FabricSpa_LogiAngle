package in.togethersolutions.logiangle.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.activities.MainActivity;
import in.togethersolutions.logiangle.activities.OrderInformation;
import in.togethersolutions.logiangle.activities.Reschedule;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.modals.BreachedListItem;
import in.togethersolutions.logiangle.modals.NewListItems;
import in.togethersolutions.logiangle.session.SessionManagement;

public class BreachedAdapter  extends RecyclerView.Adapter {
    private List<BreachedListItem> listItems;
    private Context context;
    String orderNumber;
    String formattedDate = null;
    String formattedPickUpDate = null;
    String formattedDeliverDate = null;
    int mapflag =0;
    String userName;
    boolean isLoggedIn[] = {true};
    private static final int EMPTY_VIEW = 10;
    public BreachedAdapter(List<BreachedListItem> listItems, Context context) {
       // System.out.println(listItems.size()+"Call Braeched Order");
        this.listItems = listItems;
        this.context = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        userName = SessionManagement.getLoggedInUserName(context);
        //System.out.println("view Type "+viewType);
        if(viewType == 0)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.breached_list_item, parent, false);
            return new BreachedAdapter.PickUpTypeViewHolder(view);
        }
        else if(viewType == 1)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.breached_list_item2, parent, false);
            return new BreachedAdapter.PickUpAndDeliveryTypeViewHolder(view);
        }

        return null;
    }
    @Override
    public int getItemViewType(int position) {
       // System.out.println("Hiii1"+position);
        if(listItems.isEmpty())
        {
            return 10;
        }
        else {
            switch (listItems.get(position).getType()) {
                case 0:
                    return NewListItems.PICKUP;
                case 1:
                    return NewListItems.PD;
                default:
                    return -1;
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final BreachedListItem listItem = listItems.get(position);
        if (listItem != null) {
            switch (listItem.getType())
            {
                case NewListItems.PICKUP:
                    if(listItem.getOrderTaskTypeID().equals("1"))
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ((PickUpTypeViewHolder) holder).linearLayoutTaskTypeOne.setBackground(context.getDrawable(R.drawable.card_border_assigned));
                            }
                        }
                    }
                    else if(listItem.getOrderTaskTypeID().equals("2"))
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ((PickUpTypeViewHolder) holder).linearLayoutTaskTypeOne.setBackground(context.getDrawable(R.drawable.card_border_delivery));
                            }
                        }
                    }
                    else if(listItem.getOrderTaskTypeID().equals("3"))
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ((PickUpTypeViewHolder) holder).linearLayoutTaskTypeOne.setBackground(context.getDrawable(R.drawable.card_border_pickupanddelivery));
                            }
                        }
                    }
                    else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ((PickUpTypeViewHolder) holder).linearLayoutTaskTypeOne.setBackground(context.getDrawable(R.drawable.card_border_appointment));
                            }
                        }
                    }
                    ((PickUpTypeViewHolder) holder).textViewTimeSlot.setText(listItem.getTimeSlot());
                    if (listItem.getNotes().equals("") )
                    {
                        ((PickUpTypeViewHolder) holder).txtNotes.setText("");
                    }
                    else if(listItem.getNotes().toString().equals("null")){
                        ((PickUpTypeViewHolder) holder).txtNotes.setText("");
                    }
                    else if(listItem.getNotes()!="" && listItem.getNotes()!="null")
                    {
                        ((PickUpTypeViewHolder) holder).txtNotes.setText(""+listItem.getNotes());
                    }
                    if (listItem.getTimeSlot().equals("") )
                    {
                        ((PickUpTypeViewHolder) holder).textViewTimeSlot.setText("");
                    }
                    else if(listItem.getTimeSlot().toString()=="null"){
                        ((PickUpTypeViewHolder) holder).textViewTimeSlot.setText("");
                    }
                    ((PickUpTypeViewHolder)holder).textOrderNumber.setText(listItem.getOrderNumber());
                    ((PickUpTypeViewHolder)holder).textTaskType.setText(listItem.getOrderTaskTypeName());
                    ((PickUpTypeViewHolder)holder).textClientAddress.setPaintFlags(((PickUpTypeViewHolder)holder).textClientAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    ((PickUpTypeViewHolder)holder).textClientAddress.setText(listItem.getAddress());
                    ((PickUpTypeViewHolder)holder).textClientAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(checkRiderLogin(userName)){
                                Uri uri = Uri.parse("geo:0,0?q=+"+listItem.getLatitude()+","+listItem.getLongitude());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                            else
                            {
                               /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
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
                                        SessionManagement.clearLoggedInEmailAddress(context);
                                        Intent intent = new Intent(context,LoginActivity.class);
                                        context.startActivity(intent);
                                    }
                                });
                                alertDialog.show();*/
                            }
                        }
                    });
                    ((PickUpTypeViewHolder)holder).textClientName.setText(listItem.getFirstName());
                    ((PickUpTypeViewHolder)holder).textClientMobileNumber.setText(listItem.getMobileNumber());
                    ((PickUpTypeViewHolder)holder).textStatus.setText(listItem.getOrderStatusName());
                    String dateStr = listItem.getpDateTime();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));

                    Date date = null;
                    try {
                        date = df.parse(dateStr);
                        df.setTimeZone(TimeZone.getDefault());
                        formattedDate = df.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    ((PickUpTypeViewHolder)holder).textDateTime.setText(AllConstant.convertDateFormate(formattedDate));
                    ((PickUpTypeViewHolder)holder).textCrm.setText(listItem.getCrmID());
                    if(listItem.getOrderStatusID().equals("8"))
                    {
                        ((PickUpTypeViewHolder) holder).toggleButtonStart.setChecked(false);
                        ((PickUpTypeViewHolder)holder).toggleButtonStart.setEnabled(true);
                    }
                    else if(listItem.getOrderStatusID().equals("9"))
                    {
                        ((PickUpTypeViewHolder) holder).toggleButtonStart.setEnabled(false);
                        ((PickUpTypeViewHolder)holder).toggleButtonComplete.setEnabled(true);
                        ((PickUpTypeViewHolder) holder).textNew.setEnabled(true);
                        ((PickUpTypeViewHolder)holder).textAssign.setEnabled(true);
                        ((PickUpTypeViewHolder)holder).textIntransit.setEnabled(true);
                        ((PickUpTypeViewHolder)holder).textComplete.setEnabled(true);
                    }
                    ((PickUpTypeViewHolder) holder).textNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, OrderInformation.class);
                            intent.putExtra("orderNumber", listItem.getOrderNumber());
                            intent.putExtra("totalAmount", listItem.getTotalAmount());
                            intent.putExtra("toggal1", "3");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });
                    ((PickUpTypeViewHolder) holder).textAssign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, OrderInformation.class);
                            intent.putExtra("orderNumber", listItem.getOrderNumber());
                            intent.putExtra("totalAmount", listItem.getTotalAmount());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("toggal1", "1");
                            context.startActivity(intent);
                        }
                    });
                    final String finalFormattedDate = formattedDate;
                    ((PickUpTypeViewHolder)holder).textIntransit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           /* final int version = Build.VERSION.SDK_INT;
                            System.out.println("version" + version);
                            String lat = listItem.getLatitude();
                            String longi = listItem.getLongitude();
                            Uri uri = Uri.parse("geo:0,0?q=+"+listItem.getLatitude()+","+listItem.getLongitude());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);*/
                           if(checkRiderLogin(userName)) {
                               Intent intent = new Intent(context, Reschedule.class);
                               intent.putExtra("orderNumber", listItem.getOrderNumber());
                               intent.putExtra("pClientName", listItem.getFirstName());
                               intent.putExtra("pDateTime", formattedDate);
                               intent.putExtra("pClientMobile", listItem.getMobileNumber());
                               intent.putExtra("taskType", listItem.getOrderTaskTypeName());
                               intent.putExtra("TaskTypeID", listItem.getOrderTaskTypeID());

                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               context.startActivity(intent);
                           }
                           else{
                             /*  AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
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
                                       SessionManagement.clearLoggedInEmailAddress(context);
                                       Intent intent = new Intent(context,LoginActivity.class);
                                       context.startActivity(intent);
                                   }
                               });
                               alertDialog.show();*/
                           }
                        }
                    });
                    ((PickUpTypeViewHolder)holder).textComplete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(checkRiderLogin(userName)) {
                                Intent intent = new Intent(context, OrderInformation.class);
                                intent.putExtra("orderNumber", listItem.getOrderNumber());
                                intent.putExtra("totalAmount", listItem.getTotalAmount());
                                intent.putExtra("toggal1", "2");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                            else{
                               /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
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
                                        SessionManagement.clearLoggedInEmailAddress(context);
                                        Intent intent = new Intent(context,LoginActivity.class);
                                        context.startActivity(intent);
                                    }
                                });
                                alertDialog.show();*/
                            }
                        }
                    });
                    ((PickUpTypeViewHolder) holder).toggleButtonStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (checkRiderLogin(userName)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setMessage("Would you like to start this order?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((PickUpTypeViewHolder) holder).toggleButtonStart.setChecked(true);
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        ((PickUpTypeViewHolder) holder).textNew.setEnabled(true);
                                                        ((PickUpTypeViewHolder) holder).textAssign.setEnabled(true);
                                                        ((PickUpTypeViewHolder) holder).textComplete.setEnabled(true);
                                                        ((PickUpTypeViewHolder) holder).textIntransit.setEnabled(true);
                                                        ((PickUpTypeViewHolder) holder).toggleButtonStart.setEnabled(false);
                                                        ((PickUpTypeViewHolder) holder).toggleButtonComplete.setEnabled(true);
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }) {
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("orderNumber", listItem.getOrderNumber());
                                                params.put("updateCode", String.valueOf(AllConstant.orderStatus));
                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                TimeZone utc = TimeZone.getTimeZone("UTC");
                                                df.setTimeZone(utc);
                                                final String date = df.format(Calendar.getInstance().getTime());
                                                params.put("date", date);
                                                params.put("orderStatus", "Intransit");
                                                return params;
                                            }
                                        };
                                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                                        requestQueue.add(stringRequest);
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                        orderNumber = listItem.getOrderNumber();
                                        sendMessage(orderNumber);
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((PickUpTypeViewHolder) holder).toggleButtonStart.setChecked(false);
                                    }
                                }).show();
                            }
                            else
                            {
                              /*  AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
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
                                        SessionManagement.clearLoggedInEmailAddress(context);
                                        Intent intent = new Intent(context,LoginActivity.class);
                                        context.startActivity(intent);
                                    }
                                });
                                alertDialog.show();*/
                            }
                        }
                    });
                    /*((PickUpTypeViewHolder) holder).toggleButtonStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                            //AlertDialog.Builder builder = new AlertDialog.Builder(((PickUpTypeViewHolder)holder).getApplicationContext());
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                ((PickUpTypeViewHolder) holder).textNew.setEnabled(true);
                                                ((PickUpTypeViewHolder) holder).textAssign.setEnabled(true);
                                                ((PickUpTypeViewHolder) holder).textComplete.setEnabled(true);
                                                ((PickUpTypeViewHolder) holder).txtReschedule.setEnabled(true);
                                                ((PickUpTypeViewHolder) holder).toggleButtonStart.setEnabled(false);
                                                ((PickUpTypeViewHolder) holder).toggleButtonComplete.setEnabled(true);
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("orderNumber", listItem.getOrderNumber());
                                        params.put("updateCode", String.valueOf(AllConstant.orderStatus));
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        TimeZone utc = TimeZone.getTimeZone("UTC");
                                        df.setTimeZone(utc);
                                        final String date = df.format(Calendar.getInstance().getTime());
                                        params.put("date", date);
                                        params.put("orderStatus", "Intransit");
                                        return params;
                                    }
                                };
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                RequestQueue requestQueue = Volley.newRequestQueue(context);
                                requestQueue.add(stringRequest);
                                Intent intent = new Intent(context, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                orderNumber = listItem.getOrderNumber();
                                sendMessage(orderNumber);
                            }
                        }
                    });
*/
                    ((PickUpTypeViewHolder) holder).toggleButtonComplete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(checkRiderLogin(userName)){
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setMessage("Would you like to complete this order?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((PickUpTypeViewHolder) holder).toggleButtonComplete.setChecked(true);
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {


                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("orderNumber", listItem.getOrderNumber());
                                                params.put("updateCode", String.valueOf(AllConstant.completeOrder));
                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                TimeZone utc = TimeZone.getTimeZone("UTC");
                                                df.setTimeZone(utc);
                                                final String date = df.format(Calendar.getInstance().getTime());
                                                params.put("date", date);
                                                params.put("orderStatus", "10");
                                                return params;
                                            }
                                        };
                                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        stringRequest.setShouldCache(false);
                                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                                        requestQueue.add(stringRequest);
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                        orderNumber = listItem.getOrderNumber();
                                        sendCompleteMessage(orderNumber);

                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((PickUpTypeViewHolder) holder).toggleButtonComplete.setChecked(false);
                                    }
                                }).show();

                            }
                            else
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
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
                                        SessionManagement.clearLoggedInEmailAddress(context);
                                        Intent intent = new Intent(context,LoginActivity.class);
                                        context.startActivity(intent);
                                    }
                                });
                                alertDialog.show();
                            }
                        }
                    });

                    break;
                case NewListItems.PD:

                    ((PickUpAndDeliveryTypeViewHolder)holder).textOrderNumber1.setText(listItem.getOrderNumber());
                    ((PickUpAndDeliveryTypeViewHolder)holder).textCrm1.setText(listItem.getCrmID());
                    ((PickUpAndDeliveryTypeViewHolder)holder).textStatus1.setText(listItem.getOrderTaskTypeName());
                    ((PickUpAndDeliveryTypeViewHolder)holder).textPickUpName.setText(listItem.getPickUpClientName());
                    ((PickUpAndDeliveryTypeViewHolder)holder).textPickUpAddress.setText(listItem.getPickUpClientAddress());
                    ((PickUpAndDeliveryTypeViewHolder)holder).textPickUpAddress.setPaintFlags(((PickUpAndDeliveryTypeViewHolder)holder).textPickUpAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    ((PickUpAndDeliveryTypeViewHolder)holder).textPickUpAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final int version = Build.VERSION.SDK_INT;
                            Uri uri = Uri.parse("geo:0,0?q=+" + listItem.getPickUpLatitude() + "," + listItem.getPickUpLongitude());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });
                    ((PickUpAndDeliveryTypeViewHolder)holder).textPickUpMobileNo.setText(listItem.getPickUpClientMobileNumber());
                    String dateStr1 = listItem.getPickUpDateTIme();
                    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df1.setTimeZone(TimeZone.getTimeZone("UTC"));

                    Date date1 = null;
                    try {
                        date1 = df1.parse(dateStr1);
                        df1.setTimeZone(TimeZone.getDefault());
                        formattedPickUpDate = df1.format(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ((PickUpAndDeliveryTypeViewHolder)holder).textPickUpDateTime.setText(AllConstant.convertDateFormate(formattedPickUpDate));
                    ((PickUpAndDeliveryTypeViewHolder)holder).textDeliveryName.setText(listItem.getDeliveryClientName());
                    ((PickUpAndDeliveryTypeViewHolder)holder).textDeliveryMobileNo.setText(listItem.getDeliveryClientMobileNumber());
                    ((PickUpAndDeliveryTypeViewHolder)holder).textDeliveryAddress.setText(listItem.getDeliveryClientAddress());
                    System.out.println(listItem.getPickUpDateTIme()+"  "+listItem.getDeliveryDateTime());
                    ((PickUpAndDeliveryTypeViewHolder)holder).textDeliveryAddress.setPaintFlags(((PickUpAndDeliveryTypeViewHolder)holder).textDeliveryAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    ((PickUpAndDeliveryTypeViewHolder)holder).textDeliveryAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final int version = Build.VERSION.SDK_INT;
                            Uri uri = Uri.parse("geo:0,0?q=+" + listItem.getDeliveryLatitude() + "," + listItem.getDeliveryLongitude());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });

                    String dateStr2 = listItem.getDeliveryDateTime();
                    DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df2.setTimeZone(TimeZone.getTimeZone("UTC"));

                    Date date2 = null;
                    try {
                        date2 = df2.parse(dateStr2);
                        df1.setTimeZone(TimeZone.getDefault());
                        formattedDeliverDate = df1.format(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ((PickUpAndDeliveryTypeViewHolder)holder).textDeliveryDateTime.setText(AllConstant.convertDateFormate(formattedDeliverDate));
                    if(listItem.getOrderStatusID().equals("8"))
                    {
                        ((PickUpAndDeliveryTypeViewHolder)holder).switchStart.setChecked(false);
                        ((PickUpAndDeliveryTypeViewHolder) holder).switchComplete.setEnabled(false);
                        ((PickUpAndDeliveryTypeViewHolder) holder).switchPickedUp.setEnabled(false);
                    }
                    else if(listItem.getOrderStatusID().equals("9"))
                    {
                        ((PickUpAndDeliveryTypeViewHolder) holder).textOrderCash.setEnabled(true);
                        ((PickUpAndDeliveryTypeViewHolder) holder).textOrderDetail.setEnabled(true);
                        ((PickUpAndDeliveryTypeViewHolder) holder).textOrderMap.setEnabled(true);
                        ((PickUpAndDeliveryTypeViewHolder) holder).textOrderPOD.setEnabled(true);
                        ((PickUpAndDeliveryTypeViewHolder) holder).switchStart.setChecked(true);
                        ((PickUpAndDeliveryTypeViewHolder) holder).switchStart.setEnabled(false);
                        ((PickUpAndDeliveryTypeViewHolder) holder).switchComplete.setEnabled(false);
                        ((PickUpAndDeliveryTypeViewHolder) holder).switchPickedUp.setEnabled(true);
                    }
                    ((PickUpAndDeliveryTypeViewHolder)holder).switchStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage("Would you like to start this order?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    ((PickUpAndDeliveryTypeViewHolder)holder).switchPickedUp.setEnabled(true);

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }) {
                                        protected Map<String, String> getParams() {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("orderNumber", listItem.getOrderNumber());
                                            params.put("updateCode", String.valueOf(AllConstant.orderStatus));
                                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            TimeZone utc = TimeZone.getTimeZone("UTC");
                                            df.setTimeZone(utc);
                                            final String date = df.format(Calendar.getInstance().getTime());
                                            params.put("date", date);
                                            params.put("orderStatus", "Intransit");
                                            return params;
                                        }
                                    };
                                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    //request.setShouldCache(false)
                                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                                    requestQueue.add(stringRequest);
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    orderNumber = listItem.getOrderNumber();
                                    sendMessage(orderNumber);

                                }

                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((PickUpAndDeliveryTypeViewHolder) holder).switchStart.setChecked(false);
                                }
                            }).show();

                        }
                    });

               /*     ((PickUpAndDeliveryTypeViewHolder)holder).switchStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            ((PickUpAndDeliveryTypeViewHolder)holder).switchPickedUp.setEnabled(true);

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("orderNumber", listItem.getOrderNumber());
                                    params.put("updateCode", String.valueOf(AllConstant.orderStatus));
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    TimeZone utc = TimeZone.getTimeZone("UTC");
                                    df.setTimeZone(utc);
                                    final String date = df.format(Calendar.getInstance().getTime());
                                    params.put("date", date);
                                    params.put("orderStatus", "Intransit");
                                    return params;
                                }
                            };
                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            //request.setShouldCache(false)
                            RequestQueue requestQueue = Volley.newRequestQueue(context);
                            requestQueue.add(stringRequest);
                            Intent intent = new Intent(context, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            orderNumber = listItem.getOrderNumber();
                            sendMessage(orderNumber);
                        }
                    });
               */
                    ((PickUpAndDeliveryTypeViewHolder)holder).switchPickedUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage("Would you like to pick up?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mapflag = 1;
                                    ((PickUpAndDeliveryTypeViewHolder)holder).switchComplete.setEnabled(true);
                                    ((PickUpAndDeliveryTypeViewHolder)holder).switchPickedUp.setEnabled(false);

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((PickUpAndDeliveryTypeViewHolder)holder).switchPickedUp.setChecked(false);
                                }
                            }).show();

                        }
                    });

                    ((PickUpAndDeliveryTypeViewHolder)holder).switchComplete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage("Would you like to complete this order?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {


                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("orderNumber", listItem.getOrderNumber());
                                            params.put("updateCode", String.valueOf(AllConstant.completeOrder));
                                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            TimeZone utc = TimeZone.getTimeZone("UTC");
                                            df.setTimeZone(utc);
                                            final String date = df.format(Calendar.getInstance().getTime());
                                            params.put("date", date);
                                            params.put("orderStatus", "10");
                                            return params;
                                        }
                                    };
                                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    stringRequest.setShouldCache(false);
                                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                                    requestQueue.add(stringRequest);
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    orderNumber = listItem.getOrderNumber();
                                    sendCompleteMessage(orderNumber);

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((PickUpAndDeliveryTypeViewHolder)holder).switchComplete.setChecked(false);
                                }
                            }).show();

                        }
                    });


                    ((PickUpAndDeliveryTypeViewHolder)holder).textOrderDetail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, OrderInformation.class);
                            intent.putExtra("orderNumber", listItem.getOrderNumber());
                            intent.putExtra("totalAmount", listItem.getTotalAmount());
                            intent.putExtra("toggal1", "3");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });

                    ((PickUpAndDeliveryTypeViewHolder)holder).textOrderCash.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, OrderInformation.class);
                            intent.putExtra("orderNumber", listItem.getOrderNumber());
                            intent.putExtra("totalAmount", listItem.getTotalAmount());
                            intent.putExtra("toggal1", "3");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });

                    ((PickUpAndDeliveryTypeViewHolder)holder).textOrderPOD.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, OrderInformation.class);
                            intent.putExtra("orderNumber", listItem.getOrderNumber());
                            intent.putExtra("totalAmount", listItem.getTotalAmount());
                            intent.putExtra("toggal1", "3");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });


                    ((PickUpAndDeliveryTypeViewHolder)holder).textOrderMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context,Reschedule.class);
                            intent.putExtra("orderNumber", listItem.getOrderNumber());
                            intent.putExtra("pClientName",listItem.getPickUpClientName());
                            intent.putExtra("pDateTime", formattedPickUpDate);
                            intent.putExtra("pClientMobile",listItem.getPickUpClientMobileNumber());
                            intent.putExtra("taskType",listItem.getOrderTaskTypeName());
                            intent.putExtra("TaskTypeID",listItem.getOrderTaskTypeID());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                       /* if(mapflag==0) {
                            final int version = Build.VERSION.SDK_INT;
                            Uri uri = Uri.parse("geo:0,0?q=+" + listItem.getPickUpLatitude() + "," + listItem.getPickUpLongitude());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else if(mapflag ==1) {
                            final int version = Build.VERSION.SDK_INT;
                            Uri uri = Uri.parse("geo:0,0?q=+" + listItem.getDeliveryLatitude() + "," + listItem.getDeliveryLongitude());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }*/

                        }
                    });
                    break;
            }

        }
        else{
            System.out.println("Santosh It is an empty card");
        }

    }
    private void sendCompleteMessage(final String orderNumber) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            protected Map<String,String>getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("orderNumber",orderNumber);
                params.put("updateCode", String.valueOf(AllConstant.riderCompleteMessage));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    private void sendMessage(final String orderNumber) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            protected Map<String,String>getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("orderNumber",orderNumber);
                params.put("updateCode", String.valueOf(AllConstant.riderStartMessage));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
    private class PickUpTypeViewHolder extends RecyclerView.ViewHolder {
        public TextView textOrderNumber;
        public TextView textClientName;
        public TextView textClientMobileNumber;
        public TextView textClientAddress;
        public Button textStatus;
        public TextView textNew;
        public TextView textComplete;
        public TextView textViewName;
        public TextView textAssign;
        public TextView textIntransit;
        public Switch toggleButtonStart;
        public Switch toggleButtonComplete;
        public TextView textTaskType;
        public TextView textDateTime;
        public TextView textCrm;
        public LinearLayout linearLayoutTaskTypeOne;
        public TextView textViewTimeSlot;
        public TextView txtNotes;
        public PickUpTypeViewHolder(View view) {
            super(view);
            toggleButtonStart = (Switch)itemView.findViewById(R.id.toggleButtonStart);
            toggleButtonComplete = (Switch)itemView.findViewById(R.id.toggleButtonComplete);
            textOrderNumber = (TextView)itemView.findViewById(R.id.textViewOrderNumber);
            textClientName = (TextView)itemView.findViewById(R.id.textViewClientName);
            textClientMobileNumber = (TextView)itemView.findViewById(R.id.textViewClientMobileNumber);
            textClientAddress = (TextView)itemView.findViewById(R.id.textViewClientAddress);
            textStatus = (Button) itemView.findViewById(R.id.textViewStatus);
            textComplete = (TextView)itemView.findViewById(R.id.textViewComplete);
            textNew = (TextView)itemView.findViewById(R.id.textViewNew);
            textAssign = (TextView)itemView.findViewById(R.id.textViewAssign);
            textIntransit = (TextView)itemView.findViewById(R.id.textViewIntransit);
            textViewName = (TextView)itemView.findViewById(R.id.textViewClientName);
            textTaskType = (TextView)itemView.findViewById(R.id.textViewTaskType);
            textDateTime = (TextView)itemView.findViewById(R.id.textViewSchedule);
            textCrm = (TextView)itemView.findViewById(R.id.textViewCRM);
            txtNotes = (TextView)itemView.findViewById(R.id.txtNotes);
            textViewTimeSlot = (TextView)itemView.findViewById(R.id.textViewTimeSlot);
            linearLayoutTaskTypeOne = (LinearLayout)itemView.findViewById(R.id.linearLayoutTaskTypeOne);

        }
    }

    private class PickUpAndDeliveryTypeViewHolder extends RecyclerView.ViewHolder {
        public TextView textOrderNumber1;
        public TextView textCrm1;
        public TextView textStatus1;
        public TextView textPickUpName;
        public TextView textDeliveryName;
        public TextView textPickUpAddress;
        public TextView textDeliveryAddress;
        public TextView textPickUpMobileNo;
        public TextView textDeliveryMobileNo;
        public TextView textPickUpDateTime;
        public TextView textDeliveryDateTime;
        public TextView textOrderDetail;
        public TextView textOrderCash;
        public TextView textOrderMap;
        public TextView textOrderPOD;
        public Switch switchPickedUp;
        public Switch switchStart;
        public Switch switchComplete;
        public LinearLayout linearLayoutTaskTypeTwo;
        public PickUpAndDeliveryTypeViewHolder(View view) {
            super(view);
            textOrderNumber1 = (TextView)itemView.findViewById(R.id.textViewOrderNumber);
            textCrm1 = (TextView)itemView.findViewById(R.id.textViewCRM);
            textStatus1 = (TextView)itemView.findViewById(R.id.textViewStatus);
            textPickUpName = (TextView)itemView.findViewById(R.id.pickUp_Name);
            textPickUpMobileNo = (TextView)itemView.findViewById(R.id.pickUp_MobileNo);
            textPickUpAddress = (TextView)itemView.findViewById(R.id.pickUp_Address);
            textPickUpDateTime = (TextView)itemView.findViewById(R.id.pickUp_DateTime);
            textDeliveryName = (TextView)itemView.findViewById(R.id.delivery_Name);
            textDeliveryAddress = (TextView)itemView.findViewById(R.id.delivery_Address);
            textDeliveryMobileNo = (TextView)itemView.findViewById(R.id.delivery_MobileNo);
            textDeliveryDateTime = (TextView)itemView.findViewById(R.id.delivery_DateTime);
            textOrderDetail = (TextView)itemView.findViewById(R.id.textViewOrderDetail);
            textOrderCash = (TextView)itemView.findViewById(R.id.textViewCash);
            textOrderMap = (TextView)itemView.findViewById(R.id.textViewMap);
            textOrderPOD = (TextView)itemView.findViewById(R.id.textViewPOD);
            switchStart = (Switch)itemView.findViewById(R.id.toggleButtonStart);
            switchPickedUp = (Switch)itemView.findViewById(R.id.toggleButtonPickedUp);
            switchComplete = (Switch)itemView.findViewById(R.id.toggleButtonComplete);
           // linearLayoutTaskTypeTwo= (LinearLayout)itemView.findViewById(R.id.linearLayoutTaskTypeTwo);
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View view) {
            super(view);
        }
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

                                    isLoggedIn[0] =true;
                                }
                                else
                                {
                                    isLoggedIn[0] =false;
                                    SessionManagement.clearLoggedInEmailAddress(context);
                                    Intent intent = new Intent(context,LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    Toast.makeText(context, "You have been logged out\n" +
                                            "Please try again or contact Administrator", Toast.LENGTH_SHORT).show();

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
                param.put("loginSessionID",SessionManagement.getLoggedInSessionID(context));
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        return isLoggedIn[0];

    }

}

