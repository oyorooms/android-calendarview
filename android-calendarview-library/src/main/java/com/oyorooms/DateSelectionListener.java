package com.oyorooms;

import java.util.Date;

/**
 * Created by aneesha.bahukhandi on 16/05/17
 */

public interface DateSelectionListener {
    void onRangeSelected(Date startDate, Date endDate);
    void onDateSelected(Date date);
}
