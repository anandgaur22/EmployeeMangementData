package com.employee.employee.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.employee.employee.R;
import com.employee.employee.network.AppGlobalUrl;
import com.employee.employee.network.NetworkConnectionCheck;
import com.employee.employee.network.VolleySingleton;
import com.employee.employee.utils.AppGlobalValidation;
import com.employee.employee.utils.PrefrenceManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ClaimedActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    Button claimed_btn;
    EditText claimed_amt_edtTxt;

    PrefrenceManager prefrenceManager;
    NetworkConnectionCheck networkConnectionCheck;
    ProgressDialog progressDialog;
    String user_id,claimed_amount,repo_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claimed);

        init();
    }

    public void init(){

        claimed_amt_edtTxt=findViewById(R.id.claimed_amt_edtTxt);
        claimed_btn=findViewById(R.id.claimed_btn);

        getSupportActionBar().setTitle("Claimed Amount");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);

        networkConnectionCheck = new NetworkConnectionCheck(getApplicationContext());
        prefrenceManager = new PrefrenceManager(getApplicationContext());
        user_id = prefrenceManager.fetchLoginUserId();


        Intent intent = getIntent();
        repo_id = intent.getStringExtra("repo_id");

        claimed_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.claimed_btn:

                if (validationCheck()) {

                    if (networkConnectionCheck.isConnected()) {

                        AppGlobalValidation.closeKeyboard(ClaimedActivity.this, getCurrentFocus());

                         claimed_amount_NetworkCall();
                    }
                }

                break;

            default:
                break;
        }


    }


    public boolean validationCheck() {
        boolean check = false;

        claimed_amount = claimed_amt_edtTxt.getText().toString();


        if (check == false) {

            if (claimed_amount.equals("")) {

                claimed_amt_edtTxt.requestFocus();
                claimed_amt_edtTxt.setError("Claimed amount should not be blank");

            }  else {
                check = true;
            }
        }
        return check;
    }


    public void claimed_amount_NetworkCall() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppGlobalUrl.put_claimed_amount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            progressDialog.dismiss();

                            if (jsonObject.optString("success_msg").equalsIgnoreCase("1")) {


                                Toast.makeText(ClaimedActivity.this, "" + jsonObject.optString("message"), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(ClaimedActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {

                                Toast.makeText(ClaimedActivity.this, "" + jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(ClaimedActivity.this, "no internet connection", Toast.LENGTH_SHORT).show();

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
                postparams.put("claimed_amount", claimed_amount);
                postparams.put("repo_id", repo_id);

                Log.e("post_claimed_amount---", "" + postparams);

                return postparams;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);
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
