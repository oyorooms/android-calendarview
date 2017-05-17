package com.oyorooms.calendar.models;

/**
 * Created by aneesha.bahukhandi on 15/05/17
 */

public class MonthDescriptor {

    private int daysOfMonth;
    private String monthName;
    private int firstDayOfMonthInWeek; //which day of week does the month start from
    private DateStateDescriptor[] dateStateInfo;


    public MonthDescriptor(int daysOfMonth, int firstDayOfMonthInWeek, String monthName, int currDate,
                           int month, int year){
        this.daysOfMonth = daysOfMonth;
        this.firstDayOfMonthInWeek = firstDayOfMonthInWeek;
        this.monthName = monthName;
        initStates(currDate, month, year);
    }

    private void initStates(int currDate, int month, int year){
        this.dateStateInfo = new DateStateDescriptor[this.daysOfMonth];
        int i = 0;
        if (currDate > DateStateDescriptor.noCurrDateInMonth){ //only certain dates are selectable
            for (; i < currDate - 1 && i < this.daysOfMonth; i++){
                this.dateStateInfo[i] = new DateStateDescriptor(i + 1, month, year, false, false, DateStateDescriptor.RangeState.NONE, false);
            }
            if (i < this.daysOfMonth) {
                this.dateStateInfo[i] = new DateStateDescriptor(++i, month, year, currDate <= daysOfMonth, currDate <= daysOfMonth, DateStateDescriptor.RangeState.NONE, false);
            }
        }
        //all dates are selectable
        for (; i < this.daysOfMonth; i++){
            this.dateStateInfo[i] = new DateStateDescriptor(i + 1, month, year, false, true, DateStateDescriptor.RangeState.NONE, false);
        }
    }

    public int getDaysOfMonth() {
        return daysOfMonth;
    }

    public String getMonthName() {
        return monthName;
    }

    public int getFirstDayOfMonthInWeek() {
        return firstDayOfMonthInWeek;
    }

    public DateStateDescriptor[] getDateStateInfo() {
        return dateStateInfo;
    }

    public void setPredefinedRange(int start, int end){
        if (start > 0 && start < end && start < this.daysOfMonth){
            for (int i = start - 1; i < end && i < this.daysOfMonth; i++){
                if (i == start - 1){
                    this.dateStateInfo[i].setPredefinedRangeState(DateStateDescriptor.RangeState.START);
                } else if (i == end - 1){
                    if (this.dateStateInfo[i].isSelectable()){
                        this.dateStateInfo[i].setPredefinedRangeState(DateStateDescriptor.RangeState.OPEN);
                    } else {
                        this.dateStateInfo[i].setPredefinedRangeState(DateStateDescriptor.RangeState.END);
                    }
                } else {
                    this.dateStateInfo[i].setPredefinedRangeState(DateStateDescriptor.RangeState.MIDDLE);
                }
            }
        }
    }
}
