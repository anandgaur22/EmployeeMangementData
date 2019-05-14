package com.employee.employee.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.employee.employee.R;
import com.employee.employee.services.SyncService;
import com.github.lzyzsd.circleprogress.DonutProgress;

public class SynsDataActivity extends AppCompatActivity {

    TextView total_pg_txtvIew,pg_number_txtView,syns_success_txtvIew;
    private DonutProgress progress_bar;
    int totalpage;
    ImageView back_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syns_data);

       /* getSupportActionBar().setTitle("Syns Data to Server");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);*/

        total_pg_txtvIew=findViewById(R.id.total_pg_txtvIew);
        pg_number_txtView=findViewById(R.id.pg_number_txtView);
        syns_success_txtvIew=findViewById(R.id.syns_success_txtvIew);
        back_iv=findViewById(R.id.back_iv);
        progress_bar = (DonutProgress) findViewById(R.id.progress_bar);

        Intent i = getIntent();
        totalpage = Integer.parseInt(i.getStringExtra("total_page"));

        progress_bar.setVisibility(View.VISIBLE);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        back_iv.setOnClickListener(null);


        LocalBroadcastManager.getInstance(SynsDataActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("max"));



        Intent intent = new Intent(this, SyncService.class);
        intent.putExtra("maxCount", totalpage);
        startService(intent);


        total_pg_txtvIew.setText("Total Page No : "+totalpage);

    }

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            int count = intent.getIntExtra("count",1);
            // Toast.makeText(context, ""+count, Toast.LENGTH_SHORT).show();
            pg_number_txtView.setText("No. of Page Syns : "+count);


            progress_bar.setMax(100);

            int j = 10000 / (totalpage);

            int k = (count) * (j);

            int l= k / 100;

            progress_bar.setProgress(l);

            if (count == totalpage){

                progress_bar.setVisibility(View.GONE);

                syns_success_txtvIew.setText("Total Data syns successfully in Offline");

               // Toast.makeText(context, "Total data Syns in Offline ", Toast.LENGTH_SHORT).show();

                back_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(SynsDataActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });

            }

        }
    };


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
