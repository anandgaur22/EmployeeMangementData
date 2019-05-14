package com.employee.employee.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.employee.employee.R;
import com.employee.employee.adapter.CarListAdapter;
import com.employee.employee.model.CarListModel;
import com.employee.employee.network.AppGlobalUrl;
import com.employee.employee.network.NetworkConnectionCheck;
import com.employee.employee.network.VolleySingleton;
import com.employee.employee.utils.PrefrenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CarDetailsActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    TextView car_no_txtView, car_make_txtView, car_engine_txtView, car_chasis_no_txtView,cstmor_name_txtView,phone_1,
            phone_2,phone_3,phone_4;

    Button share_btn, repo_btn;
    ProgressDialog progressDialog;
    PrefrenceManager prefrenceManager;
    NetworkConnectionCheck networkConnectionCheck;
    String user_id, car_id, car_no, car_engine, car_chasis_no, car_make,customer_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        init();


    }

    public void init() {

        car_no_txtView = findViewById(R.id.car_no_txtView);
        car_make_txtView = findViewById(R.id.car_make_txtView);
        car_engine_txtView = findViewById(R.id.car_engine_txtView);
        car_chasis_no_txtView = findViewById(R.id.car_chasis_no_txtView);
        cstmor_name_txtView = findViewById(R.id.cstmor_name_txtView);
        phone_1 = findViewById(R.id.phone_1);
        phone_2 = findViewById(R.id.phone_2);
        phone_3 = findViewById(R.id.phone_3);
        phone_4 = findViewById(R.id.phone_4);
        share_btn = findViewById(R.id.share_btn);
        repo_btn = findViewById(R.id.repo_btn);

        getSupportActionBar().setTitle("Car Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);

        networkConnectionCheck = new NetworkConnectionCheck(getApplicationContext());
        prefrenceManager = new PrefrenceManager(getApplicationContext());
        user_id = prefrenceManager.fetchLoginUserId();

        share_btn.setOnClickListener(this);
        repo_btn.setOnClickListener(this);
        phone_1.setOnClickListener(this);
        phone_2.setOnClickListener(this);
        phone_3.setOnClickListener(this);
        phone_4.setOnClickListener(this);

        Intent intent = getIntent();
        car_id = intent.getStringExtra("car_id");
        car_no = intent.getStringExtra("car_no");
        car_engine = intent.getStringExtra("car_engine");
        car_chasis_no = intent.getStringExtra("car_chasis_no");
        car_make = intent.getStringExtra("car_make");
        customer_name = intent.getStringExtra("customer_name");

        car_no_txtView.setText(car_no);
        car_make_txtView.setText(car_make);
        car_engine_txtView.setText(car_engine);
        car_chasis_no_txtView.setText(car_chasis_no);
        cstmor_name_txtView.setText(customer_name);

       /* if (networkConnectionCheck.isConnected()) {

            get_Single_carList_NetworkCall(car_id);
        }*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.share_btn:

                appShare(getApplicationContext());

                break;

            case R.id.repo_btn:

                if (networkConnectionCheck.isConnected()) {

                    generate_repoNetworkCall();
                }

                break;

            case R.id.phone_1:
                ChooseCall("8743019000");
                break;

                case R.id.phone_2:
                    ChooseCall("9040929000");
                break;

                case R.id.phone_3:
                    ChooseCall("9040829000");

                break;

                case R.id.phone_4:
                    ChooseCall("9040729000");

                break;

            default:
                break;
        }


    }

    public void get_Single_carList_NetworkCall(String car_id) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppGlobalUrl.get_single_car_info + car_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    progressDialog.dismiss();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        car_no = jsonObject.optString("car_no");
                        car_make = jsonObject.optString("car_make");
                        car_engine = jsonObject.optString("car_engine");
                        car_chasis_no = jsonObject.optString("car_chasis_no");
                        customer_name = jsonObject.optString("customer_name");

                        car_no_txtView.setText(car_no);
                        car_make_txtView.setText(car_make);
                        car_engine_txtView.setText(car_engine);
                        car_chasis_no_txtView.setText(car_chasis_no);
                        cstmor_name_txtView.setText(customer_name);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    public void generate_repoNetworkCall() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppGlobalUrl.generate_repo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            progressDialog.dismiss();

                            if (jsonObject.optString("success_msg").equalsIgnoreCase("1")) {

                                String repo_id = jsonObject.optString("repo_id");

                                Intent intent = new Intent(CarDetailsActivity.this, AddCarActivity.class);
                                intent.putExtra("repo_id", repo_id);
                                startActivity(intent);

                            } else {

                                Toast.makeText(CarDetailsActivity.this, "" + jsonObject.optString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();

                        VolleyLog.e("Error: ", "" + error.getMessage());
                        Toast.makeText(CarDetailsActivity.this, "no internet connection", Toast.LENGTH_SHORT).show();

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postparams = new HashMap<String, String>();
                postparams.put("customer_name", customer_name);
                postparams.put("car_no", car_no);
                postparams.put("car_make", car_make);
                postparams.put("car_engine", car_engine);
                postparams.put("car_chasis_no", car_chasis_no);
                postparams.put("car_id", car_id);
                postparams.put("user_id", user_id);

                Log.e("post_Data_generate---", "" + postparams);

                return postparams;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);
    }


    public void appShare(Context context) {

        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {

            String smsNumber = "91"+"9911329000";//whatsapp number
            String message = "Customer Name:"+customer_name+"\n"+"Vehicle Number:" + car_no + "\n" + "Car make:" + car_make + "\n" + "Car engine:" + car_engine + "\n" + "Car chasis no:" + car_chasis_no;
            PackageManager packageManager = context.getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            try {
                String url = "https://api.whatsapp.com/send?phone=" + smsNumber + "&text=" + URLEncoder.encode(message, "UTF-8");
                i.setPackage("com.whatsapp");
                i.setData(Uri.parse(url));
                if (i.resolveActivity(packageManager) != null) {
                    context.startActivity(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            Toast.makeText(context, "WhatsApp not Installed in your device", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void ChooseCall(String mobile) {
        if (PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(CarDetailsActivity.this,
                Manifest.permission.CALL_PHONE)) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+mobile));
            startActivity(callIntent);

        } else {
            requestWritePermission(CarDetailsActivity.this);

        }
    }


    private static void requestWritePermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CALL_PHONE)) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);

        }  else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
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
