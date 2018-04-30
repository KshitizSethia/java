package org.sethia.projects.trading;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Objects;
import org.junit.Test;

public class StockLotTest {

  @Test
  public void test_create8Format() {
    StockLot lot = StockLot.create("T1", "20180426", 1.0, 200.5);
    assertEquals(Double.valueOf(200.5), lot.getCostBasis());
    assertEquals(Double.valueOf(1.0), lot.getNumUnits());
    assertEquals("T1", lot.getTicker());
    assertEquals(2018, lot.getBuyYear());
    assertEquals(Calendar.APRIL, lot.getBuyMonth());
    assertEquals(26, lot.getBuyDayOfMonth());
  }

  @Test
  public void test_create6Format() {
    StockLot lot = StockLot.create("T1", "180426", 1.0, 200.5);
    assertEquals(2018, lot.getBuyYear());
    assertEquals(Calendar.APRIL, lot.getBuyMonth());
    assertEquals(26, lot.getBuyDayOfMonth());
  }

  @Test
  public void test_create10Format() {
    StockLot lot = StockLot.create("T1", "2018/04/26", 1.0, 200.5);
    assertEquals(2018, lot.getBuyYear());
    assertEquals(Calendar.APRIL, lot.getBuyMonth());
    assertEquals(26, lot.getBuyDayOfMonth());
  }

  @Test
  public void test_comparison(){
    StockLot smallerLot = StockLot.create("T1", "20180425", 1.0, 100.0);
    StockLot sameAsSmallerLot = StockLot.create("T1", "20180425", 1.0, 100.0);
    StockLot biggerLot = StockLot.create("T1", "20180426", 1.0, 100.0);

    assertTrue(smallerLot.compareTo(biggerLot) < 0);
    assertTrue(biggerLot.compareTo(smallerLot) > 0);
    assertTrue(smallerLot.compareTo(sameAsSmallerLot) == 0 && Objects.equals(smallerLot, sameAsSmallerLot));
  }
}