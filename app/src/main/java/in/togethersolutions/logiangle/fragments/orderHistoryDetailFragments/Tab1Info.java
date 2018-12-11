package in.togethersolutions.logiangle.fragments.orderHistoryDetailFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.WanderingCubes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.activities.MainActivity;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.session.SessionManagement;

public class Tab1Info extends Fragment {
    private String orderNumber;
    private String userName;
    public TextView textOrderNumber;
    public TextView textCRMID;
    public TextView textType;
    public TextView textStatus;
    public TextView textSchedule;
    public TextView textCompleted;
    public TextView textAmount;
    public TextView textCollected;
    public TextView textNotes;
    public String CRM =null;
    public String type = null;
    public String typeID =null;
    public String status1 =null;
    public String pschedule =null;
    public String dschedule =null;
    public String complete =null;
    public String amount =null;
    public String ramount =null;
    public String notes =null;
    ProgressBar progressBar;
    boolean isLoggedIn[] = {true};
    View rootView;
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1_info, container, false);
        inItComponent();
        if(checkRiderLogin(userName)) {
            loadData();
        }
        else
        {
            /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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
                    SessionManagement.clearLoggedInEmailAddress(getActivity());
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }
            });
            alertDialog.show();*/
        }
        return rootView;
    }



    private void inItComponent() {
        textOrderNumber = (TextView)rootView.findViewById(R.id.textOrderNumber);
        textCRMID = (TextView)rootView.findViewById(R.id.textCRM);
        textType = (TextView)rootView.findViewById(R.id.textType);
        textStatus = (TextView)rootView.findViewById(R.id.textStatus);
        textSchedule = (TextView)rootView.findViewById(R.id.textSchedule);
        textCompleted = (TextView)rootView.findViewById(R.id.textCompleted);
        textAmount = (TextView)rootView.findViewById(R.id.textAmount);
        textCollected =(TextView)rootView.findViewById(R.id.textCollected);
        textNotes = (TextView)rootView.findViewById(R.id.textNotes);
       // System.out.println("HelloUserName "+userName);
        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            progressBar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            for(int i=0;i<jsonArray.length();i++) {
                                JSONObject o = jsonArray.getJSONObject(i);
                                CRM = o.getString("CRMID");
                                type =o.getString("TaskType");
                                typeID =o.getString("TaskTypeID");
                                status1 =o.getString("OrderStatus");
                                pschedule =o.getString("PickSTime");
                                dschedule =o.getString("DeliverySTime");
                                complete =o.getString("CompleteDate");
                                amount =o.getString("TotalAmount");
                                ramount =o.getString("ReceivedAmount");
                                notes =o.getString("Notes");
                                textOrderNumber.setText(orderNumber);
                                textCRMID.setText(CRM);
                                textType.setText(type);
                                textStatus.setText(status1);
                                //System.out.println(typeID+" Satatus Type"+dschedule);
                               /* DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                               */
                                String formattedPDate = null;
                                String formattedDDate = null;
                                String formattedCDate = null;
                                Date date ;
                                Date date2;
                                Date date3;


                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                try {
                                    date = df.parse(complete);
                                    df.setTimeZone(TimeZone.getDefault());
                                    formattedCDate = df.format(date);
                                    System.out.println(formattedCDate+"complete");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    df1.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    date2 = df1.parse(pschedule);
                                    df1.setTimeZone(TimeZone.getDefault());
                                    formattedPDate = df1.format(date2);
                                    System.out.println(formattedPDate+"pickup");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    df2.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    date3 = df2.parse(dschedule);
                                    df2.setTimeZone(TimeZone.getDefault());
                                    formattedDDate = df2.format(date3);
                                    System.out.print(formattedDDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if(typeID.equals("1"))
                                {

                                    textSchedule.setText(AllConstant.convertDateFormate(formattedPDate));
                                }
                                else if(typeID.equals("2"))
                                {
                                    textSchedule.setText(AllConstant.convertDateFormate(formattedDDate));
                                }
                                else if(typeID.equals("3")&& typeID.equals("4"))
                                {
                                    textSchedule.setText(AllConstant.convertDateFormate(formattedPDate));
                                }
                                textCompleted.setText(AllConstant.convertDateFormate(formattedCDate));
                                textAmount.setText(amount);
                                if(ramount.equals("null"))
                                {
                                    textCollected.setText("");
                                }
                                else
                                {
                                    textCollected.setText(ramount);
                                }

                                textNotes.setText(notes);

                            }
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        }){
            protected Map<String,String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("userName",userName);
                params.put("orderNumber",orderNumber);
                params.put("updateCode", String.valueOf(AllConstant.orderhistoryInfo));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(rootView.getContext());
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

                                    isLoggedIn[0] =true;
                                }
                                else
                                {
                                    isLoggedIn[0] =false;
                                    SessionManagement.clearLoggedInEmailAddress(getActivity());
                                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    getActivity().startActivity(intent);
                                    Toast.makeText(getActivity(), "You have been logged out\n" +
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
                param.put("loginSessionID",SessionManagement.getLoggedInSessionID(getContext()));
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        return isLoggedIn[0];
    }
}