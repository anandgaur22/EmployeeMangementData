package com.employee.employee.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefrenceManager {

    private static final String PREF_NAME = "Employee";
    private static final String LOG_IN_OUT = "session";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    public PrefrenceManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }




    public void saveLoginResponseDetails(String user_id) {

        editor = pref.edit();
        editor.putString("user_id", user_id);

        editor.commit();

    }



    public String fetchLoginUserId() {
        return pref.getString("user_id", "");
    }



    public boolean validateSession() {
        if (pref.getBoolean(LOG_IN_OUT, false) == true) {
            return true;
        } else {
            return false;
        }
    }

    public void saveSessionLogin() {
        editor.putBoolean(LOG_IN_OUT, true);
        editor.commit();
    }

    public void isUserLogedOut() {
        editor = pref.edit();
        editor.putString("user_id", "");
        editor.clear();
        editor.commit();

    }


}
