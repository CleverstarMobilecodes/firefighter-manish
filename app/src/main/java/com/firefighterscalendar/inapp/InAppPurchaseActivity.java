package com.firefighterscalendar.inapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.facebook.appevents.AppEventsLogger;
import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;


public class InAppPurchaseActivity extends BaseActivity {

    private IInAppBillingService mService;
//    static final String SKU_FIREFIGHTER = "android.test.purchased";
    static final String SKU_FIREFIGHTER = "firefighter_099";
    private MyPurchaseHelper mPurchaseHelper;
    AppEventsLogger logger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        setSKUAndCallService(SKU_FIREFIGHTER);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(InAppPurchaseActivity.this);
    }

    private void setSKUAndCallService(final String SKU) {
        mPurchaseHelper = new MyPurchaseHelper(this, SKU, new MyPurchaseHelper.OnPurchaseListener() {
            @Override
            public void onSuccess(String msg, String orderId) {
                final Bundle bundle;
                switch (msg) {
                    case MyPurchaseHelper.MSG_ALREADY_PURCHASED:
                        Log.w("log_tag", "Already Purchased!" + orderId);
//                        logger = AppEventsLogger.newLogger(InAppPurchaseActivity.this);
//                        logger.logPurchase(BigDecimal.valueOf(0.99), Currency.getInstance("USD"));
//
//                        bundle = new Bundle();
//                        bundle.putDouble(FirebaseAnalytics.Param.VALUE, 0.99);
//                        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
//                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//                        if (utility.checkInternetConnection())
//                            setUserSubscription(orderId);
                            updateSubscription(orderId);

                        break;
                    case MyPurchaseHelper.MSG_PURCHASE_FINISH:
                        Log.w("log_tag", "Purchased Successfully!");
                        logger = AppEventsLogger.newLogger(InAppPurchaseActivity.this);
                        logger.logPurchase(BigDecimal.valueOf(0.99), Currency.getInstance("USD"));

                        bundle = new Bundle();
                        bundle.putDouble(FirebaseAnalytics.Param.VALUE, 0.99);
                        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        if (utility.checkInternetConnection())
                            setUserSubscription(orderId);
//                            updateSubscription(orderId);
                        break;
                    default:
                        mPurchaseHelper.onPurchaseClick();
                        Log.w("log_tag", "inApp Purchase...");
                        break;
                }


            }

            @Override
            public void onFail(String msg) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            unbindService(mServiceConn);
        }
    }

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);

            try {
                mService.getPurchases(3, getPackageName(), "inapp", null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Log.w("log_tag", "inApp Purchase done");
            }
        }

        if (mPurchaseHelper != null) {
            if (!mPurchaseHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
            else {
                Log.v("log_tag", "onActivityResult handled by IABUtil.");
            }
        }
    }

    private void setUserSubscription(String subscriptionKey) {
        stringHashMap = new HashMap<>();

        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("vSubscriptionKey", subscriptionKey);

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Toast.makeText(InAppPurchaseActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();

                    checkUserSubscriptionAPICall(new onTaskComplete() {
                        @Override
                        public void onComplete(String response) {
                            setResult(1);
                            logger = AppEventsLogger.newLogger(InAppPurchaseActivity.this);
                            logger.logPurchase(BigDecimal.valueOf(0.99), Currency.getInstance("USD"));

                            Bundle bundle = new Bundle();
                            bundle.putDouble(FirebaseAnalytics.Param.VALUE, 0.99);
                            bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_USER_SUBSCRIPTION);
    }

    private void updateSubscription(String subscriptionKey) {
        stringHashMap = new HashMap<>();

        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("ltSubscriptionKey", subscriptionKey);
        stringHashMap.put("eSubscription", "yes");

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Toast.makeText(InAppPurchaseActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();

                    checkUserSubscriptionAPICall(new onTaskComplete() {
                        @Override
                        public void onComplete(String response) {
                            setResult(1);
//                            logger = AppEventsLogger.newLogger(InAppPurchaseActivity.this);
//                            logger.logPurchase(BigDecimal.valueOf(0.99), Currency.getInstance("USD"));
//
//                            Bundle bundle = new Bundle();
//                            bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
//                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_UPDATE_USER_SUBSCRIPTION);
    }
}