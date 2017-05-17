package com.oyorooms;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

/**
 * Created by aneesha.bahukhandi on 15/05/17
 */

public class RangeInMonthAdapter extends BaseMonthAdapter{

    private boolean startRangeSelection = false, stopRangeSelection = false;

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

    protected void validateAndMarkBoundaries(DateStateDescriptor descriptor){
        if (!startRangeSelection){
            startRangeSelection = true;
            startCal.set(descriptor.getYear(), descriptor.getMonth(), descriptor.getDay());
            descriptor.setRangeState(DateStateDescriptor.RangeState.START);
        } else if (!stopRangeSelection){
            stopRangeSelection = true;
            endCal.set(descriptor.getYear(), descriptor.getMonth(), descriptor.getDay());
            if (endCal.compareTo(startCal) < 0){
                Calendar temp = startCal;
                startCal = endCal;
                endCal = temp;
                descriptor.setRangeState(DateStateDescriptor.RangeState.START);
            } else if (endCal.compareTo(startCal) == 0){
                descriptor.setRangeState(DateStateDescriptor.RangeState.NONE);
                descriptor.setSingleSelection(true);
            } else {
                descriptor.setRangeState(DateStateDescriptor.RangeState.END);
            }
            //calculate range
            recalculateRange();
            if (sListener != null) {
                sListener.onRangeSelected(startCal.getTime(), endCal.getTime());
            }
        } else {
            startRangeSelection = false;
            stopRangeSelection = false;
            invalidatePrevDateRange();
            validateAndMarkBoundaries(descriptor);
        }
        notifyDataSetChanged();
    }

    @Override
    protected void setCalendarCellState(DateStateDescriptor state, CalendarCellView cell) {
        Log.e("Date descriptor", state + "");
        cell.setRangeState(state.getRangeState());
        cell.setSelectable(state.isSelectable());
        cell.setToday(state.isToday());
        cell.setTag(state);
    }
}