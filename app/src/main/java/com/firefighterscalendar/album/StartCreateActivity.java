package com.firefighterscalendar.album;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.HowtoCreateActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.StartSelectionActivity;

public class StartCreateActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private FrameLayout layoutCreate, layoutSaved, layoutHowTo;
    private TextView textHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_create);
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
        layoutRight.setOnClickListener(this);


        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText(R.string.create);

        layoutCreate = (FrameLayout) findViewById(R.id.layoutCreate);
        layoutSaved = (FrameLayout) findViewById(R.id.layoutSaved);
        layoutHowTo = (FrameLayout) findViewById(R.id.layoutHowTo);

        layoutCreate.setOnClickListener(this);
        layoutSaved.setOnClickListener(this);
        layoutHowTo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == layoutRight) {
            Intent intent = new Intent(StartCreateActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (v == layoutCreate) {
            Intent intent = new Intent(StartCreateActivity.this, StartSelectionActivity.class);
            startActivity(intent);
        } else if (v == layoutSaved) {
            Intent intent = new Intent(StartCreateActivity.this, SavedImageActivity.class);
            startActivity(intent);
        } else if (v == layoutHowTo) {
            Intent intent = new Intent(StartCreateActivity.this, HowtoCreateActivity.class);
            startActivity(intent);
        }
    }
}
