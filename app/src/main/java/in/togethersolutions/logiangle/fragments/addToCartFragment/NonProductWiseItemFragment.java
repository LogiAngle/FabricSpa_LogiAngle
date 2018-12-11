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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.session.SessionManagement;

public class NonProductWiseItemFragment extends Fragment implements View.OnClickListener {

    TextView txtProductID;
    TextView txtProductDescription;
    TextView txtQuantity;
    TextView txtUnitPrice,txtGST,txtTotalAmount;
    Button btnSave;
    String userName;
    float unitPrice,quantity,gst,totalAmount,totalAmountWithGST;
    String productID,productDescription,quantityString,gstString,unitPriceString;
    String orderNumber;

    public static NonProductWiseItemFragment createInstance(String orderNumber)
    {
        NonProductWiseItemFragment nonProductWiseItemFragment = new NonProductWiseItemFragment();
        Bundle args = new Bundle();
        args.putString("OrderNumber", orderNumber);
        nonProductWiseItemFragment.setArguments(args);
        return nonProductWiseItemFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.non_productwise_item,container,false);
        orderNumber = getArguments().getString("OrderNumber");
        initComponent(v);
        initListener();
        return v;
    }

    private void initComponent(View v) {
        userName= SessionManagement.getLoggedInUserName(getActivity());
        txtGST = (TextView)v.findViewById(R.id.txtGST);
        txtProductDescription = (TextView)v.findViewById(R.id.txtProductDescription);
        txtProductID = (TextView)v.findViewById(R.id.txtProductName);
        txtUnitPrice = (TextView)v.findViewById(R.id.txtUnitPrice);
        txtQuantity = (TextView)v.findViewById(R.id.txtQuantity);
        txtTotalAmount = (TextView)v.findViewById(R.id.txtTotalAmount);
        txtTotalAmount.setTextColor(Color.BLACK);
        btnSave = (Button)v.findViewById(R.id.btnSave);
    }

    private void initListener() {
        btnSave.setOnClickListener(this);
        quantityString = txtQuantity.getText().toString();
        unitPriceString = txtUnitPrice.getText().toString();
        if(quantityString != null && unitPriceString != null) {
            txtGST.setEnabled(true);
            txtGST.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String gst1 = s.toString();
                    if (s.length() != 0) {
                        quantityString = txtQuantity.getText().toString();
                        gstString = txtGST.getText().toString();
                        unitPriceString = txtUnitPrice.getText().toString();
                        if (quantityString.equals("")&& unitPriceString.equals("")) {

                            Toast.makeText(getContext(), "Please enter quantity or unit price", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            System.out.println("Quantity11" + quantityString);
                            quantity = Float.parseFloat(quantityString);
                            gst = Float.parseFloat(gst1);
                            unitPrice = Float.parseFloat(unitPriceString);
                            totalAmount = quantity * unitPrice;
                            totalAmountWithGST = totalAmount + (totalAmount * (gst / 100));
                            txtTotalAmount.setText(String.valueOf(totalAmountWithGST));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                if(txtGST.getText().toString().equals("") || txtQuantity.getText().toString().equals("") || txtUnitPrice.getText().toString().equals("") || txtProductID.getText().toString().equals(""))
                {

                    Toast.makeText(getContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    saveProduct();
                }

        }
    }

    private void saveProduct() {
        productID = txtProductID.getText().toString();
        productDescription = txtProductDescription.getText().toString();
        quantityString = txtQuantity.getText().toString();
        gstString = txtGST.getText().toString();
        unitPriceString = txtUnitPrice.getText().toString();
        quantity  = Float.parseFloat(quantityString);
        gst = Float.parseFloat(gstString);
        unitPrice = Float.parseFloat(unitPriceString);
        totalAmount = quantity*unitPrice;
        totalAmountWithGST = totalAmount+(totalAmount*(gst/100));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.saveNonProductItem,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("SUCCESS"))
                        {
                            txtProductID.setText("");
                            txtQuantity.setText("");
                            txtProductDescription.setText("");
                            txtUnitPrice.setText("");
                            txtGST.setText("");
                            txtTotalAmount.setText("");
                            Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.equals("FAIL"))
                        {
                            txtProductID.setText("");
                            txtQuantity.setText("");
                            txtProductDescription.setText("");
                            txtUnitPrice.setText("");
                            txtGST.setText("");
                            txtTotalAmount.setText("");
                            Toast.makeText(getContext(), "Oops something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("OrderNumber",orderNumber);
                params.put("ProductID",productID);
                params.put("ProductDescription",productDescription);
                params.put("Quantity",quantityString);
                params.put("unitPrice",unitPriceString);
                params.put("TotalAmount",String.valueOf(totalAmountWithGST));
                params.put("GST",gstString);
                params.put("CreateDateTime",getUTCDate());
                return params;
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
