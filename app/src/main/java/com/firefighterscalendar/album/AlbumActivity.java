package com.firefighterscalendar.album;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.adapter.AlbumAdapter;
import com.firefighterscalendar.bean.AlbumBean;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlbumActivity extends BaseActivity {

    private List<AlbumBean> listAlbums;
    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private HashMap<String, String> stringHashMap;
    private TextView textNoResult;
    int offset = 0, flag = 0;
    private GridLayoutManager layoutManager;
    private TextView textHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initControls();
    }

    private void initControls() {
        listAlbums = new ArrayList<>();

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
                Intent intent = new Intent(AlbumActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        assert textHeader != null;
        textHeader.setText(R.string.gallery);

        recyclerView = (RecyclerView) findViewById(R.id.recycleviewAlbums);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(false);
        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(onScrollListener);

        textNoResult = (TextView) findViewById(R.id.textNoResult);
    }

    @Override
    protected void onResume() {
        super.onResume();
        albumAdapter = new AlbumAdapter(AlbumActivity.this, listAlbums, sessionManager.getStringDetail("subscription"));
        recyclerView.setAdapter(albumAdapter);

        if (utility.checkInternetConnection()) {
            offset = 0;
            flag = 0;
            listAlbums.clear();
            getAllAlbumAPICall(offset);
        }
    }

    private void getAllAlbumAPICall(int pageNo) {
        stringHashMap = new HashMap<>();

        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("offset", "" + pageNo);
        stringHashMap.put("eAlbumCategory", getIntent().getStringExtra("type"));

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    offset = jsonObject.getInt("offset");
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONArray arrayResult = jsonObject.getJSONArray("result");
                        Gson gson = new Gson();

                        for (int i = 0; i < arrayResult.length(); i++) {
                            AlbumBean albumBean = gson.fromJson(arrayResult.getJSONObject(i).toString(), AlbumBean.class);
                            listAlbums.add(albumBean);
                        }

                        albumAdapter.notifyDataSetChanged();

                        if (listAlbums.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            textNoResult.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            textNoResult.setVisibility(View.GONE);
                        }
//                        recyclerView.scrollToPosition(layoutManager.findLastVisibleItemPosition());

                        if (arrayResult.length() == 0) {
                            flag = -1;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_ALL_ALBUMS);
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && flag != -1) {
                getAllAlbumAPICall(offset);
            }
            Log.i("last position", "" + layoutManager.findLastCompletelyVisibleItemPosition());
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };
}
