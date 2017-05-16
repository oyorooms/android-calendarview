package com.oyorooms;

public class DateStateDescriptor {

    public enum RangeState {
        NONE, START, MIDDLE, END
    }

    public static final int noCurrDateInMonth = 0;

    private final int day;
    private final int month;
    private final int year;
    private final boolean isToday;
    private final boolean isSelectable;
    private RangeState rangeState;
    private boolean isSingleSelection;

    public DateStateDescriptor(int day, int month, int year, boolean today, boolean selectable,
                               RangeState rangeState, boolean isSingleSelection) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.isToday = today;
        this.isSelectable = selectable;
        this.rangeState = rangeState;
        this.isSingleSelection = isSingleSelection;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public boolean isToday() {
        return isToday;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public RangeState getRangeState() {
        return rangeState;
    }

    public boolean isSingleSelection() {
        return isSingleSelection;
    }

    public void setRangeState(RangeState rangeState) {
        this.rangeState = rangeState;
    }

    public void setSingleSelection(boolean singleSelection) {
        isSingleSelection = singleSelection;
    }

    @Override
    public String toString() {
        return "DateStateDescriptor{"
                + "date="
                + day + "/" + month + "/" + year
                + ", isToday="
                + isToday
                + ", isSelectable="
                + isSelectable
                + ", rangeState="
                + rangeState
                + ", isSingleSelection="
                + isSingleSelection
                + '}';
    }
}
