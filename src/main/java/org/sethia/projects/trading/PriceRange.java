package org.sethia.projects.trading;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * sample input string: { "1. open": "2680.8000", "2. high": "2683.5500", "3. low": "2617.3201", "4.
 * close": "2634.5601", "5. volume": "3706740000" }
 */
@AutoValue
public abstract class PriceRange {

  @JsonCreator
  static PriceRange create(
      @JsonProperty("1. open") Double open,
      @JsonProperty("2. high") Double high,
      @JsonProperty("3. low") Double low,
      @JsonProperty("4. close") Double close,
      @JsonProperty("5. volume") Long volume) {
    return new AutoValue_PriceRange(open, high, low, close, volume);
  }

  @JsonProperty("1. open")
  public abstract Double getOpen();

  @JsonProperty("2. high")
  public abstract Double getHigh();

  @JsonProperty("3. low")
  public abstract Double getLow();

  @JsonProperty("4. close")
  public abstract Double getClose();

  @JsonProperty("5. volume")
  public abstract Long getVolume();

  public final Double getDayPrice() {
    //return (getHigh() + getLow()) / 2;
    return getClose();
  }
}
