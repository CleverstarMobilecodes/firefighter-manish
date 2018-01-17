package com.firefighterscalendar.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.firefighterscalendar.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


public class MyDatePickerDialog {

    private Activity activity;
    private String title;
    private DatePickerListener datePickerListener;
    private TimePickerListener timePickerListener;
    private int type;// 1= Date  2=Year  3=Month 4=Day
    private Calendar calendar;
    private int minDay = -1, minMonth = -1, minYear = -1;
    private int maxDay = -1, maxMonth = -1, maxYear = -1;
    private int hourOfDay = -1, minute = -1;

    public MyDatePickerDialog(Activity activity) {
        this.activity = activity;
        calendar = Calendar.getInstance();
    }

    /***
     * Set minimum Date in to date picker
     *
     * @param minYear  minimum Year(-1 for set current Year)
     * @param minMonth minimum Moth(-1 for set current Moth)
     * @param minDay   minimum Day(-1 for set current Day)
     */
    public void setMinDate(int minYear, int minMonth, int minDay) {
        this.minDay = minDay;
        this.minMonth = minMonth;
        this.minYear = minYear;
    }

    /***
     * Set minimum Calendar in to date picker
     *
     * @param minDate Pass minimum Calendar
     */
    public void setMinDate(Calendar minDate) {
        this.minDay = minDate.get(Calendar.DAY_OF_MONTH);
        this.minMonth = minDate.get(Calendar.MONTH);
        this.minYear = minDate.get(Calendar.YEAR);

    }

    /***
     * Set Maximum Date in to date picker
     *
     * @param maxYear  maximum Year(-1 for set current Year)
     * @param maxMonth maximum Month(-1 for set current Moth)
     * @param maxDay   maximum Day(-1 for set current Day)
     */
    public void setMaxDate(int maxYear, int maxMonth, int maxDay) {
        this.maxDay = maxDay;
        this.maxMonth = maxMonth;
        this.maxYear = maxYear;
    }

    /***
     * Open Date Picker dialog for only Year
     *
     * @param title              Title of picker
     * @param year               selected Year (-1 for set current Year)
     * @param datePickerListener
     */
    public void showYear(String title, int year, DatePickerListener datePickerListener) {

        this.title = title;
        type = 2;
        this.datePickerListener = datePickerListener;
        showDateDialog(year, -1, -1);
    }

    /***
     * Open Date Picker dialog for only Month
     *
     * @param title              Title of picker
     * @param month              selected Month (-1 for set current Month)
     * @param datePickerListener
     */
    public void showMonth(String title, int month, DatePickerListener datePickerListener) {
        this.title = title;
        type = 3;
        this.datePickerListener = datePickerListener;
        showDateDialog(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, -1);
    }

    /***
     * Open Date Picker dialog
     *
     * @param title              Title of picker
     * @param year               selected Year (-1 for set current Year)
     * @param month              selected Month (-1 for set current Month)
     * @param day                selected Day (-1 for set current Day)
     * @param datePickerListener
     */
    public void showDate(String title, int year, int month, int day, DatePickerListener datePickerListener) {
        this.title = title;
        type = 1;
        this.datePickerListener = datePickerListener;
        showDateDialog(year, month, day);
    }


    /***
     * Open Time Picker dialog
     *
     * @param title              Title of picker
     * @param hourOfDay          selected HourOfDay (-1 for set current hourOfDay)
     * @param minute             selected Minutes (-1 for set current Minutes)
     * @param timePickerListener
     */
    public void showTime(String title, int hourOfDay, int minute, TimePickerListener timePickerListener) {
        this.title = title;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.timePickerListener = timePickerListener;
        showTimeDialog(hourOfDay, minute);

    }

    public String formatMonth(int month) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM", Locale.getDefault());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month);
        return formatter.format(calendar.getTime());
    }


    public interface TimePickerListener {

        void onCancel();

        void onOk(int hourOfDay, int minute);
    }

    public interface DatePickerListener {

        void onCancel();

        /***
         * Return selected Date
         *
         * @param year     selected Year
         * @param month    selected Month
         * @param day      selected Day
         * @param calendar Calender of Selected Date
         */
        void onOk(int year, int month, int day, Calendar calendar);


    }

    private void showTimeDialog(int hourOfDay, int minute) {
        LayoutInflater inflater = (LayoutInflater) activity.getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_time_picker, null);

        final TimePicker timePicker = (TimePicker) customView.findViewById(R.id.timePicker);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        if (hourOfDay != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(hourOfDay);
            } else {
                timePicker.setCurrentHour(hourOfDay);
            }
        }


        if (minute != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setMinute(minute);
            } else {
                timePicker.setCurrentMinute(minute);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);
        builder.setView(customView); // Set the view of the dialog to your custom layout
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                timePickerListener.onCancel();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerListener.onOk(timePicker.getHour(), timePicker.getMinute());
                } else {
                    timePickerListener.onOk(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                }
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    private void showDateDialog(int year, int month, int day) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_date_picker, null);

        final DatePicker datePicker = (DatePicker) customView.findViewById(R.id.datePicker);
        int month_id = activity.getResources().getIdentifier("android:id/month", null, null);
        int day_id = activity.getResources().getIdentifier("android:id/day", null, null);
        int year_id = activity.getResources().getIdentifier("android:id/year", null, null);
        datePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        if (type == 1) {
            if (month < 0 && year < 0 && day < 0) {
                datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } else {
                datePicker.updateDate(year, month, day);

            }

            if (minMonth != -1 && minYear != -1 && minDay != -1) {
                Calendar calendarMin = Calendar.getInstance();
                calendarMin.set(minYear, minMonth, minDay, 0, 0, 0);
                datePicker.setMinDate(calendarMin.getTime().getTime());
            }
            if (maxMonth != -1 && maxYear != -1 && maxDay != -1) {
                Calendar calendarMax = Calendar.getInstance();
                calendarMax.set(maxYear, maxMonth, maxDay, 0, 0, 0);
                datePicker.setMaxDate(calendarMax.getTime().getTime());
            }


        } else if (type == 2) {
            if (month_id != 0) {
                View monthPicker = datePicker.findViewById(month_id);
                if (monthPicker != null) {
                    monthPicker.setVisibility(View.GONE);
                }
            }
            if (day_id != 0) {
                View dayPicker = datePicker.findViewById(day_id);
                if (dayPicker != null) {
                    dayPicker.setVisibility(View.GONE);
                }
            }

            if (year != -1) {
                datePicker.updateDate(year, 0, 0);
            }

            if (maxYear != -1) {
                Calendar calendarMax = Calendar.getInstance();
                calendarMax.set(maxYear, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.setMaxDate(calendarMax.getTime().getTime());
            }

            if (minYear != -1) {
                Calendar calendarMax = Calendar.getInstance();
                calendarMax.set(minYear, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.setMinDate(calendarMax.getTime().getTime());
            }

        } else if (type == 3) {
            /*if (year_id != 0) {
                View yearPicker = datePicker.findViewById(year_id);
                if (yearPicker != null) {
                    yearPicker.setVisibility(View.GONE);
                }
            }*/
            if (day_id != 0) {
                View dayPicker = datePicker.findViewById(day_id);
                if (dayPicker != null) {
                    dayPicker.setVisibility(View.GONE);
                }
            }

            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 0, null);

            if (maxMonth != -1) {
                Calendar calendarMax = Calendar.getInstance();
                calendarMax.set(calendar.get(Calendar.YEAR), maxMonth, calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.setMaxDate(calendarMax.getTime().getTime());
            }

            if (minMonth != -1) {
                Calendar calendarMax = Calendar.getInstance();
                calendarMax.set(calendar.get(Calendar.YEAR), minMonth, calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.setMinDate(calendarMax.getTime().getTime());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);
        builder.setView(customView); // Set the view of the dialog to your custom layout
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                datePickerListener.onCancel();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, datePicker.getYear());
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                datePickerListener.onOk(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), calendar);
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}