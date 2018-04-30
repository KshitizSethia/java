package org.sethia.projects.trading;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.sethia.projects.trading.Portfolio.Builder;
import org.sethia.utils.Tuple;

public class PortfolioTest {

  @Test
  public void test_getAppreciationSinceStart() throws IOException {
    final Portfolio test_portfolio = Builder
        .fromYahooFinanceCSV("src/test/resources/test_portfolio.csv", "test_portfolio");

    test_portfolio
        .getAppreciationSinceStart();
  }
}