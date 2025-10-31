package astrolib.when;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.Test;

/** 
 Each test for a year is done with both the positive and negative value for the year. 
 Tests on the JulianDate methods are done elsewhere, on the helper class that does the calculation.
*/
public class CalendarTEST {

  @Test public void isLeapYear() {
    testIsLeapInAllCalendars(LEAP_YEARS_IN_ALL_CALENDARS);
    testIsLeapInJulianCalendar(JULIAN_LEAP_YEARS);
    testIsNotLeapInAllCalendars(NON_LEAP_YEARS_IN_ALL_CALENDARS);
    testIsNotLeapInGregorianCalendar(GREGORIAN_NON_LEAP_YEARS);
  }
  
  @Test public void daysFromJan0() {
    Stream.of(LEAP_YEARS_IN_ALL_CALENDARS).forEach(y -> testDaysFromJan0LeapYear(y));
    Stream.of(NON_LEAP_YEARS_IN_ALL_CALENDARS).forEach(y -> testDaysFromJan0NonLeapYear(y));
  }

  @Test public void daysFromDec32() {
    Stream.of(LEAP_YEARS_IN_ALL_CALENDARS).forEach(y -> testDaysFromDec32LeapYear(y));
    Stream.of(NON_LEAP_YEARS_IN_ALL_CALENDARS).forEach(y -> testDaysFromDec32NonLeapYear(y));
  }
  
  @Test public void cycleSize() {
    assertEquals(4, Calendar.JULIAN.fullCycleYears());
    assertEquals(1461, Calendar.JULIAN.fullCycleDays());
    assertEquals(400, Calendar.GREGORIAN.fullCycleYears());
  }

  private static final Integer[] LEAP_YEARS_IN_ALL_CALENDARS = {0, 4, 8, 12, 400, 800, 1200, 1600, 2000, 1960};
  private static final Integer[] NON_LEAP_YEARS_IN_ALL_CALENDARS = {1, 2, 3, 5, 6, 7, 9, 10, 99, 1962, 2001};

  private static final Integer[] JULIAN_LEAP_YEARS = {100, 200, 300, 400, 500, 600, 700, 900, 1000, 1900, 2000};
  private static final Integer[] GREGORIAN_NON_LEAP_YEARS = {100, 200, 300, 500, 600, 700, 900, 1000, 1900, 2100};

  private void testIsLeapInAllCalendars(Integer... years) {
    for (Calendar calendar : Calendar.values()) {
      Stream.of(years).forEach(y -> testIsLeap(y, calendar));
    }
  }
  
  private void testIsNotLeapInAllCalendars(Integer... years) {
    for (Calendar calendar : Calendar.values()) {
      Stream.of(years).forEach(y -> testIsNotLeap(y, calendar));
    }
  }
  
  private void testIsNotLeapInGregorianCalendar(Integer... years) {
    Stream.of(years).forEach(y -> testIsNotLeap(y, Calendar.GREGORIAN));
  }

  private void testIsLeapInJulianCalendar(Integer... years) {
    Stream.of(years).forEach(y -> testIsLeap(y, Calendar.JULIAN));
  }

  private void testIsLeap(int year, Calendar calendar) {
    assertTrue(calendar.isLeap(year));
    assertTrue(calendar.isLeap(-year));
  }
  
  private void testIsNotLeap(int year, Calendar calendar) {
    assertFalse(calendar.isLeap(year));
    assertFalse(calendar.isLeap(-year));
  }
  
  private void testDaysFromJan0(int year, int month, double day, Calendar calendar, int expected) {
    assertEquals(expected, calendar.daysFromJan0(year, month, BigDecimal.valueOf(day)).intValue());
    assertEquals(expected, calendar.daysFromJan0(-year, month, BigDecimal.valueOf(day)).intValue());
  }
  private void testDaysFromJan0LeapYear(int leapYear) {
    for(Calendar calendar : Calendar.values()) {
      assertTrue(calendar.isLeap(leapYear));
      testDaysFromJan0(leapYear, 1, 1, calendar, 1);
      testDaysFromJan0(leapYear, 1, 2, calendar, 2);
      testDaysFromJan0(leapYear, 2, 28, calendar, (31+28));
      testDaysFromJan0(leapYear, 2, 29, calendar, (31+29));
      testDaysFromJan0(leapYear, 3, 1, calendar, (31+29+1));
      testDaysFromJan0(leapYear, 12, 31, calendar, 366); 
    }
  }
  private void testDaysFromJan0NonLeapYear(int nonLeapYear) {
    for(Calendar calendar : Calendar.values()) {
      assertFalse(calendar.isLeap(nonLeapYear));
      testDaysFromJan0(nonLeapYear, 1, 1, calendar, 1);
      testDaysFromJan0(nonLeapYear, 1, 2, calendar, 2);
      testDaysFromJan0(nonLeapYear, 2, 28, calendar, (31+28));
      testDaysFromJan0(nonLeapYear, 3, 1, calendar, (31+28+1));
      testDaysFromJan0(nonLeapYear, 12, 31, calendar, 365); 
    }
  }
  
  private void testDaysFromDec32NonLeapYear(int nonLeapYear) {
    for(Calendar calendar : Calendar.values()) {
      assertFalse(calendar.isLeap(nonLeapYear));
      testDaysFromDec32(nonLeapYear, 1, 1, calendar, 366-1);
      testDaysFromDec32(nonLeapYear, 1, 2, calendar, 366-2);
      testDaysFromDec32(nonLeapYear, 2, 28, calendar, 366-(31+28));
      testDaysFromDec32(nonLeapYear, 3, 1, calendar, 366-(31+28+1));
      testDaysFromDec32(nonLeapYear, 12, 31, calendar, 366-365); 
    }
  }
  private void testDaysFromDec32LeapYear(int leapYear) {
    for(Calendar calendar : Calendar.values()) {
      assertTrue(calendar.isLeap(leapYear));
      testDaysFromDec32(leapYear, 1, 1, calendar, (31+30+31+30+31+31+30+31+30+31+29+31));
      testDaysFromDec32(leapYear, 2, 1, calendar, (31+30+31+30+31+31+30+31+30+31+29));
      testDaysFromDec32(leapYear, 2, 29, calendar, (31+30+31+30+31+31+30+31+30+31+1));
      testDaysFromDec32(leapYear, 3, 1, calendar, (31+30+31+30+31+31+30+31+30+31));
      testDaysFromDec32(leapYear, 4, 1, calendar, (31+30+31+30+31+31+30+31+30));
      testDaysFromDec32(leapYear, 5, 1, calendar, (31+30+31+30+31+31+30+31));
      testDaysFromDec32(leapYear, 6, 1, calendar, (31+30+31+30+31+31+30));
      testDaysFromDec32(leapYear, 7, 1, calendar, (31+30+31+30+31+31));
      testDaysFromDec32(leapYear, 8, 1, calendar, (31+30+31+30+31));
      testDaysFromDec32(leapYear, 9, 1, calendar, (31+30+31+30));
      testDaysFromDec32(leapYear, 10, 1, calendar, (31+30+31));
      testDaysFromDec32(leapYear, 11, 1, calendar, (31+30));
      testDaysFromDec32(leapYear, 12, 31, calendar, 1); 
    }
  }
  private void testDaysFromDec32(int year, int month, double day, Calendar calendar, int expected) {
    assertEquals(expected, calendar.daysFromDec32(year, month, BigDecimal.valueOf(day)).intValue());
    assertEquals(expected, calendar.daysFromDec32(-year, month, BigDecimal.valueOf(day)).intValue());
  }
}
