package org.sethia.projects.trading;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ComparisonChain;
import java.text.ParseException;
import java.util.Date;
import org.sethia.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoValue
public abstract class StockLot implements Comparable<StockLot> {

  private static final Logger log = LoggerFactory.getLogger(StockLot.class);

  public static StockLot create(String ticker, String dateString, Double numUnits,
      Double costBasis) throws ParseException {
    Date buyDate = parseDate(dateString);
    StockLot result = new AutoValue_StockLot(ticker, buyDate, numUnits, costBasis);
    return result;
  }

  private static Date parseDate(String dateString) throws ParseException {
    switch (dateString.length()) {
      case 6:
        dateString = "20" + dateString;
      case 8:
        return TimeUtils.PDT.fromShortString(dateString);
      case 10:
        return TimeUtils.PDT.fromSlashedString(dateString);
      default:
        throw new IllegalArgumentException(
            "dateString is not in a recognized format: " + dateString);
    }
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
}
