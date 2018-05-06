package org.sethia.utils;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Tuple<T1, T2> {

  public static <T1, T2> Tuple of(T1 left, T2 right) {
    return new AutoValue_Tuple(left, right);
  }

  public abstract T1 getLeft();

  public abstract T2 getRight();
}
