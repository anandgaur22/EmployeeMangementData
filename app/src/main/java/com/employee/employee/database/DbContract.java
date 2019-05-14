package com.employee.employee.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;



public class DbContract {

    public static final String CONTENT_AUTHORITY = "com.employee.employee.database";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_ENTRIES = "entries";

    public static class MenuEntry implements BaseColumns {

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.jsontosqlite.entries";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.jsontosqlite.entry";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

        public static final String TABLE_NAME = "car";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CAR_ID = "car_id";
        public static final String COLUMN_CAR_NO = "car_no";
        public static final String COLUMN_MAKE = "car_make";
        public static final String COLUMN_ENGINE = "car_engine";
        public static final String COLUMN_CHASIS = "car_chasis_no";

    }


}