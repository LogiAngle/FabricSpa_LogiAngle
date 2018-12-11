package in.togethersolutions.logiangle.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;

public class PasswordChange extends  AppCompatActivity {

    private Button btnPasswordChange;
    private EditText editTextCurrentPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;
    String currentPassword = null;
    String newPassword = null;
    String confirmPasword = null;
    String userName = null;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change1);
        inItComponent();
        inItListner();
    }
    private void inItComponent() {
        editTextCurrentPassword = (EditText) findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = (EditText) findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        btnPasswordChange = (Button) findViewById(R.id.buttonPasswordChange);
        userName = getIntent().getStringExtra("userName");
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
    }
    private void inItListner()
    {
        btnPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                // Toast.makeText(PasswordChange.this,response,Toast.LENGTH_LONG).show();
                                String res = response.trim();
                                if(res.equals("Success"))
                                {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(PasswordChange.this,"Password has been changed successfully",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(PasswordChange.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                                if(res.equals("Failure"))
                                {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(PasswordChange.this,"Current password is wrong",Toast.LENGTH_LONG).show();
                                }
                                if (res.equals("PasswordNotMatch"))
                                {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(PasswordChange.this,"Password not match",Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
                {
                    protected Map<String, String> getParams()
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String date = df.format(Calendar.getInstance().getTime());
                        currentPassword = editTextCurrentPassword.getText().toString().trim();
                        newPassword = editTextNewPassword.getText().toString().trim();
                        confirmPasword = editTextConfirmPassword.getText().toString().trim();
                        params.put("currentPassword",currentPassword);
                        params.put("newPassword",newPassword);
                        params.put("confirmPassword",confirmPasword);
                        params.put("currentDate",date);
                        params.put("updateCode", String.valueOf(AllConstant.passwordChange));
                        params.put("userName",userName);
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                //request.setShouldCache(false)
                RequestQueue requestQueue = Volley.newRequestQueue(PasswordChange.this);
                requestQueue.add(stringRequest);
            }
        });
    }
}
