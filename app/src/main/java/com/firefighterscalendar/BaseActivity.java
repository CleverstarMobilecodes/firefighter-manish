package com.firefighterscalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;

import com.facebook.appevents.AppEventsLogger;
import com.firefighterscalendar.utils.AlarmReceiver;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.Constant;
import com.firefighterscalendar.utils.GCMClientManager;
import com.firefighterscalendar.utils.SessionManager;
import com.firefighterscalendar.utils.Utility;
import com.firefighterscalendar.utils.onTaskComplete;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class BaseActivity extends AppCompatActivity implements Constant {

    public SessionManager sessionManager;
    public Utility utility;
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_STORAGE = 1;
    private GCMClientManager pushClientManager;
    public String HEADER_USER_NAME = "";
    public HashMap<String, String> stringHashMap;
    public static FirebaseAnalytics mFirebaseAnalytics;
    AppEventsLogger logger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        String str = printKeyHash(BaseActivity.this);
        Log.d("Key Hash= ", str);

        sessionManager = new SessionManager(this);
        utility = new Utility(this);
//        HEADER_USER_NAME = sessionManager.getStringDetail("vFirstName") + " " + sessionManager.getStringDetail("vLastName");
        HEADER_USER_NAME = getResources().getString(R.string.app_name);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }
    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.d("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.d("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
    public void GCMRegistration() {
        pushClientManager = new GCMClientManager(this);
//        sessionManager.setStringDetail("regid", "abc");
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                sessionManager.setStringDetail("regid", registrationId);
                Log.i("regist", registrationId);
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });
    }

    public String getTextvalue(EditText editText) {
        return editText.getText().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission(new onTaskComplete() {
                @Override
                public void onComplete(String response) {

                }
            });
        }*/
        AppEventsLogger.activateApp(this);
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

    // for Android, you should also log app deactivation
    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
    void checkCameraPermission(onTaskComplete onTaskComplete) {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
////            requestCameraPermission();
//        }
//        else
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            requestInternetPermission();
        }
        else if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestReadandWrite();
        }
        else if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestWrite();
        }
        else if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestNetworkStatePermission();
        }
        else if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestAccountPermission();
        }
        else if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPhoneStatePermission();
        }
        else {
            onTaskComplete.onComplete("yes");
        }
    }

    private void requestCameraPermission() {
        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    private void requestInternetPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.INTERNET)) {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{android.Manifest.permission.INTERNET},
                    3);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET},
                    3);
        }
    }

    private void requestAccountPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.GET_ACCOUNTS)) {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{android.Manifest.permission.GET_ACCOUNTS},
                    5);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.GET_ACCOUNTS},
                    5);
        }
    }

    private void requestNetworkStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_NETWORK_STATE)) {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                    3);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                    3);
        }
    }

    private void requestPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_PHONE_STATE)) {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    3);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    3);
        }
    }

    private void requestReadandWrite() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        }
        else {

            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        }
    }

    private void requestWrite() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
        else {

            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
    }

    public void checkUserSubscriptionAPICall(final onTaskComplete onTaskComplete) {
        stringHashMap = new HashMap<>();

        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONObject objResult = jsonObject.getJSONObject("result");
                        sessionManager.setStringDetail("subscription", objResult.getString("Subscription"));
                        sessionManager.setIntDetail("dayCount", objResult.getInt("dayCount"));
                        sessionManager.setStringDetail("freeSubscription", objResult.getString("freeSubscription"));
                        onTaskComplete.onComplete(objResult.getString("Subscription"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_CHECK_USER_SUBSCRIPTION);
    }

    public void setSingleTimeShare(int which) {
        stringHashMap = new HashMap<>();
        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("Created", "" + which);

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONObject objResult = jsonObject.getJSONObject("result");
                        sessionManager.setStringDetail("canCreateGreetings", objResult.getString("canCreateGreetings"));
                        sessionManager.setStringDetail("canCreateCalendar", objResult.getString("canCreateCalendar"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_USER_GREETINGS_AND_CALANDER);
    }

    public void sendNotification(long timeInMillis) {
        Log.i("time", "" + timeInMillis);

        sessionManager.setBooleanDetail("isFirst", true);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sender);
        Log.i("cancel", "cancel call");
    }
}