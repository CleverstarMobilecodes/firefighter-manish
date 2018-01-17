package com.firefighterscalendar.inapp;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Represents an in-app product's listing details.
 */
public class SkuDetails {

    String mItemType;
    String mSku;
    String mType;
    String mPrice;
    String mTitle;
    String mDescription;
    String mJson;
    String mCurrenCode;

    public SkuDetails(String jsonSkuDetails) throws JSONException {
        this(IabHelper.ITEM_TYPE_INAPP, jsonSkuDetails);
    }

    public SkuDetails(String itemType, String jsonSkuDetails) throws JSONException {
        mItemType = itemType;
        mJson = jsonSkuDetails;
        JSONObject o = new JSONObject(mJson);
        mSku = o.optString("productId");
        mType = o.optString("type");
        mPrice = o.optString("price");
        mCurrenCode = o.optString("price_currency_code");
        mTitle = o.optString("title");
        mDescription = o.optString("description");
    }

    public String getSku() {
        return mSku;
    }

    public String getType() {
        return mType;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getOriginalJSON() {
        return mJson;
    }

    public String getCurrencyCode() {
        return mCurrenCode;
    }

    @Override
    public String toString() {
        return "SkuDetails:" + mJson;
    }
}
