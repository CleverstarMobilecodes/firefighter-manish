package com.firefighterscalendar.myorder;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.adapter.MyOrderAdapter;
import com.firefighterscalendar.bean.MyOrderBean;
import com.firefighterscalendar.custom.SimpleDividerItemDecoration;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyOrderActivity extends BaseActivity {

    private RecyclerView rvMyOrder;
    private MyOrderAdapter myOrderAdapter;
    private HashMap<String, String> stringHashMap;
    private int offset = 1;
    private Toolbar toolbar;
    private LinearLayout layoutRight, llMyOrder;
    private TextView textHeader, textNoOrder;
    private List<MyOrderBean> listMyOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        initControl();
    }

    private void initControl() {
        listMyOrder = new ArrayList<>();

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

        llMyOrder = (LinearLayout) findViewById(R.id.llMyOrder);
        llMyOrder.setVisibility(View.GONE);
        layoutRight = (LinearLayout) toolbar.findViewById(R.id.layoutRight);
        layoutRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyOrderActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        textHeader = (TextView) findViewById(R.id.textHeader);
        textNoOrder = (TextView) findViewById(R.id.textNoOrder);
        textHeader.setText("My Orders");

        rvMyOrder = (RecyclerView) findViewById(R.id.rvMyOrder);
        myOrderAdapter = new MyOrderAdapter(this, listMyOrder);

        rvMyOrder.setAdapter(myOrderAdapter);
        rvMyOrder.setLayoutManager(new LinearLayoutManager(this));
        rvMyOrder.addItemDecoration(new SimpleDividerItemDecoration(this));

        if (utility.checkInternetConnection())
            myOrderAPICall(offset);
    }

    private void myOrderAPICall(int offset) {
        stringHashMap = new HashMap<>();

        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("offset", "" + offset);

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONArray arrayResult = jsonObject.getJSONArray("result");
                        Gson gson = new Gson();
                        for (int i = 0; i < arrayResult.length(); i++) {
                            listMyOrder.add(gson.fromJson(arrayResult.getJSONObject(i).toString(), MyOrderBean.class));
                        }
                        if (arrayResult.length() == 0) {
                            if (!listMyOrder.isEmpty()) {
                                textNoOrder.setVisibility(View.GONE);
                                rvMyOrder.setVisibility(View.VISIBLE);
                            } else {
                                textNoOrder.setVisibility(View.VISIBLE);
                                rvMyOrder.setVisibility(View.GONE);
                            }
                        }
                    }

                    myOrderAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_USER_ORDERS);
    }
}
