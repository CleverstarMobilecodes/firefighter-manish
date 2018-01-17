package com.firefighterscalendar.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NewsDetailsActivity extends BaseActivity implements View.OnClickListener {

    private TextView textHeader, textTitle, textDesc;
    private LinearLayout layoutRight;
    private Toolbar toolbar;
    private HashMap<String, String> stringHashMap;
    private String newsID;
    private ImageView imgNews, imgPlay;
    private VideoView videoView;
    private MediaController mediaController;
    private FrameLayout flVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        initControl();
    }

    private void initControl() {
        newsID = getIntent().getStringExtra("newsId");

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
        textTitle = (TextView) findViewById(R.id.textTitle);
        textDesc = (TextView) findViewById(R.id.textDesc);

        textHeader.setText("News");

        imgNews = (ImageView) findViewById(R.id.imgNews);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);

        flVideo = (FrameLayout) findViewById(R.id.flVideo);

        videoView = (VideoView) findViewById(R.id.videoView);
        mediaController = new MediaController(this);

        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        imgPlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!videoView.isPlaying()) {
                    videoView.start();
                    imgPlay.setVisibility(View.GONE);
                }
                return false;
            }
        });

        if (utility.checkInternetConnection())
            getNewsDetails();
    }

    private void getNewsDetails() {
        stringHashMap = new HashMap<>();
        stringHashMap.put("iNewsId", newsID);
        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONObject objResult = jsonObject.getJSONObject("result");
                        textTitle.setText(objResult.getString("vTitle"));
                        textDesc.setText(objResult.getString("txDescription"));

                        if (!objResult.getString("txImage").equalsIgnoreCase("")) {
                            imgNews.setVisibility(View.VISIBLE);
                            Glide.with(NewsDetailsActivity.this).load(objResult.getString("txImage")).fitCenter().into(imgNews);
                        } else {
                            imgNews.setVisibility(View.GONE);
                        }

                        if (!objResult.getString("txVideo").equalsIgnoreCase("")) {
                            flVideo.setVisibility(View.VISIBLE);
                            videoView.setVideoURI(Uri.parse(objResult.getString("txVideo")));
                        } else {
                            flVideo.setVisibility(View.GONE);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_NEWS_DETAILS);
    }

    @Override
    public void onClick(View view) {
        if (view == layoutRight) {
            Intent intent = new Intent(NewsDetailsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
