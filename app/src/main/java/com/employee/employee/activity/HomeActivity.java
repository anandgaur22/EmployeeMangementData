package com.employee.employee.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.employee.employee.interfaces.ICallback;
import com.employee.employee.model.CarListModel;
import com.employee.employee.network.AppGlobalUrl;
import com.employee.employee.network.NetworkConnectionCheck;
import com.employee.employee.network.VolleySingleton;
import com.employee.employee.services.SyncService;
import com.employee.employee.utils.AppGlobalValidation;
import com.employee.employee.utils.PrefrenceManager;
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    PrefrenceManager prefrenceManager;
    NetworkConnectionCheck networkConnectionCheck;
    RecyclerView recycler_view;
    List<CarListModel> carListModelsList;
    CarListAdapter carListAdapter;
    ProgressDialog progressDialog;
    String user_id,car_id,car_no,car_engine, car_chasis_no, car_make,customer_name,search_text;
    private ICallback iCallback;

    int totalpage;
    EditText search_edtText;
    TextView tvNoData;
    ImageView btn_serach_iv;
    ProgressBar myprogressbar;


    int duration;
    int startDay = 1;

  //  private myBroadcastReceiver receiver;

    private int currentPage;
    int current_item, total_item, scrolledout_item;
    Boolean isScrolling = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
       // TextChangeListnerCall();
    }

    public void init(){


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddCarActivity.class);
                startActivity(intent);
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        init2();

        get_total_page_NetworkCall();


    }

    public void init2(){

        recycler_view=findViewById(R.id.recycler_view);
        search_edtText=findViewById(R.id.search_edtText);
        tvNoData=findViewById(R.id.tvNoData);
        myprogressbar=findViewById(R.id.myprogressbar);
        btn_serach_iv=findViewById(R.id.btn_serach_iv);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);


        prefrenceManager = new PrefrenceManager(getApplicationContext());
        user_id = prefrenceManager.fetchLoginUserId();

        networkConnectionCheck = new NetworkConnectionCheck(getApplicationContext());

        carListModelsList = new ArrayList<>();
        iCallback = new ICallback() {
            @Override
            public void onItemClick(int pos) {


                car_id = carListModelsList.get(pos).getCar_id();
                car_no = carListModelsList.get(pos).getCar_no();
                car_engine = carListModelsList.get(pos).getCar_engine();
                car_chasis_no = carListModelsList.get(pos).getCar_chasis_no();
                car_make = carListModelsList.get(pos).getCar_make();
                customer_name = carListModelsList.get(pos).getCustomer_name();

                //  if (networkConnectionCheck.isConnected()) {

                Intent intent = new Intent(HomeActivity.this, CarDetailsActivity.class);
                intent.putExtra("car_id", car_id);
                intent.putExtra("car_no", car_no);
                intent.putExtra("car_engine", car_engine);
                intent.putExtra("car_chasis_no", car_chasis_no);
                intent.putExtra("car_make", car_make);
                intent.putExtra("customer_name", customer_name);
                startActivity(intent);

                // }

            }
        };

        currentPage = 1;

        carListAdapter=new CarListAdapter(carListModelsList,getApplicationContext(),iCallback);
        recycler_view.setAdapter(carListAdapter);

        if (networkConnectionCheck.isConnected()) {

            get_carList_NetworkCall();
        }


        btn_serach_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search_text=search_edtText.getText().toString().trim();

                if (validationCheck()){
                    AppGlobalValidation.closeKeyboard(HomeActivity.this, getCurrentFocus());

                    search_car_NetworkCall(search_text);
                }


            }
        });


        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = true;

                // Toast.makeText(getContext(), "OnScrollListener"+currentPage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                current_item = layoutManager.getChildCount();
                total_item = layoutManager.getItemCount();
                scrolledout_item = layoutManager.findFirstVisibleItemPosition();
                if (isScrolling && (current_item + scrolledout_item == total_item)) {
                    isScrolling = false;
                    get_carList_NetworkCall();
                    //Toast.makeText(getContext(), "current_item"+current_item+"\n"+"total_item"+total_item, Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    public boolean validationCheck() {
        boolean check = false;

        search_text = search_edtText.getText().toString().trim();


        if (check == false) {

            if (search_text.length() < 5) {

                search_edtText.requestFocus();
                search_edtText.setError("search minimum 5 character ");

            }  else {
                check = true;
            }
        }
        return check;
    }

    public void TextChangeListnerCall() {

        search_edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                try {
                    carListAdapter.filter(charSequence.toString().toLowerCase());
                    checkNoDataCase();
                }catch (Exception e){

                    Log.d("", "onTextChanged: "+e);
                }

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action

            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_update_details) {

            Intent intent = new Intent(HomeActivity.this, UpdateDetailsActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_offline_data) {

            Intent intent = new Intent(HomeActivity.this, OfflineActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_syns_data) {




            Intent intent = new Intent(HomeActivity.this, SynsDataActivity.class);
            intent.putExtra("total_page", ""+totalpage);
            startActivity(intent);

            //Toast.makeText(this, ""+totalpage, Toast.LENGTH_SHORT).show();



        }else if (id == R.id.nav_logout) {

            isUserLogedOut(true);

            Intent intent = new Intent(HomeActivity.this,
                    LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    private void isUserLogedOut(boolean status) {

        prefrenceManager.isUserLogedOut();

    }


    

    public void get_carList_NetworkCall(){

        myprogressbar.setVisibility(View.VISIBLE);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppGlobalUrl.get_car_list+currentPage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    myprogressbar.setVisibility(View.GONE);

                    if (jsonArray.length() > 0) {




                    for (int i = 0; i < jsonArray.length(); i++) {

                        CarListModel carListModel=new CarListModel();

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String car_id=  jsonObject.optString("id");
                        String car_no=  jsonObject.optString("car_no");
                        String car_make=  jsonObject.optString("car_make");
                        String car_engine=  jsonObject.optString("car_engine");
                        String car_chasis_no=  jsonObject.optString("car_chasis_no");
                        String customer_name=  jsonObject.optString("customer_name");


                        carListModel.setCar_id(car_id);
                        carListModel.setCar_no(car_no);
                        carListModel.setCar_make(car_make);
                        carListModel.setCar_engine(car_engine);
                        carListModel.setCar_chasis_no(car_chasis_no);
                        carListModel.setCustomer_name(customer_name);

                        carListModelsList.add(carListModel);


                    }

                        currentPage += 1;

                        carListAdapter.notifyDataSetChanged();

                        isScrolling = true;

                    }else {
                        isScrolling = false;

                    }



                    checkNoDataCase();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                myprogressbar.setVisibility(View.GONE);
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }


    public void search_car_NetworkCall(final String search_text) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppGlobalUrl.search_car,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            progressDialog.dismiss();
                            myprogressbar.setVisibility(View.GONE);
                            carListModelsList.clear();
                            isScrolling = false;

                            for (int i = 0; i < jsonArray.length(); i++) {

                                CarListModel carListModel=new CarListModel();

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String car_id=  jsonObject.optString("id");
                                String car_no=  jsonObject.optString("car_no");
                                String car_make=  jsonObject.optString("car_make");
                                String car_engine=  jsonObject.optString("car_engine");
                                String car_chasis_no=  jsonObject.optString("car_chasis_no");
                                String customer_name=  jsonObject.optString("customer_name");


                                carListModel.setCar_id(car_id);
                                carListModel.setCar_no(car_no);
                                carListModel.setCar_make(car_make);
                                carListModel.setCar_engine(car_engine);
                                carListModel.setCar_chasis_no(car_chasis_no);
                                carListModel.setCustomer_name(customer_name);

                                carListModelsList.add(carListModel);

                                carListAdapter=new CarListAdapter(carListModelsList,getApplicationContext(),iCallback);
                                recycler_view.setAdapter(carListAdapter);

                            }

                            carListAdapter.notifyDataSetChanged();

                            checkNoDataCase();


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
                        Toast.makeText(HomeActivity.this, "no internet connection", Toast.LENGTH_SHORT).show();

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
                postparams.put("car_no", search_text);

                Log.e("post_claimed_amount---", "" + postparams);

                return postparams;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);
    }


    public void checkNoDataCase(){

        if(carListModelsList.size()>0){

            recycler_view.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);

        }else{
            recycler_view.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }

    }



    public void get_total_page_NetworkCall(){


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppGlobalUrl.total_page, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    totalpage= Integer.parseInt(jsonObject.optString("totalpage"));

                   // Toast.makeText(HomeActivity.this, ""+totalpage, Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}
