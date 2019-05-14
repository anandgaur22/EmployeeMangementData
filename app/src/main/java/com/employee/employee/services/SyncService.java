package com.employee.employee.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.employee.employee.database.DbContract;
import com.employee.employee.database.DbHelper;
import com.employee.employee.model.CarListModel;
import com.employee.employee.network.AppGlobalUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.employee.employee.database.DbContract.MenuEntry.TABLE_NAME;

public class SyncService  extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    int maxCount;
    int getCount=1;
    List<CarListModel> carListModelsList;


    public SyncService(String name) {
        super(name);
    }
    public SyncService() {
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            maxCount = intent.getIntExtra("maxCount", 0);

            if (maxCount != 0) {

                get_carList_NetworkCall();
            }

        }
    }


    @SuppressLint("StaticFieldLeak")
    class RetrieveFeedTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            insert();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            getCount++;

            try {
                if(getCount <= maxCount) {
                    sendMessage();
                    get_carList_NetworkCall();
                    Log.d("", "onPostExecute: "+getCount+"max"+maxCount);
                   // Toast.makeText(SyncService.this, "Page Number : "+getCount+"\n"+"Total Page Number : "+maxCount, Toast.LENGTH_SHORT).show();

                }else {
                    // get_carList_NetworkCall();
                    // sendMessage();
                    // Toast.makeText(SyncService.this, "count:"+getCount+" maxcount: "+maxCount, Toast.LENGTH_SHORT).show();
                    Toast.makeText(SyncService.this, "Data syns successfully", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){

                Log.d("", "onPostExecute: "+e);
            }


        }
    }


    public  void  insert(){

        DbHelper dbHelper= new DbHelper(getApplicationContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(int i=0;i<carListModelsList.size();i++) {
            CarListModel carListModel = carListModelsList.get(i);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbContract.MenuEntry.COLUMN_CHASIS, carListModel.getCar_chasis_no());
            contentValues.put(DbContract.MenuEntry.COLUMN_CAR_NO, carListModel.getCar_no());
            contentValues.put(DbContract.MenuEntry.COLUMN_ENGINE, carListModel.getCar_engine());
            contentValues.put(DbContract.MenuEntry.COLUMN_MAKE, carListModel.getCar_make());
            contentValues.put(DbContract.MenuEntry.COLUMN_CAR_ID, carListModel.getCar_id());
            contentValues.put(DbContract.MenuEntry.COLUMN_NAME, carListModel.getCustomer_name());
            db.insert(DbContract.MenuEntry.TABLE_NAME, null, contentValues);
        }

    }

    public void get_carList_NetworkCall(){


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        carListModelsList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppGlobalUrl.get_car_list+getCount, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

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
                        new RetrieveFeedTask().execute();

                    }else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                get_carList_NetworkCall();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    private void sendMessage() {
        // The string "my-integer" will be used to filer the intent
        Intent intent = new Intent("max");
        // Adding some data
        intent.putExtra("count", getCount);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
