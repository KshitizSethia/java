package org.sethia.utils;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.sethia.projects.trading.PriceHistory;
import org.sethia.projects.trading.Ticker;

public class GrapherTest {

  @Test
  public void test_Graphing_CompareIndices() throws IOException, InterruptedException {
    Grapher grapher = new Grapher("Comparing tickers", "Appreciation percentage over time");

    final Double usd_invested = 100.0;

    for (String tickerName : new String[]{Ticker.SP500.getName(),
        Ticker.NASDAQ.getName(), Ticker.DOW30.getName()}) {
      PriceHistory prices = PriceHistory.Builder.of(tickerName);

      List<Double> appreciationPercentages = prices
          .getAppreciationHistory(300);
      List<Tuple<Double, Double>> points = new ArrayList<>();

      for (int index = 0; index < appreciationPercentages.size(); index++) {
        points.add(Tuple.of(Double.valueOf(index), appreciationPercentages.get(index)));
      }

      grapher.addLineGraph(points, tickerName);
    }
    grapher.render();
    TimeUnit.SECONDS.sleep(30);
  }

  @Test
  public void test() {
    List<String> zones = new ArrayList<>(ZoneId.getAvailableZoneIds());
    Collections.sort(zones);
    for (String id : zones) {
      System.out.println(id);
    }
  }

}