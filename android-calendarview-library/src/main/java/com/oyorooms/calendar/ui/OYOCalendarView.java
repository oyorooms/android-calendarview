package com.oyorooms.calendar.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.oyorooms.R;
import com.oyorooms.calendar.adapters.BaseMonthAdapter;
import com.oyorooms.calendar.adapters.RangeInMonthAdapter;
import com.oyorooms.calendar.adapters.SingleSelectionInMonthAdapter;
import com.oyorooms.calendar.enums.DateSelectionMode;
import com.oyorooms.calendar.listeners.DateSelectionListener;
import com.oyorooms.calendar.models.DateStateDescriptor;
import com.oyorooms.calendar.models.MonthDescriptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by aneesha.bahukhandi on 15/05/17
 */

public class OYOCalendarView extends LinearLayout {

    private Calendar mToday;
    private List<MonthDescriptor> mMonthDescriptorsList;
    private SimpleDateFormat mFullMonthNameFormat;
    private DateSelectionListener mDateSelectionListener;

    private int mNumberOfPreviousMonths;
    private int mNumberOfFutureMonths;
    private int mScrollPosition = 0;
    private DateSelectionMode mSelectionMode = DateSelectionMode.SINGLE;
    private int[] mStartPredefinedRange = {NA, NA, NA};  //[day, month, year]
    private int[] mEndPredefinedRange = {NA, NA, NA};  //[day, month, year]

    private static final int NA = -1;
    private static final int rotation = 1;
    private static final int defaultMonths = 4;
    private static final int monthsInAYear = 12;

    public OYOCalendarView(Context context) {
        super(context);
        initData();
        initView();
    }

    public OYOCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mNumberOfPreviousMonths = attrs != null ? attrs.getAttributeIntValue(R.attr.prev_months, defaultMonths) : defaultMonths;
        this.mNumberOfFutureMonths = attrs != null ? attrs.getAttributeIntValue(R.attr.next_months, defaultMonths) : defaultMonths;
        initData();
        initView();
    }

    public OYOCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mNumberOfPreviousMonths = attrs != null ? attrs.getAttributeIntValue(R.attr.prev_months, NA) : NA;
        this.mNumberOfFutureMonths = attrs != null ? attrs.getAttributeIntValue(R.attr.next_months, NA) : NA;
        initData();
        initView();
    }

    public void setNumberOfPreviousMonths(int numberOfPreviousMonths) {
        this.mNumberOfPreviousMonths = numberOfPreviousMonths;
    }

    public void setNumberOfFutureMonths(int numberOfFutureMonths) {
        this.mNumberOfFutureMonths = numberOfFutureMonths;
    }

    public void reCreateCalendar(){
        initData();
        initView();
    }

    public void setDateSelectionListener(DateSelectionListener mDateSelectionListener) {
        this.mDateSelectionListener = mDateSelectionListener;
    }

    public void setSelectionMode(DateSelectionMode mSelectionMode) {
        this.mSelectionMode = mSelectionMode;
    }

    public void setPredefinedRange(Calendar start, Calendar end){
        if (start.compareTo(end) < 0) { //start < end
            populateCalendarInArray(this.mStartPredefinedRange, start);
            populateCalendarInArray(this.mEndPredefinedRange, end);
        } else {
            throw new IllegalArgumentException("start date cannot be greater than end date");
        }
    }

    private void populateCalendarInArray(int[] predefinedRange, Calendar calendar){
        predefinedRange[0] = calendar.get(Calendar.DAY_OF_MONTH);
        predefinedRange[1] = calendar.get(Calendar.MONTH);
        predefinedRange[2] = calendar.get(Calendar.YEAR);
    }

    private void initData(){
        this.mToday = Calendar.getInstance();
        this.mMonthDescriptorsList = new ArrayList<>();
        this.mFullMonthNameFormat = new SimpleDateFormat(getContext().getString(R.string
                .header_month_name_format), Locale.getDefault());
        initMonths();
    }

    private void initMonths(){
        Calendar calculationCalendar = Calendar.getInstance();
        clearCalendar(calculationCalendar);
        int startMonth = mToday.get(Calendar.MONTH);
        if (this.mNumberOfPreviousMonths > NA){
            int currMonth = startMonth;
            startMonth = currMonth - this.mNumberOfPreviousMonths + 1;
            while (startMonth < 0){
                calculationCalendar.set(Calendar.YEAR, mToday.get(Calendar.YEAR) - 1);
                startMonth += monthsInAYear;
            }
        }
        calculationCalendar.set(Calendar.MONTH, startMonth);
        while (calculationCalendar.get(Calendar.MONTH) != mToday.get(Calendar.MONTH)
                || calculationCalendar.get(Calendar.YEAR) != mToday.get(Calendar.YEAR)){
            this.mMonthDescriptorsList.add(getNewMonthDescriptorForMonth(calculationCalendar));
            if (calculationCalendar.get(Calendar.MONTH) == Calendar.DECEMBER){
                calculationCalendar.set(Calendar.YEAR, calculationCalendar.get(Calendar.YEAR) + 1);
                calculationCalendar.set(Calendar.MONTH, Calendar.JANUARY);
            } else {
                calculationCalendar.set(Calendar.MONTH, calculationCalendar.get(Calendar.MONTH) + 1);
            }
        }
        this.mScrollPosition = mMonthDescriptorsList.size();
        this.mMonthDescriptorsList.add(getNewMonthDescriptorForMonth(calculationCalendar));
        if (this.mNumberOfFutureMonths > NA){
            int i = 0;
            int currMonth = mToday.get(Calendar.MONTH);
            while (++i <= this.mNumberOfFutureMonths){
                currMonth++;
                if (currMonth > Calendar.DECEMBER){
                    currMonth = Calendar.JANUARY;
                    calculationCalendar.set(Calendar.YEAR, calculationCalendar.get(Calendar.YEAR) + 1);
                }
                calculationCalendar.set(Calendar.MONTH, currMonth);
                this.mMonthDescriptorsList.add(getNewMonthDescriptorForMonth(calculationCalendar));
            }
        }
    }

    private void clearCalendar(Calendar calendar){
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private MonthDescriptor getNewMonthDescriptorForMonth(Calendar calendar){
        String monthName = mFullMonthNameFormat.format(calendar.getTime());

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 - rotation; //SUN - SAT :: 1 - 7 in Java
        if (firstDayOfWeek < 0) {
            firstDayOfWeek += 7;
        }

        int daysCount = getNumberOfDays(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

        int currDate = mToday.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && mToday.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) ?
                        mToday.get(Calendar.DAY_OF_MONTH) : DateStateDescriptor.noCurrDateInMonth;
        if (currDate == DateStateDescriptor.noCurrDateInMonth){
            if (calendar.compareTo(mToday) < 0){  //for prev months everything is unselectable. So currDate == daysCount
                currDate = daysCount + 1;
            }
            //for future months everything is selectable. So currDate == 0
        }

        MonthDescriptor monthDescriptor =  new MonthDescriptor(daysCount, firstDayOfWeek, monthName, currDate,
                calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

        int startPredefinedRange = NA, endPredefinedRange = daysCount + 1;
        if (this.mStartPredefinedRange[1] == (int) calendar.get(Calendar.MONTH) &&
                    this.mStartPredefinedRange[2] == (int) calendar.get(Calendar.YEAR)){
            startPredefinedRange = this.mStartPredefinedRange[0];
        }
        if (this.mEndPredefinedRange[1] == (int) calendar.get(Calendar.MONTH) &&
                this.mEndPredefinedRange[2] == (int) calendar.get(Calendar.YEAR)){
            endPredefinedRange = this.mEndPredefinedRange[0];
             //predefined range ends in another month
            startPredefinedRange = startPredefinedRange == NA ? 1 : startPredefinedRange;
        }
        monthDescriptor.setPredefinedRange(startPredefinedRange, endPredefinedRange);

        return monthDescriptor;
    }

    private int getNumberOfDays(int month, int year){
        switch(month){
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.FEBRUARY:
                if (year % 100 == 0){
                    return  year % 4 == 0? 29 : 28;
                }
                return  year % 4 == 0? 29 : 28;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
            default:
                return 30;
        }
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout parentLayout = (LinearLayout) inflater.inflate(R.layout.calendar_view, this, true);
        RecyclerView monthsList = (RecyclerView) parentLayout.findViewById(R.id.rv_months_list);
        monthsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //attach adapter
        BaseMonthAdapter adapter = getAdapter();
        adapter.setDateSelectionListener(this.mDateSelectionListener);
        monthsList.setAdapter(adapter);
        monthsList.scrollToPosition(mScrollPosition);
    }

    private BaseMonthAdapter getAdapter(){
        switch (mSelectionMode){
            case RANGE:
                return new RangeInMonthAdapter(getContext(), this.mMonthDescriptorsList);
            case SINGLE:
            default:
                return new SingleSelectionInMonthAdapter(getContext(), mMonthDescriptorsList);
        }
    }

}