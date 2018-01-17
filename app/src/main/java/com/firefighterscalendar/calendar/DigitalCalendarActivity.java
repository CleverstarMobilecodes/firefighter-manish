package com.firefighterscalendar.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.inapp.InAppPurchaseActivity;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onConfirm;
import com.firefighterscalendar.utils.onTaskComplete;
import com.marcohc.robotocalendar.RobotoCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DigitalCalendarActivity extends BaseActivity implements View.OnClickListener, RobotoCalendarView.RobotoCalendarListener {

    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private RobotoCalendarView robotoCalendarView;
    private Calendar currentCalendar;
    private int currentMonthIndex;
    private ImageView imgCalendarImage, imgCalBack;
    private TextView textHeader;
    private HashMap<String, String> stringHashMap;
    private SimpleDateFormat simpleDateFormat;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_cal);
        initControl();
    }

    private void initControl() {
        sharedPreferences = getSharedPreferences("calendar", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (getIntent().getBooleanExtra("frompush", false) && sessionManager.getIntDetail("dayCount") > 2) {

            utility.showYesNoDialog("Unlock", "Cancel", "You have used your 2 free days- for a new photo of the day every single day- upgrade here for only $0.99!", new onConfirm() {
                @Override
                public void onChooseYes(boolean isTrue) {
                    startActivity(new Intent(DigitalCalendarActivity.this, InAppPurchaseActivity.class));
                }
            });
        }

        if (!sharedPreferences.contains(sessionManager.getStringDetail("iUserId"))) {
            showOkDialog(getResources().getString(R.string.welcome_digital));
            editor.putString(sessionManager.getStringDetail("iUserId"), "true");
            editor.apply();
        }

        simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        currentMonthIndex = 0;

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
        layoutRight.setOnClickListener(this);

        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText("Photo Of The Day");

        imgCalendarImage = (ImageView) findViewById(R.id.imgCalendarImage);
        imgCalBack = (ImageView) findViewById(R.id.imgCalBack);

        robotoCalendarView = (RobotoCalendarView) findViewById(R.id.robotoCalendarPicker);
        robotoCalendarView.setRobotoCalendarListener(this);

        currentCalendar = Calendar.getInstance(Locale.getDefault());

        robotoCalendarView.markDayAsSelectedDay(currentCalendar.getTime());

        if (utility.checkInternetConnection()) {
            getCalendarImage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (utility.checkInternetConnection()) {
            updateCalendar();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        if (v == layoutRight) {
            Intent intent = new Intent(DigitalCalendarActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onDateSelected(Date date) {

        if (sessionManager.getStringDetail("subscription").equalsIgnoreCase("yes")) {
            startActivity(new Intent(DigitalCalendarActivity.this, OpenCalendarActivity.class).putExtra("date", date.getTime()));
        } else {
            utility.showYesNoDialog("Unlock", "Cancel", "The digital calendar feature of the Firefighters Calendar app is available for joined supporters only. CLICK HERE for unlimited access!", new onConfirm() {
                @Override
                public void onChooseYes(boolean isTrue) {
                    startActivity(new Intent(DigitalCalendarActivity.this, InAppPurchaseActivity.class));
                }
            });
        }
    }

    @Override
    public void onRightButtonClick() {
        currentMonthIndex++;
        updateCalendar();
    }

    @Override
    public void onLeftButtonClick() {
        currentMonthIndex--;
        updateCalendar();
    }

    private void updateCalendar() {
        currentCalendar = Calendar.getInstance(Locale.getDefault());
        currentCalendar.add(Calendar.MONTH, currentMonthIndex);
        robotoCalendarView.initializeCalendar(currentCalendar);

        robotoCalendarView.markDayAsSelectedDay(currentCalendar.getTime());
        if (utility.checkInternetConnection())
            getMonthEvent();
    }

    private void getCalendarImage() {
        stringHashMap = new HashMap<>();
        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        Glide.with(DigitalCalendarActivity.this).load(jsonObject.getJSONObject("result").getString("txCalendarImageName")).crossFade().fitCenter().into(imgCalendarImage);
                        Glide.with(DigitalCalendarActivity.this).load(jsonObject.getJSONObject("result").getString("txCalendarImageName")).crossFade().centerCrop().into(imgCalBack);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_CALENDAR_IMAGE);
    }

    private void getMonthEvent() {

        stringHashMap = new HashMap<>();
        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("iMonth", "" + (currentCalendar.get(Calendar.MONTH) + 1));
        stringHashMap.put("iYear", "" + currentCalendar.get(Calendar.YEAR));

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONArray arrayResult = jsonObject.getJSONArray("result");

                        for (int i = 0; i < arrayResult.length(); i++) {
                            daysBetween(simpleDateFormat.parse(arrayResult.getJSONObject(i).getString("iStartDate")),
                                    simpleDateFormat.parse(arrayResult.getJSONObject(i).getString("iEndDate")));
                        }
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_USER_EVENTS_MONTH);
    }

    public void daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        eDate.add(Calendar.DATE, 1);

        if (sDate.equals(eDate)) {
            robotoCalendarView.markFirstUnderlineWithStyle(R.color.red, sDate.getTime());
        }
        while (sDate.before(eDate)) {
            robotoCalendarView.markFirstUnderlineWithStyle(R.color.red, sDate.getTime());
            sDate.add(Calendar.DATE, 1);
        }
    }

    public static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

    private void showOkDialog(String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getResources().getString(R.string.app_name));
        adb.setMessage(message);
        adb.setPositiveButton("OK", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (sessionManager.getIntDetail("dayCount") <= 2 &&
                        sessionManager.getStringDetail("freeSubscription").equalsIgnoreCase("yes"))
                    utility.showOkDialog(getResources().getString(R.string.new_user_two));
            }
        });
        adb.show();
    }
}