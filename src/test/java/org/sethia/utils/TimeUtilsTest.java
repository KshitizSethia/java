package org.sethia.utils;

import java.text.ParseException;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

public class TimeUtilsTest {

  public static final long MILLISECONDS = 1525071600000L;
  private static final TimeUtils pdtTime = TimeUtils.PDT;

  @Test
  public void incrementByADay_normal() throws ParseException {
    Date initial = pdtTime.fromShortString("20180426");
    Date next = pdtTime.incrementByADay(initial);
    Assert.assertEquals("2018-04-27", pdtTime.toDashedString(next));
  }

  @Test
  public void incrementByADay_aroundYear() throws ParseException {
    Date initial = pdtTime.fromShortString("20171231");
    Date next = pdtTime.incrementByADay(initial);
    Assert.assertEquals("2018-01-01", pdtTime.toDashedString(next));
  }

  @Test
  public void incrementByADay_aroundMonth() throws ParseException {
    Date initial = pdtTime.fromShortString("20180430");
    Date next = pdtTime.incrementByADay(initial);
    Assert.assertEquals("2018-05-01", pdtTime.toDashedString(next));
  }

  @Test
  public void incrementByADay_aroundLeapYearFeb29() throws ParseException {
    Date initial = pdtTime.fromShortString("20160228");

    Date next = pdtTime.incrementByADay(initial);
    Assert.assertEquals("2016-02-29", pdtTime.toDashedString(next));

    next = pdtTime.incrementByADay(next);
    Assert.assertEquals("2016-03-01", pdtTime.toDashedString(next));
  }

  @Test
  public void fromStringThenToDashedString() throws ParseException {
    Date date = pdtTime.fromShortString("20180430");
    Assert.assertEquals(MILLISECONDS, date.toInstant().toEpochMilli());

    Assert.assertEquals("2018-04-30", pdtTime.toDashedString(date));
  }

  @Test
  public void toDouble() {
    Date date = new Date(MILLISECONDS);
    Assert.assertEquals(Double.valueOf(20180430.0), pdtTime.toDouble(date));
  }
}