package org.sethia.utils;

public class Tuple<T1, T2> {

  private final T1 left;
  private final T2 right;

  private Tuple(T1 left, T2 right) {
    this.left = left;
    this.right = right;
  }

  public static <T1, T2> Tuple of(T1 left, T2 right) {
    return new Tuple(left, right);
  }

  public T1 getLeft() {
    return left;
  }

  public T2 getRight() {
    return right;
  }
}
