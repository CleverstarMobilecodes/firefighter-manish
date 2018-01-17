package com.firefighterscalendar.custom;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firefighterscalendar.R;
import com.firefighterscalendar.inapp.InAppPurchaseActivity;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.Constant;
import com.firefighterscalendar.utils.SessionManager;
import com.firefighterscalendar.utils.onTaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UnlockDialogActivity extends Activity implements View.OnClickListener {

    private TextView textSubscribe;
    private HashMap<String, String> stringHashMap;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_unlock);
        initControl();
    }

    private void initControl() {
        sessionManager = new SessionManager(this);

        textSubscribe = (TextView) findViewById(R.id.textSubscribe);
        textSubscribe.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == textSubscribe) {
            startActivityForResult(new Intent(this, InAppPurchaseActivity.class), 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            checkUserSubscriptionAPICall();
        }
    }

    public void checkUserSubscriptionAPICall() {
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
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constant.URL_CHECK_USER_SUBSCRIPTION);
    }
}
