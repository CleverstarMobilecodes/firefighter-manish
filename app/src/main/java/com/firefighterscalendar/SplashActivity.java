package com.firefighterscalendar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.Constant;
import com.firefighterscalendar.utils.GCMClientManager;
import com.firefighterscalendar.utils.SessionManager;
import com.firefighterscalendar.utils.Utility;
import com.firefighterscalendar.utils.onTaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class SplashActivity extends BaseActivity {

    private SessionManager sessionManager;
    private Utility utility;
    private HashMap<String, String> stringHashMap;
    private GCMClientManager pushClientManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission(new onTaskComplete() {
                @Override
                public void onComplete(String response) {
                    if (response.equalsIgnoreCase("yes"))
                        if (utility.checkInternetConnection())
                            GCMRegistration();
                }
            });
        }
        else {
            if (utility.checkInternetConnection()) {
                GCMRegistration();
            }
        }
    }

    private void initControls() {
        sessionManager = new SessionManager(this);
        utility = new Utility(this);
        utility.deleteAllFiles();

       /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission(new onTaskComplete() {
                @Override
                public void onComplete(String response) {
                    if (response.equalsIgnoreCase("yes"))
                        GCMRegistration();
                    else
                        checkCameraPermission(null);
                }
            });
        }*/
    }

    public void GCMRegistration() {
        pushClientManager = new GCMClientManager(this);
//        sessionManager.setStringDetail("regid", "abcde");
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                sessionManager.setStringDetail("regid", registrationId);
                Log.i("regist", registrationId);

                stringHashMap = new HashMap<>();

                stringHashMap.put("txUniqueDeviceId", utility.getIMEI());
                stringHashMap.put("txDeviceToken", registrationId);
                stringHashMap.put("eDeviceType", Constant.DEVICE_TYPE);
                stringHashMap.put("vVersion", Constant.APPVERSION);

                new AllAPICall(SplashActivity.this, stringHashMap, null, new onTaskComplete() {
                    @Override
                    public void onComplete(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.getInt("response_status") == 1) {

                                JSONObject objResult = jsonObject.getJSONObject("result");
                                sessionManager.setStringDetail("iUserId", objResult.getString("iUserId"));
                                sessionManager.setStringDetail("vFirstName", objResult.getString("vFirstName"));
                                sessionManager.setStringDetail("vLastName", objResult.getString("vLastName"));
                                sessionManager.setStringDetail("vEmail", objResult.getString("vEmail"));
                                sessionManager.setStringDetail("vPhoneNo", objResult.getString("vPhoneNo"));
                                sessionManager.setStringDetail("dBirthdate", objResult.getString("dBirthdate"));
                                sessionManager.setStringDetail("dCredit", objResult.getString("dCredit"));
                                sessionManager.setStringDetail("txProfilePic", objResult.getString("txProfilePic"));
                                sessionManager.setStringDetail("eNotificationFlag", objResult.getString("eNotificationFlag"));
                                sessionManager.setStringDetail("canCreateGreetings", objResult.getString("canCreateGreetings"));
                                sessionManager.setStringDetail("canCreateCalendar", objResult.getString("canCreateCalendar"));
                                sessionManager.setIntDetail("dayCount", objResult.getInt("dayCount"));

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, 8);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);

                                if (!sessionManager.getBooleanDetail("isFirst")) {
                                    Calendar calendar1 = Calendar.getInstance();
                                    if (calendar1.get(Calendar.HOUR_OF_DAY) > 8) {
                                        sessionManager.setBooleanDetail("fire", false);
                                    }
                                    sendNotification(calendar.getTimeInMillis());
                                }

                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
//                            Toast.makeText(SplashActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constant.URL_SIGNUP_WITHOUT_DEVICE);
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });
    }

}
