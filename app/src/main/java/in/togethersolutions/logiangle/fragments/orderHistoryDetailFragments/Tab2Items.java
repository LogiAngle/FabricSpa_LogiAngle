package in.togethersolutions.logiangle.fragments.orderHistoryDetailFragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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

import java.util.HashMap;
import java.util.Map;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.javaClass.AllURL;

public class Tab2Items extends Fragment {
    private String orderNumber;
    private String userName;
    JSONArray data = null;
    private TableLayout tableLayout;
    ProgressBar progressBar;
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab2_items, container, false);

        tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);
        tableLayout.removeAllViews();
        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.orderInformation,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //   Toast.makeText(rootView.getContext(),response,Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObj = null;
                        try {
                            View tableRow;
                            //tableRow = LayoutInflater.from(OrderInformation.this).inflate(R.layout.table_item,null,false);
                            tableRow = LayoutInflater.from(rootView.getContext()).inflate(R.layout.tab2_table_items,null,false);
                            TextView srno  = (TextView) tableRow.findViewById(R.id.history_display_no);
                            TextView productName  = (TextView) tableRow.findViewById(R.id.history_display_date);
                            TextView quantity = (TextView) tableRow.findViewById(R.id.history_display_orderid);
                            TextView amount  = (TextView) tableRow.findViewById(R.id.history_display_quantity);
                            srno.setText("Sr.No");
                            srno.setTypeface(null, Typeface.BOLD);
                            productName.setText("Item Name");
                            productName.setTypeface(null,Typeface.BOLD);
                            quantity.setText("Quantity");
                            quantity.setTypeface(null,Typeface.BOLD);
                            amount.setText("Amount");
                            amount.setTypeface(null,Typeface.BOLD);
                            tableLayout.addView(tableRow);
                            jsonObj = new JSONObject(response);
                            data = jsonObj.getJSONArray("result");
                            for(int i=0; i<data.length();i++)
                            {
                                JSONObject c = data.getJSONObject(i);
                                tableRow = LayoutInflater.from(rootView.getContext()).inflate(R.layout.table_item,null,false);
                                srno  = (TextView) tableRow.findViewById(R.id.history_display_no);
                                productName  = (TextView) tableRow.findViewById(R.id.history_display_date);
                                quantity = (TextView) tableRow.findViewById(R.id.history_display_orderid);
                                amount  = (TextView) tableRow.findViewById(R.id.history_display_quantity);
                                srno.setText("" + (i+1));
                                productName.setText("" + c.getString("ProductName"));
                                quantity.setText("" + c.getString("Quantity"));
                                amount.setText("" + c.getString("TotalAmount"));
                                tableLayout.addView(tableRow);

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
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //request.setShouldCache(false)
        RequestQueue requestQueue = Volley.newRequestQueue(rootView.getContext());
        requestQueue.add(stringRequest);
        return rootView;
    }
}