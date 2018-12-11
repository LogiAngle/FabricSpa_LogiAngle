package in.togethersolutions.logiangle.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaCas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.*;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.services.GPSTracker;
import in.togethersolutions.logiangle.services.LocationService;
import in.togethersolutions.logiangle.services.NotificationService;
import in.togethersolutions.logiangle.session.SessionManagement;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class Reschedule extends AppCompatActivity implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public TextView textOrderNumber;
    public TextView textTaskType;
    public TextView textClientName;
    public TextView textScheduleDateTime;
    public TextView textClientMobileNumber;
    public EditText textDate;
    public EditText textTime;
    public EditText textComment;
    public TextView txtCalendarDate;
    public TextView txtCalendarTime;
    public Button btnSave;
    public int mYear;
    public int mMonth;
    public int mDay;
    public String orderNumber;
    public String pickupClientName;
    public String deliveryClientName;
    public String pickDateTime;
    public String deliveryDateTime;
    public String pickupClientMobileNUmber;
    public String deliveryClientMobileNumber;
    public String TaskTypeID;
    public String tType;
    public String currentDate;
    private Date rescheduledDate;
    private Date currentResDate;
    String dateconveted;
    ProgressBar progressBar;
    MaterialBetterSpinner spinnerReason;
    String userName;
    boolean isLoggedIn[] ={true};
    String cancelNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule);
        inItComponent();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);
        userName = SessionManagement.getLoggedInUserName(Reschedule.this);
    }



    private void inItComponent() {
        orderNumber = getIntent().getStringExtra("orderNumber");
        pickupClientName = getIntent().getStringExtra("pClientName");
        pickDateTime = getIntent().getStringExtra("pDateTime");
        pickupClientMobileNUmber = getIntent().getStringExtra("pClientMobile");
        TaskTypeID = getIntent().getStringExtra("taskType");
        tType = getIntent().getStringExtra("TaskTypeID");
        spinnerReason = (MaterialBetterSpinner )findViewById(R.id.spinnerReason);
        textOrderNumber = (TextView)findViewById(R.id.textViewOrderNumber);
        txtCalendarDate= (TextView)findViewById(R.id.txtCalenderDate);
        txtCalendarTime= (TextView)findViewById(R.id.txtCalenderTime);
        textTaskType = (TextView)findViewById(R.id.textViewTaskType);
        textClientName = (TextView)findViewById(R.id.textViewClientName);
        textScheduleDateTime = (TextView)findViewById(R.id.textViewScheduleDateTime);
        textClientMobileNumber =(TextView)findViewById(R.id.textViewClientMobileNumber);
        textDate = (EditText)findViewById(R.id.textViewDate);
        textTime = (EditText)findViewById(R.id.textViewTime);
        textComment = (EditText)findViewById(R.id.textComment);
        btnSave = (Button)findViewById(R.id.saveButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Reschedule.this, android.R.layout.simple_spinner_item,
               getResources().getStringArray(R.array.cancle_reason));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReason.setAdapter(adapter);
        spinnerReason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerReason.getText().toString().trim().equals("Other"))
                {
                    textComment.setVisibility(View.VISIBLE);
                   // cancelNotes = spinnerReason.getText().toString().trim();
                    System.out.println("Rescheduled other notes="+cancelNotes);
                }
                else{
                    textComment.setVisibility(View.GONE);
                    //cancelNotes = spinnerReason.getText().toString().trim();
                }
            }
        });
      //  Toast.makeText(this, ""+pickDateTime, Toast.LENGTH_SHORT).show();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkRiderLogin(userName)) {
                    loadData();
                }
                else
                {
                   /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(Reschedule.this);
                    //setting dialog title
                    alertDialog.setTitle("Logout");
                    //setting dialog message
                    alertDialog.setCancelable(false);
                    alertDialog.setIcon(R.drawable.logout);

                    alertDialog.setMessage("You have been logged out\n" +
                            "Please try again or contact Administrator ");

                    // On pressing Settings button
                    alertDialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SessionManagement.clearLoggedInEmailAddress(Reschedule.this);
                            Intent intent = new Intent(Reschedule.this,LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    alertDialog.show();*/
                }

            }
        });

        txtCalendarDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                java.util.Calendar now = java.util.Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog datepickerdialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(Reschedule.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                //  datepickerdialog.setThemeDark(true); //set dark them for dialog?
                datepickerdialog.vibrate(true); //vibrate on choosing date?
                datepickerdialog.dismissOnPause(true); //dismiss dialog when onPause() called?
                datepickerdialog.showYearPickerFirst(false); //choose year first?
                datepickerdialog.setAccentColor(Color.parseColor("#1E73BE")); // custom accent color
                datepickerdialog.setTitle("Please select a date"); //dialog title
                datepickerdialog.show(getFragmentManager(), "Datepickerdialog"); //show dialog
            }
        });
        txtCalendarTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        Reschedule.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        now.get(Calendar.AM_PM),
                        false
                );
                tpd.is24HourMode();
                tpd.vibrate(true); //vibrate on choosing date?
                tpd.dismissOnPause(true);
                tpd.enableSeconds(true);
                tpd.setAccentColor(Color.parseColor("#1E73BE"));
                tpd.setTitle("Please select a time"); //dialog title
                tpd.show(getFragmentManager(), "Datepickerdialog"); //show dialog

            }
        });
        textOrderNumber.setText(orderNumber);
        textTaskType.setText(TaskTypeID);
        textClientMobileNumber.setText(pickupClientMobileNUmber);
        textClientName.setText(pickupClientName);
        textScheduleDateTime.setText(pickDateTime);
    }

    private String getConvertDateTime(String pickDateTime) {
        String formattedDate=null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(pickDateTime);
            df.setTimeZone(TimeZone.getDefault());
            formattedDate = df.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Current UTC=>"+formattedDate);
        return formattedDate;
    }

    private void loadData() {
        /*Toast.makeText(Reschedule.this,"Click Reschdule",Toast.LENGTH_LONG).show();*/
        if(spinnerReason.getText().toString().equals("Other"))
        {
            cancelNotes = textComment.getText().toString();
        }
        else
        {
            cancelNotes = spinnerReason.getText().toString();
        }
        if(cancelNotes.equals("")||cancelNotes.equals(null))
        {
            Toast.makeText(this, "Please enter notes", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String date=null;
            String time=null;
            String datetime;
            date = textDate.getText().toString().trim();
            time = textTime.getText().toString().trim();
            if(date.equals("")&&time.equals("")) {
                Toast.makeText(this, "Enter Date and Time ", Toast.LENGTH_SHORT).show();
            }
            else if(date.equals(""))
            {
                Toast.makeText(this, "Enter Date", Toast.LENGTH_SHORT).show();
            }
            else if(time.equals(""))
            {
                Toast.makeText(this, "Enter Time ", Toast.LENGTH_SHORT).show();
            }
            else
            {
                datetime = convertDateDDMMYYYToYYYYMMDD(date) + " " + time;
                Date d =null;
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    d=df.parse(datetime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TimeZone utc = TimeZone.getTimeZone("UTC");
                df.setTimeZone(utc);
                dateconveted = df.format(d);

                currentDate = getCurrentUTCDate();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {

                    rescheduledDate = formatter.parse(datetime);
                    currentResDate = formatter.parse(getLocalTime());
                    System.out.println(rescheduledDate+"   "+currentResDate);

               /* long MAX_DURATION = MILLISECONDS.convert(20, MINUTES);

                long duration = currentResDate.getTime() - rescheduledDate.getTime();*/
                    //Toast.makeText(this, "hiii"+duration, Toast.LENGTH_SHORT).show();
               /* if (duration >= MAX_DURATION) {
                    Toast.makeText(this, "hiii"+duration, Toast.LENGTH_SHORT).show();
                }*/
                    String dateDiffer = printDifference(currentResDate,rescheduledDate);

                    String[] separatedDateDiffer = dateDiffer.split("@");
                    String day= separatedDateDiffer[0]; // this will contain "Fruit"
                    String hour = separatedDateDiffer[1];
                    String minute = separatedDateDiffer[2];
                    //  Toast.makeText(this, "day "+day+" hour "+hour+" minute "+minute, Toast.LENGTH_SHORT).show();
                    if(Integer.parseInt(day)>=0&&Integer.parseInt(hour)>=0&&Integer.parseInt(minute)>=30)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllURL.updateURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressBar.setVisibility(View.GONE);
                                        System.out.println(response);
                                        Toast.makeText(Reschedule.this,"Order has been successfully rescheduled",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Reschedule.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }){
                            protected Map<String,String> getParams()
                            {
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("orderNumber",orderNumber);
                                params.put("UserName", SessionManagement.getLoggedInUserName(Reschedule.this));
                                params.put("updateCode", String.valueOf(AllConstant.reschduleFetchRecord));
                                params.put("taskType",tType);
                                params.put("updateDateTime",dateconveted);
                                params.put("Comments",cancelNotes);
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                TimeZone utc = TimeZone.getTimeZone("UTC");
                                df.setTimeZone(utc);
                                final String date = df.format(Calendar.getInstance().getTime());
                                params.put("date", date);
                                return params;
                            }
                        };
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        RequestQueue requestQueue = Volley.newRequestQueue(Reschedule.this);
                        requestQueue.add(stringRequest);
                    }
                    else
                    {
                        Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show();
                    }
               /* if(rescheduledDate.compareTo(currentResDate)>=0)
                {

                }else
                {
                    Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show();
                }*/
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }



    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        DecimalFormat formatter = new DecimalFormat("00");
        String day = formatter.format(dayOfMonth);
        String month = formatter.format(++monthOfYear);
        String date = day + "-" + (month) + "-" + year;
        textDate.setText(date);
        textDate.setTextColor(Color.BLACK);
    }
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String secondString = second < 10 ? "0"+second : ""+second;
        String time = hourString+":"+minuteString+":"+secondString;
        textTime.setText(time);
        textTime.setTextColor(Color.BLACK);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private String getCurrentUTCDate()
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        df.setTimeZone(utc);
        String date = df.format(Calendar.getInstance().getTime());
        return date;
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
                                    SessionManagement.clearLoggedInEmailAddress(Reschedule.this);
                                    Intent intent = new Intent(Reschedule.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Intent i2 = new Intent(Reschedule.this,NotificationService.class);
                                    stopService(i2);
                                    Intent i3= new Intent(Reschedule.this,LocationService.class);
                                    stopService(i3);
                                    startActivity(intent);
                                    new GPSTracker(Reschedule.this).stopUsingGPS();
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
                param.put("loginSessionID",SessionManagement.getLoggedInSessionID(Reschedule.this));
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Reschedule.this);
        requestQueue.add(stringRequest);
        return isLoggedIn[0];

    }
    public String getLocalTime()
    {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
       // System.out.println("Minute Differ "+minutesInMilli);
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        return  elapsedDays+"@"+elapsedHours+"@"+elapsedMinutes+"@"+elapsedSeconds;
    }

    public String convertDateDDMMYYYToYYYYMMDD(String datetime)
    {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd" );
        Date date;
        String changeDateFormatted=null;
        try {
            date = originalFormat.parse(datetime);

            changeDateFormatted = targetFormat.format(date);
        } catch (ParseException ex) {
            // Handle Exception.
        }
        return changeDateFormatted;
    }
}
