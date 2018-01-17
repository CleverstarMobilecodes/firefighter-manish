package com.firefighterscalendar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.firefighterscalendar.R;
import com.firefighterscalendar.bean.ImageListBean;

public class FullImageActivity extends Activity {

    private ImageView imgFull;
    private ImageListBean imageListBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_full_image);
        initControl();
    }

    private void initControl() {
        imgFull = (ImageView) findViewById(R.id.imgFull);
        imageListBean = (ImageListBean) getIntent().getSerializableExtra("imagebean");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getScreenWidth() - 100, getScreenHeight() - 100);
        imgFull.setLayoutParams(layoutParams);

        Glide.with(this).load(imageListBean.imageUrl).fitCenter().into(imgFull);
    }

    public int getScreenWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public int getScreenHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }
}
