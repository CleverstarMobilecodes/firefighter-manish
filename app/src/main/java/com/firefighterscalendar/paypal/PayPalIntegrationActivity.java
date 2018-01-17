package com.firefighterscalendar.paypal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PayPalIntegrationActivity extends BaseActivity {

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    // sandbox
//    private static final String CONFIG_CLIENT_ID = "ARJaAnrKx6FPeLGdkZ2kwEb-Qg7Sa78mCJIVAMEyAw0XPMNQdZjnLvW2N7SC4J3Bv1M8W8rVNg3LHrbv";
    // live
    private static final String CONFIG_CLIENT_ID = "Ae_yB47l78M96CgZXxaW0572csUu8JrmPwuOU324IJweOXVXJoFg1xxbqzOy4IinCW9oVsataF854kaJ";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);

    private double amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amount = getIntent().getDoubleExtra("amount", 0);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        onBuyPressed();
    }

    public void onBuyPressed() {

        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal(amount), "USD", getResources().getString(R.string.app_name),
                paymentIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("tag_client", confirm.toJSONObject().toString(4));
                        Log.i("tag", confirm.getPayment().toJSONObject().toString(4));

                        JSONObject jsonObject = new JSONObject(confirm.toJSONObject().toString());
                        if (jsonObject.getString("response_type").equals("payment")) {
                            Intent intent = new Intent();
                            intent.putExtra("paykey", jsonObject.getJSONObject("response").getString("id"));
                            setResult(1, intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e("tag", "an extremely unlikely failure occurred: ", e);
                    }
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("tag", "The user canceled.");
                finish();
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(PayPalIntegrationActivity.this, "The specified amount must be greater than zero", Toast.LENGTH_SHORT).show();
                finish();
                Log.i("tag", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}
