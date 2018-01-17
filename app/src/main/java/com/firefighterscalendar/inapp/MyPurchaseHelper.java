package com.firefighterscalendar.inapp;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class MyPurchaseHelper {

    private IabHelper mHelper;

    private boolean already_purchased = false;

    private Activity mActivity;

    public static final String MSG_INITIAL_INVENTORY_FINISH = "msg_intial_inventory_finish";
    public static final String MSG_PURCHASE_FINISH = "msg_purchase_finish";
    public static final String MSG_ALREADY_PURCHASED = "msg_already_purchased";

    private String selectedSKU;
    private boolean isUnManagedSKU = false;

    public interface OnPurchaseListener {

        void onSuccess(String msg, String orderId);

        void onFail(String msg);
    }

    private OnPurchaseListener onPurchaseListener;

    public MyPurchaseHelper(Activity activity, String selectedSKU, OnPurchaseListener listener) {

        this.selectedSKU = selectedSKU;
        mActivity = activity;
        onPurchaseListener = listener;
        init();
    }


    private void init() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAofJbyq9BlgB7brRGjZWaknv87eS4LMSRxIxoH96h4jiGeH8VUJ8h9yE1gBJRPt0CW/janUbEyFqXDeC98GTrfIdfajHkcz8BYPX9ZLsytCzdVvupVzj2bEA2QrgLawm2JZoLKKhbEhccJofAAnH9QRq/hUHgkGTgqGRkj2lBQg/Ov2oTWQyVBboSFqUMJPkJ3SXl53Ev90Yqh+KTnwqlYUSGKCd/EVIggxgCJ76KTbBYXrUvyP+UHMInWnFlhKKoxitEiMqpGEiRbhKuOnNionvgAoHyhgJJnyd1TWty3ptK/k6g9ERG2AM9cTCLPTaE6+TqDohUU7SLsPpN9rFfJQIDAQAB";

        Log.d("log_tag", "Creating IAB helper.");
        mHelper = new IabHelper(mActivity, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            public void onIabSetupFinished(IabResult result) {
                Log.d("log_tag", "Setup finished.  result: " + result);
                if (!result.isSuccess()) {
                    complain(result.mMessage);
                    return;
                } else {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                }
                Log.d("log_tag", "Setup successful. Querying inventory.");

            }
        });
    }

    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d("log_tag", "Query inventory finished.");

            if (result.isFailure()) {
                complain(result.mMessage);
                return;
            }

            Log.d("log_tag", "Query inventory was successful.");

            Purchase applicationSubscription = inventory.getPurchase(selectedSKU);
            if (applicationSubscription != null) {
                Log.w("log_tag", "inApp/Subscription Res... " + applicationSubscription.toString());
                already_purchased = true;
                onPurchaseListener.onSuccess(MSG_ALREADY_PURCHASED, applicationSubscription.mOrderId);
                return;
            }

            Log.d("log_tag", "Initial inventory query finished; enabling main UI.");
            onPurchaseListener.onSuccess(MSG_INITIAL_INVENTORY_FINISH, "");
        }
    };

    public void onPurchaseClick() {
        if (!mHelper.subscriptionsSupported()) {
            toast("Subscriptions not supported on your device yet. Sorry!");
            complain("Subscriptions not supported on your device yet. Sorry!");
            return;
        }

        if (already_purchased) {
            onPurchaseListener.onSuccess(MSG_ALREADY_PURCHASED, "");
            return;
        }
        if (mHelper.mAsyncInProgress) {
            complain("Task already running!");
            return;
        }
        mHelper.launchPurchaseFlow(mActivity, selectedSKU, 10001, mPurchaseFinishedListener, "");
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {

        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d("log_tag", "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                complain(result.mMessage);
                return;
            }

            Log.d("log_tag", "Purchase successful.");
            if (purchase.getSku().equals(selectedSKU)) {
                if (isUnManagedSKU) {
                    //only for Unmanaged
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } else {
                    //only for managed
                    onPurchaseListener.onSuccess(MSG_PURCHASE_FINISH, purchase.mOrderId);
                    Log.d("log_tag", "purchased.");
                }
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {

        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d("log_tag", "Consumption finished. Purchase: " + purchase + ", result: " + result);

            if (result.isSuccess()) {
                onPurchaseListener.onSuccess(MSG_PURCHASE_FINISH, purchase.mOrderId);
            } else {
                complain("Error while consuming: " + result);
            }
            Log.d("log_tag", "End consumption flow.");
        }
    };


    private void toast(String string) {
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    protected void complain(String string) {
        Log.e("log_tag", string);
        onPurchaseListener.onFail(string);
    }
}