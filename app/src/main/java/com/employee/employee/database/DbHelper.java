package com.employee.employee.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.employee.employee.R;
import com.employee.employee.model.CarListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    private Resources mResources;
    private static final String DATABASE_NAME = "car.db";
    private static final int DATABASE_VERSION = 1;
    Context context;
    SQLiteDatabase db;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mResources = context.getResources();

        db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_BUGS_TABLE = "CREATE TABLE " + DbContract.MenuEntry.TABLE_NAME + " (" +
                        DbContract.MenuEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.MenuEntry.COLUMN_CAR_ID + " TEXT , " +
                DbContract.MenuEntry.COLUMN_NAME + " TEXT , " +
                DbContract.MenuEntry.COLUMN_CAR_NO + " TEXT , " +
                DbContract.MenuEntry.COLUMN_MAKE + " TEXT , " +
                DbContract.MenuEntry.COLUMN_ENGINE + " TEXT , " +
                DbContract.MenuEntry.COLUMN_CHASIS + " TEXT  " + " );";


        db.execSQL(SQL_CREATE_BUGS_TABLE);
        Log.d(TAG, "Database Created Successfully" );



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public List<CarListModel> getdata(){
        // DataModel dataModel = new DataModel();
        List<CarListModel> data=new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+DbContract.MenuEntry.TABLE_NAME+" ;",null);
        StringBuffer stringBuffer = new StringBuffer();
        CarListModel dataModel = null;
        while (cursor.moveToNext()) {
            dataModel= new CarListModel();
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String car_id = cursor.getString(cursor.getColumnIndexOrThrow("car_id"));
            String car_no = cursor.getString(cursor.getColumnIndexOrThrow("car_no"));
            String car_make = cursor.getString(cursor.getColumnIndexOrThrow("car_make"));
            String car_engine = cursor.getString(cursor.getColumnIndexOrThrow("car_engine"));
            String car_chasis_no = cursor.getString(cursor.getColumnIndexOrThrow("car_chasis_no"));
            dataModel.setCustomer_name(name);
            dataModel.setCar_chasis_no(car_chasis_no);
            dataModel.setCar_engine(car_engine);
            dataModel.setCar_make(car_make);
            dataModel.setCar_no(car_no);
            dataModel.setCar_id(car_id);
            stringBuffer.append(dataModel);
            // stringBuffer.append(dataModel);
            data.add(dataModel);
        }

        for (CarListModel mo:data ) {

            //Log.i("Hellomo",""+mo.getCity());
        }

        //

        return data;
    }

}
