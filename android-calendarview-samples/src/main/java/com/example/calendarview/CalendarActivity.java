package com.example.calendarview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.oyorooms.calendar.listeners.DateSelectionListener;
import com.oyorooms.calendar.ui.OYOCalendarView;

import java.util.Calendar;
import java.util.Date;


public class CalendarActivity extends Activity implements DateSelectionListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        OYOCalendarView mOYOCalendarView = (OYOCalendarView) findViewById(R.id.calendar_view);
        mOYOCalendarView.setDateSelectionListener(this);
    }

    @Override
    public void onRangeSelected(Date startDate, Date endDate) {
        Log.e("Range :: start - end  ", startDate + "  " + endDate);
    }

    @Override
    public void onDateSelected(Date date) {
        Log.e("Date :: ", date + "  ");
    }

    private Calendar getPredefinedRange(int option){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (option == 0){
            calendar.set(2017, Calendar.MAY, 5);
        } else {
            calendar.set(2017, Calendar.MAY, 10);
        }
        return calendar;
    }
}
