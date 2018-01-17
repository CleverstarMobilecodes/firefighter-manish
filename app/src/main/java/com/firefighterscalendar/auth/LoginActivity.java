package com.firefighterscalendar.auth;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword;
    private LinearLayout layoutSignUp;
    private TextView textSignIn;
    private TextView textForgot;
    private HashMap<String, String> stringHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GCMRegistration();
        initControls();
    }

    private void initControls() {
        textForgot = (TextView) findViewById(R.id.textForgot);
        textForgot.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        layoutSignUp = (LinearLayout) findViewById(R.id.layoutSignUp);
        layoutSignUp.setOnClickListener(this);

        textSignIn = (TextView) findViewById(R.id.textSignIn);
        textSignIn.setOnClickListener(this);
    }

    private void checkValidation() {
        if (!utility.isValidEmail(editTextEmail)) {
            editTextEmail.setError("Please enter valid email address");
        } else if (!utility.isValidPassword(editTextPassword)) {

        } else {
            if (utility.checkInternetConnection())
                loginAPICall();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == textSignIn) {
            checkValidation();
        } else if (v == layoutSignUp) {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.putExtra("isEdit", false);
            startActivity(intent);
        } else if (v == textForgot) {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        }
    }

    private void loginAPICall() {
        stringHashMap = new HashMap<>();
        stringHashMap.put("vEmail", getTextvalue(editTextEmail));
        stringHashMap.put("vPassword", getTextvalue(editTextPassword));
        stringHashMap.put("txDeviceToken", sessionManager.getStringDetail("regid"));
        stringHashMap.put("eDeviceType", DEVICE_TYPE);
        stringHashMap.put("vVersion", APPVERSION);

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
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


                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    Toast.makeText(LoginActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_LOGIN);
    }
}