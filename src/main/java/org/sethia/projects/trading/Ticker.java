package org.sethia.projects.trading;

public enum Ticker {
  SP500("^GSPC"),
  NASDAQ("^IXIC"),
  DOW30("^DJI");

  private final String name;

  Ticker(String name){
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
