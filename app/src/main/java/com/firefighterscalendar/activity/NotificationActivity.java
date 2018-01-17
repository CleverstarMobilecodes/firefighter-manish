package com.firefighterscalendar.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class NotificationActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private TextView textHeader, tvaboutcontent;
    private Switch switchNotification;
    private RelativeLayout rlNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initControl();
    }

    private void initControl() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_inner);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layoutRight = (LinearLayout) toolbar.findViewById(R.id.layoutRight);
        layoutRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        rlNotification = (RelativeLayout) findViewById(R.id.rlNotification);
        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText("Notification");
        switchNotification = (Switch) findViewById(R.id.switchNotification);
        if (sessionManager.getStringDetail("eNotificationFlag").equalsIgnoreCase("N")) {
            switchNotification.setChecked(false);
        } else {
            switchNotification.setChecked(true);
        }
        rlNotification.setOnClickListener(this);

        switchNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callNotificationUpdateAPI(switchNotification.isChecked() ? "Y" : "N");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlNotification:
                callNotificationUpdateAPI(switchNotification.isChecked() ? "N" : "Y");
                break;
        }
    }

    private void callNotificationUpdateAPI(final String eNotificationFlag) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        hashMap.put("eNotificationFlag", eNotificationFlag);
        new AllAPICall(this, hashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONObject objResult = jsonObject.getJSONObject("result");
                        if (eNotificationFlag.equalsIgnoreCase("N")) {
                            sessionManager.setStringDetail("eNotificationFlag", "N");
                            switchNotification.setChecked(false);
                        } else {
                            sessionManager.setStringDetail("eNotificationFlag", "Y");
                            switchNotification.setChecked(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_UPDATE_NOTIFICATION);

    }
}
