package com.firefighterscalendar.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.adapter.NewsAdapter;
import com.firefighterscalendar.bean.NewsBean;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NewsActivity extends BaseActivity implements View.OnClickListener {

    private ListView lvNews;
    private NewsAdapter newsAdapter;
    private HashMap<String, String> stringHashMap;
    private int offset = 1;
    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private TextView textHeader, textNoResult;
    private List<NewsBean> listNews;
    private int flag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        initControl();
    }

    private void initControl() {
        listNews = new ArrayList<>();

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
        textNoResult = (TextView) findViewById(R.id.textNoResult);
        textHeader.setText(R.string.news);

        lvNews = (ListView) findViewById(R.id.lvNews);

        newsAdapter = new NewsAdapter(this, listNews);
        lvNews.setAdapter(newsAdapter);
        lvNews.setOnItemClickListener(onItemClickListener);

        lvNews.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {
                if (flag == 1 && state == SCROLL_STATE_IDLE) {
                    if (utility.checkInternetConnection())
                        getNewsInfo();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        if (utility.checkInternetConnection())
            getNewsInfo();
    }


    private void getNewsInfo() {
        stringHashMap = new HashMap<>();
        stringHashMap.put("offset", "" + offset);

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        Gson gson = new Gson();
                        JSONArray arrayResult = jsonObject.getJSONArray("result");
                        for (int i = 0; i < arrayResult.length(); i++) {
                            listNews.add(gson.fromJson(arrayResult.getJSONObject(i).toString(), NewsBean.class));
                        }

                        if (arrayResult.length() == 0) {
                            if (!listNews.isEmpty()) {
                                flag = -1;
                                textNoResult.setVisibility(View.GONE);
                                lvNews.setVisibility(View.VISIBLE);
                            } else {
                                textNoResult.setVisibility(View.VISIBLE);
                                lvNews.setVisibility(View.GONE);
                            }
                        }
                        newsAdapter.notifyDataSetChanged();
                    }
                    offset = jsonObject.getInt("offset");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_ALL_NEWS);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            startActivity(new Intent(NewsActivity.this, NewsDetailsActivity.class).putExtra("newsId", listNews.get(i).iNewsId));
        }
    };

    @Override
    public void onClick(View view) {
        if (view == layoutRight) {
            Intent intent = new Intent(NewsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
