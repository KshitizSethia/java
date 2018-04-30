package org.sethia.projects.trading;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ComparisonChain;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@AutoValue
public abstract class StockLot implements Comparable<StockLot> {

  private static final SimpleDateFormat EIGHT_FORMAT = new SimpleDateFormat("yyyyMMdd");
  private static final SimpleDateFormat TEN_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
  private final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PDT"));

  public static StockLot create(String ticker, String dateString, Double numUnits,
      Double costBasis) {
    Date buyDate = null;
    try {
      buyDate = parseDate(dateString);
      StockLot result = new AutoValue_StockLot(ticker, buyDate, numUnits, costBasis);
      result.setTimeInCalendar(buyDate);
      return result;
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static Date parseDate(String dateString) throws ParseException {
    SimpleDateFormat format;
    switch (dateString.length()) {
      case 6:
        dateString = "20" + dateString;
      case 8:
        format = EIGHT_FORMAT;
        break;
      case 10:
        format = TEN_FORMAT;
        break;
      default:
        throw new IllegalArgumentException(
            "dateString is not in a recognized format: " + dateString);
    }

    return format.parse(dateString);
  }

  @Override
  public final int compareTo(StockLot o) {
    return ComparisonChain.start()
        .compare(this.getBuyDate(), o.getBuyDate())
        .compare(this.getTicker(), o.getTicker())
        .compare(this.getCostBasis(), o.getCostBasis())
        .compare(this.getNumUnits(), o.getNumUnits())
        .result();
  }

  public abstract String getTicker();

  public abstract Date getBuyDate();

  public abstract Double getNumUnits();

  public abstract Double getCostBasis();

  protected final void setTimeInCalendar(Date date) {
    cal.setTime(date);
  }

  public final int getBuyYear() {
    return cal.get(Calendar.YEAR);
  }

  /**
   * @return int which matches with @{link {@link Calendar.JANUARY}} etc.
   */
  public final int getBuyMonth() {
    return cal.get(Calendar.MONTH);
  }

  public final int getBuyDayOfMonth() {
    return cal.get(Calendar.DAY_OF_MONTH);
  }
}
