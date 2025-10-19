package astrolib.when.big;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.Test;

/** 
 Each test for a year is done with both the positive and negative value for the year. 
 Tests on the JulianDate methods are done elsewhere, on the helper class that does the calculation.
*/
public class BigCalendarTEST {

  @Test
  public void isLeapYear() {
    testIsLeapInAllCalendars(LEAP_YEARS_IN_ALL_CALENDARS);
    testIsLeapInJulianCalendar(JULIAN_LEAP_YEARS);
    testIsNotLeapInAllCalendars(NON_LEAP_YEARS_IN_ALL_CALENDARS);
    testIsNotLeapInGregorianCalendar(GREGORIAN_NON_LEAP_YEARS);
  }
  
  @Test
  public void daysFromJan0() {
    Stream.of(LEAP_YEARS_IN_ALL_CALENDARS).forEach(y -> testDaysFromJan0LeapYear(y));
    Stream.of(NON_LEAP_YEARS_IN_ALL_CALENDARS).forEach(y -> testDaysFromJan0NonLeapYear(y));
  }

  private static final Integer[] LEAP_YEARS_IN_ALL_CALENDARS = {0, 4, 8, 12, 400, 800, 1200, 1600, 2000, 1960};
  private static final Integer[] NON_LEAP_YEARS_IN_ALL_CALENDARS = {1, 2, 3, 5, 6, 7, 9, 10, 99, 1962, 2001};

  private static final Integer[] JULIAN_LEAP_YEARS = {100, 200, 300, 400, 500, 600, 700, 900, 1000, 1900, 2000};
  private static final Integer[] GREGORIAN_NON_LEAP_YEARS = {100, 200, 300, 500, 600, 700, 900, 1000, 1900, 2100};

  private void testIsLeapInAllCalendars(Integer... years) {
    for (BigCalendar calendar : BigCalendar.values()) {
      Stream.of(years).forEach(y -> testIsLeap(y, calendar));
    }
  }
  
  private void testIsNotLeapInAllCalendars(Integer... years) {
    for (BigCalendar calendar : BigCalendar.values()) {
      Stream.of(years).forEach(y -> testIsNotLeap(y, calendar));
    }
  }
  
  private void testIsNotLeapInGregorianCalendar(Integer... years) {
    Stream.of(years).forEach(y -> testIsNotLeap(y, BigCalendar.GREGORIAN));
  }

  private void testIsLeapInJulianCalendar(Integer... years) {
    Stream.of(years).forEach(y -> testIsLeap(y, BigCalendar.JULIAN));
  }

  private void testIsLeap(int year, BigCalendar calendar) {
    assertTrue(calendar.isLeap(year));
    assertTrue(calendar.isLeap(-year));
  }
  
  private void testIsNotLeap(int year, BigCalendar calendar) {
    assertFalse(calendar.isLeap(year));
    assertFalse(calendar.isLeap(-year));
  }
  
  private void testDaysFromJan0(int year, int month, double day, BigCalendar calendar, int expected) {
    assertEquals(BigDecimal.valueOf(expected), calendar.daysFromJan0(year, month, BigDecimal.valueOf(day)));
    assertEquals(BigDecimal.valueOf(expected), calendar.daysFromJan0(-year, month, BigDecimal.valueOf(day)));
  }
  
  private void testDaysFromJan0LeapYear(int leapYear) {
    for(BigCalendar calendar : BigCalendar.values()) {
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
    for(BigCalendar calendar : BigCalendar.values()) {
      assertFalse(calendar.isLeap(nonLeapYear));
      testDaysFromJan0(nonLeapYear, 1, 1, calendar, 1);
      testDaysFromJan0(nonLeapYear, 1, 2, calendar, 2);
      testDaysFromJan0(nonLeapYear, 2, 28, calendar, (31+28));
      testDaysFromJan0(nonLeapYear, 3, 1, calendar, (31+28+1));
      testDaysFromJan0(nonLeapYear, 12, 31, calendar, 365); 
    }
  }
}
