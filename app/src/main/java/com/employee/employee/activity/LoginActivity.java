package com.employee.employee.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.provider.Settings;
import android.provider.Settings.System;


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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {

    ImageView username_blank_imgView, username_fiil_imgView, password_blank_imgView, password_fill_imgView;
    EditText username_editText, password_editText;
    Button login_btn, register_btn;
    String username, password, mac_code;
    PrefrenceManager prefrenceManager;
    NetworkConnectionCheck networkConnectionCheck;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        TextChangeListnerCall();

    }


    public void init() {
        username_blank_imgView = findViewById(R.id.username_blank_imgView);
        username_fiil_imgView = findViewById(R.id.username_fiil_imgView);
        password_blank_imgView = findViewById(R.id.password_blank_imgView);
        password_fill_imgView = findViewById(R.id.password_fill_imgView);
        username_editText = findViewById(R.id.username_editText);
        password_editText = findViewById(R.id.password_editText);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);

        login_btn.setOnClickListener(this);
        register_btn.setOnClickListener(this);

        networkConnectionCheck = new NetworkConnectionCheck(getApplicationContext());
        prefrenceManager = new PrefrenceManager(getApplicationContext());
        mac_code = System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_btn:

                if (validationCheck() == true) {

                    if (networkConnectionCheck.isConnected()) {

                        AppGlobalValidation.closeKeyboard(LoginActivity.this, getCurrentFocus());

                        appLoginNetworkCall();
                    }
                }

                break;

            case R.id.register_btn:

                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

                break;
            default:
                break;
        }

    }

    public void TextChangeListnerCall() {

        username_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {

                username = username_editText.getText().toString();
                if (username.length() == 0) {
                    username_blank_imgView.setVisibility(View.VISIBLE);
                    username_fiil_imgView.setVisibility(View.GONE);

                } else {
                    username_blank_imgView.setVisibility(View.VISIBLE);
                    username_fiil_imgView.setVisibility(View.VISIBLE);
                }

            }
        });

        password_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                password = password_editText.getText().toString();
                if (password.length() == 0) {
                    // invalid_password_imgView.setVisibility(View.GONE);
                    password_blank_imgView.setVisibility(View.VISIBLE);
                    password_fill_imgView.setVisibility(View.GONE);

                } else {
                    password_blank_imgView.setVisibility(View.VISIBLE);
                    password_fill_imgView.setVisibility(View.VISIBLE);
                }

            }
        });

    }


    public boolean validationCheck() {
        boolean check = false;

        username = username_editText.getText().toString();
        password = password_editText.getText().toString();


        if (check == false) {

            if (username.equals("")) {

                username_editText.requestFocus();
                username_editText.setError("Username should not be blank");

            } else if (password.equals("")) {

                password_editText.requestFocus();
                password_editText.setError("Password should not be blank");

            } else {

                check = true;
            }
        }
        return check;
    }

    /*App login network call*/

    public void appLoginNetworkCall() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppGlobalUrl.Login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            progressDialog.dismiss();

                            if (jsonObject.optString("success_msg").equalsIgnoreCase("1")){

                             String user_id=  jsonObject.optString("user_id");

                                prefrenceManager.saveLoginResponseDetails(user_id);
                                prefrenceManager.saveSessionLogin();

                                Toast.makeText(LoginActivity.this, "" + jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }else {

                                Toast.makeText(LoginActivity.this, "" + jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(LoginActivity.this, "no internet connection", Toast.LENGTH_SHORT).show();

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
                postparams.put("username", username);
                postparams.put("password", password);
                postparams.put("mac_code", mac_code);

                Log.e("post_Data_login---", "" + postparams);

                return postparams;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        return false;
    }
}
