package astrolib.when;

import static org.junit.Assert.*;
import org.junit.Test;

import astrolib.util.Consts;

import static astrolib.when.Calendar.*;
import static astrolib.when.JulianDateConverter.*;

/** JUnit 4 tests.*/
public class JulianDateConverterTEST {

  @Test public void smallYears() {
    testSmallYears(JULIAN, JULIAN_BASE);
    testSmallYears(GREGORIAN, GREGORIAN_BASE);
  }
  
  @Test public void testLeapDayInGregorianCenturyYears() {
    //Explanatory Supplement 1961, p.437
    Calendar cal = GREGORIAN;
    double base = GREGORIAN_BASE;
    testDate(1500, 3, 1, 2268923 + 0.5 + 59, cal, base); 
    testDate(1600, 3, 1, 2305447 + 0.5 + 60, cal, base); //March 1 is after Feb 29; only this one is a leap year
    testDate(1700, 3, 1, 2341972 + 0.5 + 59, cal, base);
    testDate(1800, 3, 1, 2378496 + 0.5 + 59, cal, base);
    testDate(1900, 3, 1, 2415020 + 0.5 + 59, cal, base);
    
    cal = JULIAN;
    base = JULIAN_BASE;
    testDate(1500, 3, 1, 2268932 + 0.5 + 60, cal, base); //these are all leap years
    testDate(1600, 3, 1, 2305457 + 0.5 + 60, cal, base); 
    testDate(1700, 3, 1, 2341982 + 0.5 + 60, cal, base);
    testDate(1800, 3, 1, 2378507 + 0.5 + 60, cal, base);
    testDate(1900, 3, 1, 2415032 + 0.5 + 60, cal, base);
  }  
  
  @Test public void specificCasesJulian() {
    Calendar cal = Calendar.JULIAN;
    double base = JULIAN_BASE;
    //Explanatory Supplement tables, 1961
    testDate(-5, 1, 1.5, 1719232.0, cal, base);
    testDate(1,  1, 1.5, 2415385.0 + 1.0 - 693962.0, cal, base);
    testDate(2,  1, 1.5, 2415750.0 + 1.0 - 693962.0, cal, base);
    testDate(3,  1, 1.5, 2416115.0 + 1.0 - 693962.0, cal, base);
    testDate(4,  1, 1.5, 2416480.0 + 1.0 - 693962.0, cal, base);
    testDate(5,  1, 1.5, 2416846.0 + 1.0 - 693962.0, cal, base);
    
    //USNO https://aa.usno.navy.mil/data/JulianDate
    testDate(399, 12, 31.0, 1867156.5, cal, base);
    
    //Meeus 1991, page 61 and 62
    testDate(333, 1, 27.5, 1842713.0, cal, base);
    testDate(837, 4, 10.3, 2026871.8, cal, base);
    testDate(-1000, 7, 12.5, 1356001.0, cal, base);
    testDate(-1000, 2, 29.0, 1355866.5, cal, base);
    testDate(-1001, 8, 17.9, 1355671.4, cal, base); 
    testDate(-4712, 1, 1.5, 0.0, cal, base);
    //by extension:
    testDate(-4712, 1, 1.0, -0.5, cal, base);
    testDate(-4713, 12, 31.0, -1.5, cal, base);
    testDate(-4713, 12, 30.0, -2.5, cal, base);
    
    testDate(-98, 1, 1.5, 1684532.0 + 1.0 + LEAP_YEAR_NUM_DAYS + NORMAL_YEAR_NUM_DAYS, cal, base); 
    testDate(-99, 1, 1.5, 1684532.0 + 1.0 + LEAP_YEAR_NUM_DAYS, cal, base); 
    testDate(-100, 1, 1.5, 1684532.0 + 1.0, cal, base); 
    testDate(-101, 1, 1.5, 1684532.0 + 1.0 - NORMAL_YEAR_NUM_DAYS, cal, base);
    testDate(-102, 1, 1.5, 1684532.0 + 1.0 - 2*NORMAL_YEAR_NUM_DAYS, cal, base);
    
    testDate(-600, 1, 1.5, 1501907.0 + 1.0, cal, base); 
    testDate(-2000, 1, 1.5, 990557.0 + 1.0, cal, base);
    
    testDate(-1000, 1, 1.5, 1355807.0 + 1.0, cal, base);
    testDate(-1001, 1, 1.5, 1355807.0 + 1.0 - NORMAL_YEAR_NUM_DAYS, cal, base);
    
    testDate(100, 1, 1.5, 1757582.0 + 1.0, cal, base);
    testDate(200, 1, 1.5, 1794107.0 + 1.0, cal, base);
    testDate(300, 1, 1.5, 1830632.0 + 1.0, cal, base);
    testDate(400, 1, 1.5, 1867157.0 + 1.0, cal, base);
    testDate(500, 1, 1.5, 1903682.0 + 1.0, cal, base);
    
    testDate(1234, 5, 5.5, 2171901.0, cal, base);
    testDate(200, 1, 1.5, 1794108.0, cal, base);
    testDate(1500, 1, 1.5, 2268933.0, cal, base);
    testDate(1600, 1, 1.5, 2305458.0, cal, base);
    testDate(1800, 1, 1.5, 2378507.0 + 1.0, cal, base);
    testDate(1900, 1, 1.5, 2415032.0 + 1.0, cal, base);
  }
  
  @Test public void specificCasesGregorian() {
    Calendar cal = Calendar.GREGORIAN;
    double base = GREGORIAN_BASE;
    
    //Meeus 1991
    testDate(1957, 10, 4.81, 2436116.31, cal, base);
    
    //From Vondrak, Wallace, Capitaine 2011
    // -1374 May 3, at 13:52:19.2 TT 
    testDate(-1374, 5, 3.578, 1219339.078, cal, base);    
    
    //https://legacy-www.math.harvard.edu/computing/javascript/Calendar/index.html
    testDate(-8, 1, 1.5, 1718138.0, cal, base);
    testDate(-101, 1, 1.5, 1684171.0, cal, base);
    testDate(-799, 1, 1.5, 1429232.0, cal, base);
    testDate(-800, 1, 1.5, 1428866.0, cal, base);
    testDate(-801, 1, 1.5, 1428501.0, cal, base);
    testDate(99, 12,31.5, 1757584.0, cal, base);
    testDate(100,1,1.5, 1757584.0 + 1.0, cal, base);
    testDate(100,1,31.5, 1757584.0 + 31.0, cal, base);
    testDate(100,2,1.5, 1757584.0 + 31.0 + 1.0, cal, base);
    testDate(100,2,28.5, 1757584.0 + 31.0 + 28.0, cal, base); //100 is not a leap year
    testDate(100,3,1.5, 1757584.0 + 31.0 + 28.0 + 1.0, cal, base);
    testDate(3000, 1, 1.5, 2816788, cal, base);
    testDate(30000, 1, 1.5, 12678335, cal, base);
        
    testDate(100,1,1.5, 1757585.0, cal, base);
    testDate(101,1,1.5, 1757950.0, cal, base); 
    testDate(200,1,1.5, 1794109.0, cal, base); 
    testDate(300,1,1.5, 1830633.0, cal, base); 
    testDate(400,1,1.5, 1867157, cal, base); 
    testDate(700,1,1.5, 1976730, cal, base);  
    testDate(800,1,1.5, 2013254, cal, base);
    
    //Explanatory Supplement 1961, p 438
    testYear(1920, 2422324 - 0.5, cal, base);
    testYear(1960, 2436934 - 0.5, cal, base);
    testYear(1964, 2438395 - 0.5, cal, base);
    testYear(1980, 2444239 - 0.5, cal, base);
    testYear(1990, 2447892 - 0.5, cal, base);
  }
  
  private static final Timescale TIMESCALE = Timescale.TT;
  
  /** It's easy to compute the JD manually. */
  private void testSmallYears(Calendar calendar, double base) {
    testYear(0, base + 0*366 + 0*365, calendar, base);
    testYear(1, base + 1*366 + 0*365, calendar, base);
    testYear(2, base + 1*366 + 1*365, calendar, base);
    testYear(3, base + 1*366 + 2*365, calendar, base);
    testYear(4, base + 1*366 + 3*365, calendar, base);
    testYear(5, base + 2*366 + 3*365, calendar, base);
    testYear(6, base + 2*366 + 4*365, calendar, base);
    testYear(7, base + 2*366 + 5*365, calendar, base);
    testYear(8, base + 2*366 + 6*365, calendar, base);
    testYear(9, base + 3*366 + 6*365, calendar, base);
    testYear(10, base + 3*366 + 7*365, calendar, base);
    testYear(11, base + 3*366 + 8*365, calendar, base);
    testYear(12, base + 3*366 + 9*365, calendar, base);
  }
  
  private void testYear(int year, double jan_0_in_given_year, Calendar calendar, double base) {
    testDateToJdForYear(year, jan_0_in_given_year, calendar, base);
    testJdToDateForYear(year, jan_0_in_given_year, calendar, base);
  }
  
  private void testDate(int year, int month, double day, double jd, Calendar calendar, double base) {
    testDateToJd(year, month, day, jd, calendar, base);
    testJdToDate(year,  month,  day, jd, calendar, base);
  }

  private void testDateToJdForYear(int year, double jan_0_given_year, Calendar calendar, double base) {
    Date day = Date.from(year, 1,  1, calendar);
    for(int i = 0; i < calendar.numDaysIn(year); ++i) {
      testDateToJd(year, day.month(), day.day(), jan_0_given_year + 1 + i, calendar, base);
      day = day.next();
    }
  }

  private void testJdToDateForYear(int year, double jan_0_in_given_year, Calendar calendar, double base) {
    Date day = Date.from(year, 1, 1, calendar); //Jan 1 of the given year
    for(int i = 1; i <= calendar.numDaysIn(year); ++i) {
      testJdToDate(day.year(), day.month(), day.day(), jan_0_in_given_year + i, calendar, base);
      day = day.next();
    }
  }
  
  private void testDateToJd(int year, int month, double day, double jd_expected, Calendar calendar, double base) {
    JulianDateConverter convert = new JulianDateConverter(base, calendar);
    JulianDate jd = convert.toJulianDate(year, month, day, TIMESCALE);
    assertEquals(jd_expected, jd.jd(), Consts.EPSILON);
  }
  
  private void testJdToDate(int y_expected, int m_expected, double d_expected, double jd, Calendar calendar, double base) {
    JulianDateConverter convert = new JulianDateConverter(base, calendar);
    DateTime dt = convert.toDateTime(JulianDate.from(jd, TIMESCALE));
    assertEquals(dt.year(), y_expected);
    assertEquals(dt.month(), m_expected);
    assertEquals(dt.fractionalDay(), d_expected, Consts.EPSILON);
  }
}
