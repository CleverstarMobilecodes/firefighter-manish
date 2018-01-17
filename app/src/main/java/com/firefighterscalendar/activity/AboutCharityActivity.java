package com.firefighterscalendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;

public class AboutCharityActivity extends BaseActivity {

    private TextView textHeader, tvaboutcontent;
    private Toolbar toolbar;
    private LinearLayout layoutRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_charity);
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
                Intent intent = new Intent(AboutCharityActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        tvaboutcontent = (TextView) findViewById(R.id.tvaboutcontent);
        textHeader.setText("About the charity");
        tvaboutcontent.setText(Html.fromHtml("The <em>Children Hospital Foundation</em> is the official charity of the <em>Lady Cilento Children Hospital and the Centre for Children Health Research</em> - working wonders for sick kids by funding life-saving medical research, investing in vital new equipment, and providing comfort, entertainment, support and care for children and their families."));
    }
}
