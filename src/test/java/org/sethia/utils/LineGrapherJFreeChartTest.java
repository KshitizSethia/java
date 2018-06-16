package org.sethia.utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;
import org.sethia.projects.trading.PriceHistory;
import org.sethia.projects.trading.Ticker;
import org.sethia.utils.graphing.LineGrapherJFreeChart;

public class LineGrapherJFreeChartTest {

  @Test
  @Ignore
  public void test_Graphing_CompareIndices() throws IOException, InterruptedException {
    LineGrapherJFreeChart lineGrapher = new LineGrapherJFreeChart("Comparing tickers", "Time",
        "Gain %age");

    final Double usd_invested = 100.0;

    for (String tickerName : new String[]{Ticker.SP500.getName(),
        Ticker.NASDAQ.getName(), Ticker.DOW30.getName()}) {
      PriceHistory prices = PriceHistory.Builder.of(tickerName);

      List<Tuple<Date, Double>> appreciationPercentages = prices
          .getAppreciationHistory(300);

      appreciationPercentages
          .forEach(tuple ->
              lineGrapher.addPoint(tuple.getLeft(), tuple.getRight(), tickerName)
          );
    }
    lineGrapher.render();
    TimeUnit.SECONDS.sleep(5);
  }

  @Test
  public void test_datesAndDoubles_2Lines() throws ParseException, InterruptedException {
    LineGrapherJFreeChart grapher = new LineGrapherJFreeChart("Test graph", "Time", "Gain %age");

    Random random = new Random();

    Date today = TimeUtils.PDT.getNow();

    for (Date currentDay = TimeUtils.PDT.fromShortString("20170523");
        currentDay.compareTo(today) <= 0;
        currentDay = TimeUtils.PDT.incrementByADay(currentDay)) {

      grapher.addPoint(currentDay, 10 + random.nextDouble() * 90, "line100");
      grapher.addPoint(currentDay, random.nextDouble() * 10, "line10");
    }

    grapher.render();
    TimeUnit.SECONDS.sleep(45);
  }

}