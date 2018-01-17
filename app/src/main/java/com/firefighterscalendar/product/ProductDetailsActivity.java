package com.firefighterscalendar.product;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.myorder.CheckOutOrderActivity;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onConfirm;
import com.firefighterscalendar.utils.onTaskComplete;
import com.shawnlin.numberpicker.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ProductDetailsActivity extends BaseActivity implements View.OnClickListener, android.widget.NumberPicker.OnValueChangeListener {

    private TextView textPrice, textProductTitle, textDescription, textAddedItem, textQty, textItemPrice;
    private ImageView imgCheckOut, imgAddToCart, imgProduct;
    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private HashMap<String, String> stringHashMap;
    private TextView textHeader;
    private LinearLayout llSingleCart;
    int quantity = 1;
    int max = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
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
                Intent intent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        textHeader = (TextView) findViewById(R.id.textHeader);
        textPrice = (TextView) findViewById(R.id.textPrice);
        textProductTitle = (TextView) findViewById(R.id.textProductTitle);
        textDescription = (TextView) findViewById(R.id.textDescription);

        textAddedItem = (TextView) findViewById(R.id.textAddedItem);
        textQty = (TextView) findViewById(R.id.textQty);
        textItemPrice = (TextView) findViewById(R.id.textItemPrice);

        textHeader.setText(HEADER_USER_NAME);

        imgCheckOut = (ImageView) findViewById(R.id.imgCheckOut);
        imgProduct = (ImageView) findViewById(R.id.imgProduct);
        imgAddToCart = (ImageView) findViewById(R.id.imgAddToCart);

        imgCheckOut.setOnClickListener(this);
        imgAddToCart.setOnClickListener(this);

        llSingleCart = (LinearLayout) findViewById(R.id.llSingleCart);

        textAddedItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (textAddedItem.getLeft() - textAddedItem.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        utility.showYesNoDialog("Yes", "No", "Are you sure want to remove item from cart ?", new onConfirm() {
                            @Override
                            public void onChooseYes(boolean isTrue) {
                                if (utility.checkInternetConnection())
                                    deleteItemFromCart();
                            }
                        });
                    }
                }
                return true;
            }
        });

        if (utility.checkInternetConnection())
            getProductDetailsAPICall();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (utility.checkInternetConnection())
            getProductDetailsAPICall();
    }

    @Override
    public void onClick(View v) {
        if (v == imgCheckOut) {
            startActivity(new Intent(this, CheckOutOrderActivity.class));
        } else if (v == imgAddToCart) {
            openQuantityDialog();
        }
    }

    private void getProductDetailsAPICall() {
        stringHashMap = new HashMap<>();

        stringHashMap.put("iProductId", getIntent().getStringExtra("productid"));
        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONObject objResult = jsonObject.getJSONObject("result");

                        textPrice.setText(Html.fromHtml("<sup><small>$</small></sup><big>" + objResult.getString("dPrice").substring(0, objResult.getString("dPrice").indexOf(".")) + "</big><sup><small>"
                                + String.format("%.2f", objResult.getDouble("dPrice")).substring(objResult.getString("dPrice").indexOf(".") + 1) + "</small></sup>"));

                        textProductTitle.setText(objResult.getString("vProductName"));
                        textDescription.setText(Html.fromHtml(objResult.getString("txDescription")));
                        Glide.with(ProductDetailsActivity.this).load(objResult.getString("txImage")).into(imgProduct);

                        textAddedItem.setText(objResult.getString("vProductName"));
                        textQty.setText(objResult.getString("iQty"));
                        textItemPrice.setText("$" + String.format("%.2f", objResult.getDouble("dPrice")));

                        max = objResult.getInt("vStock");

                        if (objResult.getInt("iQty") == 0) {
                            llSingleCart.setVisibility(View.GONE);
                        } else {
                            llSingleCart.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_PRODUCT_DETAILS);
    }

    private void openQuantityDialog() {
        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_number_picker);
        dialog.setTitle("Select quantity");
        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.number_picker);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                quantity = np.getValue();
                if (utility.checkInternetConnection())
                    addToCartAPICall();
            }
        });

        np.setMaxValue(max);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {

    }

    private void addToCartAPICall() {
        stringHashMap = new HashMap<>();

        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("iProductId", getIntent().getStringExtra("productid"));
        stringHashMap.put("iQty", "" + quantity);

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Toast.makeText(ProductDetailsActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    onResume();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_ADD_PRODUCT_INTO_CART);
    }

    private void deleteItemFromCart() {
        stringHashMap = new HashMap<>();

        stringHashMap.put("iProductId", getIntent().getStringExtra("productid"));
        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        Toast.makeText(ProductDetailsActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        onResume();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_REMOVE_PRODUCT_FROM_CART);
    }


}