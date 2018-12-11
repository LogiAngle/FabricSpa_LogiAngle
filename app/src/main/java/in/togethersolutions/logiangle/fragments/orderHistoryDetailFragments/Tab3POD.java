package in.togethersolutions.logiangle.fragments.orderHistoryDetailFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.adapters.PODImageAdapter;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.modals.ListPODImages;
import in.togethersolutions.logiangle.session.SessionManagement;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Tab3POD extends Fragment implements RecyclerView.OnScrollChangeListener{

    private String orderNumber;
    private String userName;
    View rootView ;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    ProgressBar progressBar;
    //Creating a List of ListPODImage
    List<ListPODImages> listPODImages;
    boolean isLoggedIn[]={true};
    //Volley Request Queue
    private RequestQueue requestQueue;

    //The request counter to send ?page=1, ?page=2  requests
    private int requestCount = 1;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab3_pod, container, false);
        inItComponent();
        return rootView;
    }

    @SuppressLint("NewApi")
    private void inItComponent() {
        //Initializing Views
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewPOD);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        //Initializing our ListPODImages list
        listPODImages = new ArrayList<>();

        //Calling method to get data to fetch data
        if(checkRiderLogin(userName)) {
            getDataFromServer();
        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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
            alertDialog.show();
        }
        //Adding an scroll change listener to recyclerview
        recyclerView.setOnScrollChangeListener(this);

        //initializing our adapter
        adapter =new PODImageAdapter(listPODImages,rootView.getContext());

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

    }
    private void getDataFromServer() {
        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.podImage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("PODImage  "+response);
                        parseData(response);
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                System.out.println(error);
            }
        }){
            protected Map<String,String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                params.put("orderNumber",orderNumber);
                params.put("baseURL", AllURL.BaseURL);
                return params;
            }
        };
        //request.setShouldCache(false)
        RequestQueue requestQueue = Volley.newRequestQueue(rootView.getContext());
        requestQueue.add(stringRequest);
    }

    private void parseData(String data) {

        JSONObject jsonObj = null;
        JSONArray response =null;
        try {
            jsonObj = new JSONObject(data);
            response = jsonObj.getJSONArray("result");
            for (int i = 0; i < response.length(); i++) {
                ListPODImages lpi = new ListPODImages();
                JSONObject json = null;
                try
                {
                    json = response.getJSONObject(i);
                    lpi.setImageURL(json.getString("Image"));
                    lpi.setImageName(json.getString("Name"));

                }
                catch (Exception e)
                {
                    Toast.makeText(rootView.getContext(),"Opps!!! Something went wrong",Toast.LENGTH_LONG).show();
                }
                listPODImages.add(lpi);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

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
                param.put("loginSessionID",SessionManagement.getLoggedInSessionID(getActivity()));
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        return isLoggedIn[0];
    }
}
