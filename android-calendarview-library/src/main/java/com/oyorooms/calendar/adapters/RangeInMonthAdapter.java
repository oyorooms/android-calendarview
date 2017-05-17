package com.oyorooms.calendar.adapters;

import android.content.Context;

import com.oyorooms.calendar.models.DateStateDescriptor;
import com.oyorooms.calendar.models.MonthDescriptor;
import com.oyorooms.calendar.ui.CalendarCellView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by aneesha.bahukhandi on 15/05/17
 */

public class RangeInMonthAdapter extends BaseMonthAdapter {

    private boolean startRangeSelection = false, stopRangeSelection = false;
    private DateStateDescriptor openRangeDescriptor;

    public RangeInMonthAdapter(Context context, List<MonthDescriptor> monthsList){
        super(context, monthsList);
    }

    private void recalculateRange(){
        boolean shouldStartRange = false;
        for (MonthDescriptor descriptor : mMonthsList){
            for (DateStateDescriptor descriptor1 : descriptor.getDateStateInfo()){
                if (!shouldStartRange) {
                    if (descriptor1.getRangeState() == DateStateDescriptor.RangeState.START) {
                        shouldStartRange = true;
                        continue;
                    }
                }
                if (shouldStartRange){
                    if (descriptor1.getRangeState() == DateStateDescriptor.RangeState.START ||
                            descriptor1.getRangeState() == DateStateDescriptor.RangeState.END) {
                        descriptor1.setRangeState(DateStateDescriptor.RangeState.END);
                        return;
                    } else {
                        descriptor1.setRangeState(DateStateDescriptor.RangeState.MIDDLE);
                    }
                }
            }
        }
    }

    private void recalculateExtension(){
        boolean shouldStartRange = false;
        for (MonthDescriptor descriptor : mMonthsList){
            for (DateStateDescriptor descriptor1 : descriptor.getDateStateInfo()){
                if (!shouldStartRange) {
                    if (descriptor1.getPredefinedRangeState() == DateStateDescriptor.RangeState.OPEN) {
                        descriptor1.setPredefinedRangeState(DateStateDescriptor.RangeState.MIDDLE);
                        shouldStartRange = true;
                        continue;
                    }
                }
                if (shouldStartRange){
                    if (descriptor1.getRangeState() != DateStateDescriptor.RangeState.END) {
                        descriptor1.setRangeState(DateStateDescriptor.RangeState.MIDDLE);
                    } else if (descriptor1.getRangeState() == DateStateDescriptor.RangeState.END){
                        return;
                    }
                }
            }
        }
    }

    private void invalidatePrevDateRange(){
        for (MonthDescriptor descriptor : mMonthsList){
            for (DateStateDescriptor descriptor1 : descriptor.getDateStateInfo()){
                if (descriptor1.getRangeState() != DateStateDescriptor.RangeState.NONE){
                    if (descriptor1.getRangeState() == DateStateDescriptor.RangeState.END) {
                        descriptor1.setRangeState(DateStateDescriptor.RangeState.NONE);
                        return;
                    }
                    descriptor1.setRangeState(DateStateDescriptor.RangeState.NONE);
                }
            }
        }
    }

    private void invalidateRangeExtension(){
        boolean invalidate = false;
        openRangeDescriptor.setPredefinedRangeState(DateStateDescriptor.RangeState.OPEN);
        for (MonthDescriptor descriptor : mMonthsList){
            for (DateStateDescriptor descriptor1 : descriptor.getDateStateInfo()){
                if (invalidate) {
                    DateStateDescriptor.RangeState rangeState = descriptor1.getRangeState();
                    descriptor1.setRangeState(DateStateDescriptor.RangeState.NONE);
                    if (rangeState == DateStateDescriptor.RangeState.END){
                        return;
                    }
                } else if (descriptor1.getPredefinedRangeState() == DateStateDescriptor.RangeState.OPEN){
                    invalidate = true;
                }
            }
        }
    }

    protected void validateAndMarkBoundaries(DateStateDescriptor descriptor){
        if (!startRangeSelection){
            startRangeSelection = true;
            startCal.set(descriptor.getYear(), descriptor.getMonth(), descriptor.getDayOfMonth());
            descriptor.setRangeState(DateStateDescriptor.RangeState.START);
        } else if (!stopRangeSelection){
            stopRangeSelection = true;
            endCal.set(descriptor.getYear(), descriptor.getMonth(), descriptor.getDayOfMonth());
            if (this.openRangeDescriptor == null) {// normal range selection
                if (endCal.compareTo(startCal) < 0) {
                    Calendar temp = startCal;
                    startCal = endCal;
                    endCal = temp;
                    descriptor.setRangeState(DateStateDescriptor.RangeState.START);
                } else if (endCal.compareTo(startCal) == 0) {
                    descriptor.setRangeState(DateStateDescriptor.RangeState.NONE);
                    descriptor.setSingleSelection(true);
                } else {
                    descriptor.setRangeState(DateStateDescriptor.RangeState.END);
                }
                //calculate range
                recalculateRange();
            } else {
                if (endCal.compareTo(startCal) > 0) {
                    descriptor.setRangeState(DateStateDescriptor.RangeState.END);
                    recalculateExtension();
                } else if (endCal.compareTo(startCal) < 0){
                    return;
                }
            }
            if (sListener != null) {
                sListener.onRangeSelected(startCal.getTime(), endCal.getTime());
            }
        } else {
            startRangeSelection = openRangeDescriptor != null;
            stopRangeSelection = false;
            if (openRangeDescriptor == null) { //normal range selection only
                invalidatePrevDateRange();
            } else {
                invalidateRangeExtension();
            }
            validateAndMarkBoundaries(descriptor);
        }
        notifyDataSetChanged();
    }

    @Override
    protected void setCalendarCellState(DateStateDescriptor state, CalendarCellView cell) {
        cell.setRangeState(state.getRangeState());
        cell.setSelectable(state.isSelectable());
        cell.setToday(state.isToday());
        cell.setPredefinedRangeState(state.getPredefinedRangeState());
        cell.setTag(state);
        if (state.getPredefinedRangeState() == DateStateDescriptor.RangeState.OPEN){
            this.openRangeDescriptor = state;
            this.startRangeSelection = true;
            startCal.set(state.getYear(), state.getMonth(), state.getDayOfMonth());
        }
    }
}