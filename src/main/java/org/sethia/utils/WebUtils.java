package org.sethia.utils;


import java.io.IOException;
import java.util.Optional;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

public enum WebUtils {

  // normal web utils
  INSTANCE(0),
  // util which caches responses to web requests for a duration of 6 hours
  INSTANCE_WITH_CACHING(6 * 60 * 60);

  private static final org.slf4j.Logger log = LoggerFactory.getLogger(WebUtils.class);
  private static final CloseableHttpClient CLIENT = HttpClients.createDefault();

  WebUtils(final long secondsToRememberFor) {

  }

  public String makeGETRequest(String url) throws IOException {
    //try returning a cached instance
    Optional<String> cachedResult = DiskCache.EXPIRING_6_HOURS.get(url);

    if (cachedResult.isPresent()) {
      log.debug("found item in cache");
      return cachedResult.get();
    }

    log.debug("cache miss, downloading from web");
    // go back to making a web call
    HttpGet request = new HttpGet(url);
    try (CloseableHttpResponse response = CLIENT.execute(request)) {
      final int responseCode = response.getStatusLine().getStatusCode();
      final String responseBody = EntityUtils.toString(response.getEntity());
      if (responseCode / 100 != 2) {
        log.error("got error while GETting url: {}{} response code: {}{} response body: {}",
            url, System.lineSeparator(), responseCode, System.lineSeparator(), responseBody);

      }

      // store created instance in cache
      DiskCache.EXPIRING_6_HOURS.put(url, responseBody);
      return responseBody;
    }
  }
}
