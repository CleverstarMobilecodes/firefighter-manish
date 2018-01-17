package com.firefighterscalendar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.adapter.ExpandableListAdapter;
import com.firefighterscalendar.bean.CategoryBean;
import com.firefighterscalendar.bean.SubCategoryBean;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.OpenGallery;
import com.firefighterscalendar.utils.onTaskComplete;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectThemeActivity extends BaseActivity {

    private ExpandableListView mExpandableListView;
    private int lastExpandedPosition = -1;
    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private HashMap<String, String> stringHashMap;
    private List<CategoryBean> listCategory;
    private ExpandableListAdapter listAdapter;
    private TextView textHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_theme);
        initControls();
    }

    private void initControls() {
        listCategory = new ArrayList<>();

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
                Intent intent = new Intent(SelectThemeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText(HEADER_USER_NAME);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expanListTheme);

        listAdapter = new ExpandableListAdapter(this, listCategory);

        mExpandableListView.setAdapter(listAdapter);
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(SelectThemeActivity.this, AddTextActivity.class);
                intent.putExtra("imgurl", listCategory.get(groupPosition).listSubCategory.get(childPosition).subCategoryImage);
                startActivity(intent);
                return false;
            }
        });

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listCategory.get(groupPosition).categoryName.equalsIgnoreCase("custom"))
                    startActivityForResult(new Intent(SelectThemeActivity.this, OpenGallery.class), 1);
                return false;
            }
        });

        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    mExpandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        if (utility.checkInternetConnection())
            getCategories();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (utility.resize(data.getStringExtra("picturepath")) != null) {
                Intent intent = new Intent(SelectThemeActivity.this, AddTextActivity.class);
                intent.putExtra("imgurl", data.getStringExtra("picturepath"));
                startActivity(intent);
            } else
                utility.showOkDialog("Unable to fetch image.Please select another image");
        }
    }

    private void getCategories() {

        stringHashMap = new HashMap<>();
        stringHashMap.put("offset", "1");

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
                            listCategory.add(gson.fromJson(arrayResult.getJSONObject(i).toString(), CategoryBean.class));

                            JSONArray arraySubCategory = arrayResult.getJSONObject(i).getJSONArray("subCategory");
                            listCategory.get(i).listSubCategory = new ArrayList<>();

                            for (int j = 0; j < arraySubCategory.length(); j++) {
                                listCategory.get(i).listSubCategory.add(gson.fromJson(arraySubCategory.getJSONObject(j).toString(), SubCategoryBean.class));
                            }
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_ALL_CATEGORIES);
    }
}
