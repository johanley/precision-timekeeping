package astrolib.when;

import static org.junit.Assert.*;

import java.time.Month;
import java.util.stream.Stream;

import org.junit.Test;

/** JUnit 4 tests.*/
public class DateTEST {

  @Test public void sanity() {
    sanityTest(0,1,1);
    sanityTest(0,1,2);
    sanityTest(0,2,28);
    sanityTest(0,2,29);
    sanityTest(0,3,1);
    sanityTest(0,12,31);
    sanityTest(1,1,1);
    sanityTest(2025,10,9);
  }
  
  @Test public void ordering() {
    orderingTest(2025, 10, 2);
    orderingTest(-2025, 10, 2);
  }
  
  @Test public void unequal() {
    Date x = Date.gregorian(2025, 10, 2);
    Date y = Date.julian(2025, 10, 2);
    assertFalse(x.equals(y));
    
    x = Date.gregorian(2025, 10, 2);
    y = Date.gregorian(2025, 10, 3);
    assertFalse(x.equals(y));
  }
  
  @Test public void startOfMonth() {
    startOfMonthTest(0, 1, 2);
    startOfMonthTest(1, 1, 2);
    startOfMonthTest(1, 12, 2);
    startOfMonthTest(2025, 10, 2);
  }
  
  @Test public void lastOfMonth() {
    lastOfMonthTest(0, 1, 15);
    lastOfMonthTest(0, 2, 15);
    lastOfMonthTest(0, 3, 15);
    lastOfMonthTest(0, 12, 15);
    lastOfMonthTest(1, 1, 15);
    lastOfMonthTest(1, 2, 15);
    lastOfMonthTest(1, 3, 15);
    lastOfMonthTest(1960, 1, 15);
    lastOfMonthTest(1960, 2, 15);
    lastOfMonthTest(1960, 3, 15);
    lastOfMonthTest(1960, 10, 15);
    lastOfMonthTest(1962, 1, 15);
    lastOfMonthTest(1962, 2, 15);
    lastOfMonthTest(1962, 3, 15);
  }
  
  /** The tests for the Calendar class test this item robustly. */
  @Test public void dayOfYear() {
    for(Calendar calendar : Calendar.values()) {
      testDayOfYearLeap(1960, calendar);
      testDayOfYearNonLeap(1962, calendar);
    }
  }
  
  @Test public void plusOrMinus() {
    Stream.of(Calendar.values()).forEach(calendar -> plusMinusSanity(calendar));
    nextPrevTest();
  }
  
  @Test public void next365NonLeap() {
    next365NonLeap(2021);
    prev365NonLeap(2022);
  }
  
  private void sanityTest(int y, int m, int d) {
    for (Calendar calendar : Calendar.values()) {
      Date dt = Date.from(y, m, d, calendar);
      assertEquals(y, dt.year());
      assertEquals(m, dt.month());
      assertEquals(d, dt.day());
      assertEquals(calendar, dt.calendar());

      //neg year
      dt = Date.from(-y, m, d, calendar);
      assertEquals(-y, dt.year());
      assertEquals(m, dt.month());
      assertEquals(d, dt.day());
      assertEquals(calendar, dt.calendar());
    }
  }
  
  private void testDayOfYearNonLeap(int notLeapYear, Calendar calendar) {
    Date x = Date.from(notLeapYear, 1, 1, calendar);
    assertEquals(1, x.dayOfYear());
    for(int i=2; i<=365; ++i) {
      x = x.plusMinusDays(1);
      assertEquals(x.dayOfYear(), i);
    }
    assertEquals(365, x.dayOfYear());
    Date y = Date.from(notLeapYear, 12, 31, calendar);
    assertEquals(365, y.dayOfYear());
  }
  
  private void testDayOfYearLeap(int leapYear, Calendar calendar) {
    Date x = Date.from(leapYear, 1, 1, calendar);
    assertEquals(x.dayOfYear(), 1);
    for(int i=2; i<=(31+28); ++i) {
      x = x.plusMinusDays(1);
      assertEquals(i, x.dayOfYear());
    }
    x = x.plusMinusDays(1); //Feb 29
    assertEquals(x.dayOfYear(), 60);
    //March 1 .. December 31
    for(int i=61; i<=366; ++i) {
      x = x.plusMinusDays(1);
      assertEquals(i, x.dayOfYear());
    }
  }
  
  private void plusMinusSanity(Calendar calendar) {
    //near a leap day, and across month-end
    Date x = Date.from(1960, 2, 27, calendar);
    Date y = x.plusMinusDays(1);
    assertEquals(28, y.day());
    assertEquals(2, y.month());
    assertEquals(1960, y.year());
    
    y = x.plusMinusDays(2);
    assertEquals(29, y.day());
    assertEquals(2, y.month());
    assertEquals(1960, y.year());

    y = x.plusMinusDays(-1);
    assertEquals(26, y.day());
    assertEquals(2, y.month());
    assertEquals(1960, y.year());

    y = x.plusMinusDays(-2);
    assertEquals(25, y.day());
    assertEquals(2, y.month());
    assertEquals(1960, y.year());
    
    x = Date.from(1960, 3, 1, calendar);
    y = x.plusMinusDays(-1);
    assertEquals(29, y.day());
    assertEquals(2, y.month());
    assertEquals(1960, y.year());
    
    //across a year-end
    x = Date.from(1960, 12, 31, calendar);
    y = x.plusMinusDays(1);
    assertEquals(1, y.day());
    assertEquals(1, y.month());
    assertEquals(1961, y.year());
    
    x = Date.from(1961, 1, 1, calendar);
    y = x.plusMinusDays(-1);
    assertEquals(31, y.day());
    assertEquals(12, y.month());
    assertEquals(1960, y.year());
  }
  
  private void nextPrevTest() {
    for(Calendar calendar : Calendar.values()) {
      nextPrevSanityTest(1, 1, 15, calendar);
      nextPrevSanityTest(-1, 1, 15, calendar);
      nextPrevSanityTest(2025, 10, 15, calendar);
      nextPrevSanityTest(-2025, 10, 15, calendar);
      
      nextPrevFeb28Test(0, calendar);
      nextPrevFeb28Test(1, calendar);
      nextPrevFeb28Test(-1, calendar);
      nextPrevFeb28Test(1960, calendar);
      nextPrevFeb28Test(1962, calendar);
      
      nextPrevMonthEndTest(0, 11, calendar);
      nextPrevMonthEndTest(1, 11, calendar);
      nextPrevMonthEndTest(-1, 11, calendar);
      nextPrevMonthEndTest(1960, 11, calendar);
      nextPrevMonthEndTest(1962, 11, calendar);

      nextPrevYearEndTest(0, calendar);
      nextPrevYearEndTest(1, calendar);
      nextPrevYearEndTest(-1, calendar);
      nextPrevMonthEndTest(1960, 11, calendar);
      nextPrevMonthEndTest(-1960, 11, calendar);
      nextPrevMonthEndTest(1962, 11, calendar);
      nextPrevMonthEndTest(-1962, 11, calendar);
    }
  }
  
  /** d is mid-month-ish. */
  private void nextPrevSanityTest(int y, int m, int d, Calendar calendar) {
    Date a = Date.from(y, m, d, calendar);
    Date aPlus1 = Date.from(y, m, d+1, calendar); 
    Date aMinus1 = Date.from(y, m, d-1, calendar); 
    assertTrue(a.next().eq(aPlus1));
    assertTrue(a.previous().eq(aMinus1));
  }
  
  /** The date is Feb 28.  */
  private void nextPrevFeb28Test(int y, Calendar calendar) {
    boolean isLeap = calendar.isLeap(y);
    Date a = Date.from(y, 2, 28, calendar);
    Date aPlus1 = isLeap ? Date.from(y, 2, 29, calendar) : Date.from(y, 3, 1, calendar); 
    Date aMinus1 = Date.from(y, 2, 27, calendar); 
    assertTrue(a.next().eq(aPlus1));
    assertTrue(a.previous().eq(aMinus1));
  }

  /** The date is the end of the month. The month is not February, and not December. */
  private void nextPrevMonthEndTest(int y, int m, Calendar calendar) {
    assertFalse(m == 2 || m == 12);
    boolean isLeap = calendar.isLeap(y);
    int lastDay = Month.of(m).length(isLeap);
    Date a = Date.from(y, m, lastDay, calendar);
    Date aPlus1 = Date.from(y, m + 1, 1, calendar); 
    Date aMinus1 = Date.from(y, m, lastDay - 1, calendar); 
    assertTrue(a.next().eq(aPlus1));
    assertTrue(a.previous().eq(aMinus1));
  }

  /** The month is December. */
  private void nextPrevYearEndTest(int y, Calendar calendar) {
    Date a = Date.from(y, 12, 31, calendar);
    Date aPlus1 = Date.from(y + 1, 1, 1, calendar); 
    Date aMinus1 = Date.from(y, 12, 30, calendar); 
    assertTrue(a.next().eq(aPlus1));
    assertTrue(a.previous().eq(aMinus1));
  }

  /** Two consecutive non-leap years in all calendars. */
  private void next365NonLeap(int y) {
    for(Calendar calendar : Calendar.values()) {
      Date jan1 = Date.from(y, 1, 1, calendar);
      Date expected = Date.from(y + 1, 1, 1, calendar);
      Date result = jan1.plusMinusDays(365);
      assertEquals(expected, result);
    }
  }
  
  /** Two consecutive non-leap years in all calendars. */
  private void prev365NonLeap(int y) {
    for(Calendar calendar : Calendar.values()) {
      Date jan1 = Date.from(y, 1, 1, calendar);
      Date expected = Date.from(y - 1, 1, 1, calendar);
      Date result = jan1.plusMinusDays(-365);
      assertEquals(expected, result);
    }
  }
  
  private void orderingTest(int y, int m, int d) {
    for(Calendar calendar: Calendar.values()) {
      Date p = Date.from(y, m, d, calendar);
      
      Date q = p.next();
      assertTrue(p.lt(q));
      assertTrue(q.gt(p));
      
      Date o = p.previous();
      assertTrue(p.gt(o));
      assertTrue(o.lt(p));
      
      assertTrue(p.lteq(q));
      assertTrue(q.gteq(p));
      
      Date q2 = Date.from(q.year(), q.month(), q.day(), q.calendar());
      assertTrue(q.equals(q2));
      assertTrue(q2.equals(q));
    }
  }

  private void startOfMonthTest(int y, int m, int d) {
    for(Calendar calendar : Calendar.values()) {
      Date a = Date.from(y, m, d, calendar);
      Date b = Date.from(y, m, 1, calendar);
      assertTrue(b.eq(a.startOfMonth()));
      
      //neg year
      a = Date.from(-y, m, d, calendar);
      b = Date.from(-y, m, 1, calendar);
      assertTrue(b.eq(a.startOfMonth()));
    }
  }
  
  private void lastOfMonthTest(int y, int m, int d) {
    for(Calendar calendar : Calendar.values()) {
      Date a = Date.from(y, m, d, calendar);
      int endOfMonth = Month.of(m).length(calendar.isLeap(y));
      Date b = Date.from(y, m, endOfMonth, calendar);
      assertTrue(b.eq(a.endOfMonth()));
      
      //neg year
      a = Date.from(-y, m, d, calendar);
      endOfMonth = Month.of(m).length(calendar.isLeap(-y));
      b = Date.from(-y, m, endOfMonth, calendar);
      assertTrue(b.eq(a.endOfMonth()));
    }
  }
}
