package org.sethia.projects.trading;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Date;
import org.junit.Test;

public class PriceHistoryTest {

  @Test
  public void test_MSFT() throws IOException {
    PriceHistory priceHistory = PriceHistory.Builder.of("MSFT");
    assertEquals("MSFT", priceHistory.getTicker());
    assertNotNull(priceHistory);
    assertNotNull(priceHistory.getPriceForDate(new Date()));
  }

  @Test
  public void test_SP500() throws IOException {
    PriceHistory priceHistory = PriceHistory.Builder.of(Ticker.SP500.getName());
    assertEquals(Ticker.SP500.getName(), priceHistory.getTicker());
    assertNotNull(priceHistory);
    assertNotNull(priceHistory.getPriceForDate(new Date()));
  }
}