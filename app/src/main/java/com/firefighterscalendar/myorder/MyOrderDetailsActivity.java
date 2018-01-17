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
import com.firefighterscalendar.adapter.MyOrderDetailAdapter;
import com.firefighterscalendar.bean.MyOrderBean;
import com.firefighterscalendar.custom.SimpleDividerItemDecoration;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MyOrderDetailsActivity extends BaseActivity {

    private HashMap<String, String> stringHashMap;
    private TextView textOrderNo, textAmount, textOrderDate, textStatus;
    private RecyclerView rvMyOrder;
    private MyOrderDetailAdapter myOrderAdapter;
    private List<MyOrderBean> listMyOrder;
    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private TextView textHeader;
    private MyOrderBean myOrderBean;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        initControl();
    }

    private void initControl() {

        listMyOrder = new ArrayList<>();
        myOrderBean = (MyOrderBean) getIntent().getSerializableExtra("order");
        simpleDateFormat = new SimpleDateFormat("dd MMM yy hh:mm aaa", Locale.getDefault());

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
                Intent intent = new Intent(MyOrderDetailsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText(HEADER_USER_NAME);

        textOrderNo = (TextView) findViewById(R.id.textOrderNo);
        textAmount = (TextView) findViewById(R.id.textAmount);
        textOrderDate = (TextView) findViewById(R.id.textOrderDate);
        textStatus = (TextView) findViewById(R.id.textStatus);

        textOrderDate.setText(simpleDateFormat.format(new Date(Long.parseLong(myOrderBean.iOrderTime) * 1000)));
        textOrderNo.setText("Order No. " + myOrderBean.iOrderNumber);
        textAmount.setText("Total: $" + myOrderBean.dTotalPrice);
        textStatus.setText("Status: " + myOrderBean.eOrderStatus);

        rvMyOrder = (RecyclerView) findViewById(R.id.rvMyOrder);
        myOrderAdapter = new MyOrderDetailAdapter(this, listMyOrder);

        rvMyOrder.setAdapter(myOrderAdapter);
        rvMyOrder.setLayoutManager(new LinearLayoutManager(this));
        rvMyOrder.addItemDecoration(new SimpleDividerItemDecoration(this));

        if (utility.checkInternetConnection())
            getMyorderDetailsAPICall();
    }

    private void getMyorderDetailsAPICall() {
        stringHashMap = new HashMap<>();

        stringHashMap.put("vOrderNumber", myOrderBean.iOrderNumber);

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
                    }
                    myOrderAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_DETAIL_USER_ORDER);
    }
}
