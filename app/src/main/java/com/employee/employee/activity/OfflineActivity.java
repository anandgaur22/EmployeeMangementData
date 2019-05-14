package com.employee.employee.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.employee.employee.R;
import com.employee.employee.adapter.CarListAdapter;
import com.employee.employee.database.DbHelper;
import com.employee.employee.interfaces.ICallback;
import com.employee.employee.model.CarListModel;

import java.util.ArrayList;
import java.util.List;

public class OfflineActivity extends AppCompatActivity {


    RecyclerView recycler_view;
    List<CarListModel> carListModelsList;
    CarListAdapter carListAdapter;
    EditText search_edtText;
    TextView tvNoData;
    ImageView btn_serach_iv;
    private ICallback iCallback;
    String user_id,car_id,car_no,car_engine, car_chasis_no, car_make,customer_name,search_text;

    DbHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        getSupportActionBar().setTitle("Offline Data Show");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);

        init();
         TextChangeListnerCall();
    }

    public void init(){

        recycler_view=findViewById(R.id.recycler_view);
        search_edtText=findViewById(R.id.search_edtText);
        tvNoData=findViewById(R.id.tvNoData);
        btn_serach_iv=findViewById(R.id.btn_serach_iv);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);

        carListModelsList = new ArrayList<>();

        database = new DbHelper(this);
        carListModelsList=  database.getdata();

        carListAdapter=new CarListAdapter(carListModelsList,getApplicationContext(),iCallback);
        recycler_view.setAdapter(carListAdapter);

        checkNoDataCase();

        //carListAdapter.notifyDataSetChanged();

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

                Intent intent = new Intent(OfflineActivity.this, CarDetailsActivity.class);
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

    public void checkNoDataCase(){

        if(carListModelsList.size()>0){

            recycler_view.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);

        }else{
            recycler_view.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
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
}
