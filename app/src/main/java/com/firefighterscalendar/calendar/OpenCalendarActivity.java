package com.firefighterscalendar.calendar;

import android.content.Intent;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onTaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class OpenCalendarActivity extends BaseActivity implements View.OnClickListener, WeekView.EventClickListener, WeekView.EventLongPressListener {

    private WeekView weekView;
    private ImageView imgClose, imgAddEvent;
    private TextView textCalDate;
    private List<WeekViewEvent> events;
    private Date date;
    private Calendar mainCalendar, selectedEventDate;
    private SimpleDateFormat timestampFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aaa", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_calendar);
        initControl();
    }

    private void initControl() {
        events = new ArrayList<>();

        weekView = (WeekView) findViewById(R.id.weekView);

        date = new Date(getIntent().getLongExtra("date", 0));

        mainCalendar = Calendar.getInstance();
        mainCalendar.setTime(date);

        weekView.goToDate(mainCalendar);
        weekView.setMinDate(mainCalendar);

        weekView.setScrollListener(new WeekView.ScrollListener() {
            @Override
            public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault());
                textCalDate.setText(simpleDateFormat.format(newFirstVisibleDay.getTimeInMillis()));
                selectedEventDate = newFirstVisibleDay;
            }
        });

        weekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                if (newMonth == mainCalendar.get(Calendar.MONTH))
                    return events;
                else {
                    return new ArrayList<>();
                }
            }
        });

        weekView.setOnEventClickListener(this);
        weekView.setEventLongPressListener(this);

        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgAddEvent = (ImageView) findViewById(R.id.imgAddEvent);
        imgClose.setOnClickListener(this);
        imgAddEvent.setOnClickListener(this);

        textCalDate = (TextView) findViewById(R.id.textCalDate);

        weekView.setEventTextColor(R.color.white_dark);
//        setupDateTimeInterpreter(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (utility.checkInternetConnection()) {
            events.clear();
            getDayEvent();
        }
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        weekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == imgClose) {
            finish();
        } else if (v == imgAddEvent) {
            startActivity(new Intent(this, NewEventActivity.class).putExtra("eventdate", selectedEventDate.getTimeInMillis()));
        }
    }

    private void getDayEvent() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);

        String tmp = String.valueOf(calendar.getTimeInMillis());

        stringHashMap = new HashMap<>();
        stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        stringHashMap.put("iDate", "" + tmp.substring(0, tmp.length() - 3));
        stringHashMap.put("offset", "" + 1);

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        JSONArray arrayResult = jsonObject.getJSONArray("result");
                        for (int i = 0; i < arrayResult.length(); i++) {
                            JSONObject objResult = arrayResult.getJSONObject(i);
                            WeekViewEvent weekViewEvent = new WeekViewEvent();

                            Calendar startCalendar = Calendar.getInstance();
                            startCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                            startCalendar.setTime(new Date(objResult.getLong("iStartDate") * 1000));
                            startCalendar.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY));
                            startCalendar.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE));
                            startCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.get(Calendar.DAY_OF_MONTH));
                            startCalendar.set(Calendar.MONTH, startCalendar.get(Calendar.MONTH));
                            startCalendar.set(Calendar.YEAR, startCalendar.get(Calendar.YEAR));

                            Calendar endCalendar = Calendar.getInstance();
                            endCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                            endCalendar.setTime(new Date(objResult.getLong("iEndDate") * 1000));
                            endCalendar.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY));
                            endCalendar.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE));
                            endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH));
                            endCalendar.set(Calendar.MONTH, endCalendar.get(Calendar.MONTH));
                            endCalendar.set(Calendar.YEAR, endCalendar.get(Calendar.YEAR));
//
                            weekViewEvent.setId(objResult.getLong("iEventId"));
                            weekViewEvent.setName(objResult.getString("vTitle"));
                            weekViewEvent.setLocation(objResult.getString("vLocation") + objResult.getString("vLocation"));
                            weekViewEvent.setStartTime(startCalendar);
                            weekViewEvent.setEndTime(endCalendar);
                            weekViewEvent.setColor(ContextCompat.getColor(OpenCalendarActivity.this, R.color.light_theme_color));

                            events.add(weekViewEvent);
                        }
                        weekView.setEventTextColor(ContextCompat.getColor(OpenCalendarActivity.this, R.color.red));
                        weekView.notifyDatasetChanged();
                    }

                    if (events.isEmpty()) {
                        findViewById(R.id.textNoEvent).setVisibility(View.VISIBLE);
                        weekView.setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.textNoEvent).setVisibility(View.GONE);
                        weekView.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_GET_USER_EVENTS_DAY);
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Intent intent = new Intent(this, NewEventActivity.class);
        intent.putExtra("isedit", true);
        intent.putExtra("location", event.getLocation());
        intent.putExtra("name", event.getName());
        intent.putExtra("eventdate", selectedEventDate.getTimeInMillis());
        intent.putExtra("id", event.getId());
        startActivity(intent);
    }


    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }
}
