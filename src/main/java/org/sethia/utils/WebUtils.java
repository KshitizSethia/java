package org.sethia.utils;


import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

public enum WebUtils {

  INSTANCE;

  private static final org.slf4j.Logger log = LoggerFactory.getLogger(WebUtils.class);
  private static final CloseableHttpClient CLIENT = HttpClients.createDefault();

  public String makeGETRequest(String url) throws IOException {
    HttpGet request = new HttpGet(url);
    try (CloseableHttpResponse response = CLIENT.execute(request)) {
      final int responseCode = response.getStatusLine().getStatusCode();
      final String responseBody = EntityUtils.toString(response.getEntity());
      if (responseCode / 100 != 2) {
        log.error("got error while GETting url: {}%n response code: {}%n response body: {}",
            url, responseCode, responseBody);

      }
      return responseBody;
    }
  }
}
