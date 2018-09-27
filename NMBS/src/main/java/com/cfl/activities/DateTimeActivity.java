package com.cfl.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cfl.R;
import com.cfl.activity.BaseActivity;
import com.cfl.application.NMBSApplication;
import com.cfl.model.TravelRequest;
import com.cfl.services.impl.SettingService;
import com.cfl.util.DateUtils;
import com.cfl.util.GoogleAnalyticsUtil;
import com.cfl.util.TrackerConstant;
import com.cfl.util.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by shig on 2016/1/13.
 */
public class DateTimeActivity extends BaseActivity {

    private SettingService settingService = null;
    private DatePicker dpDate;
    private TimePicker tpTime;
    private LinearLayout departureAndArriveLayout;
    private Calendar calendar;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private Date date;
    private String dateFormatted;
    private TextView departureTextView, arriveTextView;
    private static final String TAG = DateTimeActivity.class.getSimpleName();
    public static final String PRMA_DATE_FORMATTED = "DateFormatted";
    public static final String PRMA_DATE = "Date";
    public static final String PRMA_TIME_PREFERENCE = "TimePreference";
    private static boolean isFromSchedule;
    private TravelRequest.TimePreference selectTimePreference = TravelRequest.TimePreference.DEPARTURE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        settingService = ((NMBSApplication) getApplication()).getSettingService();
        //settingService.initLanguageSettings();
        setContentView(R.layout.activity_datetime);
        dpDate = (DatePicker) findViewById(R.id.dp_date);
        tpTime = (TimePicker) findViewById(R.id.tp_time);
        departureAndArriveLayout = (LinearLayout) findViewById(R.id.ll_activity_time_select_time_preference);
        departureTextView = (TextView) findViewById(R.id.tv_date_time_departures);
        arriveTextView = (TextView) findViewById(R.id.tv_date_time_arrivals);
        date = (Date) getIntent().getSerializableExtra(PRMA_DATE);
        dpDate.setMinDate(System.currentTimeMillis() - 1000);
        calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        selectTimePreference = (TravelRequest.TimePreference)getIntent().getSerializableExtra(PRMA_TIME_PREFERENCE);

        //calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        //初始化DatePicker组件，初始化时指定监听器
        dpDate.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker arg0, int year, int month,
                                      int day) {
                DateTimeActivity.this.year = year;
                DateTimeActivity.this.month = month;
                DateTimeActivity.this.day = day;

                setDate(year, month, day, hour, minute);
            }
        });

        tpTime.setCurrentHour(hour);
        tpTime.setCurrentMinute(minute);
        tpTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker arg0, int hour, int minute) {
                DateTimeActivity.this.hour = hour;
                DateTimeActivity.this.minute = minute;
                // 显示当前日期、时间
                setDate(year, month, day, hour, minute);
            }
        });

        if(isFromSchedule){
            departureAndArriveLayout.setVisibility(View.VISIBLE);
            if(selectTimePreference == TravelRequest.TimePreference.ARRIVAL){
                selectArrive(arriveTextView);
            }else{
                selectDeparture(departureTextView);
            }
            GoogleAnalyticsUtil.getInstance().sendScreen(DateTimeActivity.this, TrackerConstant.SCHEDULE_DATESELECTION);
        }else{
            departureAndArriveLayout.setVisibility(View.GONE);
            GoogleAnalyticsUtil.getInstance().sendScreen(DateTimeActivity.this, TrackerConstant.STATIONBOARD_DATESELECTION);
        }
    }

    public void cancel(View view) {
        finish();
    }

    public void done(View view) {
        if(dateFormatted == null || dateFormatted.isEmpty()){
            //Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            setDate(year, month, day, hour, minute);
        }
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable(PRMA_DATE_FORMATTED, dateFormatted);
        b.putSerializable(PRMA_DATE, date);
        if(isFromSchedule){
            b.putSerializable(PRMA_TIME_PREFERENCE, selectTimePreference);
        }
        intent.putExtras(b);
        // the value means fill from value or to value
        setResult(RESULT_OK, (intent).setAction(Intent.ACTION_VIEW));
        finish();
    }

    private void setDate(int year, int month, int day, int hour, int minute){
        calendar.set(year, month, day, hour, minute);
        date = calendar.getTime();
        dateFormatted = DateUtils.dateTimeToString(date, "dd MMM yyyy - HH:mm");
        //Log.d(TAG, "Selected date is:::" + dateFormatted);
    }

    public static Intent createIntent(Context context, Date date){
        Intent intent = new Intent(context, DateTimeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(PRMA_DATE, date);

        isFromSchedule = false;
        return intent;
    }

    public static Intent createIntent(Context context, boolean fromSchedule, Date date, TravelRequest.TimePreference selectTimePreference){
        Intent intent = new Intent(context, DateTimeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        isFromSchedule = fromSchedule;
        intent.putExtra(PRMA_DATE, date);
        intent.putExtra(PRMA_TIME_PREFERENCE, selectTimePreference);
        return intent;
    }

    public void selectDeparture(View view){
        this.departureTextView.setBackgroundResource(R.color.background_button_secondaction);
        this.arriveTextView.setBackgroundResource(R.color.background_secondaryaction);
        this.departureTextView.setTextColor(getResources().getColor(R.color.text_white_color));
        this.arriveTextView.setTextColor(getResources().getColor(R.color.text_blue_color));
        selectTimePreference = TravelRequest.TimePreference.DEPARTURE;
    }

    public void selectArrive(View view){
        this.departureTextView.setBackgroundResource(R.color.background_secondaryaction);
        this.arriveTextView.setBackgroundResource(R.color.background_button_secondaction);
        this.arriveTextView.setTextColor(getResources().getColor(R.color.text_white_color));
        this.departureTextView.setTextColor(getResources().getColor(R.color.text_blue_color));
        selectTimePreference = TravelRequest.TimePreference.ARRIVAL;
    }
}
