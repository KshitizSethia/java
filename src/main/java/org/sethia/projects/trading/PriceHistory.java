package org.sethia.projects.trading;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.util.Strings;
import org.sethia.utils.TimeUtils;
import org.sethia.utils.Tuple;
import org.sethia.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceHistory {

  private static final Logger log = LoggerFactory.getLogger(PriceHistory.class);
  private static final double INITIAL_INVESTMENT = 100.0;

  @JsonProperty("Time Series (Daily)")
  final Map<String, PriceRange> pricesSortedByDate;

  @JsonIgnore
  private String ticker;

  @JsonCreator
  PriceHistory(
      @JsonProperty("Time Series (Daily)")
          Map<String, PriceRange> prices) {
    // sort map by date and store
    this.pricesSortedByDate = Collections.unmodifiableMap(new TreeMap<>(prices));
  }

  public PriceRange getPriceForDate(final Date date) {
    final String key = TimeUtils.PDT.toDashedString(date);
    return pricesSortedByDate.get(key);
  }

  public List<Tuple<Date, Double>> getAppreciationHistory(int tradingDays) {
    List<String> days = new ArrayList<>(pricesSortedByDate.keySet());
    int initialDayIndex = days.size() - tradingDays;
    List<Tuple<Date, Double>> result = new ArrayList<>();
    final double units_bought =
        INITIAL_INVESTMENT / pricesSortedByDate.get(days.get(initialDayIndex)).getDayPrice();

    for (int index = initialDayIndex; index < days.size(); index++) {
      PriceRange priceRange = pricesSortedByDate.get(days.get(index));

      final double currentValue = units_bought * priceRange.getDayPrice();
      final double gain = currentValue - INITIAL_INVESTMENT;

      result.add(Tuple.of(days.get(index)
          , gain * 100.0 / INITIAL_INVESTMENT));
    }

    return result;
  }

  public String getTicker() {
    return ticker;
  }

  // TODO try eliminating this to make class immutable
  private void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public static class Builder {

    // TODO make a Config class
    private static final String KEY_PATH = "src/main/resources/config.txt";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String API_KEY = readApiKey();

    private static final String URI_STARTER = "https://www.alphavantage.co/query?"
        + "function=TIME_SERIES_DAILY"
        + "&dataType=json"
        + "&outputsize=full"
        + "&apikey=" + API_KEY
        + "&symbol=";

    private static String readApiKey() {
      try {
        return Files.asCharSource(new File(KEY_PATH), Charset.forName("UTF-8")).read();
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }

    public static PriceHistory of(final String ticker) throws IOException {
      String response = Strings.EMPTY;
      try {
        final String safeTicker = URLEncoder.encode(ticker, "UTF-8");
        final String uri = URI_STARTER + safeTicker;
        log.debug("uri for retrieving stock history: " + uri);
        response = WebUtils.INSTANCE_WITH_CACHING.makeGETRequest(uri);
        log.trace("response with stock history: " + response);
        final PriceHistory result = mapper.readValue(response, PriceHistory.class);
        // TODO remove this setter
        result.setTicker(ticker);
        return result;
      } catch (Exception forward) {
        log.error("Error in retrieving PriceHistory for {}, response recieved: {}", ticker,
            response, forward);
        throw forward;
      }
    }

    public static Map<String, PriceHistory> of(final Set<String> tickers) {
      Map<String, PriceHistory> result = new HashMap<>();

      tickers.parallelStream()
          .forEach(ticker -> {
            try {
              TimeUnit.MILLISECONDS.sleep(500);
              result.put(ticker, of(ticker));
            } catch (IOException e) {
              e.printStackTrace();
              throw new RuntimeException(e);
            } catch (InterruptedException e) {
              e.printStackTrace();
              log.warn("Interrupted while sleeping to rate limit.");
            }
          });

      return result;
    }
  }
}
