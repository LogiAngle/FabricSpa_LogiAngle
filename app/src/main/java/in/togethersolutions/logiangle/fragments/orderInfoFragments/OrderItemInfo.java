package in.togethersolutions.logiangle.fragments.orderInfoFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.fragments.addToCartFragment.TabbedDialog;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.session.SessionManagement;

public class OrderItemInfo extends Fragment implements View.OnClickListener {

    View rootView;

    TextView txtOrderNumber;
    TextView txtClientName;
    TextView txtOrderTask;
    TextView txtAddItem;
    TextView txtClientNumber;

    TableLayout tblItemDetail;

    LinearLayout txtOrderItemLayout;

    ProgressBar progressBar;

    String orderNumber,orderTaskName,clientName,clientNumber,taskTypeID,userName;
    String prodNo;

    JSONObject jsonObject,jObject;
    JSONArray jsonArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.order_item_detail,container,false);
        inItComponent(rootView);
        inItListener(rootView);
        getDataFromIntent();
        loadOrderDetailRecord();
        setComponent();
        return rootView;
    }


    private void getDataFromIntent() {

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            orderNumber = bundle.getString("orderNumber");
            orderTaskName = bundle.getString("OrderTaskType");
            clientName = bundle.getString("ClientName");
            clientNumber = bundle.getString("ClientNumber");
            taskTypeID = bundle.getString("taskTypeID");


        }
    }

    private void inItComponent(View rootView) {
        userName = SessionManagement.getLoggedInUserName(getActivity());
        txtOrderNumber = (TextView)rootView.findViewById(R.id.txtOrderNumber);
        txtOrderTask = (TextView)rootView.findViewById(R.id.txtTaskType);
        txtClientName = (TextView)rootView.findViewById(R.id.txtClientName);
        txtClientNumber = (TextView)rootView.findViewById(R.id.txtClientMobileNumber);
        txtAddItem = (TextView)rootView.findViewById(R.id.txtAddItem);
        tblItemDetail = (TableLayout)rootView.findViewById(R.id.tblItemDetail);
        txtOrderItemLayout = (LinearLayout)rootView.findViewById(R.id.txtOrderItemLayout);
    /*    progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);*/
    }



    private void inItListener(View rootView) {
        txtAddItem.setOnClickListener(this);
    }

    private void loadOrderDetailRecord() {
        StringRequest stringRequestLoadOrderItem = new StringRequest(Request.Method.POST, AllURL.orderInformation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonObject = new JSONObject(response);
                            jsonArray = jsonObject.getJSONArray("result");
                            for(int i = 0;i< jsonArray.length();i++)
                            {
                                jObject = jsonArray.getJSONObject(i);
                                if(jObject.getBoolean("Status"))
                                {
                                    txtAddItem.setVisibility(View.GONE);
                                    txtOrderItemLayout.setVisibility(View.GONE);
                                    View tableRow;
                                    tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_item,null,false);
                                    TextView srno  = (TextView) tableRow.findViewById(R.id.history_display_no);
                                    TextView productName  = (TextView) tableRow.findViewById(R.id.history_display_date);
                                    TextView quantity = (TextView) tableRow.findViewById(R.id.history_display_orderid);
                                    TextView amount  = (TextView) tableRow.findViewById(R.id.history_display_quantity);
                                    srno.setText("" +  jObject.getString("ProductName"));
                                    productName.setText("" + jObject.getString("Description"));
                                    quantity.setText("" + jObject.getString("TotalAmount"));
                                    amount.setText("" + jObject.getString("Quantity"));
                                    tblItemDetail.addView(tableRow);
                                }
                                //show plus symbol
                                else
                                {
                                    if(orderTaskName.equals("Pickup"))
                                    {
                                        txtAddItem.setVisibility(View.VISIBLE);
                                        txtAddItem.setVisibility(View.VISIBLE);
                                        txtOrderItemLayout.setVisibility(View.VISIBLE);

                                    }
                                    else
                                    {
                                        txtAddItem.setVisibility(View.GONE);
                                        txtOrderItemLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        catch(Exception e)
                        {
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
                param.put("userName",userName);
                param.put("orderNumber",orderNumber);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequestLoadOrderItem);
    }

    private void setComponent() {
        txtOrderNumber.setText(orderNumber);
        txtOrderTask.setText(orderTaskName);
        txtClientName.setText(clientName);
        txtClientNumber.setText(clientNumber);
    }


    //set on click listener methods
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // on click order add item icon
            case R.id.txtAddItem :
                //addOrderItem();
                //initiatePopupWindow(v);
                initiateNewPopupWindow(v);
        }
    }

    private void initiateNewPopupWindow(View v) {
      /*  final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.popup_add_to_cart);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();*/

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        TabbedDialog dialogFragment = new TabbedDialog();
        Bundle bundle = new Bundle();
        bundle.putString("OrderNumber", orderNumber);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(ft,"dialog");

    }

    private void addOrderItem() {
    }


    private void initiatePopupWindow(View v) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.popup_add_item);
        dialog.setTitle("Add Items");
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText prod_Name,prod_Description,prod_UnitPrice,prod_Quantity,prod_TotalAmount;
        Button buttonSave,buttonClear;
        prod_Name = (EditText)dialog.findViewById(R.id.textProductName);
        prod_Description = (EditText)dialog.findViewById(R.id.textProductDescription);
        prod_UnitPrice = (EditText)dialog.findViewById(R.id.textProductUnitPrice);
        prod_Quantity = (EditText)dialog.findViewById(R.id.textProductQuantity);
        buttonClear =(Button)dialog.findViewById(R.id.clearButton);
        buttonSave = (Button)dialog.findViewById(R.id.saveButton);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prod_Name.getText().clear();
                prod_Description.getText().clear();
                prod_UnitPrice.getText().clear();
                prod_Quantity.getText().clear();

            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = prod_Name.getText().toString().trim();
                final String description = prod_Description.getText().toString().trim();
                final String unitPrice = prod_UnitPrice.getText().toString().trim();
                final String quantity1 = prod_Quantity.getText().toString().trim();
                if(name.equals("") || description.equals("") || unitPrice.equals("") || quantity1.equals(""))
                {
                    Toast.makeText(getActivity(),"Please enter product details",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    StringRequest stringRequest1 = new StringRequest(Request.Method.POST, AllURL.updateURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                                    prodNo = response.toString().trim();
                                    //txtAddItem.setVisibility(View.INVISIBLE);
                                    //txtAddItem.setVisibility(View.INVISIBLE);
                                    txtOrderItemLayout.setVisibility(View.GONE);

                                    final View tableRow;
                                    tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_item,null,false);
                                    TextView srno  = (TextView) tableRow.findViewById(R.id.history_display_no);
                                    TextView productName  = (TextView) tableRow.findViewById(R.id.history_display_date);
                                    TextView quantity = (TextView) tableRow.findViewById(R.id.history_display_orderid);
                                    TextView amount  = (TextView) tableRow.findViewById(R.id.history_display_quantity);
                                    final TextView prodID = (TextView)tableRow.findViewById(R.id.history_prod_id);
                                    srno.setText(""+name);
                                    productName.setText(""+description);
                                    quantity.setText(""+unitPrice);
                                    amount.setText(""+quantity1);
                                    prodID.setText(""+response);
                                    tblItemDetail.addView(tableRow);
                                    tableRow.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View view) {
                                            TableRow tablerow = (TableRow) view;
                                            final TextView sample = (TextView) tablerow.getChildAt(1);
                                            final TextView description = (TextView) tablerow.getChildAt(2);
                                            final TextView uprice =(TextView) tablerow.getChildAt(3);
                                            final TextView qty12 = (TextView) tablerow.getChildAt(4);

                                            final String itname = sample.getText().toString();
                                            final String des = description.getText().toString();
                                            final String up = uprice.getText().toString();
                                            final String q1= qty12.getText().toString();
                                            final Dialog dialog = new Dialog(getActivity());
                                            dialog.setContentView(R.layout.popup_add_item);
                                            dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            final EditText prod_Name,prod_Description,prod_UnitPrice,prod_Quantity,prod_TotalAmount;
                                            Button buttonSave=null,buttonClear=null;
                                            prod_Name = (EditText)dialog.findViewById(R.id.textProductName);
                                            prod_Description = (EditText)dialog.findViewById(R.id.textProductDescription);
                                            prod_UnitPrice = (EditText)dialog.findViewById(R.id.textProductUnitPrice);
                                            prod_Quantity = (EditText)dialog.findViewById(R.id.textProductQuantity);
                                            buttonClear =  (Button)dialog.findViewById(R.id.clearButton);
                                            buttonSave = (Button)dialog.findViewById(R.id.saveButton);

                                            /*buttonSave.setOnClickListener();*/
                                            buttonSave.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                   // progressBar.setVisibility(View.VISIBLE);
                                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    prod_Name.setText(itname);
                                                                    prod_Description.setText(des);
                                                                    prod_Quantity.setText(q1);
                                                                    prod_UnitPrice.setText(up);
                                                                  //  progressBar.setVisibility(View.GONE);
                                                                    Toast.makeText(getActivity(),"Order update successfully",Toast.LENGTH_SHORT).show();
                                                                    sample.setText(prod_Name.getText().toString().trim());
                                                                    description.setText(prod_Description.getText().toString().trim());
                                                                    uprice.setText(prod_UnitPrice.getText().toString().trim());
                                                                    qty12.setText(prod_Quantity.getText().toString().trim());
                                                                }
                                                            }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                         //   progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }){
                                                        protected Map<String,String> getParams()
                                                        {
                                                            Map<String, String> params = new HashMap<>();
                                                            params.put("productID",prodNo);
                                                            params.put("prodName",prod_Name.getText().toString().trim());
                                                            params.put("prodDesc",prod_Description.getText().toString().trim());
                                                            params.put("prodQty",prod_Quantity.getText().toString().trim());
                                                            params.put("produnitprice",prod_UnitPrice.getText().toString().trim());
                                                            params.put("updateCode", String.valueOf(AllConstant.prodUpdate));
                                                            return params;
                                                        }
                                                    };
                                                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                                                    requestQueue.add(stringRequest);
                                                    dialog.dismiss();

                                                }
                                            });

                                            dialog.show();



                                        }
                                    });

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error.toString());
                        }
                    }){
                        protected Map<String,String> getParams()
                        {
                            float ut = Float.parseFloat(unitPrice);
                            float qty= Float.parseFloat(quantity1);
                            float TotalAmount = ut*qty;
                            String date;
                            Map<String, String> params = new HashMap<>();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            TimeZone utc = TimeZone.getTimeZone("UTC");
                            df.setTimeZone(utc);
                            date = df.format(Calendar.getInstance().getTime());
                            params.put("orderNumber",orderNumber);
                            params.put("name",name);
                            params.put("description",description);
                            params.put("unitprice",unitPrice);
                            params.put("quantity",quantity1);
                            params.put("totalamount", String.valueOf(TotalAmount));
                            params.put("updateCode", "16");
                            params.put("date1",date);


                           System.out.println(orderNumber+" "+name+" "+description+" "+unitPrice+" "+quantity1+" "+TotalAmount);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    requestQueue.add(stringRequest1);
                    dialog.dismiss();

                }

            }
        });


        dialog.show();

    }
}
