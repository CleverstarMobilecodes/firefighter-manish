package com.firefighterscalendar.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.utils.MyDatePickerDialog;
import com.marcohc.robotocalendar.RobotoCalendarView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PrintableCalendarActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private TextView textHeader, textNext;
    String path = "";
    private ImageView imgCalendarImage, leftButton, rightButton;
    private RobotoCalendarView robotoCalendarView;
    private TextView textSelectDate;
    private SimpleDateFormat simpleDateFormat;
    private Calendar tmpcalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printable_calendar);
        initControl();
    }

    private void initControl() {
        path = getIntent().getStringExtra("path");
        simpleDateFormat = new SimpleDateFormat("MMM yyyy");
        tmpcalendar = Calendar.getInstance();

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
                Intent intent = new Intent(PrintableCalendarActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        textSelectDate = (TextView) findViewById(R.id.textSelectDate);
        textNext = (TextView) findViewById(R.id.textNext);
        textNext.setOnClickListener(this);
        textHeader.setText("Select Calendar");

        textSelectDate.setOnClickListener(this);

        robotoCalendarView = (RobotoCalendarView) findViewById(R.id.robotoCalendarView);

        leftButton = (ImageView) robotoCalendarView.findViewById(R.id.leftButton);
        rightButton = (ImageView) robotoCalendarView.findViewById(R.id.rightButton);

        leftButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);

        imgCalendarImage = (ImageView) findViewById(R.id.imgCalendarImage);

        Glide.with(this).load(new File(path)).listener(new RequestListener<File, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(imgCalendarImage);
    }

    @Override
    public void onClick(View view) {
        if (view == textSelectDate) {
            MyDatePickerDialog myDatePickerDialog = new MyDatePickerDialog(this);
            myDatePickerDialog.showMonth("Select Month", 0, new MyDatePickerDialog.DatePickerListener() {
                @Override
                public void onCancel() {
                }

                @Override
                public void onOk(int year, int month, int day, Calendar calendar) {
                    robotoCalendarView.initializeCalendar(calendar);
                    textSelectDate.setText(simpleDateFormat.format(calendar.getTime()));
                    tmpcalendar = calendar;
                }
            });
        } else if (view == textNext) {

            Intent intent = new Intent(PrintableCalendarActivity.this, PrintCalendarTextActivity.class);
            intent.putExtra("calendar", tmpcalendar.getTimeInMillis());
            intent.putExtra("path", path);
            startActivity(intent);
        }
    }
}
