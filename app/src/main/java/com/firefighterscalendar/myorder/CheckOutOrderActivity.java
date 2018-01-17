package com.firefighterscalendar.myorder;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.bean.ProductBean;
import com.firefighterscalendar.custom.CustomListView;
import com.firefighterscalendar.paypal.PayPalIntegrationActivity;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.Constant;
import com.firefighterscalendar.utils.Utility;
import com.firefighterscalendar.utils.onConfirm;
import com.firefighterscalendar.utils.onTaskComplete;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckOutOrderActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llItems, llShipingAddress, llItemsDetail, llShippingAddressDetail;
    private ImageView imgIndicator1, imgIndicator2, btnCheckout;
    private CustomListView lvOrders;
    private List<ProductBean> listData;
    private Toolbar toolbar;
    private LinearLayout layoutRight, llCartHeader;
    private TextView textHeader, textSubTotal, textSavings, textGST, textShipping, textTotal, textEmptyCart;
    private OrderItemAdapter orderItemAdapter;
    private EditText editName, editAddress, editState, editPincode, editPhone, editEmail;
    private double totalPrice = 0, shippingAmount = 0;
    private JSONArray arrayProductId, arrayQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_order);
        initControl();
    }

    private void initControl() {
        listData = new ArrayList<>();

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

        llCartHeader = (LinearLayout) findViewById(R.id.llCartHeader);
        layoutRight = (LinearLayout) toolbar.findViewById(R.id.layoutRight);
        layoutRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOutOrderActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        textSubTotal = (TextView) findViewById(R.id.textSubTotal);
        textSavings = (TextView) findViewById(R.id.textSavings);
        textGST = (TextView) findViewById(R.id.textGST);
        textTotal = (TextView) findViewById(R.id.textTotal);
        textShipping = (TextView) findViewById(R.id.textShipping);
        textEmptyCart = (TextView) findViewById(R.id.textEmptyCart);

        textHeader.setText("My Cart");

        editName = (EditText) findViewById(R.id.editName);
        editAddress = (EditText) findViewById(R.id.editAddress);
        editState = (EditText) findViewById(R.id.editState);
        editPincode = (EditText) findViewById(R.id.editPincode);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editEmail = (EditText) findViewById(R.id.editEmail);

        lvOrders = (CustomListView) findViewById(R.id.lvOrders);

        imgIndicator1 = (ImageView) findViewById(R.id.imgIndicator1);
        imgIndicator2 = (ImageView) findViewById(R.id.imgIndicator2);

        btnCheckout = (ImageView) findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(this);

        llItems = (LinearLayout) findViewById(R.id.llItems);
        llShipingAddress = (LinearLayout) findViewById(R.id.llShipingAddress);
        llItemsDetail = (LinearLayout) findViewById(R.id.llItemsDetail);
        llShippingAddressDetail = (LinearLayout) findViewById(R.id.llShippingAddressDetail);

        llItems.setOnClickListener(this);
        llShipingAddress.setOnClickListener(this);

        orderItemAdapter = new OrderItemAdapter(this, sessionManager.getStringDetail("iUserId"));
        lvOrders.setAdapter(orderItemAdapter);

        if (utility.checkInternetConnection()) {
            displayUserCart();
            getAddress();
        }
        showItems(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            Log.i("transaction key", data.getStringExtra("paykey"));
            if (utility.checkInternetConnection())
                placeOrderAPICall(data.getStringExtra("paykey"));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnCheckout) {
            if (listData.isEmpty()) {
                utility.showYesNoDialog("Yes", "No", "Your cart is empty. Would you like to continue shopping ?", new onConfirm() {
                    @Override
                    public void onChooseYes(boolean isTrue) {
                        if (isTrue)
                            startActivity(new Intent(CheckOutOrderActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
            }
            else {
                checkValidation();
            }
        }
        else if (v == llItems) {
            showItems(true);
            showShipping(false);
        }
        else if (v == llShipingAddress) {
            showItems(false);
            showShipping(true);
        }
        utility.hideSoftKeyboard(CheckOutOrderActivity.this);
    }

    private void checkValidation() {

        if (TextUtils.isEmpty(getTextvalue(editName))) {
            editName.setError("Please enter name");
            editName.requestFocus();
            llShippingAddressDetail.setVisibility(View.VISIBLE);
        }
        else if (TextUtils.isEmpty(getTextvalue(editAddress))) {
            editAddress.setError("Please enter address");
            editAddress.requestFocus();
            llShippingAddressDetail.setVisibility(View.VISIBLE);
        }
        else if (TextUtils.isEmpty(getTextvalue(editPincode))) {
            llShippingAddressDetail.setVisibility(View.VISIBLE);
            editPincode.setError("Please enter pincode");
            editPincode.requestFocus();
        }
        else if (TextUtils.isEmpty(getTextvalue(editPhone))) {
            llShippingAddressDetail.setVisibility(View.VISIBLE);
            editPhone.setError("Please enter phone number");
            editPhone.requestFocus();
        }
        else if (!utility.isValidEmail(editEmail)) {
            llShippingAddressDetail.setVisibility(View.VISIBLE);
            editEmail.setError("Please enter valid email address");
            editEmail.requestFocus();
        }
        else {
            llShippingAddressDetail.setVisibility(View.GONE);
            if (utility.checkInternetConnection())
                startActivityForResult(new Intent(this, PayPalIntegrationActivity.class).putExtra("amount", totalPrice), 1);
        }
    }


    private void showItems(boolean b) {
        if (b) {
            llItemsDetail.setVisibility(llItemsDetail.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            imgIndicator1.setImageResource(llItemsDetail.getVisibility() == View.VISIBLE ? R.drawable.downarrow : R.drawable.leftarrow);
        }
        else {
            llItemsDetail.setVisibility(View.GONE);
            imgIndicator1.setImageResource(R.drawable.leftarrow);
        }
    }

    private void showShipping(boolean b) {
        if (b) {
            llShippingAddressDetail.setVisibility(llShippingAddressDetail.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            imgIndicator2.setImageResource(llShippingAddressDetail.getVisibility() == View.VISIBLE ? R.drawable.downarrow : R.drawable.leftarrow);
        }
        else {
            llShippingAddressDetail.setVisibility(View.GONE);
            imgIndicator2.setImageResource(R.drawable.leftarrow);
        }
    }

    private void getAddress() {
        stringHashMap = new HashMap<>();
        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONObject objResult = jsonObject.getJSONObject("result");

                        editName.setText(objResult.getString("vShippingName"));
                        editAddress.setText(objResult.getString("txShippingAddress"));
                        editState.setText(objResult.getString("vStateName"));
                        editPincode.setText(objResult.getString("iPincode"));
                        editPhone.setText(objResult.getString("vShippingPhoneNo"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_SHIPPING_ADDRESS);
    }

    private void displayUserCart() {
        stringHashMap = new HashMap<>();
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
                            listData.add(gson.fromJson(arrayResult.getJSONObject(i).toString(), ProductBean.class));
                        }

                        if (!listData.isEmpty()) {
                            textEmptyCart.setVisibility(View.GONE);
                            lvOrders.setVisibility(View.VISIBLE);
                            llCartHeader.setVisibility(View.VISIBLE);
                        }
                        else {
                            textEmptyCart.setVisibility(View.VISIBLE);
                            lvOrders.setVisibility(View.GONE);
                            llCartHeader.setVisibility(View.GONE);
                        }

                        updatePrice();
                    }
                    orderItemAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_DISPLAY_USER_CART);
    }

    private void updatePrice() {
        shippingAmount = 0;
        totalPrice = 0;

        arrayProductId = new JSONArray();
        arrayQty = new JSONArray();

        for (int i = 0; i < listData.size(); i++) {
            totalPrice = totalPrice + (Double.parseDouble(listData.get(i).dPrice) * Integer.parseInt(listData.get(i).iQty));

            arrayQty.put(listData.get(i).iQty);
            arrayProductId.put(listData.get(i).iProductId);
        }

        textSubTotal.setText("$" + String.format("%.2f", totalPrice));
        textShipping.setText("FREE");
        textTotal.setText("$" + String.format("%.2f", (totalPrice + shippingAmount)));
        textGST.setText("$0.0");
        textSavings.setText("$0.0");

    }

    private void placeOrderAPICall(String transactionId) {
        stringHashMap = new HashMap<>();

        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("iProductId", arrayProductId.toString());
        stringHashMap.put("iQty", arrayQty.toString());
        stringHashMap.put("txTransactionId", transactionId);
        stringHashMap.put("dTotalPrice", "" + totalPrice);
        stringHashMap.put("vShippingName", getTextvalue(editName));
        stringHashMap.put("vShippingPhoneNo", getTextvalue(editPhone));
        stringHashMap.put("txShippingAddress", getTextvalue(editAddress));
        stringHashMap.put("vStateName", getTextvalue(editState));
        stringHashMap.put("iPincode", getTextvalue(editPincode));

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        startActivity(new Intent(CheckOutOrderActivity.this, ProcessOrderActivity.class).putExtra("response", response));
                    }
                    else {
                        utility.showOkDialog(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_ADD_USER_ORDER);
    }


    public class OrderItemAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private MyViewHolder mViewHolder;
        private Utility utility;
        private Context context;
        private HashMap<String, String> stringHashMap;
        private String userId;

        public OrderItemAdapter(Context context, String userId) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            utility = new Utility(context);
            this.userId = userId;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public ProductBean getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            mViewHolder = new MyViewHolder();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_order_list_item, parent, false);
                mViewHolder.textOrderItem = (TextView) convertView.findViewById(R.id.textOrderItem);
                mViewHolder.imgRemove = (ImageView) convertView.findViewById(R.id.imgRemove);
                mViewHolder.textItemPrice = (TextView) convertView.findViewById(R.id.textItemPrice);
                mViewHolder.textQty = (TextView) convertView.findViewById(R.id.textQty);
                convertView.setTag(mViewHolder);
            }
            else {
                mViewHolder = (MyViewHolder) convertView.getTag();
            }

            mViewHolder.textOrderItem.setText(getItem(position).vProductName);
            mViewHolder.textQty.setText(getItem(position).iQty);
            mViewHolder.textItemPrice.setText("$" + String.format("%.2f", Double.parseDouble(getItem(position).dPrice)));

            mViewHolder.imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utility.showYesNoDialog("Yes", "No", "Are you sure want to remove " + getItem(position).vProductName + " from cart?", new onConfirm() {
                        @Override
                        public void onChooseYes(boolean isTrue) {
                            if (utility.checkInternetConnection())
                                removeItemFromCart(position);
                        }
                    });
                }
            });

            return convertView;
        }

        private class MyViewHolder {
            TextView textOrderItem, textItemPrice, textQty;
            ImageView imgRemove;
        }

        private void removeItemFromCart(final int position) {
            stringHashMap = new HashMap<>();

            stringHashMap.put("iProductId", getItem(position).iProductId);
            stringHashMap.put("iUserId", userId);

            new AllAPICall(context, stringHashMap, null, new onTaskComplete() {
                @Override
                public void onComplete(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (jsonObject.getInt("response_status") == 1) {
                            listData.remove(position);
                            notifyDataSetChanged();
                            updatePrice();
                        }
                        Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constant.URL_REMOVE_PRODUCT_FROM_CART);
        }
    }
}
