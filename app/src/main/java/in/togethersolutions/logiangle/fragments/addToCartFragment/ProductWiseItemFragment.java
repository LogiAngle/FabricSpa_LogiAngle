package in.togethersolutions.logiangle.fragments.addToCartFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.session.SessionManagement;

public class ProductWiseItemFragment extends Fragment implements View.OnClickListener {
    AutoCompleteTextView txtProductID;
    TextView txtProductDescription;
    TextView txtQuantity;
    TextView txtUOM;
    TextView txtUnitPrice,txtGST,txtTotalAmount;
    Button btnSave;
    String userName;
    JSONObject jsonObject,jObject;
    JSONArray jsonArray;
    float unitPrice,quantity,gst,totalAmount,totalAmountWithGST;
    String  orderNumber;



    public static ProductWiseItemFragment createInstance(String orderNumber)
    {
        ProductWiseItemFragment fragment = new ProductWiseItemFragment();
        Bundle args = new Bundle();
        args.putString("OrderNumber", orderNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.productwiseorderitem,container,false);
        orderNumber = getArguments().getString("OrderNumber");
        inItComponent(v);
        inItListener();
        loadProductID();
        return v;
    }

    private void inItComponent(View v) {
        userName= SessionManagement.getLoggedInUserName(getActivity());
        txtGST = (TextView)v.findViewById(R.id.txtGST);
        txtProductDescription = (TextView)v.findViewById(R.id.txtProductDescription);
        txtProductID = (AutoCompleteTextView)v.findViewById(R.id.txtProductID);
        txtUnitPrice = (TextView)v.findViewById(R.id.txtUnitPrice);
        txtQuantity = (TextView)v.findViewById(R.id.txtQuantity);
        txtTotalAmount = (TextView)v.findViewById(R.id.txtTotalAmount);
        txtUOM = (TextView)v.findViewById(R.id.txtUOM);
        txtTotalAmount.setTextColor(Color.BLACK);
        btnSave = (Button)v.findViewById(R.id.btnSave);
        txtQuantity.setEnabled(false);
        txtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String qty = s.toString();
                String unitP;
                String gstString;
                if(s.length() != 0){
                    quantity = Float.parseFloat(qty);
                    System.out.println("Quantiy = "+quantity);
                    unitP = txtUnitPrice.getText().toString();
                    unitPrice = Float.parseFloat(unitP);
                    gstString = txtGST.getText().toString();
                    gst = Float.parseFloat(gstString);
                    totalAmount = unitPrice*quantity;
                    totalAmountWithGST = totalAmount + (totalAmount*(gst/100));
                    txtTotalAmount.setText(String.valueOf(totalAmountWithGST));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void inItListener() {
        btnSave.setOnClickListener(this);
    }

    private void loadProductID() {
        final ArrayList<String> ar = new ArrayList<String>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.getProductID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Result="+response);

                        try {
                            jsonObject = new JSONObject(response);

                            jsonArray = jsonObject.getJSONArray("result");
                            System.out.println(response);
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                jObject = jsonArray.getJSONObject(i);
                                if(jObject.getBoolean("Status")){
                                    ar.add((String) jObject.get("ProductID"));
                                }
                                else
                                {
                                    System.out.println("No Product found");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String, String>();
                param.put("userName",userName);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.select_dialog_item, ar);
        txtProductID.setThreshold(1);
        txtProductID.setTextColor(Color.BLACK);
        txtProductID.setAdapter(adapter);
        txtProductID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(txtProductID.getText().toString());
                loadPartDetail(txtProductID.getText().toString());

            }
        });
    }

    private void loadPartDetail(final String productID) {

        StringRequest getProductDetail = new StringRequest(Request.Method.POST, AllURL.getProductDetail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonObject = new JSONObject(response);
                            jsonArray = jsonObject.getJSONArray("result");
                             System.out.println(response);
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                jObject = jsonArray.getJSONObject(i);
                                if(jObject.getBoolean("Status"))
                                {
                                    txtQuantity.setEnabled(true);
                                    unitPrice = jObject.getInt("UnitPrice");
                                    gst = jObject.getInt("GST");
                                    txtProductDescription.setTextColor(Color.BLACK);
                                    txtProductDescription.setText(jObject.getString("ProductName"));
                                    txtQuantity.setEnabled(true);
                                    txtUnitPrice.setText(String.valueOf(unitPrice));
                                    txtGST.setTextColor(Color.BLACK);
                                    txtGST.setText(String.valueOf(gst));
                                    txtUnitPrice.setTextColor(Color.BLACK);
                                    txtUOM.setText(jObject.getString("UOM"));
                                    txtUOM.setTextColor(Color.BLACK);

                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Please contact to master admin", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String, String>();
                param.put("ProductID",productID);
                param.put("OrderID",orderNumber);
                return param;
            }
        };
        RequestQueue requestQueue =  Volley.newRequestQueue(getContext());
        requestQueue.add(getProductDetail);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnSave:
                if(txtQuantity.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please select product and insert quantity", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    saveProductItem(orderNumber);

                }
        }
    }

    private void saveProductItem(final String orderNumber) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.saveProductItem,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response = "+response);
                        if(response.equals("SUCCESS"))
                        {
                            txtTotalAmount.setText("");
                            txtGST.setText("");
                            txtUnitPrice.setText("");
                            txtProductDescription.setText("");
                            txtProductID.setText("");
                            txtQuantity.setText("");
                            txtUOM.setText("");
                            Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            txtTotalAmount.setText("");
                            txtGST.setText("");
                            txtUnitPrice.setText("");
                            txtProductDescription.setText("");
                            txtProductID.setText("");
                            txtQuantity.setText("");
                            txtUOM.setText("");
                            Toast.makeText(getContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String, String>();
                param.put("OrderNumber",orderNumber);
                param.put("ProductID",txtProductID.getText().toString().trim());
                param.put("Quantity",txtQuantity.getText().toString().trim());
                param.put("TotalAmount",txtTotalAmount.getText().toString().trim());
                param.put("UserName",userName);
                param.put("CreateDateTime",getUTCDate());
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    private String getUTCDate(){
        String date;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        df.setTimeZone(utc);
        date = df.format(Calendar.getInstance().getTime());
        return date;
    }

}
