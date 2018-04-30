package org.sethia.features.auto_value;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;

/**
 * AutoValue tied class For customizations read: https://github.com/google/auto/blob/master/value/userguide/howto.md#nullable
 */
@AutoValue
public abstract class Money {

  public static Money create(
      String currency,
      long amount,
      String nullableField,
      ImmutableList<String> immutables) {
    return new AutoValue_Money(currency, amount, nullableField, immutables);
  }

  public abstract String getCurrency();

  public abstract long getAmount();

  @Nullable
  public abstract String getNullableField();

  public abstract ImmutableList<String> getImmutables();
}
