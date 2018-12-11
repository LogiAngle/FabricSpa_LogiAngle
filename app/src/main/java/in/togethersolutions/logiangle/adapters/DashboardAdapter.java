package in.togethersolutions.logiangle.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

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
import in.togethersolutions.logiangle.activities.OrderInformationNew;
import in.togethersolutions.logiangle.activities.Reschedule;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.modals.NewListItems;
import in.togethersolutions.logiangle.session.SessionManagement;

public class DashboardAdapter extends RecyclerView.Adapter {

        private List<NewListItems> listItems;
        private Context context;
        String orderNumber;
        String formattedDate = null;
        String formattedPickUpDate = null;
        String formattedDeliverDate = null;
        int mapflag = 0;
        EditText edtNotes;
        String cancelNotes;
        private static final int EMPTY_VIEW = 10;
        String userName;
        boolean isLoggedIn[] = {true};
        //create constructor for get listitems and application context from dashboard page
        public DashboardAdapter(List<NewListItems> listItems, Context context) {
                this.listItems = listItems;
                this.context = context;
        }

        //create view holder
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = null;
                userName = SessionManagement.getLoggedInUserName(context);
                // check view type for view holder
                if (viewType == 0) {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordermanagment_pickupordelivery_list_item, parent, false);
                        return new PickUpTypeViewHolder(view);
                } else if (viewType == 1) {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordermanagement_pickanddelivery_list_item, parent, false);
                        return new PickUpAndDeliveryTypeViewHolder(view);
                } else if (viewType == 10) {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emptycard, parent, false);
                        return new EmptyViewHolder(view);
                }
                return null;
        }

        /*Check view type is pickup or delivery or pickup & delivery*/
        @Override
        public int getItemViewType(int position) {

                switch (listItems.get(position).getType()) {
                        case 0:
                                return NewListItems.PICKUP;
                        case 1:
                                return NewListItems.PD;
                        default:
                                return -1;
                }
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
                final NewListItems listItem = listItems.get(position);
                if (listItem != null) {
                        // Initialization and declaration of pickup or delivery type component
                        switch (listItem.getType()) {
                                case NewListItems.PICKUP:
                                        //checkReschdule();
                                        String rAmount = listItem.getrAmount();
                                        String Count = listItem.getCompletionCount();
                                        if(listItem.getOrderTaskTypeID().equals("1"))
                                        {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    ((PickUpTypeViewHolder) holder).linearCardViewTypeOne.setBackground(context.getDrawable(R.drawable.card_border_assigned));
                                                }
                                            }
                                        }
                                        else if(listItem.getOrderTaskTypeID().equals("2"))
                                        {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                ((PickUpTypeViewHolder) holder).linearCardViewTypeOne.setBackground(context.getDrawable(R.drawable.card_border_delivery));
                                            }
                                        }
                                        else if(listItem.getOrderTaskTypeID().equals("3"))
                                        {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                ((PickUpTypeViewHolder) holder).linearCardViewTypeOne.setBackground(context.getDrawable(R.drawable.card_border_pickupanddelivery));
                                            }
                                        }
                                        else {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                ((PickUpTypeViewHolder) holder).linearCardViewTypeOne.setBackground(context.getDrawable(R.drawable.card_border_appointment));
                                            }
                                        }
                                        ((PickUpTypeViewHolder) holder).textOrderNumber.setText(listItem.getOrderNumber());
                                        ((PickUpTypeViewHolder) holder).textTaskType.setText(listItem.getOrderTaskTypeName());
                                        ((PickUpTypeViewHolder) holder).textClientAddress.setPaintFlags(((PickUpTypeViewHolder) holder).textClientAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                        ((PickUpTypeViewHolder) holder).textClientAddress.setText(listItem.getAddress());
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


                                        ((PickUpTypeViewHolder) holder).textClientAddress.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                        if(checkRiderLogin(userName)) {
                                                                Uri uri = Uri.parse("geo:0,0?q=+" + listItem.getLatitude() + "," + listItem.getLongitude());
                                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                context.startActivity(intent);
                                                        }
                                                        else
                                                        {
                                                                /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                                                                //setting dialog title
                                                                alertDialog.setTitle("Logout");
                                                                //setting dialog message
                                                                alertDialog.setIcon(R.drawable.logout);

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


                                        ((PickUpTypeViewHolder) holder).textClientName.setText(listItem.getFirstName());
                                        ((PickUpTypeViewHolder) holder).textClientMobileNumber.setText(listItem.getMobileNumber());
                                        ((PickUpTypeViewHolder) holder).textStatus.setText(listItem.getOrderStatusName());
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


                                        ((PickUpTypeViewHolder) holder).textDateTime.setText(AllConstant.convertDateFormate(formattedDate));
                                        ((PickUpTypeViewHolder) holder).textCrm.setText(listItem.getCrmID());
                                        if (listItem.getOrderStatusID().trim().equals("8")) {
                                                ((PickUpTypeViewHolder) holder).toggleButtonStart.setChecked(false);
                                                ((PickUpTypeViewHolder) holder).toggleButtonStart.setEnabled(true);
                                                ((PickUpTypeViewHolder) holder).textStatus.setText(listItem.getOrderStatusName());
                                        } else if (listItem.getOrderStatusID().trim().equals("9")) {
                                                ((PickUpTypeViewHolder) holder).toggleButtonStart.setEnabled(false);
                                                ((PickUpTypeViewHolder) holder).toggleButtonComplete.setEnabled(true);
                                                ((PickUpTypeViewHolder) holder).textOrderDetail.setEnabled(false);
                                                ((PickUpTypeViewHolder) holder).textPOD.setEnabled(true);
                                                ((PickUpTypeViewHolder) holder).txtReschedule.setEnabled(true);
                                                ((PickUpTypeViewHolder) holder).textCash.setEnabled(true);
                                                ((PickUpTypeViewHolder) holder).textClientMobileNumber.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                                if(checkRiderLogin(userName)){
                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                                        builder.setMessage("Would you like to call this client?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                        //calling to the client
                                                                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                                                                        String mobileNumber = ((PickUpTypeViewHolder) holder).textClientMobileNumber.getText().toString().trim();
                                                                                        intent.setData(Uri.parse("tel:" + mobileNumber));
                                                                                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                                                                // TODO: Consider calling
                                                                                                //    ActivityCompat#requestPermissions
                                                                                                // here to request the missing permissions, and then overriding
                                                                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                                                                //                                          int[] grantResults)
                                                                                                // to handle the case where the user grants the permission. See the documentation
                                                                                                // for ActivityCompat#requestPermissions for more details.
                                                                                                return;
                                                                                        }
                                                                                        context.startActivity(intent);
                                                                                }
                                                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {

                                                                                }
                                                                        }).show();
                                                                }

                                                                else
                                                                {
                                                                        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
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


                               // System.out.println("Received Amount"+rAmount);
                                if((rAmount.equals("0.00") || rAmount.equals("null"))&& Count.equals("0" ))
                                {
                                        ((PickUpTypeViewHolder)holder).txtReschedule.setEnabled(true);
                                        ((PickUpTypeViewHolder)holder).textCash.setEnabled(true);
                                }
                                else
                                {
                                        ((PickUpTypeViewHolder)holder).txtReschedule.setEnabled(false);
                                        ((PickUpTypeViewHolder)holder).textCash.setEnabled(false);
                                }
                               // ((PickUpTypeViewHolder)holder).textCash.setEnabled(false);
                                }

                        ((PickUpTypeViewHolder) holder).textPOD.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                        if(checkRiderLogin(userName)){
                                        Intent intent = new Intent(context, OrderInformationNew.class);
                                        intent.putExtra("orderNumber", listItem.getOrderNumber());
                                        intent.putExtra("ClientName",listItem.getFirstName());
                                        intent.putExtra("ClientNumber",listItem.getMobileNumber());
                                        intent.putExtra("OrderTaskType",listItem.getOrderTaskTypeName());
                                        intent.putExtra("totalAmount", listItem.getTotalAmount());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("toggal1", 1);
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



                        //order reschedule
                        ((PickUpTypeViewHolder)holder).txtReschedule.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        if (checkRiderLogin(userName)) {

                                                Intent intent = new Intent(context, Reschedule.class);
                                                intent.putExtra("orderNumber", listItem.getOrderNumber());
                                                intent.putExtra("pClientName", listItem.getFirstName());
                                                intent.putExtra("pDateTime", listItem.getpDateTime());
                                                intent.putExtra("pClientMobile", listItem.getMobileNumber());
                                                intent.putExtra("taskType", listItem.getOrderTaskTypeName());
                                                intent.putExtra("TaskTypeID", listItem.getOrderTaskTypeID());

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
                        ((PickUpTypeViewHolder) holder).toggleButtonStart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        if(checkRiderLogin(userName)){
                                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                builder.setMessage("Would you like to start this order?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                                ((PickUpTypeViewHolder) holder).toggleButtonStart.setChecked(true);
                                                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                                                        new Response.Listener<String>() {
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                                ((PickUpTypeViewHolder) holder).textOrderDetail.setEnabled(false);
                                                                                ((PickUpTypeViewHolder) holder).textPOD.setEnabled(true);
                                                                                ((PickUpTypeViewHolder) holder).textCash.setEnabled(true);
                                                                                ((PickUpTypeViewHolder) holder).txtReschedule.setEnabled(true);
                                                                                ((PickUpTypeViewHolder) holder).toggleButtonStart.setEnabled(false);
                                                                                ((PickUpTypeViewHolder) holder).toggleButtonComplete.setEnabled(true);
                                                                                ((PickUpTypeViewHolder) holder).textStatus.setText("INTRANSIT");
                                                                                ((PickUpTypeViewHolder) holder).textClientMobileNumber.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                                                                builder.setMessage("Would you like to call this client?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                                        @Override
                                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                                                //calling to the client
                                                                                                                Intent intent = new Intent(Intent.ACTION_CALL);
                                                                                                                String mobileNumber = ((PickUpTypeViewHolder) holder).textClientMobileNumber.getText().toString().trim();
                                                                                                                intent.setData(Uri.parse("tel:" + mobileNumber));
                                                                                                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                                                                                        // TODO: Consider calling
                                                                                                                        //    ActivityCompat#requestPermissions
                                                                                                                        // here to request the missing permissions, and then overriding
                                                                                                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                                                                                        //                                          int[] grantResults)
                                                                                                                        // to handle the case where the user grants the permission. See the documentation
                                                                                                                        // for ActivityCompat#requestPermissions for more details.
                                                                                                                        return;
                                                                                                                }
                                                                                                                context.startActivity(intent);
                                                                                                        }
                                                                                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                                        @Override
                                                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                                                        }
                                                                                                }).show();
                                                                                        }
                                                                                });
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

                                        }

                                }
                                });
                                ((PickUpTypeViewHolder) holder).toggleButtonComplete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                                if(checkRiderLogin(userName)) {
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
                                                        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
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

                        break;
                case NewListItems.PD:
                        String rAmount1 = listItem.getrAmount();
                        String Count1=listItem.getCompletionCount();
                   //     System.out.println(listItem.getCompletionCount());

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
                                ((PickUpAndDeliveryTypeViewHolder) holder).textOrderCash.setEnabled(false);
                                ((PickUpAndDeliveryTypeViewHolder) holder).textOrderDetail.setEnabled(false);
                                ((PickUpAndDeliveryTypeViewHolder) holder).textOrderPOD.setEnabled(true);
                                ((PickUpAndDeliveryTypeViewHolder) holder).switchStart.setChecked(true);
                                ((PickUpAndDeliveryTypeViewHolder) holder).switchStart.setEnabled(false);
                                ((PickUpAndDeliveryTypeViewHolder) holder).switchComplete.setEnabled(false);
                                ((PickUpAndDeliveryTypeViewHolder) holder).switchPickedUp.setEnabled(true);
                                if(rAmount1.equals("null")&&Count1.equals("0"))
                                {
                                     ((PickUpAndDeliveryTypeViewHolder) holder).textOrderMap.setEnabled(true);
                                }
                                else
                                {

                                        ((PickUpAndDeliveryTypeViewHolder) holder).textOrderMap.setEnabled(false);
                                }

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
                                       /* Intent intent = new Intent(context, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                        orderNumber = listItem.getOrderNumber();*/
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
                Intent intent = new Intent(context, OrderInformationNew.class);
                intent.putExtra("orderNumber", listItem.getOrderNumber());
                intent.putExtra("totalAmount", listItem.getTotalAmount());
                intent.putExtra("ClientName",listItem.getPickUpClientName());
                intent.putExtra("ClientNumber",listItem.getPickUpClientMobileNumber());
                intent.putExtra("OrderTaskType",listItem.getOrderTaskTypeName());
                intent.putExtra("toggal1", 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
        }
        });

        ((PickUpAndDeliveryTypeViewHolder)holder).textOrderCash.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
                Intent intent = new Intent(context, OrderInformationNew.class);
                intent.putExtra("orderNumber", listItem.getOrderNumber());
                intent.putExtra("totalAmount", listItem.getTotalAmount());
                intent.putExtra("ClientName",listItem.getPickUpClientName());
                intent.putExtra("ClientNumber",listItem.getPickUpClientMobileNumber());
                intent.putExtra("OrderTaskType",listItem.getOrderTaskTypeName());
                intent.putExtra("toggal1", 2);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
        }
        });

        ((PickUpAndDeliveryTypeViewHolder)holder).textOrderPOD.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
                Intent intent = new Intent(context, OrderInformationNew.class);
                intent.putExtra("orderNumber", listItem.getOrderNumber());
                intent.putExtra("totalAmount", listItem.getTotalAmount());
                intent.putExtra("ClientName",listItem.getPickUpClientName());
                intent.putExtra("ClientNumber",listItem.getPickUpClientMobileNumber());
                intent.putExtra("OrderTaskType",listItem.getOrderTaskTypeName());
                intent.putExtra("toggal1", 1);
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

                }
        });
        break;
        }
        }

}

        private void orderCancel(AlertDialog.Builder alertDialog) {

                alertDialog = new AlertDialog.Builder(context);
                LayoutInflater factory = LayoutInflater.from(context);
                final View view = factory.inflate(R.layout.order_cancel_popup, null);
                alertDialog.setView(view);
                MaterialSpinner spinnerReason;
                EditText edtNotes;
                spinnerReason = (MaterialSpinner)view.findViewById(R.id.spinnerReason);
                edtNotes = (EditText)view.findViewById(R.id.edtTxtComment);
                spinnerReason.setItems("Customer not Available", "Duplicate pickup", "Logistics not reached", "Prices High", "Customer Cancelled the pickup","Customer Postponed","Other");
                spinnerReason.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                        @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                        }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                });
                alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                });
                alertDialog.show();
                /*final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.order_cancel_popup);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.show();*/
        }

        private void checkReschdule() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                                Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
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
                        params.put("updateCode",String.valueOf(AllConstant.checkReschdule));
                        return params;
                }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
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

        public void removeItem(int position) {
                listItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listItems.size());
        }

private class PickUpTypeViewHolder extends RecyclerView.ViewHolder {
    public TextView textOrderNumber;
    public TextView textClientName;
    public TextView textClientMobileNumber;
    public TextView textClientAddress;
    public Button textStatus;
    public TextView textOrderDetail;
    public TextView textCash;
    public TextView textViewName;
    public TextView textPOD;
    public TextView txtReschedule;
    public TextView txtNotes;
    public Switch toggleButtonStart;
    public Switch toggleButtonComplete;
    public TextView textTaskType;
    public TextView textDateTime;
    public TextView textCrm;
    public TextView textViewTimeSlot;
    public LinearLayout linearCardViewTypeOne;
    public PickUpTypeViewHolder(View view) {
        super(view);
        toggleButtonStart = (Switch)itemView.findViewById(R.id.toggleButtonStart);
        toggleButtonComplete = (Switch)itemView.findViewById(R.id.toggleButtonComplete);
        textOrderNumber = (TextView)itemView.findViewById(R.id.textViewOrderNumber);
        textClientName = (TextView)itemView.findViewById(R.id.textViewClientName);
        textClientMobileNumber = (TextView)itemView.findViewById(R.id.textViewClientMobileNumber);
        textClientAddress = (TextView)itemView.findViewById(R.id.textViewClientAddress);
        textStatus = (Button) itemView.findViewById(R.id.textViewStatus);
        textCash = (TextView)itemView.findViewById(R.id.textViewCash);
        textOrderDetail = (TextView)itemView.findViewById(R.id.textViewOrderDetail);
        textPOD = (TextView)itemView.findViewById(R.id.textViewPOD);
        txtReschedule = (TextView)itemView.findViewById(R.id.textViewRescheduled);
        textViewName = (TextView)itemView.findViewById(R.id.textViewClientName);
        textTaskType = (TextView)itemView.findViewById(R.id.textViewTaskType);
        textDateTime = (TextView)itemView.findViewById(R.id.textViewSchedule);
        textCrm = (TextView)itemView.findViewById(R.id.textViewCRM);
        txtNotes = (TextView)itemView.findViewById(R.id.txtNotes);
        textViewTimeSlot = (TextView)itemView.findViewById(R.id.textViewTimeSlot);
        linearCardViewTypeOne = (LinearLayout)itemView.findViewById(R.id.linearCardViewTypeOne);

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
    }
}

private class EmptyViewHolder extends RecyclerView.ViewHolder {
    public EmptyViewHolder(View view) {
        super(view);
    }
}

public String getUTCDateTime()
{
        String date;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                                         //       System.out.println(jsonObject1.getBoolean("isLoggedIn"));
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

