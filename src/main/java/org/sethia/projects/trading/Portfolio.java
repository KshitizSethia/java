package org.sethia.projects.trading;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
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


public class Portfolio {

  private static final org.slf4j.Logger log = LoggerFactory.getLogger(Portfolio.class);

  private final List<StockLot> stocks;
  private final String name;
  private final Map<String, PriceHistory> priceHistoryMap;

  private Portfolio(List<StockLot> stocks, String portfolioName) {
    Collections.sort(stocks);
    this.stocks = Collections.unmodifiableList(stocks);
    log.debug("initialized portfolio with:");
    for (StockLot lot : stocks) {
      log.debug(lot.toString());
    }
    this.name = portfolioName;
    Set<String> tickers = stocks.stream().map(s -> s.getTicker()).collect(Collectors.toSet());
    this.priceHistoryMap = Collections.unmodifiableMap(PriceHistory.Builder.of(tickers));
  }

  public List<StockLot> getStocks() {
    return stocks;
  }

  public String getName() {
    return name;
  }

  public Map<String, List<Tuple<Date, Double>>> compareGainsWith(final String... tickers) {
    final Map<String, PriceHistory> priceHistoriesForReference = PriceHistory.Builder
        .of(Sets.newHashSet(tickers));

    Map<String, Double> holdingsInReferenceStocks = new HashMap<>();
    Map<String, List<Tuple<Date, Double>>> results = new HashMap<>();
    for (String tickerName : tickers) {
      results.put(tickerName, new ArrayList<>());
      holdingsInReferenceStocks.put(tickerName, 0.0);
    }

    //loop helpers
    final Date today = TimeUtils.PDT.getNow();
    final PeekingIterator<StockLot> iterator = Iterators.peekingIterator(getStocks().iterator());
    double amountInvestedSinceStart = 0.0;

    for (Date tradeDay = getStocks().get(0).getBuyDate();
        tradeDay.compareTo(today) <= 0;
        tradeDay = TimeUtils.PDT.incrementByADay(tradeDay)) {
      try {
        log.trace("Calculating comparative gains for date: " + tradeDay);

        //add holdings in reference stocks for actual stocks bought on this tradeDay
        double amountInvestedToday = 0.0;
        while (iterator.hasNext() && iterator.peek().getBuyDate().equals(tradeDay)) {
          StockLot lot = iterator.next();
          log.debug("Found a lot to add for comparison: {}", lot);
          amountInvestedToday += lot.getCostBasis() * lot.getNumUnits();
        }
        amountInvestedSinceStart += amountInvestedToday;

        for (String ticker : tickers) {
          final double costBasis = priceHistoriesForReference.get(ticker)
              .getPriceForDate(tradeDay).getDayPrice();

          final double unitsHeld =
              (amountInvestedToday / costBasis) + holdingsInReferenceStocks.get(ticker);

          holdingsInReferenceStocks.put(ticker, unitsHeld);

          final double appreciationPercentage =
              ((unitsHeld * costBasis) - amountInvestedSinceStart) * 100.0
                  / amountInvestedSinceStart;
          log.trace("By {}, {} has appreciated {}%", tradeDay.toString(), ticker,
              appreciationPercentage);
          results.get(ticker).add(Tuple.of(tradeDay, appreciationPercentage));
        }
        log.trace("total investment: {}, holdings: {}", amountInvestedSinceStart,
            holdingsInReferenceStocks);

      } catch (NullPointerException npe) {
        log.info("Did not find trading data for " + tradeDay);
      }
    }

    return results;
  }

  public List<Tuple<Date, Double>> getAppreciationSinceStart() {
    List<Tuple<Date, Double>> result = new ArrayList<>();
    double amountInvested = 0.0;

    final Date today = TimeUtils.PDT.getNow();
    final PeekingIterator<StockLot> iterator = Iterators.peekingIterator(getStocks().iterator());

    Map<String, Double> currentHoldingsInStock = new HashMap<>();
    int failTracker = 0;
    for (Date currentDate = getStocks().get(0).getBuyDate();
        currentDate.compareTo(today) <= 0;
        currentDate = TimeUtils.PDT.incrementByADay(currentDate)) {
      log.trace("Calculating portfolio gains for date: " + currentDate);
      // add stocks added today
      while (iterator.hasNext() && iterator.peek().getBuyDate().equals(currentDate)) {
        StockLot lot = iterator.next();
        log.debug("Found a lot to add to portfolio: {}", lot);
        amountInvested += lot.getCostBasis() * lot.getNumUnits();

        double unitsBought = lot.getNumUnits();
        if (currentHoldingsInStock.containsKey(lot.getTicker())) {
          unitsBought += currentHoldingsInStock.get(lot.getTicker());
        }
        currentHoldingsInStock.put(lot.getTicker(), unitsBought);
      }
      log.trace("total investment: {}, holdings: {}", amountInvested, currentHoldingsInStock);

      try {
        final Date finalCurrentDate = currentDate;
        double currentValue = currentHoldingsInStock.entrySet().stream().map(e ->
            this.priceHistoryMap.get(e.getKey()).getPriceForDate(finalCurrentDate).getDayPrice()
                * e.getValue()
        ).mapToDouble(Double::valueOf).sum();
        log.trace("value of portfolio on {}: {}", finalCurrentDate.toString(), currentValue);

        double appreciation = (currentValue - amountInvested) * 100 / amountInvested;
        log.trace("appreciation of {}% on {}", appreciation, currentDate.toString());

        result.add(Tuple.of(finalCurrentDate, appreciation));
        failTracker = 0;
      } catch (NullPointerException npe) {
        // entry for this trading day was not found.
        failTracker++;
        log.info("no historical data found for " + currentDate);
        if (failTracker > 5) {// more than 5 days have been giving errors, not usual
          log.error("Haven't found historical data for more than 5 days", npe);
          throw npe;
        }
      }
    }

    return result;
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
     * Make a {@link Portfolio} with csv exported fromShortString Yahoo Finance
     */
    public static final Portfolio fromYahooFinanceCSV(String csvFilePath,
        final String portfolioName)
        throws IOException {
      List<StockLot> stockLots = new ArrayList<>();

      try (Reader in = openFile(csvFilePath)) {

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
            log.warn("Error in reading line from CSV: " + record.toString(), logged);
          }
        }
      }
      return new Portfolio(stockLots, portfolioName);
    }


    public static Portfolio fromExistingPortfolioWithReplacedTicker(final Portfolio other,
        final String newTickerName) throws IOException, ParseException {
      PriceHistory history = PriceHistory.Builder.of(newTickerName);
      List<StockLot> newLots = new ArrayList<>();

      for (StockLot lot : other.getStocks()) {
        log.debug("copying {} to new portfolio with {}", lot.toString(), newTickerName);
        double amountSpentInLot = lot.getNumUnits() * lot.getCostBasis();
        double newCostBasis = history.getPriceForDate(lot.getBuyDate()).getDayPrice();
        double numUnitsOfNewTicker = amountSpentInLot / newCostBasis;

        StockLot newLot = StockLot
            .create(newTickerName, TimeUtils.PDT.toShortString(lot.getBuyDate()),
                numUnitsOfNewTicker,
                newCostBasis);
        newLots.add(newLot);
      }

      return new Portfolio(newLots, "Buying " + newTickerName + " instead of " + other.getName());
    }

    static Reader openFile(String csvFilePath) throws FileNotFoundException {
      return new FileReader(csvFilePath);
    }
  }

}
