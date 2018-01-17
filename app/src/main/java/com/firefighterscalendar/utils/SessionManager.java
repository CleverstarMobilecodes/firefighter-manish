package com.firefighterscalendar.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.firefighterscalendar.R;

/* =================================================================== */
public class SessionManager {
    private SharedPreferences objPreferences;
    private SharedPreferences.Editor objEditor;

    public SessionManager(Context context) {

        objPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        objEditor = objPreferences.edit();
    }

    /**
     * setBooleanDetail : set Boolean value into SP
     */
    public void setBooleanDetail(String key, Boolean value) {
        objEditor = objPreferences.edit();
        objEditor.putBoolean(key, value);
        objEditor.apply();
    }

    /**
     * getBooleanDetail : get Boolean value from SP
     */
    public boolean getBooleanDetail(String key) {
        objEditor = objPreferences.edit();
        return objPreferences.getBoolean(key, false);
    }

    /**
     * setIntDetail : set int value into SP
     */
    public void setIntDetail(String key, int value) {
        objEditor = objPreferences.edit();
        objEditor.putInt(key, value);
        objEditor.apply();
    }

    /**
     * getIntDetail : get int value from SP
     */
    public int getIntDetail(String key) {
        objEditor = objPreferences.edit();
        int status = objPreferences.getInt(key, 0);
        objEditor.apply();
        return status;
    }

    /**
     * setStringDetail : set String value into SP
     */
    public void setStringDetail(String key, String value) {
        objEditor = objPreferences.edit();
        objEditor.putString(key, value);
        objEditor.apply();
        objEditor.commit();
    }

    /**
     * getStringDetail : get String value from SP
     */
    public String getStringDetail(String key) {
        objEditor = objPreferences.edit();
        return objPreferences.getString(key, "");
    }

    /**
     * clearAllSP : clear your SP
     */
    public void clearAllSP() {
        objEditor.clear();
        objEditor.commit();
    }

    public boolean isContains(String key) {
        return objPreferences.contains(key);
    }
}
