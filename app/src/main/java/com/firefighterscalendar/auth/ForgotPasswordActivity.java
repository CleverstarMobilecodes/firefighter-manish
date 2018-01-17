package com.firefighterscalendar.auth;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firefighterscalendar.R;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.Constant;
import com.firefighterscalendar.utils.onTaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPasswordActivity extends Activity implements View.OnClickListener {

    private TextView textHeader, textResetPassword;
    private EditText editEmail;
    private HashMap<String, String> stringHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forgot_password);
        initControl();
    }

    private void initControl() {

        textResetPassword = (TextView) findViewById(R.id.textResetPassword);
        textResetPassword.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.editEmail);
    }

    @Override
    public void onClick(View v) {
        if (v == textResetPassword) {
            checkValidation();
        }
    }

    private void checkValidation() {
        stringHashMap = new HashMap<>();

        if (TextUtils.isEmpty(editEmail.getText().toString())) {
            editEmail.setError("Please enter email address");
        } else {
            stringHashMap.put("vEmail", editEmail.getText().toString());
            new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
                @Override
                public void onComplete(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (jsonObject.getInt("response_status") == 1) {
                            Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constant.URL_FORGOT_PASSWORD);
        }
    }
}