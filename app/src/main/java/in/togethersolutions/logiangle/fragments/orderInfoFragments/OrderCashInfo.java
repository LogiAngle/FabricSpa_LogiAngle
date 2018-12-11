package in.togethersolutions.logiangle.fragments.orderInfoFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.session.SessionManagement;

public class OrderCashInfo extends Fragment implements View.OnClickListener {
    View rootView;


    TextView txtOrderNumber;
    TextView txtClientName;
    TextView txtOrderTask;
    TextView txtCash,txtCashInfo;
    TextView txtCOD;

    EditText edtComments;
    EditText edtCash;

    Button btnSubmit;

    ProgressBar progressBar;

    String orderNumber,orderTaskName,clientName,clientNumber,orderAmount;
    String userName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.order_cash_detail,container,false);
        inItComponent(rootView);
        inItListener(rootView);
        getDataFromIntent();
        userName = SessionManagement.getLoggedInUserName(getActivity());

            loadOrderDetailRecord();
        /*}
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            //setting dialog title
            alertDialog.setTitle("Logout");
            //setting dialog message


            alertDialog.setMessage("Login has expired from admin");
            alertDialog.setCancelable(false);


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
        }    */
        setComponent();
        return rootView;
    }

    private void inItComponent(View rootView) {
        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
        txtOrderNumber = (TextView)rootView.findViewById(R.id.txtOrderNumber);
        txtOrderTask = (TextView)rootView.findViewById(R.id.txtTaskType);
        txtClientName = (TextView)rootView.findViewById(R.id.txtClientName);
        txtCash = (TextView)rootView.findViewById(R.id.txtCash);
        txtCashInfo = (TextView)rootView.findViewById(R.id.txtCashInfo);
        txtCOD = (TextView)rootView.findViewById(R.id.txtCOD);
        btnSubmit = (Button)rootView.findViewById(R.id.btnCashSubmit);

        edtCash = (EditText)rootView.findViewById(R.id.edtTxtAmount);
        edtComments = (EditText)rootView.findViewById(R.id.edtTxtComment);



    }
    private void inItListener(View rootView) {
        //txtAddItem.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void loadOrderDetailRecord() {

            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBar.setVisibility(View.GONE);
                            JSONObject jsonObject = null;
                            JSONArray jsonArray = null;
                            String tAmount = null;
                            String rAmount = null;
                            String recordCount = null;
                            String note = null;
                            String isCashOnDelivery =null;
                            String taskType =null;

                            try {
                                jsonObject = new JSONObject(response);
                                jsonArray = jsonObject.getJSONArray("result");
                                for(int i=0;i<jsonArray.length();i++) {
                                    JSONObject o = jsonArray.getJSONObject(i);
                                    tAmount = o.getString("totalAmount");
                                    rAmount = o.getString("receivedAmount");
                                    recordCount = o.getString("recordCount");
                                    note = o.getString("note");
                                    isCashOnDelivery = o.getString("isCashOnDelivery");
                                    taskType = o.getString("taskType");
                                    if (isCashOnDelivery.equals("0")) {
                                        edtCash.setEnabled(false);
                                        edtComments.setEnabled(false);
                                        btnSubmit.setVisibility(View.GONE);
                                        edtComments.setText("Collected");
                                        edtCash.setText("0.0");
                                        txtCOD.setText("No");
                                        txtCashInfo.setText("Total Amount");
                                    }
                                    else
                                    {
                                        txtCashInfo.setText("Total amount to be collected");
                                        txtCOD.setText("Yes");
                                    }
                                    if(rAmount.equals("null"))
                                    {
                                        System.out.println("Received Amount is Null");
                                    }
                                    else
                                    {
                                        edtCash.setEnabled(false);
                                        edtComments.setEnabled(false);
                                        btnSubmit.setVisibility(View.GONE);
                                        edtComments.setText(note);
                                        edtCash.setText(rAmount);
                                    }
                                    //   System.out.println(taskType+" TaskType");
                                }
                            } catch (JSONException e) {

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
                    params.put("updateCode", String.valueOf(AllConstant.checkOrderCompleteOrNot));
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);



    }

    private void setComponent() {
        txtOrderNumber.setText(orderNumber);
        txtOrderTask.setText(orderTaskName);
        txtClientName.setText(clientName);
        txtCash.setText(orderAmount);

    }

    private void getDataFromIntent() {

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            orderNumber = bundle.getString("orderNumber");
            orderTaskName = bundle.getString("OrderTaskType");
            clientName = bundle.getString("ClientName");
            clientNumber = bundle.getString("ClientNumber");
            orderAmount = bundle.getString("totalAmount");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnCashSubmit:
                saveCash();
        }
    }

    private void saveCash() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String res = response;
                        res = response.trim();
                        if(res.equals("CashNull"))
                        {
                            Toast.makeText(getActivity(),"Please Enter Cash",Toast.LENGTH_LONG).show();
                        }
                        if(res.equals("Success"))
                        {
                            Toast.makeText(getActivity(),"Payment update successfully",Toast.LENGTH_LONG).show();
                            edtCash.setEnabled(false);
                            edtComments.setEnabled(false);
                            btnSubmit.setEnabled(false);
                            btnSubmit.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(getActivity(), "No internet connection", duration).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }){
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("orderNumber",orderNumber);
                params.put("userName",userName);
                params.put("notes",edtComments.getText().toString().trim());
                params.put("cash",edtCash.getText().toString().trim());
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                TimeZone utc = TimeZone.getTimeZone("UTC");
                df.setTimeZone(utc);
                String date = df.format(Calendar.getInstance().getTime());
                params.put("date",date);
                params.put("updateCode", String.valueOf(AllConstant.completeOrderCash));
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}
