package org.sethia.utils;

import java.util.Calendar;
import java.util.Date;

public abstract class TimeUtils {

  public static Date incrementByADay(Date currentDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(currentDate);
    cal.add(Calendar.DATE, 1); //minus number would decrement the days
    return cal.getTime();
  }
}
