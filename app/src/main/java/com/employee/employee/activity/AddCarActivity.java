package com.employee.employee.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.employee.employee.R;
import com.employee.employee.network.AppGlobalUrl;
import com.employee.employee.network.CustomVolleyRequest;
import com.employee.employee.network.NetworkConnectionCheck;
import com.employee.employee.network.VolleyMultipartRequest;
import com.employee.employee.utils.AppGlobalValidation;
import com.employee.employee.utils.PrefrenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.employee.employee.utils.AppGlobalValidation.isEmailValid;

public class AddCarActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    ImageView images1, images2, images3, images4, images5, images6, images7, images8, images9, images10, images11, images12;
    Button claimed_amount_btn;
    String car_no, user_id, repo_id;
    PrefrenceManager prefrenceManager;
    NetworkConnectionCheck networkConnectionCheck;
    ProgressDialog progressDialog;

    String image1="";
    String image2="";
    String image3="";
    String image4="";
    String image5="";
    String image6="";
    String image7="";
    String image8="";
    String image9="";
    String image10="";
    String image11="";
    String image12="";

    int number;
    int flag;

    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    public static String imageurl;

    private boolean fileSelection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        init();
    }

    public void init() {

        images1 = findViewById(R.id.images1);
        images2 = findViewById(R.id.images2);
        images3 = findViewById(R.id.images3);
        images4 = findViewById(R.id.images4);
        images5 = findViewById(R.id.images5);
        images6 = findViewById(R.id.images6);
        images7 = findViewById(R.id.images7);
        images8 = findViewById(R.id.images8);
        images9 = findViewById(R.id.images9);
        images10 = findViewById(R.id.images10);
        images11 = findViewById(R.id.images11);
        images12 = findViewById(R.id.images12);
        claimed_amount_btn = findViewById(R.id.claimed_amount_btn);

        getSupportActionBar().setTitle("Repo Upload Picture");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);

        networkConnectionCheck = new NetworkConnectionCheck(getApplicationContext());
        prefrenceManager = new PrefrenceManager(getApplicationContext());
        user_id = prefrenceManager.fetchLoginUserId();

        Intent intent = getIntent();
        repo_id = intent.getStringExtra("repo_id");

        claimed_amount_btn.setOnClickListener(this);
        images1.setOnClickListener(this);
        images2.setOnClickListener(this);
        images3.setOnClickListener(this);
        images4.setOnClickListener(this);
        images5.setOnClickListener(this);
        images6.setOnClickListener(this);
        images7.setOnClickListener(this);
        images8.setOnClickListener(this);
        images9.setOnClickListener(this);
        images10.setOnClickListener(this);
        images11.setOnClickListener(this);
        images12.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.claimed_amount_btn:

               // if (validationCheck()){

                    if (networkConnectionCheck.isConnected()) {

                        Intent intent = new Intent(AddCarActivity.this, ClaimedActivity.class);
                        intent.putExtra("repo_id", repo_id);
                        startActivity(intent);

                   // }
                }



                break;

            case R.id.images1:
                flag=1;
                showPictureDialog("1");
                break;

            case R.id.images2:
                flag=2;
                showPictureDialog("2");
                break;

            case R.id.images3:
                flag=3;
                showPictureDialog("3");
                break;

            case R.id.images4:

                flag=4;
                showPictureDialog("4");
                break;

            case R.id.images5:

                flag=5;
                showPictureDialog("5");
                break;

            case R.id.images6:
                flag=6;
                showPictureDialog("6");
                break;

            case R.id.images7:
                flag=7;
                showPictureDialog("7");
                break;

            case R.id.images8:

                flag=8;
                showPictureDialog("8");
                break;

            case R.id.images9:

                flag=9;
                showPictureDialog("9");
                break;

            case R.id.images10:

                flag=10;
                showPictureDialog("10");
                break;

            case R.id.images11:

                flag=11;
                showPictureDialog("11");
                break;

            case R.id.images12:

                flag=12;
                showPictureDialog("12");
                break;

            default:

                Toast.makeText(this, "No view found", Toast.LENGTH_SHORT).show();

                break;
        }


    }


    private void showPictureDialog(final String number) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(AddCarActivity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from Gallery",
                "Capture photo from Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary(number);

                                break;
                            case 1:
                                takePhotoFromCamera(number);

                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary(String number) {
        if (PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(AddCarActivity.this,
                Manifest.permission.CAMERA) && PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(AddCarActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY);


        } else {
            requestWritePermission(AddCarActivity.this);
           /* Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY);*/
        }
    }

    private void takePhotoFromCamera(String number) {
        if (PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(AddCarActivity.this,
                Manifest.permission.CAMERA) && PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(AddCarActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);

        } else {
          /*  Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);*/
            requestWritePermission(AddCarActivity.this);
        }
    }

    private static void requestWritePermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            new android.app.AlertDialog.Builder(context)
                    .setMessage("This app needs permission to use The phone Camera in order to activate the Scanner")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 1);
                        }
                    }).show();

        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new android.app.AlertDialog.Builder(context)
                    .setMessage("This app needs permission to use storage to save the clicked Image")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }).show();

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), contentURI);
                    //String path = saveImage(bitmap)

                    if (flag==1){
                        images1.setImageBitmap(bitmap);
                        image1="1";
                        upload_car_repo_network_call(1);

                    }else if(flag==2){

                        images2.setImageBitmap(bitmap);
                        image2="1";
                        upload_car_repo_network_call(2);
                    }else if(flag==3){

                        images3.setImageBitmap(bitmap);
                        image3="1";
                        upload_car_repo_network_call(3);
                    }else if(flag==4){

                        images4.setImageBitmap(bitmap);
                        image4="1";
                        upload_car_repo_network_call(4);
                    }else if(flag==5){

                        images5.setImageBitmap(bitmap);
                        image5="1";
                        upload_car_repo_network_call(5);
                    }else if(flag==6){

                        images6.setImageBitmap(bitmap);
                        image6="1";
                        upload_car_repo_network_call(6);
                    }else if(flag==7){

                        images7.setImageBitmap(bitmap);
                        image7="1";
                        upload_car_repo_network_call(7);
                    }else if(flag==8){

                        images8.setImageBitmap(bitmap);
                        image8="1";
                        upload_car_repo_network_call(8);
                    }else if(flag==9){

                        images9.setImageBitmap(bitmap);
                        image9="1";
                        upload_car_repo_network_call(9);
                    }else if(flag==10){

                        images10.setImageBitmap(bitmap);
                        image10="1";
                        upload_car_repo_network_call(10);
                    }else if(flag==11){

                        images11.setImageBitmap(bitmap);
                        image11="1";
                        upload_car_repo_network_call(11);
                    }else if(flag==12){

                        images12.setImageBitmap(bitmap);
                        image12="1";
                        upload_car_repo_network_call(12);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");


            if (flag==1){
                images1.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image1="1";
                upload_car_repo_network_call(1);

            }else if(flag==2){

                images2.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image2="1";
                upload_car_repo_network_call(2);
            }else if(flag==3){

                images3.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image3="1";
                upload_car_repo_network_call(3);
            }else if(flag==4){

                images4.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image4="1";
                upload_car_repo_network_call(4);
            }else if(flag==5){

                images5.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image5="1";
                upload_car_repo_network_call(5);
            }else if(flag==6){

                images6.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image6="1";
                upload_car_repo_network_call(6);
            }else if(flag==7){

                images7.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image7="1";
                upload_car_repo_network_call(7);
            }else if(flag==8){

                images8.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image8="1";
                upload_car_repo_network_call(8);
            }else if(flag==9){

                images9.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image9="1";
                upload_car_repo_network_call(9);
            }else if(flag==10){

                images10.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image10="1";
                upload_car_repo_network_call(10);
            }else if(flag==11){

                images11.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image11="1";
                upload_car_repo_network_call(11);
            }else if(flag==12){

                images12.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                image12="1";
                upload_car_repo_network_call(12);
            }


            //saveImage(thumbnail);

        }
    }


    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.e("TAG", "File Saved::--->" + f.getAbsolutePath());
            imageurl = f.getAbsolutePath();


            Log.d("", "saveImage: " + f.getAbsolutePath());


            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public boolean validationCheck() {
        boolean check = false;


        if (check == false) {

            if (image1.equals("")) {

                Toast.makeText(this, "Please Upload Photo of Inventory", Toast.LENGTH_SHORT).show();

            } else if (image2.equals("")) {

                Toast.makeText(this, "Please Upload Photo of Pre Inventory", Toast.LENGTH_SHORT).show();


            } else if (image3.equals("")) {

                Toast.makeText(this, "Please Upload Photo of Post Intimation", Toast.LENGTH_SHORT).show();


            }  else if (image4.equals("")) {

                Toast.makeText(this, "Please Upload Photo of Vehicle Front", Toast.LENGTH_SHORT).show();


            } else if (image5.equals("")) {

                Toast.makeText(this, "Please Upload Photo of Vehicle Rear", Toast.LENGTH_SHORT).show();

            }else if (image6.equals("")) {

                Toast.makeText(this, "Please Upload Photo Vehicle Right side", Toast.LENGTH_SHORT).show();

            }else if (image7.equals("")) {

                Toast.makeText(this, "Please Upload Photo Vehicle Left side", Toast.LENGTH_SHORT).show();

            }else if (image8.equals("")) {

                Toast.makeText(this, "Please Upload Photo of Vehicle Meter", Toast.LENGTH_SHORT).show();

            }else if (image9.equals("")) {

                Toast.makeText(this, "Please Upload Photo Vehicle Documents 1", Toast.LENGTH_SHORT).show();

            }else if (image10.equals("")) {

                Toast.makeText(this, "Please Upload Photo Vehicle Documents 2", Toast.LENGTH_SHORT).show();

            }else if (image11.equals("")) {

                Toast.makeText(this, "Please Upload Photo Vehicle Documents 3", Toast.LENGTH_SHORT).show();

            }else if (image12.equals("")) {

                Toast.makeText(this, "Please Upload Photo Vehicle Documents 4", Toast.LENGTH_SHORT).show();

            } else {

                check = true;
            }
        }
        return check;
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }


    public void upload_car_repo_network_call(final int ImageNumber) {

        final ProgressDialog dialog = ProgressDialog.show(AddCarActivity.this, null, "Please Wait");

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppGlobalUrl.put_image, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                dialog.dismiss();

                String resultResponse = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);

                    if (jsonObject.optString("success_msg").equalsIgnoreCase("1")) {


                        Toast.makeText(AddCarActivity.this, "" + jsonObject.optString("message"), Toast.LENGTH_LONG).show();


                    } else {

                        Toast.makeText(AddCarActivity.this, "" + jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                dialog.dismiss();
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("repo_id", repo_id);
                params.put("image_type_no", ""+ImageNumber);

                Log.d("", "post_blog: " + params);

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView

                switch (ImageNumber) {

                    case 1:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images1.getDrawable()), "image/jpeg"));
                        break;

                    case 2:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images2.getDrawable()), "image/jpeg"));
                        break;

                    case 3:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images3.getDrawable()), "image/jpeg"));
                        break;


                    case 4:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images4.getDrawable()), "image/jpeg"));
                        break;

                    case 5:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images5.getDrawable()), "image/jpeg"));
                        break;

                    case 6:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images6.getDrawable()), "image/jpeg"));
                        break;

                    case 7:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images7.getDrawable()), "image/jpeg"));
                        break;

                    case 8:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images8.getDrawable()), "image/jpeg"));
                        break;

                    case 9:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images9.getDrawable()), "image/jpeg"));
                        break;

                    case 10:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images10.getDrawable()), "image/jpeg"));
                        break;

                    case 11:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images11.getDrawable()), "image/jpeg"));
                        break;

                    case 12:
                        params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images12.getDrawable()), "image/jpeg"));
                        break;
                }

                //params.put("images_file", new VolleyMultipartRequest.DataPart("repo.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), images1.getDrawable()), "image/jpeg"));

                return params;
            }
        };

        CustomVolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {


        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        return false;
    }
}
