package com.firefighterscalendar.product;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.adapter.ProductAdapter;
import com.firefighterscalendar.bean.ProductBean;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopListActivity extends BaseActivity {

    private GridView gvItemList;
    private ProductAdapter productAdapter;
    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private HashMap<String, String> stringHashMap;
    private List<ProductBean> listProduct;
    private int offset = 1, flag = 1;
    private TextView textHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        initControl();
    }

    private void initControl() {
        listProduct = new ArrayList<>();

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
                Intent intent = new Intent(ShopListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText("Shop");

        gvItemList = (GridView) findViewById(R.id.gvItemList);

        productAdapter = new ProductAdapter(this, listProduct);
        gvItemList.setAdapter(productAdapter);
        gvItemList.setFocusable(false);

        gvItemList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && flag != -1)
                    if (utility.checkInternetConnection())
                        getAllProducts();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        gvItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(ShopListActivity.this, ProductDetailsActivity.class).putExtra("productid", listProduct.get(position).iProductId));
            }
        });

        if (utility.checkInternetConnection())
            getAllProducts();
    }

    private void getAllProducts() {
        stringHashMap = new HashMap<>();

        stringHashMap.put("offset", "" + offset);
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
                            listProduct.add(gson.fromJson(arrayResult.getJSONObject(i).toString(), ProductBean.class));
                        }
                        offset = jsonObject.getInt("offset");
                        productAdapter.notifyDataSetChanged();
                        if (arrayResult.length() == 0) {
                            if (!listProduct.isEmpty()) {
                                flag = -1;
                            }
                        }
                    } else
                        Toast.makeText(ShopListActivity.this, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_ALL_PRODUCTS);
    }
}
