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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.sethia.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceHistory {

  private static final Logger log = LoggerFactory.getLogger(PriceHistory.class);
  private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
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
    final String key = DF.format(date);
    return pricesSortedByDate.get(key);
  }

  //TODO return tuple with date
  public List<Double> getAppreciationHistory(int tradingDays) {
    List<String> days = new ArrayList<>(pricesSortedByDate.keySet());
    int initialDayIndex = days.size() - tradingDays;
    List<Double> result = new ArrayList<>();
    final double units_bought =
        INITIAL_INVESTMENT / pricesSortedByDate.get(days.get(initialDayIndex)).getDayPrice();

    for (int index = initialDayIndex; index < days.size(); index++) {
      PriceRange priceRange = pricesSortedByDate.get(days.get(index));

      final double currentValue = units_bought * priceRange.getDayPrice();
      final double gain = currentValue - INITIAL_INVESTMENT;

      result.add(gain * 100.0 / INITIAL_INVESTMENT);
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
    private static final String KEY_PATH = "src/main/java/org/sethia/projects/trading/config.txt";
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
      try {
        final String safeTicker = URLEncoder.encode(ticker, "UTF-8");
        final String uri = URI_STARTER + safeTicker;
        log.debug("uri for retrieving stock history: " + uri);
        final String response = WebUtils.INSTANCE.makeGETRequest(uri);
        log.debug("response with stock history: " + response);
        final PriceHistory result = mapper.readValue(response, PriceHistory.class);
        // TODO remove this setter
        result.setTicker(ticker);
        return result;
      } catch (Exception forward) {
        log.error("Error in retrieving PriceHistory for " + ticker, forward);
        throw forward;
      }
    }

    public static Map<String, PriceHistory> of(final Set<String> tickers) {
      Map<String, PriceHistory> result = new HashMap<>();

      tickers.parallelStream()
          .forEach(ticker -> {
            try {
              result.put(ticker, of(ticker));
            } catch (IOException e) {
              e.printStackTrace();
              throw new RuntimeException(e);
            }
          });

      return result;
    }
  }
}
