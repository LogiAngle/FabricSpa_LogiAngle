package in.togethersolutions.logiangle.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.WanderingCubes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.session.SessionManagement;

public class OrderInformation extends AppCompatActivity {
    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4;
    String orderNumber=null;
    String userName = null;
    String cash = null;
    String prodNo;
    private TextView textOrderNumber;
    private TableLayout tableLayout;
    private Uri fileUri;
    private Uri fileUri2;

    Bitmap bitmap;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private int PICK_IMAGE_REQUEST=1;
    public ImageView imageView1;
    public ImageView imageView2;
    public ImageView imageView3;
    public SignaturePad signaturePad;
    JSONArray data = null;
    Bitmap bitmap1;
    Bitmap bitmap3;
    public EditText textCash;
    public EditText textNotes;
    public Button btnCashSubmit;
    public Button btnImageSubmit;
    public String  totalPrice = null;
    public TextView textViewCash;
    public String toggle;
    public TextView textAddItem;
    private String date=null;
    String mCurrentPhotoPath;
    String ba1;
    Button buttonSubmit;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String SessionImage1 = "image1";
    public static final String SessionImage2 = "image2";

    String im1;
    String im2;

    ProgressBar progressBar;

    SharedPreferences sharedpreferences;
    static String  bitImageString1 =null;
    static String bitImageString2 = null;

    private PopupWindow pw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
        inItComponent();
        checkPODRecord();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        im2 = sharedpreferences.getString(SessionImage2,"");
        im1 = sharedpreferences.getString(SessionImage1,"");



        if(im1.length()!=0)
        {
            byte[] decodedString12 = Base64.decode(im1, Base64.DEFAULT);
            Bitmap decodedByteImage1 = BitmapFactory.decodeByteArray(decodedString12, 0, decodedString12.length);
            imageView1.setImageBitmap(decodedByteImage1);
        }
        if(im2.length()!=0)
        {
            byte[] decodedString = Base64.decode(im2, Base64.DEFAULT);
            Bitmap decodedByteImage1 = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView2.setImageBitmap(decodedByteImage1);
        }

    }

    private void checkPODRecord() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                                //   System.out.println(taskType+" TaskType");
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                        textViewCash.setText(tAmount);
                        //Toast.makeText(OrderInformation.this,isCashOnDelivery,Toast.LENGTH_SHORT).show();
                        System.out.println(response+" TaskType");
                        if(rAmount.equals("null"))
                        {
                            System.out.println("Received Amount is Null");
                        }
                        else
                        {

                            textCash.setEnabled(false);
                            textNotes.setEnabled(false);
                            textCash.setText(rAmount);
                            textNotes.setText(note);
                            btnCashSubmit.setVisibility(View.GONE);
                        }
                        if(taskType.equals("1"))
                        {
                            StringRequest strreq = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            System.out.println("Checkispresent "+response);
                                            if(response.trim().equals("1"))
                                            {
                                                textAddItem.setVisibility(View.GONE);
                                            }
                                            else if(response.trim().equals("0"))
                                            {
                                                textAddItem.setVisibility(View.VISIBLE);
                                                textAddItem.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        initiatePopupWindow(v);
                                                    }
                                                });
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }){
                                protected Map<String,String> getParams()
                                {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("orderNumber",orderNumber);
                                    params.put("updateCode", "15");
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(OrderInformation.this);
                            requestQueue.add(strreq);

                        }
                        else
                        {
                            textAddItem.setVisibility(View.GONE);
                        }
                        if(recordCount.equals("1"))
                        {
                            imageView1.setEnabled(false);
                            imageView2.setEnabled(false);
                            imageView3.setEnabled(false);
                            imageView3.setImageResource(R.drawable.complete);
                            imageView2.setImageResource(R.drawable.complete);
                            imageView1.setImageResource(R.drawable.complete);
                            btnImageSubmit.setEnabled(false);
                            btnImageSubmit.setVisibility(View.GONE);
                        }
                        if(isCashOnDelivery.equals("0"))
                        {
                            textCash.setEnabled(false);
                            textNotes.setEnabled(false);
                            textCash.setText("0.0");
                            textNotes.setText("Collected");
                            btnCashSubmit.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        RequestQueue requestQueue = Volley.newRequestQueue(OrderInformation.this);
        requestQueue.add(stringRequest);

    }

    private void initiatePopupWindow(View v) {

        final Dialog dialog = new Dialog(OrderInformation.this);
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
                    Toast.makeText(OrderInformation.this,"Please enter product details",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    StringRequest stringRequest1 = new StringRequest(Request.Method.POST, AllURL.updateURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    prodNo = response.toString().trim();

                                    TableLayout tl=(TableLayout)findViewById(R.id.tableLayout);
                                    final View tableRow;
                                    tableRow = LayoutInflater.from(OrderInformation.this).inflate(R.layout.table_item,null,false);
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
                                    tableLayout.addView(tableRow);
                                    tableRow.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View view) {
                                            TableRow tablerow = (TableRow) view;
                                            final TextView sample = (TextView) tablerow.getChildAt(1);
                                            final TextView description = (TextView) tablerow.getChildAt(2);
                                            final TextView uprice =(TextView) tablerow.getChildAt(3);
                                            final TextView qty12 = (TextView) tablerow.getChildAt(4);

                                            String itname = sample.getText().toString();
                                            String des = description.getText().toString();
                                            String up = uprice.getText().toString();
                                            String q1= qty12.getText().toString();
                                            final Dialog dialog = new Dialog(OrderInformation.this);
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
                                            prod_Name.setText(itname);
                                            prod_Description.setText(des);
                                            prod_Quantity.setText(q1);
                                            prod_UnitPrice.setText(up);
                                            /*buttonSave.setOnClickListener();*/
                                            buttonSave.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    Toast.makeText(OrderInformation.this,"Order update successfully",Toast.LENGTH_SHORT).show();
                                                                    sample.setText(prod_Name.getText().toString().trim());
                                                                    description.setText(prod_Description.getText().toString().trim());
                                                                    uprice.setText(prod_UnitPrice.getText().toString().trim());
                                                                    qty12.setText(prod_Quantity.getText().toString().trim());
                                                                }
                                                            }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(OrderInformation.this,"Something went wrong",Toast.LENGTH_SHORT).show();
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
                                                    RequestQueue requestQueue = Volley.newRequestQueue(OrderInformation.this);
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

                        }
                    }){
                        protected Map<String,String> getParams()
                        {
                            int ut = Integer.parseInt(unitPrice);
                            int qty= Integer.parseInt(quantity1);
                            int TotalAmount = ut*qty;

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
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(OrderInformation.this);
                    requestQueue.add(stringRequest1);
                    dialog.dismiss();

                }

            }
        });


        dialog.show();

    }
    /*Initialize components */
    private void inItComponent() {
        orderNumber = getIntent().getStringExtra("orderNumber");
        totalPrice = getIntent().getStringExtra("totalAmount");
        toggle = getIntent().getStringExtra("toggal1");
        textOrderNumber = (TextView) findViewById(R.id.textViewOrderNumber);
        textOrderNumber.setText(orderNumber);
        userName= SessionManagement.getLoggedInUserName(this);
        imageView1 = (ImageView)findViewById(R.id.imageView1);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        imageView3 = (ImageView)findViewById(R.id.imageView3);
        btnImageSubmit = (Button)findViewById(R.id.buttomImageAck);
        textCash = (EditText)findViewById(R.id.editTextPaidAmount);
        textNotes = (EditText)findViewById(R.id.editText);
        textViewCash =(TextView)findViewById(R.id.textViewCash);

        btnCashSubmit = (Button)findViewById(R.id.buttonSubmitCash);
        /*Back Button on Toolbar*/
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textAddItem = (TextView)findViewById(R.id.textAddItem);
        //buttonSubmit = (Button)findViewById(R.id.buttonSaveTableRecord);
    }

    public void expandableButton4(View view) {
        expandableLayout4 = (ExpandableRelativeLayout)findViewById(R.id.expandableLayout4);
        expandableLayout4.toggle();
    }

    public void expandableButton3(View view) {
        expandableLayout3 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout3);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        tableLayout.removeAllViews();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.orderInformation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObj = null;
                        try {
                            View tableRow;
                            tableRow = LayoutInflater.from(OrderInformation.this).inflate(R.layout.table_item,null,false);
                            TextView srno  = (TextView) tableRow.findViewById(R.id.history_display_no);
                            TextView productName  = (TextView) tableRow.findViewById(R.id.history_display_date);
                            TextView quantity = (TextView) tableRow.findViewById(R.id.history_display_orderid);
                            TextView amount  = (TextView) tableRow.findViewById(R.id.history_display_quantity);
                            srno.setText("Item Name");
                            productName.setText("Description");
                            quantity.setText("Unit Price");
                            amount.setText("Quantity");
                            tableLayout.addView(tableRow);
                            jsonObj = new JSONObject(response);
                            data = jsonObj.getJSONArray("result");
                            for(int i=0; i<data.length();i++)
                            {
                                JSONObject c = data.getJSONObject(i);
                                tableRow = LayoutInflater.from(OrderInformation.this).inflate(R.layout.table_item,null,false);
                                srno  = (TextView) tableRow.findViewById(R.id.history_display_no);
                                productName  = (TextView) tableRow.findViewById(R.id.history_display_date);
                                quantity = (TextView) tableRow.findViewById(R.id.history_display_orderid);
                                amount  = (TextView) tableRow.findViewById(R.id.history_display_quantity);
                                srno.setText("" +  c.getString("ProductName"));
                                productName.setText("" + c.getString("Description"));
                                quantity.setText("" + c.getString("TotalAmount"));
                                amount.setText("" + c.getString("Quantity"));
                                tableLayout.addView(tableRow);

                            }

                        } catch (JSONException e) {
                            System.out.println("Items Not Found");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            protected Map<String,String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("userName",userName);
                params.put("orderNumber",orderNumber);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(OrderInformation.this);
        requestQueue.add(stringRequest);
        expandableLayout3.toggle();
    }

    /*Method Proof Of Delivery*/

    public void expandableButton1(View view) {
        progressBar = (ProgressBar)findViewById(R.id.spin_kit2);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
        expandableLayout1 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);
        imageView1 = (ImageView)findViewById(R.id.imageView1);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        imageView3 = (ImageView)findViewById(R.id.imageView3);
        btnImageSubmit = (Button)findViewById(R.id.buttomImageAck);
        checkPODRecord();

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(R.id.imageView1);
                /*if (getApplicationContext().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA)) {
                    // Open default camera
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    // start the image capture Intent
                    startActivityForResult(intent, 100);

                } else {
                    Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
                }*/
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(R.id.imageView2);
               /* if (getApplicationContext().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA)) {
                    // Open default camera
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri2);

                    // start the image capture Intent
                    startActivityForResult(intent, 101);


                } else {
                    Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
                }*/
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrderInformation.this,Sign.class);
                intent.putExtra("orderNumber",orderNumber);
                startActivity(intent);

            }
        });
        final byte[] byteArray = getIntent().getByteArrayExtra("image");

        if(byteArray != null)
        {
            bitmap3 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            // bitmap = ((BitmapDrawable) imageView3.getDrawable()).getBitmap();
            imageView3.setImageBitmap(bitmap3);

        }
        btnImageSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                StringRequest stringRequest =  new StringRequest(Request.Method.POST, AllURL.updateURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("dfsfsffdsf"+response);
                                String res=response.trim();
                                progressBar.setVisibility(View.GONE);
                                if(res.equals("Success"))
                                {
                                    Toast.makeText(OrderInformation.this,"POD updated successfully",Toast.LENGTH_SHORT).show();

                                    btnImageSubmit.setEnabled(false);
                                    btnImageSubmit.setVisibility(View.GONE);
                                    imageView1.setEnabled(false);
                                    imageView2.setEnabled(false);
                                    imageView3.setEnabled(false);
                                    imageView3.setImageResource(R.drawable.complete);
                                    imageView2.setImageResource(R.drawable.complete);
                                    imageView1.setImageResource(R.drawable.complete);
                                    progressBar.setVisibility(View.GONE);
                                }
                                if (res.equals("dataalreadysaved"))
                                {
                                    Toast.makeText(OrderInformation.this,"POD updated successfully",Toast.LENGTH_SHORT).show();

                                    btnImageSubmit.setVisibility(View.GONE);
                                    btnImageSubmit.setEnabled(false);
                                    imageView1.setEnabled(false);
                                    imageView2.setEnabled(false);
                                    imageView3.setEnabled(false);
                                    imageView3.setImageResource(R.drawable.complete);
                                    imageView2.setImageResource(R.drawable.complete);
                                    imageView1.setImageResource(R.drawable.complete);
                                    progressBar.setVisibility(View.GONE);

                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            int duration = Toast.LENGTH_SHORT;

                            Toast.makeText(OrderInformation.this, "No internet connection", duration).show();
                        }

                        //   System.out.println(error);
                    }
                }){
                    protected Map<String,String> getParams()
                    {
                        String encodedImage =null;
                        Map<String,String>params = new HashMap<String, String>();
                        String image1 = getStringImage(bitmap);
                        String image2 = getStringImage1(bitmap1);
                        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        TimeZone utc = TimeZone.getTimeZone("UTC");
                        df.setTimeZone(utc);
                        final String date = df.format(Calendar.getInstance().getTime());
                        params.put("orderNumber",orderNumber);
                        params.put("image1",im1);
                        params.put("image2",im2);
                        params.put("image3", encodedImage);
                        params.put("date",date);
                        params.put("updateCode", String.valueOf(AllConstant.completeOrderImage));
                        return checkParams(params);
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                //request.setShouldCache(false)
                RequestQueue requestQueue = Volley.newRequestQueue(OrderInformation.this);
                requestQueue.add(stringRequest);

            }

            private Map<String,String> checkParams(Map<String, String> map) {
                Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
                    if(pairs.getValue()==null){
                        map.put(pairs.getKey(), "");
                    }
                }
                return map;
            }

            /****************Convert Image Into String*******************/
            private String getStringImage(Bitmap bitmap) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                String encodedImage =null;
                if(bitmap!=null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    return encodedImage;
                }
                return encodedImage;
            }
            private String getStringImage1(Bitmap bitmap) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                String encodedImage =null;
                if(bitmap!=null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    return encodedImage;
                }
                return encodedImage;
            }
        });


        expandableLayout1.toggle();
        // toggle expand and collapse
    }



    /**********************Get Image From camera to Image View************************/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            setPic(1);

            /*fileUri = data.getData();
            bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteFormat = stream.toByteArray();
            bitImageString1 = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
            imageView1.setImageBitmap(bitmap);
            imagSession(bitImageString1);*/

        }
        if (requestCode == 101 && resultCode == RESULT_OK) {
            setPic(2);
           /* fileUri2 = data.getData();
            bitmap1 = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteFormat = stream.toByteArray();
            bitImageString2 = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
            imageView2.setImageBitmap(bitmap1);
            imagSession1(bitImageString2);*/
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPic(int i) {
        // Get the dimensions of the View
        int targetW = imageView1.getWidth();
        int targetH = imageView1.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        if(i==1)
        {
            imageView1.setImageBitmap(bitmap);
            Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            ba1 = Base64.encodeToString(ba,Base64.DEFAULT);
            imagSession(ba1);
        }
        else if( i==2)
        {
            imageView2.setImageBitmap(bitmap);
            Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            ba1 = Base64.encodeToString(ba,Base64.DEFAULT);
            imagSession1(ba1);
        }
    }


    private void captureImage(int imageView1) {
        if(imageView1 == R.id.imageView1)
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile(1);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, 100);
                }
            }
        }
        if(imageView1 == R.id.imageView2)
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile(2);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, 101);
                }
            }
        }
    }

    private File createImageFile(int i) throws IOException {
        // Create an image file name
        String imageFileName = orderNumber+"_Image"+i;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



    private void imagSession1(String bitImageString2) {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SessionImage2,bitImageString2);
        editor.apply();
        im2 = sharedpreferences.getString(SessionImage2,"");
    }

    private void imagSession(String bitImageString1) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SessionImage1,bitImageString1);
        editor.apply();
        im1 = sharedpreferences.getString(SessionImage1,"");
    }

    public void expandableButton2(View view) {
        progressBar = (ProgressBar)findViewById(R.id.spin_kit3);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
        expandableLayout2 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout2);
        btnCashSubmit = (Button)findViewById(R.id.buttonSubmitCash);
        btnCashSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                String res = response;
                                res = response.trim();
                                // Toast.makeText(OrderInformation.this,response,Toast.LENGTH_LONG).show();
                                if(res.equals("CashNull"))
                                {
                                    Toast.makeText(OrderInformation.this,"Please Enter Cash",Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                                if(res.equals("Success"))
                                {
                                    Toast.makeText(OrderInformation.this,"Payment update successfully",Toast.LENGTH_LONG).show();
                                    textCash.setEnabled(false);
                                    textNotes.setEnabled(false);
                                    btnCashSubmit.setEnabled(false);
                                    btnCashSubmit.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  progressDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            int duration = Toast.LENGTH_SHORT;

                            Toast.makeText(OrderInformation.this, "No internet connection", duration).show();
                        }

                    }
                }){
                    protected Map<String,String> getParams()
                    {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("orderNumber",orderNumber);
                        params.put("userName",userName);
                        params.put("notes",textNotes.getText().toString().trim());
                        params.put("cash",textCash.getText().toString().trim());
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
                RequestQueue requestQueue = Volley.newRequestQueue(OrderInformation.this);
                requestQueue.add(stringRequest);
            }
        });
        expandableLayout2.toggle();
    }

    @Override
    public void onBackPressed() {
        //sharedpreferences = getPreferences(0);
        Intent setIntent = new Intent(this,MainActivity.class);
        startActivity(setIntent);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            Intent setIntent = new Intent(this,MainActivity.class);
            startActivity(setIntent);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
