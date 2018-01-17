package com.firefighterscalendar.auth;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private HashMap<String, String> stringHashMap;
    private EditText editPassword, editNewPassword, editRetypePass;
    private TextView textChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initControl();
    }

    private void initControl() {
        editPassword = (EditText) findViewById(R.id.editPassword);
        editNewPassword = (EditText) findViewById(R.id.editNewPassword);
        editRetypePass = (EditText) findViewById(R.id.editRetypePass);

        textChangePassword = (TextView) findViewById(R.id.textChangePassword);
        textChangePassword.setOnClickListener(this);
    }

    private void changePasswordAPICall() {
        stringHashMap = new HashMap<>();
        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("vCurrentPassword", getTextvalue(editPassword));
        stringHashMap.put("vPassword", getTextvalue(editPassword));
        stringHashMap.put("vConfPassword", getTextvalue(editNewPassword));

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_CHANGE_PASSWORD);
    }

    private void checkValidation() {
        if (TextUtils.isEmpty(getTextvalue(editPassword))) {
            editPassword.setError("Please enter current password");
        } else if (!utility.isValidPassword(editNewPassword)) {
            editNewPassword.setError("Please enter new password");
        } else if (!utility.isValidPassword(editRetypePass)) {
            editRetypePass.setError("Please retype password");
        } else if (!utility.isValidPassword(editNewPassword, editRetypePass)) {
            utility.showOkDialog("Password not match!");
        } else {
            if (utility.checkInternetConnection())
                changePasswordAPICall();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == textChangePassword) {
            checkValidation();
        }
    }
}
