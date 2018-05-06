package org.sethia.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public enum TimeUtils {
  PDT(TimeZone.getTimeZone("America/Los_Angeles"));

  private final DateFormat DF_SHORT, DF_DASHED, DF_SLASHED;

  TimeUtils(final TimeZone timeZone) {
    DF_SHORT = new SimpleDateFormat("yyyyMMdd");
    DF_SHORT.setTimeZone(timeZone);
    DF_DASHED = new SimpleDateFormat("yyyy-MM-dd");
    DF_DASHED.setTimeZone(timeZone);
    DF_SLASHED = new SimpleDateFormat("yyyy/MM/dd");
    DF_SLASHED.setTimeZone(timeZone);
  }

  public Date incrementByADay(Date currentDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(currentDate);
    cal.add(Calendar.DATE, 1); //minus number would decrement the days
    return cal.getTime();
  }

  // helps mocking current time for unit tests
  public Date getNow() {
    return new Date();
  }

  public Date fromShortString(final String yyyyMMdd) throws ParseException {
    return DF_SHORT.parse(yyyyMMdd);
  }

  public Date fromSlashedString(String dateString) throws ParseException {
    return DF_SLASHED.parse(dateString);
  }

  public Double toDouble(Date date) {
    final String dateStr = DF_SHORT.format(date);
    return Double.valueOf(dateStr);
  }

  /**
   * @return date in dashed format: yyyy-MM-dd
   */
  public String toDashedString(Date date) {
    return DF_DASHED.format(date);
  }

  /**
   * @return date in short format yyyyMMdd
   */
  public String toShortString(Date date) {
    return DF_SHORT.format(date);
  }
}
