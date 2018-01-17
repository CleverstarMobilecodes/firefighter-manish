package com.firefighterscalendar.myorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.product.ShopListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProcessOrderActivity extends BaseActivity implements View.OnClickListener {

    private TextView textReviewOrder, textKeepShopping, textHomeScreen, textHeader,
            textThankyou, textShippingCount, textShippingAdd;
    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private String response = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_order);
        initControl();
    }

    private void initControl() {
        response = getIntent().getStringExtra("response");

        toolbar = (Toolbar) findViewById(R.id.toolbar_inner);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToHome();
            }
        });

        layoutRight = (LinearLayout) toolbar.findViewById(R.id.layoutRight);
        layoutRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        textReviewOrder = (TextView) findViewById(R.id.textReviewOrder);
        textKeepShopping = (TextView) findViewById(R.id.textKeepShopping);
        textHomeScreen = (TextView) findViewById(R.id.textHomeScreen);
        textHeader = (TextView) findViewById(R.id.textHeader);
        textThankyou = (TextView) findViewById(R.id.textThankyou);
        textShippingCount = (TextView) findViewById(R.id.textShippingCount);
        textShippingAdd = (TextView) findViewById(R.id.textShippingAdd);

        textHeader.setText(HEADER_USER_NAME);

        textReviewOrder.setOnClickListener(this);
        textKeepShopping.setOnClickListener(this);
        textHomeScreen.setOnClickListener(this);

        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0).getJSONObject("result");
            textThankyou.setText("Thank you, " + jsonObject.getString("vShippingName"));
            textShippingCount.setText(jsonObject.getString("iProductcount") + " Items Shipping to:");

            textShippingAdd.setText(jsonObject.getString("vShippingName") + "\n" +
                    jsonObject.getString("txShippingAddress") + "\n" +
                    jsonObject.getString("vStateName") + "\n" +
                    jsonObject.getString("iPincode"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        redirectToHome();
    }

    private void redirectToHome() {
        Intent intent = new Intent(ProcessOrderActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == textReviewOrder) {
            Intent intent = new Intent(ProcessOrderActivity.this, MyOrderActivity.class);
            startActivity(intent);
        } else if (v == textKeepShopping) {
            Intent intent = new Intent(ProcessOrderActivity.this, ShopListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (v == textHomeScreen) {
            Intent intent = new Intent(ProcessOrderActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
