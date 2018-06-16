package org.sethia.projects.trading;

import static org.sethia.test.TestUtils.arrayOf;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;
import org.sethia.projects.trading.Portfolio.Builder;
import org.sethia.utils.DiskCache;
import org.sethia.utils.TimeUtils;
import org.sethia.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * reference: https://github.com/ajermakovics/eclipse-jmockit/wiki/JMockit-Quick-Tutorial-(Cheat-sheet)
 */
public class PortfolioTest {

  private static final Logger log = LoggerFactory.getLogger(PortfolioTest.class);

  private void assertAppreciations(List<Tuple<Date, Double>> actualAppreciationsSinceStart,
      Double[] expectedAppreciations, String[] expectedDates) throws ParseException {

    log.debug("actual appreciations: {}", actualAppreciationsSinceStart);
    log.debug("expected: {}, {}", expectedAppreciations, expectedDates);

    for (int index = 0; index < actualAppreciationsSinceStart.size(); index++) {
      String failureMessage = "failed at index " + index;
      log.debug("checking index {}", index);
      Assert.assertEquals(failureMessage,
          expectedAppreciations[index],
          actualAppreciationsSinceStart.get(index).getRight());

      Assert.assertEquals(failureMessage,
          TimeUtils.PDT.fromShortString(expectedDates[index]),
          actualAppreciationsSinceStart.get(index).getLeft());
    }
  }

  @Test
  public void test_getAppreciationSinceStart_singleStock_2tradingDays(
      @Mocked CloseableHttpClient httpClient,
      @Mocked CloseableHttpResponse httpResponseForStock,
      @Mocked StatusLine statusLine) throws IOException, ParseException {

    new MockUp<TimeUtils>() {
      @Mock
      public Date getNow() {
        try {
          return TimeUtils.PDT.fromShortString("20180430");
        } catch (ParseException e) {
          Assert.fail("this can not happen");
          return null;
        }
      }
    };

    new MockUp<Portfolio.Builder>() {
      @Mock
      public Reader openFile(String path) {
        return new StringReader(
            "Symbol,Current Price,Date,Time,Change,Open,High,Low,Volume,Trade Date,Purchase Price,Quantity,Commission,High Limit,Low Limit,Comment\n"
                + "singleStock_2tradingDays,100.00,2018/04/26,16:01 EDT,0.22999573,196.63,197.96,196.24,2323187,20180427,100.0,1.0,,,,");
      }
    };

    // disable caching to disk
    new MockUp<DiskCache>() {
      @Mock
      public Optional<String> get(String key) {
        return Optional.empty();
      }

      @Mock
      public void put(String key, String value) {

      }
    };

    new Expectations() {{
      httpClient.execute((HttpGet) any);
      result = httpResponseForStock;

      httpResponseForStock.getStatusLine();
      result = statusLine;

      statusLine.getStatusCode();
      result = 200;

      httpResponseForStock.getEntity();
      result = new StringEntity("{\n"
          + "    \"Meta Data\": {\n"
          + "        \"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\",\n"
          + "        \"2. Symbol\": \"singleStock_2tradingDays\",\n"
          + "        \"3. Last Refreshed\": \"2018-04-30\",\n"
          + "        \"4. Output Size\": \"Compact\",\n"
          + "        \"5. Time Zone\": \"US/Eastern\"\n"
          + "    },\n"
          + "    \"Time Series (Daily)\": {\n"
          + "        \"2018-04-30\": {\n"
          + "            \"1. open\": \"110.0\",\n"
          + "            \"2. high\": \"110.0\",\n"
          + "            \"3. low\": \"110.0\",\n"
          + "            \"4. close\": \"110.0\",\n"
          + "            \"5. volume\": \"41092435\"\n"
          + "        },\n"
          + "        \"2018-04-27\": {\n"
          + "            \"1. open\": \"100.0\",\n"
          + "            \"2. high\": \"100.0\",\n"
          + "            \"3. low\": \"100.0\",\n"
          + "            \"4. close\": \"100.0\",\n"
          + "            \"5. volume\": \"48272780\"\n"
          + "        }\n"
          + "    }\n"
          + "}");

      httpResponseForStock.close();
    }};

    final Portfolio test_portfolio = Builder
        .fromYahooFinanceCSV("", "test_portfolio");

    final List<Tuple<Date, Double>> appreciationSinceStart = test_portfolio
        .getAppreciationSinceStart();

    assertAppreciations(appreciationSinceStart,
        arrayOf(0.0, 10.0),
        arrayOf("20180427", "20180430"));
  }

  @Test
  public void test_getAppreciationSinceStart_2stocks_3tradingDays(
      @Mocked CloseableHttpClient httpClient,
      @Mocked CloseableHttpResponse httpResponseForStock,
      @Mocked StatusLine statusLine) throws IOException, ParseException {

    new MockUp<TimeUtils>() {
      @Mock
      public Date getNow() {
        try {
          return TimeUtils.PDT.fromShortString("20180430");
        } catch (ParseException e) {
          Assert.fail("this can not happen");
          return null;
        }
      }
    };

    new MockUp<Portfolio.Builder>() {
      @Mock
      public Reader openFile(String path) {
        return new StringReader(
            "Symbol,Current Price,Date,Time,Change,Open,High,Low,Volume,Trade Date,Purchase Price,Quantity,Commission,High Limit,Low Limit,Comment\n"
                + "2stocks_3tradingDays_1,100.00,2018/04/26,16:01 EDT,0.22999573,196.63,197.96,196.24,2323187,20180426,100.0,1.0,,,,\n"
                + "2stocks_3tradingDays_2,200.00,2018/04/26,16:01 EDT,0.22999573,196.63,197.96,196.24,2323187,20180427,200.0,1.0,,,,\n");
      }
    };

    // disable caching to disk
    new MockUp<DiskCache>() {
      @Mock
      public Optional<String> get(String key) {
        return Optional.empty();
      }

      @Mock
      public void put(String key, String value) {

      }
    };

    new Expectations() {{
      httpClient.execute((HttpGet) any);
      result = httpResponseForStock;

      httpResponseForStock.getStatusLine();
      result = statusLine;

      statusLine.getStatusCode();
      result = 200;

      httpResponseForStock.getEntity();
      returns(
          // stock1
          new StringEntity("{\n"
              + "    \"Meta Data\": {\n"
              + "        \"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\",\n"
              + "        \"2. Symbol\": \"2stocks_3tradingDays_1\",\n"
              + "        \"3. Last Refreshed\": \"2018-04-30\",\n"
              + "        \"4. Output Size\": \"Compact\",\n"
              + "        \"5. Time Zone\": \"US/Eastern\"\n"
              + "    },\n"
              + "    \"Time Series (Daily)\": {\n"
              + "        \"2018-04-30\": {\n"
              + "            \"1. open\": \"120.0\",\n"
              + "            \"2. high\": \"120.0\",\n"
              + "            \"3. low\": \"120.0\",\n"
              + "            \"4. close\": \"120.0\",\n"
              + "            \"5. volume\": \"41092435\"\n"
              + "        },\n"
              + "        \"2018-04-27\": {\n"
              + "            \"1. open\": \"110.0\",\n"
              + "            \"2. high\": \"110.0\",\n"
              + "            \"3. low\": \"110.0\",\n"
              + "            \"4. close\": \"110.0\",\n"
              + "            \"5. volume\": \"48272780\"\n"
              + "        },\n"
              + "        \"2018-04-26\": {\n"
              + "            \"1. open\": \"100.0\",\n"
              + "            \"2. high\": \"100.0\",\n"
              + "            \"3. low\": \"100.0\",\n"
              + "            \"4. close\": \"100.0\",\n"
              + "            \"5. volume\": \"48272780\"\n"
              + "        }\n"
              + "    }\n"
              + "}"),
          // stock2
          new StringEntity("{\n"
              + "    \"Meta Data\": {\n"
              + "        \"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\",\n"
              + "        \"2. Symbol\": \"2stocks_3tradingDays_2\",\n"
              + "        \"3. Last Refreshed\": \"2018-04-30\",\n"
              + "        \"4. Output Size\": \"Compact\",\n"
              + "        \"5. Time Zone\": \"US/Eastern\"\n"
              + "    },\n"
              + "    \"Time Series (Daily)\": {\n"
              + "        \"2018-04-30\": {\n"
              + "            \"1. open\": \"220.0\",\n"
              + "            \"2. high\": \"220.0\",\n"
              + "            \"3. low\": \"220.0\",\n"
              + "            \"4. close\": \"220.0\",\n"
              + "            \"5. volume\": \"41092435\"\n"
              + "        },\n"
              + "        \"2018-04-27\": {\n"
              + "            \"1. open\": \"210.0\",\n"
              + "            \"2. high\": \"210.0\",\n"
              + "            \"3. low\": \"210.0\",\n"
              + "            \"4. close\": \"210.0\",\n"
              + "            \"5. volume\": \"48272780\"\n"
              + "        },\n"
              + "        \"2018-04-26\": {\n"
              + "            \"1. open\": \"200.0\",\n"
              + "            \"2. high\": \"200.0\",\n"
              + "            \"3. low\": \"200.0\",\n"
              + "            \"4. close\": \"200.0\",\n"
              + "            \"5. volume\": \"48272780\"\n"
              + "        }\n"
              + "    }\n"
              + "}"));

      httpResponseForStock.close();
    }};

    final Portfolio test_portfolio = Builder
        .fromYahooFinanceCSV("", "test_portfolio");

    final List<Tuple<Date, Double>> appreciationSinceStart = test_portfolio
        .getAppreciationSinceStart();

    assertAppreciations(appreciationSinceStart, arrayOf(0.0, 20.0 / 3.0, 40.0 / 3.0),
        arrayOf("20180426", "20180427", "20180430"));
  }

  @Test
  public void test_getAppreciationSinceStart_2lotsOfSameStock_3_tradingDays(
      @Mocked CloseableHttpClient httpClient,
      @Mocked CloseableHttpResponse httpResponseForStock,
      @Mocked StatusLine statusLine) throws IOException, ParseException {

    new MockUp<TimeUtils>() {
      @Mock
      public Date getNow() {
        try {
          return TimeUtils.PDT.fromShortString("20180430");
        } catch (ParseException e) {
          Assert.fail("this can not happen");
          return null;
        }
      }
    };

    new MockUp<Portfolio.Builder>() {
      @Mock
      public Reader openFile(String path) {
        return new StringReader(
            "Symbol,Current Price,Date,Time,Change,Open,High,Low,Volume,Trade Date,Purchase Price,Quantity,Commission,High Limit,Low Limit,Comment\n"
                + "2lotsOfSameStock_3_tradingDays,100.00,2018/04/26,16:01 EDT,0.22999573,196.63,197.96,196.24,2323187,20180426,100.0,1.0,,,,\n"
                + "2lotsOfSameStock_3_tradingDays,110.00,2018/04/26,16:01 EDT,0.22999573,196.63,197.96,196.24,2323187,20180427,110.0,2.0,,,,\n");
      }
    };

    // disable caching to disk
    new MockUp<DiskCache>() {
      @Mock
      public Optional<String> get(String key) {
        return Optional.empty();
      }

      @Mock
      public void put(String key, String value) {

      }
    };

    new Expectations() {{
      httpClient.execute((HttpGet) any);
      result = httpResponseForStock;

      httpResponseForStock.getStatusLine();
      result = statusLine;

      statusLine.getStatusCode();
      result = 200;

      httpResponseForStock.getEntity();
      result =
          // stock1
          new StringEntity("{\n"
              + "    \"Meta Data\": {\n"
              + "        \"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\",\n"
              + "        \"2. Symbol\": \"2lotsOfSameStock_3_tradingDays\",\n"
              + "        \"3. Last Refreshed\": \"2018-04-30\",\n"
              + "        \"4. Output Size\": \"Compact\",\n"
              + "        \"5. Time Zone\": \"US/Eastern\"\n"
              + "    },\n"
              + "    \"Time Series (Daily)\": {\n"
              + "        \"2018-04-30\": {\n"
              + "            \"1. open\": \"120.0\",\n"
              + "            \"2. high\": \"120.0\",\n"
              + "            \"3. low\": \"120.0\",\n"
              + "            \"4. close\": \"120.0\",\n"
              + "            \"5. volume\": \"41092435\"\n"
              + "        },\n"
              + "        \"2018-04-27\": {\n"
              + "            \"1. open\": \"110.0\",\n"
              + "            \"2. high\": \"110.0\",\n"
              + "            \"3. low\": \"110.0\",\n"
              + "            \"4. close\": \"110.0\",\n"
              + "            \"5. volume\": \"48272780\"\n"
              + "        },\n"
              + "        \"2018-04-26\": {\n"
              + "            \"1. open\": \"100.0\",\n"
              + "            \"2. high\": \"100.0\",\n"
              + "            \"3. low\": \"100.0\",\n"
              + "            \"4. close\": \"100.0\",\n"
              + "            \"5. volume\": \"48272780\"\n"
              + "        }\n"
              + "    }\n"
              + "}");

      httpResponseForStock.close();
    }};

    final Portfolio test_portfolio = Builder
        .fromYahooFinanceCSV("", "test_portfolio");

    final List<Tuple<Date, Double>> appreciationSinceStart = test_portfolio
        .getAppreciationSinceStart();

    assertAppreciations(appreciationSinceStart, arrayOf(0.0, 10.0 / 3.2, 40.0 / 3.2),
        arrayOf("20180426", "20180427", "20180430"));
  }
}