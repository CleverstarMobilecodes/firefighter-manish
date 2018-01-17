package com.firefighterscalendar.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.OpenGallery;
import com.firefighterscalendar.utils.onTaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private TextView txtCreateAcc;
    private EditText editFirstName, editLastName, editEmail, editPassword, editRetypePass;
    private HashMap<String, String> stringHashMap;
    private boolean isEdit;
    private ImageView imgPic, imgPlaceHolder;
    private static String imgPath;
    private HashMap<String, File> fileHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        GCMRegistration();
        initControls();
    }

    private void initControls() {
        isEdit = getIntent().getBooleanExtra("isEdit", false);

        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editRetypePass = (EditText) findViewById(R.id.editRetypePass);

        txtCreateAcc = (TextView) findViewById(R.id.txtCreateAcc);
        txtCreateAcc.setOnClickListener(this);

        if (isEdit) {
            editPassword.setVisibility(View.GONE);
            editRetypePass.setVisibility(View.GONE);
            txtCreateAcc.setText(R.string.lbl_save_changes);
            editFirstName.setText(sessionManager.getStringDetail("vFirstName"));
            editLastName.setText(sessionManager.getStringDetail("vLastName"));
            editEmail.setText(sessionManager.getStringDetail("vEmail"));

            setImage(sessionManager.getStringDetail("txProfilePic"));
        }
    }

    private void setImage(String uri) {
        FrameLayout flProfilepic = (FrameLayout) findViewById(R.id.flProfilepic);
        flProfilepic.setVisibility(View.VISIBLE);

        imgPic = (ImageView) findViewById(R.id.imgPic);
        imgPlaceHolder = (ImageView) findViewById(R.id.imgPlaceHolder);

        Glide.with(this).load(uri).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgPic) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imgPic.setImageDrawable(circularBitmapDrawable);
            }
        });

        Glide.with(this).load(R.drawable.white_bg).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgPlaceHolder) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imgPlaceHolder.setImageDrawable(circularBitmapDrawable);
            }
        });

        flProfilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SignUpActivity.this, OpenGallery.class), 1);
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (!TextUtils.isEmpty(imgPath) || imgPath != null)
            setImage(utility.resize(imgPath).getAbsolutePath());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (utility.resize(data.getStringExtra("picturepath")) != null) {
                fileHashMap = new HashMap<>();
                imgPath = data.getStringExtra("picturepath");
                setImage(utility.resize(data.getStringExtra("picturepath")).getAbsolutePath());
                fileHashMap.put("txProfilePic", utility.resize(data.getStringExtra("picturepath")));
            }
            else
                utility.showOkDialog("Unable to fetch image.Please select another image");
        }
    }

    private void checkValidation() {
        if (!utility.isEmptyText(editFirstName)) {
            editFirstName.setError("Please enter first name");
        }
        else if (!utility.isEmptyText(editLastName)) {
            editLastName.setError("Please enter last name");
        }
        else if (!utility.isValidEmail(editEmail)) {
            editEmail.setError("Please enter valid email address");
        }
        else {
            if (isEdit) {
                if (utility.checkInternetConnection())
                    editProfile();
            }
            else {
                if (!utility.isValidPassword(editPassword)) {

                }
                else if (!utility.isEmptyText(editRetypePass)) {
                    editRetypePass.setError("Please retype password");
                }
                else if (!utility.isValidPassword(editPassword, editRetypePass)) {
                    utility.showOkDialog("Password & Retype password must be same");
                }
                else {
                    if (utility.checkInternetConnection())
                        registerApiCall();
                }
            }
        }
    }

    private void registerApiCall() {
        stringHashMap = new HashMap<>();

        stringHashMap.put("vFirstName", getTextvalue(editFirstName));
        stringHashMap.put("vLastName", getTextvalue(editLastName));
        stringHashMap.put("vEmail", getTextvalue(editEmail));
        stringHashMap.put("vPassword", getTextvalue(editPassword));
        stringHashMap.put("vConfPassword", getTextvalue(editRetypePass));
        stringHashMap.put("txDeviceToken", sessionManager.getStringDetail("regid"));
        stringHashMap.put("eDeviceType", DEVICE_TYPE);
        stringHashMap.put("vVersion", APPVERSION);

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONObject objResult = jsonObject.getJSONObject("result");
                        sessionManager.setStringDetail("iUserId", objResult.getString("iUserId"));
                        sessionManager.setStringDetail("vFirstName", objResult.getString("vFirstName"));
                        sessionManager.setStringDetail("vLastName", objResult.getString("vLastName"));
                        sessionManager.setStringDetail("vEmail", objResult.getString("vEmail"));
                        sessionManager.setStringDetail("vPhoneNo", objResult.getString("vPhoneNo"));
                        sessionManager.setStringDetail("dBirthdate", objResult.getString("dBirthdate"));
                        sessionManager.setStringDetail("dCredit", objResult.getString("dCredit"));
                        sessionManager.setStringDetail("txProfilePic", objResult.getString("txProfilePic"));
                        sessionManager.setStringDetail("eNotificationFlag", objResult.getString("eNotificationFlag"));
                        sessionManager.setStringDetail("canCreateGreetings", objResult.getString("canCreateGreetings"));
                        sessionManager.setStringDetail("canCreateCalendar", objResult.getString("canCreateCalendar"));
                        sessionManager.setIntDetail("dayCount", objResult.getInt("dayCount"));

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, 8);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);


                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    }
                    Toast.makeText(SignUpActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_SIGNUP);
    }

    private void editProfile() {

        stringHashMap = new HashMap<>();
        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("vFirstName", getTextvalue(editFirstName));
        stringHashMap.put("vLastName", getTextvalue(editLastName));
        stringHashMap.put("vEmail", getTextvalue(editEmail));

        new AllAPICall(this, stringHashMap, fileHashMap, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONObject objResult = jsonObject.getJSONObject("result");
                        sessionManager.clearAllSP();
                        sessionManager.setStringDetail("iUserId", objResult.getString("iUserId"));
                        sessionManager.setStringDetail("vFirstName", objResult.getString("vFirstName"));
                        sessionManager.setStringDetail("vLastName", objResult.getString("vLastName"));
                        sessionManager.setStringDetail("vEmail", objResult.getString("vEmail"));
                        sessionManager.setStringDetail("vPhoneNo", objResult.getString("vPhoneNo"));
                        sessionManager.setStringDetail("dCredit", objResult.getString("dCredit"));
                        sessionManager.setStringDetail("txProfilePic", objResult.getString("txProfilePic"));
                        finish();
                    }
                    Toast.makeText(SignUpActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_EDIT_PROFILE);
    }

    @Override
    public void onClick(View v) {
        if (v == txtCreateAcc) {
            checkValidation();
        }
    }
}
