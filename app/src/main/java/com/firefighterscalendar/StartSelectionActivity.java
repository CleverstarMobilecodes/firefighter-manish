package com.firefighterscalendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.inapp.InAppPurchaseActivity;
import com.firefighterscalendar.utils.onConfirm;

public class StartSelectionActivity extends BaseActivity {

    private LinearLayout layoutCalendar, layoutGreetingCard, layoutRight;
    private Toolbar toolbar;
    private TextView textHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_selection);
        initControls();
    }

    private void initControls() {
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
                Intent intent = new Intent(StartSelectionActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText("Create");

        layoutCalendar = (LinearLayout) findViewById(R.id.layoutCalendar);
        layoutGreetingCard = (LinearLayout) findViewById(R.id.layoutGreetingCard);

        layoutCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getStringDetail("subscription").equalsIgnoreCase("yes") ||
                        (sessionManager.getStringDetail("canCreateCalendar").equalsIgnoreCase("yes"))) {
                    Intent intent = new Intent(StartSelectionActivity.this, SelectImageActivity.class);
                    intent.putExtra("calendar", true);
                    startActivity(intent);
                } else {
                    utility.showYesNoDialog("Unlock", "Cancel", "You have used your 2 free designs, CLICK HERE to support the Firefighters Calendar and gain unlimited access to all app features!", new onConfirm() {
                        @Override
                        public void onChooseYes(boolean isTrue) {
                            startActivity(new Intent(StartSelectionActivity.this, InAppPurchaseActivity.class));
                        }
                    });
                }
            }
        });

        layoutGreetingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getStringDetail("subscription").equalsIgnoreCase("yes") ||
                        (sessionManager.getStringDetail("canCreateGreetings").equalsIgnoreCase("yes"))) {
                    Intent intent = new Intent(StartSelectionActivity.this, SelectImageActivity.class);
                    startActivity(intent);
                } else {
                    utility.showYesNoDialog("Unlock", "Cancel", "You have used your 2 free designs, CLICK HERE to support the Firefighters Calendar and gain unlimited access to all app features!", new onConfirm() {
                        @Override
                        public void onChooseYes(boolean isTrue) {
                            startActivity(new Intent(StartSelectionActivity.this, InAppPurchaseActivity.class));
                        }
                    });
                }
            }
        });
    }
}
