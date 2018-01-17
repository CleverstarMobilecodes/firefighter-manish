package com.firefighterscalendar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.adapter.ImageSavedAdapter;
import com.firefighterscalendar.bean.ImageListBean;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectImageActivity extends BaseActivity {

    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private RecyclerView recyclerView;
    private ImageSavedAdapter imageSavedAdapter;
    private List<ImageListBean> listImages;
    private HashMap<String, String> stringHashMap;
    private int pageNo = 1, flag = 1;
    private TextView textHeader;
    private boolean isCalendar = false;
    private TextView textNoImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images);
        initControls();
    }

    private void initControls() {
        listImages = new ArrayList<>();
        isCalendar = getIntent().getBooleanExtra("calendar", false);

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
                Intent intent = new Intent(SelectImageActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        textNoImages = (TextView) findViewById(R.id.textNoImages);
        textHeader.setText("Select Image");

        recyclerView = (RecyclerView) findViewById(R.id.recycleviewImages);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        imageSavedAdapter = new ImageSavedAdapter(this, listImages, "text", isCalendar);
        recyclerView.setAdapter(imageSavedAdapter);

        recyclerView.addOnScrollListener(onScrollListener);

        if (utility.checkInternetConnection())
            getSavedImagesAPICall(pageNo, true);
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && flag == 1) {
                if (utility.checkInternetConnection())
                    getSavedImagesAPICall(pageNo, false);
            }
        }
    };

    private void getSavedImagesAPICall(int offset, boolean b) {
        stringHashMap = new HashMap<>();

        stringHashMap.put("offset", String.valueOf(offset));
        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));

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
                            listImages.add(gson.fromJson(arrayResult.getJSONObject(i).toString(), ImageListBean.class));
                        }
                        pageNo = jsonObject.getInt("offset");

                        if (arrayResult.length() == 0) {
                            if (listImages.isEmpty()) {
                                textNoImages.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                textNoImages.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            flag = -1;
                        }
                        imageSavedAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, b).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_ALL_ALBUM_IMAGES);
    }
}
