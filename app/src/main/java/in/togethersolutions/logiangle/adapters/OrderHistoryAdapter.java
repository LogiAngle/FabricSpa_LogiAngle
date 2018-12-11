package in.togethersolutions.logiangle.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.activities.OrderHistoryInformation;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.modals.ListItem;
import in.togethersolutions.logiangle.modals.NewListItems;
import in.togethersolutions.logiangle.services.GPSTracker;
import in.togethersolutions.logiangle.session.SessionManagement;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;
    String orderNumber;
    Activity mActivity = null;
    LinearLayout linearLayout;
    private boolean onBind;
    GPSTracker gps;
    String userName = null;
    boolean isLoggedIn[] ={true};
    public OrderHistoryAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        userName = SessionManagement.getLoggedInUserName(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListItem listItem = listItems.get(position);

        holder.textOrderNumber.setText(listItem.getOrderNumber());
        holder.textClientName.setText(listItem.getClientName());
        holder.textClientMobileNumber.setText(listItem.getClientMobileNumber());
        holder.textCrm.setText(listItem.getCrmID());
        holder.textStatus.setText(listItem.getStatus());
        if(listItem.getTaskTypeID().equals("1"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.linearLayoutTaskType.setBackground(context.getDrawable(R.drawable.card_border_assigned));
                }
            }
        }
        else if(listItem.getTaskTypeID().equals("2"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.linearLayoutTaskType.setBackground(context.getDrawable(R.drawable.card_border_delivery));
                }
            }
        }
        else if(listItem.getTaskTypeID().equals("3"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    (holder).linearLayoutTaskType.setBackground(context.getDrawable(R.drawable.card_border_pickupanddelivery));
                }
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    (holder).linearLayoutTaskType.setBackground(context.getDrawable(R.drawable.card_border_appointment));
                }
            }
        }
        holder.textInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkRiderLogin(userName)){
                    Intent intent = new Intent(context,OrderHistoryInformation.class);
                    intent.putExtra("orderNumber",listItem.getOrderNumber());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
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
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!onBind) {
            // your process when checkBox changed
            // ...

            notifyDataSetChanged();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView textOrderNumber;
        public TextView textClientName;
        public TextView textClientMobileNumber;
        public Button textStatus;
        public TextView textNew;
        public TextView textCrm;
        public TextView textInfo;
        public LinearLayout linearLayoutTaskType;
        public ViewHolder(final View itemView) {
            super(itemView);

            textOrderNumber = (TextView)itemView.findViewById(R.id.textViewOrderNumber);
            textClientName = (TextView)itemView.findViewById(R.id.textViewClientName);
            textClientMobileNumber = (TextView)itemView.findViewById(R.id.textViewClientMobileNumber);
            textStatus = (Button) itemView.findViewById(R.id.textViewStatus);
            textNew = (TextView)itemView.findViewById(R.id.textViewNew);
            textCrm = (TextView)itemView.findViewById(R.id.textViewCRM);
            textInfo = (TextView)itemView.findViewById(R.id.textViewOrderInformation);
            linearLayoutTaskType = (LinearLayout)itemView.findViewById(R.id.linearCardViewTaskType);
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
                                    isLoggedIn[0] =false;
                                    SessionManagement.clearLoggedInEmailAddress(context);
                                    Intent intent = new Intent(context,LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

