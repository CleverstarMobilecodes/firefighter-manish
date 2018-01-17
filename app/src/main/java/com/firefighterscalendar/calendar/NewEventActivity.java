package com.firefighterscalendar.calendar;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.utils.AllAPICall;
import com.firefighterscalendar.utils.onConfirm;
import com.firefighterscalendar.utils.onTaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class NewEventActivity extends BaseActivity implements View.OnClickListener {

    private EditText editTitle, editLocation, editNotes;
    private Switch switchDay;
    private TextView textStartTime, textEndTime, textRepeat, textCancel, textAddEvent, textDeleteEvent, lblNewEvent;
    private HashMap<String, String> stringHashMap;
    private String eventStartDate, eventEndDate, eventStartTime, eventEndTime;
    private Calendar startCalendar, endCalendar, tmpcalendar;
    private SimpleDateFormat dateFormatter, timeFormatter;
    private SimpleDateFormat timestampFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a");
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private boolean isEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        initControl();
    }

    private void initControl() {
        isEdit = getIntent().getBooleanExtra("isedit", false);

        startCalendar = Calendar.getInstance(TimeZone.getDefault());
        endCalendar = Calendar.getInstance(TimeZone.getDefault());
        endCalendar.add(Calendar.HOUR_OF_DAY, 1);

        tmpcalendar = Calendar.getInstance(TimeZone.getDefault());

        if (getIntent().getLongExtra("eventdate", 0) > tmpcalendar.getTimeInMillis()) {
            startCalendar.setTimeInMillis(getIntent().getLongExtra("eventdate", 0));
            endCalendar.setTimeInMillis(getIntent().getLongExtra("eventdate", 0));

            startCalendar.set(Calendar.HOUR, tmpcalendar.get(Calendar.HOUR));
            endCalendar.set(Calendar.HOUR, tmpcalendar.get(Calendar.HOUR));
            startCalendar.set(Calendar.MINUTE, tmpcalendar.get(Calendar.MINUTE));
            endCalendar.set(Calendar.MINUTE, tmpcalendar.get(Calendar.MINUTE));
            startCalendar.set(Calendar.AM_PM, tmpcalendar.get(Calendar.AM_PM));
            endCalendar.set(Calendar.AM_PM, tmpcalendar.get(Calendar.AM_PM));

            endCalendar.add(Calendar.HOUR, 1);

        }

        dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        timeFormatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        eventStartDate = dateFormatter.format(startCalendar.getTimeInMillis());
        eventEndDate = dateFormatter.format(endCalendar.getTimeInMillis());

        eventStartTime = timeFormatter.format(startCalendar.getTime());
        eventEndTime = timeFormatter.format(endCalendar.getTime());

        editTitle = (EditText) findViewById(R.id.editTitle);
        editLocation = (EditText) findViewById(R.id.editLocation);
        editNotes = (EditText) findViewById(R.id.editNotes);

        switchDay = (Switch) findViewById(R.id.switchDay);

        textStartTime = (TextView) findViewById(R.id.textStartTime);
        textEndTime = (TextView) findViewById(R.id.textEndTime);
        textRepeat = (TextView) findViewById(R.id.textRepeat);
        textCancel = (TextView) findViewById(R.id.textCancel);
        textAddEvent = (TextView) findViewById(R.id.textAddEvent);
        textDeleteEvent = (TextView) findViewById(R.id.textDeleteEvent);
        lblNewEvent = (TextView) findViewById(R.id.lblNewEvent);

        if (isEdit) {
            textDeleteEvent.setVisibility(View.VISIBLE);
            textAddEvent.setText("Update");
            editTitle.setText(getIntent().getStringExtra("name"));
            editLocation.setText(getIntent().getStringExtra("location"));
            lblNewEvent.setText("Update Event");
        }

        textCancel.setOnClickListener(this);
        textAddEvent.setOnClickListener(this);
        textStartTime.setOnClickListener(this);
        textEndTime.setOnClickListener(this);
        textRepeat.setOnClickListener(this);
        textDeleteEvent.setOnClickListener(this);

        setEventTiming();
    }

    @Override
    public void onClick(View v) {
        if (v == textCancel) {
            finish();
        } else if (v == textAddEvent) {
            checkValidation();
        } else if (v == textStartTime) {
            utility.hideSoftKeyboard(NewEventActivity.this);
            openStartDatePicker();
        } else if (v == textEndTime) {
            utility.hideSoftKeyboard(NewEventActivity.this);
            openEndDatePicker();
        } else if (v == textRepeat) {
            setEventRepeat();
        } else if (v == textDeleteEvent) {
            utility.showYesNoDialog("Yes", "No", "Are you sure you want to delete event?", new onConfirm() {
                @Override
                public void onChooseYes(boolean isTrue) {
                    deleteEvent();
                }
            });
        }
    }


    private String getTimeStamp(TextView textView) {
        try {
            Date date = timestampFormat.parse(textView.getText().toString());
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            calendar.setTime(date);
            String tmp = String.valueOf(calendar.getTimeInMillis());
            Log.i("selected date for time", "" + tmp.substring(0, tmp.length() - 3));
            return "" + tmp.substring(0, tmp.length() - 3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void checkValidation() {
        try {
            if (TextUtils.isEmpty(getTextvalue(editTitle))) {
                editTitle.setError("Please enter title");
                editTitle.requestFocus();
            } else if (TextUtils.isEmpty(getTextvalue(editLocation))) {
                editLocation.setError("Please enter location");
                editLocation.requestFocus();
            } else if (dateFormatter.parse(eventStartDate).after(dateFormatter.parse(eventEndDate))) {
                utility.showOkDialog("Event date should be greater than selected date");
            } else {
                if (utility.checkInternetConnection()) {
                    if (!isEdit) {
                        addEventAPICall(URL_ADD_USER_EVENT);
                    } else {
                        addEventAPICall(URL_UPDATE_USER_EVENT);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventTiming() {
        textStartTime.setText(eventStartDate + " " + eventStartTime);
        textEndTime.setText(eventEndDate + " " + eventEndTime);
    }

    private void deleteEvent() {
        stringHashMap = new HashMap<>();
        stringHashMap.put("iEventId", "" + getIntent().getLongExtra("id", 0));

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray JSONArray = new JSONArray(response);
                    JSONObject jsonObject = JSONArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        finish();
                    }
                    Toast.makeText(NewEventActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_DELETE_USER_EVENT);
    }

    private void addEventAPICall(String url) {
        stringHashMap = new HashMap<>();

        if (isEdit) {
            stringHashMap.put("iEventId", "" + getIntent().getLongExtra("id", 0));
        } else {
            stringHashMap.put("iUserId", sessionManager.getStringDetail("iUserId"));
        }

        stringHashMap.put("vTitle", getTextvalue(editTitle));
        stringHashMap.put("vLocation", getTextvalue(editLocation));
        stringHashMap.put("eAllDay", (switchDay.isChecked()) ? "yes" : "no");
        stringHashMap.put("iStartDate", getTimeStamp(textStartTime));
        stringHashMap.put("iEndDate", getTimeStamp(textEndTime));
        stringHashMap.put("iEndRepeatDate", eventEndDate);
        stringHashMap.put("eRepeatType", textRepeat.getText().toString());
        stringHashMap.put("txNotes", getTextvalue(editNotes));
        stringHashMap.put("eAlert", "yes");

        new AllAPICall(this, stringHashMap, null, new onTaskComplete() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.getInt("response_status") == 1) {
                        finish();
                    }
                    Toast.makeText(NewEventActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    public void openStartDatePicker() {
        Calendar dateCalendar = startCalendar;

        //date picker dialog
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String tmp = String.valueOf(calendar.getTimeInMillis());

                Log.i("selected date for time", "" + tmp.substring(0, tmp.length() - 3));
                eventStartDate = dateFormatter.format(calendar.getTime());
                endCalendar = startCalendar;

                timePickerDialog.show();
            }
        }, dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();

        //time picker dialog
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                eventStartTime = timeFormatter.format(calendar.getTime());
                setEventTiming();
            }
        }, dateCalendar.get(Calendar.HOUR), dateCalendar.get(Calendar.MINUTE), false);


        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    private void openEndDatePicker() {
        //date picker dialog
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                eventEndDate = dateFormatter.format(calendar.getTime());
                timePickerDialog.show();

            }
        }, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();

        //time picker dialog
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                timeFormatter.format(calendar.getTime());
                eventEndTime = timeFormatter.format(calendar.getTime());
                setEventTiming();
            }
        }, endCalendar.get(Calendar.HOUR), endCalendar.get(Calendar.MINUTE), false);

        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    private void setEventRepeat() {
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.arr_event_repeat)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View mView = super.getView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                mTextView.setBackgroundColor(ContextCompat.getColor(NewEventActivity.this, R.color.dark_theme));
                mTextView.setTextColor(ContextCompat.getColor(NewEventActivity.this, android.R.color.white));
                return mView;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(stringArrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textRepeat.setText(stringArrayAdapter.getItem(which));
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.dark_theme);
        alertDialog.show();
    }
}
