package in.togethersolutions.logiangle.fragments.orderInfoFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.WanderingCubes;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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

import id.zelory.compressor.Compressor;
import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.activities.LoginActivity;
import in.togethersolutions.logiangle.activities.MainActivity;
import in.togethersolutions.logiangle.javaClass.AllConstant;
import in.togethersolutions.logiangle.javaClass.AllURL;
import in.togethersolutions.logiangle.session.SessionManagement;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OrderPODInfo extends Fragment implements View.OnClickListener {

    View rootView;

    TextView txtOrderNumber,txtOrderTaskType,txtClientName,txtClientNumber,btnCamera,btnSign;

    Button btnSubmit;

    boolean isLoggedIn[]={true};

    ImageView imgCameraImage1,imgCameraImage2,imgCameraImage3,imgSignature,imgReCapture1,imgReCapture2;

    LinearLayout linearLayoutMain,linearLayoutImage1,linearLayoutImage2,linearLayoutSign,linearLayoutImage3;

    CardView cardViewImages;

    ProgressBar progressBar;
    ProgressDialog loading;
    SharedPreferences sharedpreferences;

    int clickedCameraCont=1;

    byte[] byteArray;

    JSONObject jsonImageObject;
    JSONArray jsonImageArray;


    JSONObject jsonSignObject;
    JSONArray jsonSignArray;
    String orderNumber,orderTaskType,clientName,clientNumber,userName,mCurrentPhotoPath;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String SessionImage1 = "image1";
    public static final String SessionImage2 = "image2";
    public static final String SessionImage3 = "image3";
    String im1;
    String im2;
    String im3;
    String ba1;
    String encodedImage =null;
    Dialog dialog;

    private int PICK_IMAGE_REQUEST=1;

    Bitmap bitmap,getBitmapSign;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.order_pod_detail, container, false);
        inItComponent(rootView);
        getIntentData();
        inItListener();
        if(checkRiderLogin(userName)) {
            checkPODRecord();
        }
        else {
           /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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

    private void getIntentData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            orderNumber = bundle.getString("orderNumber");
            orderTaskType = bundle.getString("OrderTaskType");
            clientName = bundle.getString("ClientName");
            clientNumber = bundle.getString("ClientNumber");
        }
    }

    private void inItComponent(View rootView) {
        dialog = new Dialog(getActivity());

        userName = SessionManagement.getLoggedInUserName(getActivity());
        jsonImageArray = new JSONArray();
        jsonSignArray = new JSONArray();
        txtOrderNumber = (TextView)rootView.findViewById(R.id.txtOrderNumber);
        txtOrderTaskType = (TextView)rootView.findViewById(R.id.txtTaskType);
        txtClientName = (TextView)rootView.findViewById(R.id.txtClientName);
        txtClientNumber = (TextView)rootView.findViewById(R.id.txtClientMobileNumber);

        btnCamera = (TextView)rootView.findViewById(R.id.btn_camera);
        btnSign = (TextView)rootView.findViewById(R.id.btn_sign);

        btnSubmit = (Button)rootView.findViewById(R.id.btnImageAck);

        imgCameraImage1 = (ImageView)rootView.findViewById(R.id.imgCamera1);
        imgCameraImage2 = (ImageView)rootView.findViewById(R.id.imgCamera2);
        imgCameraImage3 = (ImageView)rootView.findViewById(R.id.imgCamera3);
        imgSignature = (ImageView)rootView.findViewById(R.id.imgSign);
        imgReCapture1 = (ImageView)rootView.findViewById(R.id.ivImage1Refresh);
        imgReCapture2 = (ImageView)rootView.findViewById(R.id.ivImage2Refresh);

        linearLayoutImage1 = (LinearLayout)rootView.findViewById(R.id.ll_image1);
        linearLayoutImage2 = (LinearLayout)rootView.findViewById(R.id.ll_image2);
        linearLayoutSign = (LinearLayout)rootView.findViewById(R.id.ll_Sign);
        linearLayoutImage3 = (LinearLayout)rootView.findViewById(R.id.ll_image3);
        linearLayoutImage1.setVisibility(View.INVISIBLE);
        linearLayoutSign.setVisibility(View.GONE);
        linearLayoutImage2.setVisibility(View.INVISIBLE);
        linearLayoutImage3.setVisibility(View.GONE);
        cardViewImages =  (CardView)rootView.findViewById(R.id.crdView_Image);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        im2 = sharedpreferences.getString(SessionImage2,"");
        im1 = sharedpreferences.getString(SessionImage1,"");

        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        FoldingCube foldingCube = new FoldingCube();
        WanderingCubes wanderingCubes = new WanderingCubes();
        ChasingDots chasingDots=new ChasingDots();
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        progressBar.setVisibility(View.GONE);


    }

    private void inItListener() {

        txtOrderNumber.setText(orderNumber);
        txtOrderTaskType.setText(orderTaskType);
        txtClientName.setText(clientName);
        txtClientNumber.setText(clientNumber);

        cardViewImages.setVisibility(View.INVISIBLE);

        btnCamera.setOnClickListener(this);
        btnSign.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

    }

    private void checkPODRecord() {
        progressBar.setVisibility(View.VISIBLE);

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
                        progressBar.setVisibility(View.GONE);
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

                        if(recordCount.equals("1"))
                        {
                            cardViewImages.setVisibility(View.VISIBLE);
                            linearLayoutImage1.setVisibility(View.VISIBLE);
                            linearLayoutImage2.setVisibility(View.VISIBLE);
                            linearLayoutImage3.setVisibility(View.VISIBLE);
                            linearLayoutSign.setVisibility(View.VISIBLE);
                            imgCameraImage1.setEnabled(false);
                            imgCameraImage2.setEnabled(false);
                            imgCameraImage3.setEnabled(false);
                            imgSignature.setEnabled(false);
                            imgCameraImage2.setImageResource(R.drawable.complete);
                            imgCameraImage1.setImageResource(R.drawable.complete);
                            imgSignature.setImageResource(R.drawable.complete);
                            imgCameraImage3.setImageResource(R.drawable.complete);
                            btnSubmit.setEnabled(false);
                            btnSubmit.setVisibility(View.GONE);
                            btnCamera.setEnabled(false);
                            btnSign.setEnabled(false);

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



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_camera :

               // captureImage(R.id.btn_camera);

                if(checkRiderLogin(userName)) {
                    captureImage(R.id.btn_camera);
                }
                else {
               /*     AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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
                break;
            case R.id.btn_sign:
                if(checkRiderLogin(userName)) {
                    captureSign();
                }
                else {
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

                break;
            case R.id.btnImageAck:
                if(checkRiderLogin(userName)) {
                    saveImage();
                }
                else {
                /*    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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

                break;
        }
    }

    private void captureImage(int btn_camera) {

        if(clickedCameraCont==1)
        {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
        if(clickedCameraCont==2)
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
        if(clickedCameraCont ==3)
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile(3);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, 102);
                }
            }
        }

    }

    private File createImageFile(int i) throws IOException {
        // Create an image file name
        String imageFileName = orderNumber+"_Image"+i;
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == getActivity().RESULT_OK) {
            linearLayoutImage1.setVisibility(View.VISIBLE);
            setPic(1);
        }
        if (requestCode == 101 && resultCode == getActivity().RESULT_OK) {
            linearLayoutImage2.setVisibility(View.VISIBLE);
            setPic(2);
        }if (requestCode == 102 && resultCode == getActivity().RESULT_OK) {
            linearLayoutImage3.setVisibility(View.VISIBLE);
            setPic(3);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
   /* private void setPic1(int i)
    {
        cardViewImages.setVisibility(View.VISIBLE);
        if(i==1)
        {
            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath,options);
                Glide.with(getActivity()).load(mCurrentPhotoPath).into(imgCameraImage1);
                int rotation = 0;
                try {
                    ExifInterface exifInterface = new ExifInterface(mCurrentPhotoPath);
                    int orientation = exifInterface.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotation = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotation = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotation = 270;
                            break;
                    }
                } catch (IOException e) {
                    // Handle any errors
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (bmp != null) {
                    if (!bmp.isRecycled()) {
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                      //  imgCameraImage1.setImageBitmap(bmp);
                    }
                    //imgCameraImage1.setImageBitmap(bmp);
                   // bmp.recycle();
                    System.out.println(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)+" Santosh Image First");
                    imagSession(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
                   // return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                } else {
                    //return "";
                }
            }catch (Exception ex){
                ex.printStackTrace();
                //return "";
            }
        }
    }*/
    private void setPic(int i) {
        cardViewImages.setVisibility(View.VISIBLE);
        //changes start

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath,options);
//        Glide.with(getActivity()).load(mCurrentPhotoPath).into(imgCameraImage1);
        int rotation = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(mCurrentPhotoPath);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
        } catch (IOException e) {
                // Handle any errors
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        if(i==1)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bmp != null) {
                if (!bmp.isRecycled()) {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    //  imgCameraImage1.setImageBitmap(bmp);
                }
                //
                try {
                   // Glide.with(getActivity().getApplicationContext()).load(createImageFile(i)).into(imgCameraImage1);
                    imgCameraImage1.setImageBitmap(bmp);
                    clickedCameraCont = ++clickedCameraCont;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // bmp.recycle();
            //    System.out.println(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)+" Santosh Image First");
                imagSession(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
                // return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            } else {
                //return "";
            }
        }
        if(i==2)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bmp != null) {
                if (!bmp.isRecycled()) {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    //  imgCameraImage1.setImageBitmap(bmp);
                }
                //imgCameraImage1.setImageBitmap(bitmap);
                try {
                    imgCameraImage2.setImageBitmap(bmp);
                    clickedCameraCont = ++clickedCameraCont;
                    //Glide.with(getActivity().getApplicationContext()).load(createImageFile(i)).into(imgCameraImage2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // bmp.recycle();
               // System.out.println(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)+" Santosh Image First");
                imagSession1(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
                // return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            } else {
                //return "";
            }
        }
        if(i==3)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bmp != null) {
                if (!bmp.isRecycled()) {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    //  imgCameraImage1.setImageBitmap(bmp);
                }
                //imgCameraImage1.setImageBitmap(bitmap);
                try {
                    imgCameraImage3.setImageBitmap(bmp);
                    clickedCameraCont = ++clickedCameraCont;
                    //Glide.with(getActivity().getApplicationContext()).load(createImageFile(i)).into(imgCameraImage2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // bmp.recycle();
               // System.out.println(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)+" Santosh Image First");
                imagSession2(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
                // return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            } else {
                //return "";
            }
        }

        //changes 30-09-2018
        /*// Get the dimensions of the View
        int targetW = imgCameraImage1.getWidth();
        int targetH = imgCameraImage1.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = 80;
        int photoH = 80;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        jsonImageObject = new JSONObject();
        Bitmap bitmap=null;
        try {
            bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
        }

        if(i==1)
        {
            imgCameraImage1.setImageBitmap(bitmapbitmap);
            Glide.with(getActivity()).load(bitmap).into(imgCameraImage1);
            try{

                Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 10, bao);
                byte[] ba = bao.toByteArray();
                try{
                    System.gc();
                    ba1 = Base64.encodeToString(ba,Base64.DEFAULT);
                    jsonImageObject.put("image1",ba1);
                }catch(OutOfMemoryError e){

                    bitmap.compress(Bitmap.CompressFormat.JPEG,50, bao);
                    ba=bao.toByteArray();
                    ba1=Base64.encodeToString(ba, Base64.DEFAULT);
                    try {
                        jsonImageObject.put("Image1",ba1);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    //Log.e("EWN", "Out of memory error catched");
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                // ba1 = Base64.encodeToString(ba,Base64.DEFAULT);
                imagSession(ba1);

            }
            catch (OutOfMemoryError outOfMemoryError)
            {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                clickedCameraCont = clickedCameraCont-1;
            }
            catch (Exception e)
            {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
               // clickedCameraCont = clickedCameraCont-1;
            }

           // ba1 = Base64.encodeToString(ba,Base64.DEFAULT);
            imagSession(ba1);
        }
        else if( i==2)
        {
            try {
               // imgCameraImage2.setImageBitmap(bitmap);
                Glide.with(getActivity()).load(mCurrentPhotoPath).into(imgCameraImage2);
                Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 10, bao);
                byte[] ba = bao.toByteArray();
                try{
                    System.gc();

                    ba1 = Base64.encodeToString(ba,Base64.DEFAULT);
                    jsonImageObject.put("image2",ba1);
                }catch(OutOfMemoryError e){

                    bitmap.compress(Bitmap.CompressFormat.JPEG,50, bao);
                    ba=bao.toByteArray();
                    ba1=Base64.encodeToString(ba, Base64.DEFAULT);
                    try {
                        jsonImageObject.put("image2",ba1);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    Log.e("EWN", "Out of memory error catched");
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            catch (OutOfMemoryError outOfMemoryError)
            {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                clickedCameraCont = clickedCameraCont-1;
            }catch (Exception e)
            {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }

            //ba1 = Base64.encodeToString(ba,Base64.DEFAULT);
            imagSession1(ba1);
        }
        else if(i==3)
        {
            imgCameraImage3.setImageBitmap(bitmap);
            Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 10, bao);
            byte[] ba = bao.toByteArray();
           // ba1 = Base64.encodeToString(ba,Base64.DEFAULT);
            try{
                System.gc();
                ba1 = Base64.encodeToString(ba,Base64.DEFAULT);
                jsonImageObject.put("image4",ba1);

            }catch(OutOfMemoryError e){

                bitmap.compress(Bitmap.CompressFormat.JPEG,50, bao);
                ba=bao.toByteArray();
                ba1=Base64.encodeToString(ba, Base64.DEFAULT);
                try {
                    jsonImageObject.put("image4",ba1);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                Log.e("EWN", "Out of memory error catched");
            }
            catch(Exception e){
                e.printStackTrace();
            }
            imagSession2(ba1);
        }*/
        jsonImageArray.put(jsonImageObject);
    }

    private void imagSession2(String bitImageString3) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SessionImage3,bitImageString3);
        editor.apply();
        im3 = sharedpreferences.getString(SessionImage3,"");
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

    private void captureSign() {

        dialog.setContentView(R.layout.activity_sign);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final SignaturePad signPad;
        final Button btnSave,btnClear;
        signPad = (SignaturePad)dialog.findViewById(R.id.signature_pad);
        btnSave = (Button)dialog.findViewById(R.id.save_button);
        btnClear = (Button)dialog.findViewById(R.id.clear_button);
        signPad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }
            @Override
            public void onSigned() {
                btnSave.setEnabled(true);
                btnClear.setEnabled(true);
            }
            @Override
            public void onClear() {
                btnSave.setEnabled(false);
                btnClear.setEnabled(false);
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signPad.clear();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap signatureBitmap = signPad.getSignatureBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byteArray = stream.toByteArray();
                jsonSignObject = new JSONObject();
                if(byteArray != null)
                {
                    cardViewImages.setVisibility(View.VISIBLE);
                    linearLayoutSign.setVisibility(View.VISIBLE);
                    imgSignature.setVisibility(View.VISIBLE);
                    getBitmapSign = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    imgSignature.setImageBitmap(getBitmapSign);
                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    try {
                        jsonSignObject.put("Sign",encodedImage.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonSignArray.put(jsonSignObject);

                }
                dialog.dismiss();
            }
        });
        dialog.show();



    }

    private void saveImage() {
       //  Toast.makeText(getContext(), "Test"+jsonSignArray.length(), Toast.LENGTH_SHORT).show();
       /* progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest =  new StringRequest(Request.Method.POST, AllURL.savePOD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response+"   resdfghhj");
                      //  System.out.println(response+"JSON of Image"+jsonImageArray.toString()+" Sign Object"+jsonSignArray.toString());


                        Toast.makeText(getActivity(),"POD updated successfully",Toast.LENGTH_SHORT).show();
                        *//*Intent inte = new Intent(getContext(), MainActivity.class);
                        startActivity(inte);
                        String res=response.trim();
                        //progressBar.setVisibility(View.GONE);
                        System.out.println(response+"POD");
                        if(res.equals("SUCCESS"))
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"POD updated successfully",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                            btnCamera.setEnabled(false);
                            btnSign.setEnabled(false);
                            btnSubmit.setVisibility(View.GONE);
                            imgCameraImage1.setEnabled(false);
                            imgCameraImage2.setEnabled(false);
                            imgCameraImage3.setEnabled(false);
                            imgSignature.setEnabled(false);
                                *//**//*  imageView3.setImageResource(R.drawable.complete);
                            imageView2.setImageResource(R.drawable.complete);
                            imageView1.setImageResource(R.drawable.complete);*//**//*
                            //   progressBar.setVisibility(View.GONE);
                        }
                        if (res.equals("dataalreadysaved"))
                        {

                            progressBar.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"POD updated successfully",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                            imgCameraImage3.setEnabled(false);
                            imgCameraImage1.setEnabled(false);
                            imgCameraImage2.setEnabled(false);
                            imgSignature.setEnabled(false);
//                            progressBar.setVisibility(View.GONE);

                        }*//*
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    int duration = Toast.LENGTH_SHORT;

                    Toast.makeText(getActivity(), "No internet connection", duration).show();
                }
                //   System.out.println(error);
            }
        }){
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                TimeZone utc = TimeZone.getTimeZone("UTC");
                df.setTimeZone(utc);
                final String date = df.format(Calendar.getInstance().getTime());
                params.put("orderNumber",orderNumber);
                params.put("imagearray",jsonImageArray.toString());
                params.put("Signarray",jsonSignArray.toString());
                params.put("date",date);
                params.put("updateCode", String.valueOf(AllConstant.completeOrderImage));
                return params;
            }
        };
      *//*  stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*//*

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //request.setShouldCache(false)
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);*/

        loading = ProgressDialog.show(getContext(), "Please Wait...", "", false, false);



       // progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest =  new StringRequest(Request.Method.POST, AllURL.updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
               //         System.out.println("dfsfsffdsf"+response);
                        String res=response.trim();
                        //progressBar.setVisibility(View.GONE);Toast.makeText(getActivity(),"POD updated successfully",Toast.LENGTH_SHORT).show();
                        //                            Intent intent = new Intent(getContext(),MainActivity.class);
                        //                            startActivity(intent);
                        Toast.makeText(getActivity(),"POD updated successfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(),MainActivity.class);
                        startActivity(intent);

                        if(res.equals("Success"))
                        {

                           // progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"POD updated successfully",Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(getContext(),MainActivity.class);
                            startActivity(intent);

                            btnCamera.setEnabled(false);
                            btnSign.setEnabled(false);
                            btnSubmit.setVisibility(View.GONE);
                            imgCameraImage1.setEnabled(false);
                            imgCameraImage2.setEnabled(false);
                            imgSignature.setEnabled(false);
                           // loading.dismiss();
                        }
                        if (res.equals("dataalreadysaved"))
                        {

                            Toast.makeText(getActivity(),"POD updated successfully",Toast.LENGTH_SHORT).show();
                            Intent intent2 = new Intent(getContext(),MainActivity.class);
                            startActivity(intent2);


                           // progressBar.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.GONE);
                            imgCameraImage1.setEnabled(false);
                            imgCameraImage2.setEnabled(false);
                            imgSignature.setEnabled(false);
//                            progressBar.setVisibility(View.GONE);
                            //loading.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    int duration = Toast.LENGTH_SHORT;

                    Toast.makeText(getActivity(), "No internet connection", duration).show();
                }

                //   System.out.println(error);
            }
        }){
            protected Map<String,String> getParams()
            {
              //  String encodedImage =null;
                Map<String,String> params = new HashMap<String, String>();

              //  encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                TimeZone utc = TimeZone.getTimeZone("UTC");
                df.setTimeZone(utc);
                final String date = df.format(Calendar.getInstance().getTime());
                params.put("orderNumber",orderNumber);
                params.put("image1",im1);
                params.put("image2",im2);
                params.put("image3", encodedImage);
                params.put("image4",im3);
                params.put("date",date);
                params.put("updateCode", String.valueOf(AllConstant.completeOrderImage));
                return checkParams(params);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //request.setShouldCache(false)
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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

    @Override
    public void onStop() {
        super.onStop();
        if(loading!=null && loading.isShowing()){

            loading.dismiss();
        }
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
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        return isLoggedIn[0];
    }
}
