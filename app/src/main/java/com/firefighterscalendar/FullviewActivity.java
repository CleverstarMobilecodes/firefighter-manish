package com.firefighterscalendar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.adapter.ImagePagerAdapter;
import com.firefighterscalendar.bean.ImageListBean;
import com.firefighterscalendar.custom.UnlockDialogActivity;

import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class FullviewActivity extends BaseActivity implements View.OnClickListener {

    private TextView textUnlock;
    private boolean flag = false;
    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private AutoScrollViewPager viewPager;
    private List<ImageListBean> listImageSlide;
    private TextView textHeader;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullview);
        initControls();
    }

    private void initControls() {
        listImageSlide = (List<ImageListBean>) getIntent().getSerializableExtra("listImages");
        position = getIntent().getIntExtra("position", 0);

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
                Intent intent = new Intent(FullviewActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText(listImageSlide.get(0).title);

        textUnlock = (TextView) findViewById(R.id.textUnlock);
        textUnlock.setOnClickListener(this);

        viewPager = (AutoScrollViewPager) findViewById(R.id.viewPager);
        setSliderTime(0, flag);

        if (sessionManager.getStringDetail("subscription").equalsIgnoreCase("yes")) {
            textUnlock.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.img_play, 0, 0);
            textUnlock.setText(R.string.tap_to_play);
        }
        else {
            textUnlock.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.img_lock, 0, 0);
            textUnlock.setText(R.string.tap_to_unlock);
        }
    }

    private void setSliderTime(int time, boolean isSlide) {
        viewPager.setInterval(time);
        viewPager.startAutoScroll();
        viewPager.setBorderAnimation(true);
        viewPager.setCycle(true);
        viewPager.setAdapter(new ImagePagerAdapter(this, listImageSlide, isSlide));

        if (sessionManager.getStringDetail("subscription").equalsIgnoreCase("yes"))
            viewPager.setCurrentItem(position);
    }

    @Override
    public void onClick(View v) {
        if (v == textUnlock) {
            if (sessionManager.getStringDetail("subscription").equalsIgnoreCase("no"))
                startActivityForResult(new Intent(this, UnlockDialogActivity.class), 1);
            else
                showTimeDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            textUnlock.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.img_play, 0, 0);
            textUnlock.setText(R.string.tap_to_play);
            flag = true;
        }
    }

    private void showTimeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_slide_time);
        dialog.setTitle("");
        dialog.setCanceledOnTouchOutside(false);

        dialog.findViewById(R.id.text3s).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSliderTime(3000, true);
                dialog.dismiss();
                findViewById(R.id.llSlide).setVisibility(View.GONE);
                textUnlock.setVisibility(View.GONE);
            }
        });
        dialog.findViewById(R.id.text5s).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSliderTime(5000, true);
                dialog.dismiss();
                findViewById(R.id.llSlide).setVisibility(View.GONE);
                textUnlock.setVisibility(View.GONE);
            }
        });
        dialog.findViewById(R.id.text10s).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSliderTime(10000, true);
                dialog.dismiss();
                findViewById(R.id.llSlide).setVisibility(View.GONE);
                textUnlock.setVisibility(View.GONE);
            }
        });

        dialog.show();
    }
}
