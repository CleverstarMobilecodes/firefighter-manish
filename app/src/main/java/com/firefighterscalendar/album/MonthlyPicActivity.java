package com.firefighterscalendar.album;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.inapp.InAppPurchaseActivity;
import com.firefighterscalendar.utils.onConfirm;

public class MonthlyPicActivity extends BaseActivity implements View.OnClickListener {

    private FrameLayout flMonthly, flFireFighter;
    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private TextView textHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_pic);
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
                Intent intent = new Intent(MonthlyPicActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText("Select Image");

        flMonthly = (FrameLayout) findViewById(R.id.flMonthly);
        flFireFighter = (FrameLayout) findViewById(R.id.flFireFighter);

        flMonthly.setOnClickListener(this);
        flFireFighter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == flMonthly) {
            if (sessionManager.getStringDetail("subscription").equalsIgnoreCase("yes") && sessionManager.getStringDetail("freeSubscription").equalsIgnoreCase("no"))
                startActivity(new Intent(this, AlbumActivity.class).putExtra("type", "monthly_pics"));
            else
                utility.showYesNoDialog("Gain Access", "Cancel", "This exclusive feature is for our paid users only, Click here to get access! Each month we hand select out latest and greatest exclusive images purely for this album!", new onConfirm() {
                    @Override
                    public void onChooseYes(boolean isTrue) {
                        startActivity(new Intent(MonthlyPicActivity.this, InAppPurchaseActivity.class));
                    }
                });

        } else if (v == flFireFighter) {
            startActivity(new Intent(this, AlbumActivity.class).putExtra("type", "firefighter"));
        }
    }
}
