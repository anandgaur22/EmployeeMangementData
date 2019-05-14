package com.employee.employee.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.employee.employee.R;
import com.employee.employee.network.AppGlobalUrl;
import com.employee.employee.network.CustomVolleyRequest;
import com.employee.employee.network.NetworkConnectionCheck;
import com.employee.employee.network.VolleyMultipartRequest;
import com.employee.employee.network.VolleySingleton;
import com.employee.employee.utils.AppGlobalValidation;
import com.employee.employee.utils.PrefrenceManager;

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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{

    ImageView name_blank_imgView, name_fiil_imgView, email_blank_imgView, email_fiil_imgView, validate_email_imgView, wrong_email_imgView,
            phone_blank_imgView, phone_fill_imgView, whatsapp_blank_imgView, whatsapp_fill_imgView,pan_fill_imgView,
            pan_blank_imgView,attache_imgView,drop_down_prove_type_imgView;

    EditText name_editText, email_editText, phone_editText, whatsapp_editText,pan_no_editText;
    CheckBox saveTermsConditionsCheckBox;
    TextView txtView_terms_conditions,upload_txtView;
    Button register_btn;
    String name, email,phone,whtsapp, mac_code,adhar_no,voter_no,driving_no,pancard_no,proofType,upload;
    PrefrenceManager prefrenceManager;
    NetworkConnectionCheck networkConnectionCheck;
    ProgressDialog progressDialog;
    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    public static String imageurl;

    private boolean fileSelection = false;
    String flag="";
    Spinner prove_type_spinner;
    ArrayAdapter<String> ProveAdapter;

    String[] proofItem = {"Select ID proof type", "Adhar", "Voter ID", "Driving Licence"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        TextChangeListnerCall();


    }

    public void init(){

        name_blank_imgView = findViewById(R.id.name_blank_imgView);
        name_fiil_imgView = findViewById(R.id.name_fiil_imgView);
        name_editText = findViewById(R.id.name_editText);
        email_blank_imgView = findViewById(R.id.email_blank_imgView);
        email_fiil_imgView = findViewById(R.id.email_fiil_imgView);
        email_editText = findViewById(R.id.email_editText);
        validate_email_imgView = findViewById(R.id.validate_email_imgView);
        wrong_email_imgView = findViewById(R.id.wrong_email_imgView);
        phone_blank_imgView = findViewById(R.id.phone_blank_imgView);
        phone_fill_imgView = findViewById(R.id.phone_fill_imgView);
        phone_editText = findViewById(R.id.phone_editText);
        whatsapp_blank_imgView = findViewById(R.id.whatsapp_blank_imgView);
        whatsapp_fill_imgView = findViewById(R.id.whatsapp_fill_imgView);
        whatsapp_editText = findViewById(R.id.whatsapp_editText);
        saveTermsConditionsCheckBox = findViewById(R.id.saveTermsConditionsCheckBox);
        txtView_terms_conditions = findViewById(R.id.txtView_terms_conditions);
        register_btn = findViewById(R.id.register_btn);
        pan_no_editText = findViewById(R.id.pan_no_editText);
        pan_fill_imgView = findViewById(R.id.pan_fill_imgView);
        pan_blank_imgView = findViewById(R.id.pan_blank_imgView);
        prove_type_spinner = findViewById(R.id.prove_type_spinner);
        drop_down_prove_type_imgView = findViewById(R.id.drop_down_prove_type_imgView);
        upload_txtView = findViewById(R.id.upload_txtView);
        attache_imgView = findViewById(R.id.attache_imgView);


        register_btn.setOnClickListener(this);
        saveTermsConditionsCheckBox.setOnClickListener(this);
        txtView_terms_conditions.setOnClickListener(this);
        attache_imgView.setOnClickListener(this);
        drop_down_prove_type_imgView.setOnClickListener(this);

        networkConnectionCheck = new NetworkConnectionCheck(getApplicationContext());
        prefrenceManager = new PrefrenceManager(getApplicationContext());
        mac_code = Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        register_btn.setAlpha(0.5f);
        register_btn.setClickable(false);

        /*terms and conditions text underline*/
        String text = "terms & conditions";
        SpannableString ss = new SpannableString(text);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ss.setSpan(underlineSpan, 0, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtView_terms_conditions.setText(ss);

        ProveAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.simple_spinner_dropdown, proofItem);
        ProveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prove_type_spinner.setAdapter(ProveAdapter);


        prove_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                proofType = prove_type_spinner.getItemAtPosition(prove_type_spinner.getSelectedItemPosition()).toString();

                TextView spinner_txtView = view.findViewById(R.id.spinner_txtView);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        requestWritePermission(SignUpActivity.this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.register_btn:

                if (validationCheck()) {

                    if (networkConnectionCheck.isConnected()) {

                        AppGlobalValidation.closeKeyboard(SignUpActivity.this, getCurrentFocus());

                        appRegisterNetworkCall();
                    }
                }

                break;

            case R.id.saveTermsConditionsCheckBox:

                if (!saveTermsConditionsCheckBox.isChecked()) {
                    register_btn.setEnabled(false);
                    register_btn.setClickable(false);
                    register_btn.setAlpha(0.5f);
                } else {
                    register_btn.setEnabled(true);
                    register_btn.setClickable(true);
                    register_btn.setAlpha(1);
                }

                break;

            case R.id.txtView_terms_conditions:

                Intent intent = new Intent(SignUpActivity.this, TermsConditionsActivity.class);
                startActivity(intent);
                break;

                case R.id.attache_imgView:

                    showPictureDialog();
                break;

            case R.id.drop_down_prove_type_imgView:

                prove_type_spinner.performClick();

                break;

            default:
                break;
        }


    }

    public void TextChangeListnerCall() {

        name_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {

               name = name_editText.getText().toString();
                if (name.length() == 0) {
                    name_blank_imgView.setVisibility(View.VISIBLE);
                    name_fiil_imgView.setVisibility(View.GONE);

                } else {
                    name_blank_imgView.setVisibility(View.VISIBLE);
                    name_fiil_imgView.setVisibility(View.VISIBLE);
                }

            }
        });

        email_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                email = email_editText.getText().toString();
                if (email.length() == 0) {
                    validate_email_imgView.setVisibility(View.GONE);
                    wrong_email_imgView.setVisibility(View.GONE);
                    email_blank_imgView.setVisibility(View.VISIBLE);
                    email_fiil_imgView.setVisibility(View.GONE);

                } else if (isEmailValid(email)) {

                    wrong_email_imgView.setVisibility(View.GONE);
                    validate_email_imgView.setVisibility(View.VISIBLE);
                    email_fiil_imgView.setVisibility(View.VISIBLE);
                    email_blank_imgView.setVisibility(View.VISIBLE);

                } else {
                    wrong_email_imgView.setVisibility(View.VISIBLE);
                    validate_email_imgView.setVisibility(View.GONE);
                    email_fiil_imgView.setVisibility(View.VISIBLE);

                }

            }
        });

        phone_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {

                phone = phone_editText.getText().toString();
                if (phone.length() == 0) {
                    phone_blank_imgView.setVisibility(View.VISIBLE);
                    phone_fill_imgView.setVisibility(View.GONE);

                } else {
                    phone_blank_imgView.setVisibility(View.VISIBLE);
                    phone_fill_imgView.setVisibility(View.VISIBLE);
                }

            }
        });

        whatsapp_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {

                whtsapp = whatsapp_editText.getText().toString();
                if (whtsapp.length() == 0) {
                    whatsapp_blank_imgView.setVisibility(View.VISIBLE);
                    whatsapp_fill_imgView.setVisibility(View.GONE);

                } else {
                    whatsapp_blank_imgView.setVisibility(View.VISIBLE);
                    whatsapp_fill_imgView.setVisibility(View.VISIBLE);
                }

            }
        });

        pan_no_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {

                pancard_no = pan_no_editText.getText().toString();
                if (pancard_no.length() == 0) {
                    pan_blank_imgView.setVisibility(View.VISIBLE);
                    pan_fill_imgView.setVisibility(View.GONE);

                } else {
                    pan_blank_imgView.setVisibility(View.VISIBLE);
                    pan_fill_imgView.setVisibility(View.VISIBLE);
                }

            }
        });



    }


    public boolean validationCheck() {
        boolean check = false;

        name = name_editText.getText().toString();
        email = email_editText.getText().toString();
        phone = phone_editText.getText().toString();
        whtsapp = whatsapp_editText.getText().toString();
        pancard_no = pan_no_editText.getText().toString();
        upload = upload_txtView.getText().toString();


        if (check == false) {

            try {
                if (name.equals("")) {

                    name_editText.requestFocus();
                    name_editText.setError("Name should not be blank");

                } else if (email.equals("")) {

                    email_editText.requestFocus();
                    email_editText.setError("Email should not be blank");

                } else if ((isEmailValid(email) == false)) {

                    email_editText.requestFocus();
                    email_editText.setError("Invalid email");

                }else if (phone.equals("")) {

                    phone_editText.requestFocus();
                    phone_editText.setError("Phone number should not be blank");

                } else if (whtsapp.equals("")) {

                    whatsapp_editText.requestFocus();
                    whatsapp_editText.setError("WhatsApp number should not be blank");

                }else if (pancard_no.equals("")) {

                    pan_no_editText.requestFocus();
                    pan_no_editText.setError("Pancard number should not be blank");

                }else if (proofType.equalsIgnoreCase("Select ID proof type")) {

                    Toast.makeText(getApplicationContext(), "Please Select ID proof", Toast.LENGTH_SHORT).show();

                }else if (  flag.equals("")) {

                    Toast.makeText(getApplicationContext(), "Upload selected id proof first", Toast.LENGTH_SHORT).show();

                } else {

                    check = true;
                }
            }catch (Exception e){

                Log.d("", "validationCheck: "+e);
            }


        }
        return check;
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(SignUpActivity.this);
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
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        if (PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(SignUpActivity.this,
                Manifest.permission.CAMERA) && PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(SignUpActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY);
        } else {
            requestWritePermission(SignUpActivity.this);
           /* Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY);*/
        }
    }

    private void takePhotoFromCamera() {
        if (PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(SignUpActivity.this,
                Manifest.permission.CAMERA) && PackageManager.PERMISSION_GRANTED
                == ActivityCompat.checkSelfPermission(SignUpActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);
        } else {
          /*  Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);*/
            requestWritePermission(SignUpActivity.this);
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
                fileSelection = true;
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    //Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();

                    String path1 = path;
                    int lastIndex = path1.lastIndexOf("/");
                    int prevIndex = path1.lastIndexOf("/", lastIndex - 1);
                    String laststringa = path1.substring(lastIndex + 1, path.length()).trim();

                    flag="1";

                    upload_txtView.setText(laststringa);
                    attache_imgView.setImageBitmap(bitmap);

//                    profileBitmap = encodeTobase64(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            fileSelection = true;
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            attache_imgView.setImageBitmap(thumbnail);
            String thumbnal = saveImage(thumbnail);

            String path1 = thumbnal;
            int lastIndex = path1.lastIndexOf("/");
            int prevIndex = path1.lastIndexOf("/", lastIndex - 1);
            String laststringa = path1.substring(lastIndex + 1, thumbnal.length()).trim();

            upload_txtView.setText(laststringa);
            flag="1";
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


    /*App signup network call*/
    public void appRegisterNetworkCall() {


        final ProgressDialog dialog = ProgressDialog.show(SignUpActivity.this, "Register", "Please Wait....");

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppGlobalUrl.Register, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                dialog.dismiss();

                String resultResponse = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);

                    if (jsonObject.optString("success_msg").equalsIgnoreCase("1")) {


                        Toast.makeText(SignUpActivity.this, "" + jsonObject.optString("message"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {

                        Toast.makeText(SignUpActivity.this, "" + jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

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
                Map<String, String> postparams = new HashMap<>();

                postparams.put("mac_code", mac_code);
                postparams.put("email", email);
                postparams.put("name", name);
                postparams.put("phone", phone);
                postparams.put("whatsapp_no", whtsapp);
                postparams.put("prove_type", proofType);
                postparams.put("pancard_no", pancard_no);


                Log.d("", "post_register: " + postparams);

                return postparams;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView

                params.put("photo_copy", new VolleyMultipartRequest.DataPart("register.jpg", AppGlobalValidation.getFileDataFromDrawable(getApplicationContext(), attache_imgView.getDrawable()), "image/jpeg"));

                return params;
            }
        };

        CustomVolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {


        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        return false;
    }
}
