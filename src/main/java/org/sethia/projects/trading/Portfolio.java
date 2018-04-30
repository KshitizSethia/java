package org.sethia.projects.trading;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.sethia.utils.TimeUtils;
import org.sethia.utils.Tuple;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class Portfolio {

  private static final org.slf4j.Logger log = LoggerFactory.getLogger(Portfolio.class);
  private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");

  private final List<StockLot> stocks;
  private final String name;
  private final Map<String, PriceHistory> priceHistoryMap;

  private Portfolio(List<StockLot> stocks, String portfolioName) {
    Collections.sort(stocks);
    this.stocks = Collections.unmodifiableList(stocks);
    this.name = portfolioName;
    Set<String> tickers = stocks.stream().map(s -> s.getTicker()).collect(Collectors.toSet());
    this.priceHistoryMap = PriceHistory.Builder.of(tickers);
  }

  public List<StockLot> getStocks() {
    return stocks;
  }

  public Map<String, List<Tuple<Double, Double>>> compareGainsWith(List<String> tickers) {
    Map<String, List<Tuple<Double, Double>>> results = new HashMap<>();
    results.put(getName(), new ArrayList<Tuple<Double, Double>>());
    for (String tickerName : tickers) {
      results.put(tickerName, new ArrayList<Tuple<Double, Double>>());
    }

    throw new NotImplementedException();
  }

  public List<Tuple<Date, Double>> getAppreciationSinceStart() {
    List<Tuple<Date, Double>> result = new ArrayList<>();
    double amountInvested = 0.0;

    final Date today = new Date();
    final Date firstDate = getStocks().get(0).getBuyDate();

    final PeekingIterator<StockLot> iterator = Iterators.peekingIterator(getStocks().iterator());

    Date currentDate = (Date) firstDate.clone();
    Map<String, Double> currentHoldingsInStock = new HashMap<>();
    while (currentDate.compareTo(today) < 0) {
      // add stocks added today
      while (iterator.hasNext() && iterator.peek().getBuyDate().equals(currentDate)) {
        StockLot lot = iterator.next();
        amountInvested += lot.getCostBasis() * lot.getNumUnits();

        double unitsBought = lot.getNumUnits();
        if (currentHoldingsInStock.containsKey(lot.getTicker())) {
          unitsBought += lot.getNumUnits();
        }
        currentHoldingsInStock.put(lot.getTicker(), unitsBought);
      }

      try {
        final Date finalCurrentDate = currentDate;
        double currentValue = currentHoldingsInStock.entrySet().stream().map(e ->
            this.priceHistoryMap.get(e.getKey()).getPriceForDate(finalCurrentDate).getDayPrice()
                * e.getValue()
        ).mapToDouble(Double::valueOf).sum();

        double appreciation = (currentValue - amountInvested) * 100 / amountInvested;
        result.add(Tuple.of(finalCurrentDate, appreciation));

      } catch (NullPointerException ignored) {
        // entry for this trading day was not found.
      }

      currentDate = TimeUtils.incrementByADay(currentDate);
    }

    return result;
  }

  public String getName() {
    return name;
  }

  public static class Builder {

    private static final String YFINANCE_PURCHASE_PRICE = "Purchase Price";
    private static final String YFINANCE_QUANTITY = "Quantity";
    private static final String YFINANCE_TRADE_DATE = "Trade Date";
    private static final String YFINANCE_TICKER = "Symbol";

    private static final String[] HEADERS = new String[]{YFINANCE_PURCHASE_PRICE, YFINANCE_QUANTITY,
        YFINANCE_TRADE_DATE,
        YFINANCE_TICKER};

    /**
     * Make a {@link Portfolio} with csv exported from Yahoo Finance
     */
    public static final Portfolio fromYahooFinanceCSV(String csvFilePath,
        final String portfolioName)
        throws IOException {
      List<StockLot> stockLots = new ArrayList<>();

      try (Reader in = new FileReader(csvFilePath)) {

        Iterable<CSVRecord> records = CSVFormat.DEFAULT
            .withHeader(HEADERS)
            .withFirstRecordAsHeader()
            .parse(in);

        for (CSVRecord record : records) {
          try {
            final String ticker = record.get(YFINANCE_TICKER);
            final String dateString = record.get(YFINANCE_TRADE_DATE);
            final Double numUnits = Double.valueOf(record.get(YFINANCE_QUANTITY));
            final Double costBasis = Double.valueOf(record.get(YFINANCE_PURCHASE_PRICE));

            StockLot lot = StockLot.create(ticker, dateString, numUnits, costBasis);

            if (lot != null) {
              stockLots.add(lot);
            }
          } catch (Exception logged) {
            log.error("Error in reading line from CSV: " + record, logged);
          }
        }
      }
      return new Portfolio(stockLots, portfolioName);
    }
  }

}
