package com.firefighterscalendar.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

public class AllAPICall extends AsyncTask<String, String, String> {

    private onTaskComplete mOnTaskComplete;
    private GetJsonUrl jsonParser;
    private Context context;
    private Utility utility;
    private HashMap<String, String> pairListString;
    private HashMap<String, File> pairListFile;
    private String response;
    boolean isShow = true;

    public AllAPICall(Context context, HashMap<String, String> pairListString, HashMap<String, File> pairListFile, onTaskComplete taskComplete) {
        this.mOnTaskComplete = taskComplete;
        this.context = context;
        utility = new Utility(context);
        this.pairListString = pairListString;
        this.pairListFile = pairListFile;
        isShow = true;
        if (pairListString != null)
            this.pairListString = SecurityUtils.setSecureParams(pairListString);
    }

    public AllAPICall(Context context, HashMap<String, String> pairListString, HashMap<String, File> pairListFile, onTaskComplete taskComplete, boolean isShow) {
        this.mOnTaskComplete = taskComplete;
        this.context = context;
        utility = new Utility(context);
        this.pairListString = pairListString;
        this.pairListFile = pairListFile;
        this.isShow = isShow;
        if (pairListString != null)
            this.pairListString = SecurityUtils.setSecureParams(pairListString);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        jsonParser = new GetJsonUrl();
        if (isShow)
            utility.showProgress();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.i("URL : ", params[0]);
        if (pairListString != null && pairListFile == null) {
            Log.i("params", pairListString.toString());
            response = jsonParser.getJSONResponseFromUrl(params[0], pairListString, null);
        } else if (pairListFile != null && pairListString != null) {
            Log.i("params", pairListString.toString() + " " + pairListFile.toString());
            response = jsonParser.getJSONResponseFromUrl(params[0], pairListString, pairListFile);
        } else
            response = jsonParser.getJSONResponseFromUrl(params[0], null, null);

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (isShow) {
            utility.hideProgress();
        }
        if (result != null && !result.equals("")) {
            Log.i("response", result);
            mOnTaskComplete.onComplete(result);
        } else {
            Toast.makeText(context, "Something went wrong please try again later", Toast.LENGTH_SHORT).show();
        }
    }
}
