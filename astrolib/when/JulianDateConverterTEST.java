package astrolib.when;

import static astrolib.when.Calendar.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

/** Unit tests.*/
public final class JulianDateConverterTEST {

  @Test public void smallYears() {
    testSmallYears(JULIAN);
    testSmallYears(GREGORIAN);
  }
  
  @Test public void testLeapDayInGregorianCenturyYears() {
    //Explanatory Supplement 1961, p.437
    Calendar cal = GREGORIAN;
    testDate(1500, 3, 1, 2268923 + 0.5 + 59, cal); 
    testDate(1600, 3, 1, 2305447 + 0.5 + 60, cal); //March 1 is after Feb 29; only this one is a leap year
    testDate(1700, 3, 1, 2341972 + 0.5 + 59, cal);
    testDate(1800, 3, 1, 2378496 + 0.5 + 59, cal);
    testDate(1900, 3, 1, 2415020 + 0.5 + 59, cal);
    
    cal = JULIAN;
    testDate(1500, 3, 1, 2268932 + 0.5 + 60, cal); //these are all leap years
    testDate(1600, 3, 1, 2305457 + 0.5 + 60, cal); 
    testDate(1700, 3, 1, 2341982 + 0.5 + 60, cal);
    testDate(1800, 3, 1, 2378507 + 0.5 + 60, cal);
    testDate(1900, 3, 1, 2415032 + 0.5 + 60, cal);
  }  
  
  @Test public void specificCasesJulian() {
    Calendar cal = Calendar.JULIAN;
    //Explanatory Supplement tables, 1961
    testDate(-5, 1, 1.5, 1719232.0, cal);
    testDate(1,  1, 1.5, 2415385.0 + 1.0 - 693962.0, cal);
    testDate(2,  1, 1.5, 2415750.0 + 1.0 - 693962.0, cal);
    testDate(3,  1, 1.5, 2416115.0 + 1.0 - 693962.0, cal);
    testDate(4,  1, 1.5, 2416480.0 + 1.0 - 693962.0, cal);
    testDate(5,  1, 1.5, 2416846.0 + 1.0 - 693962.0, cal);
    
    //USNO https://aa.usno.navy.mil/data/JulianDate
    testDate(399, 12, 31.0, 1867156.5, cal);
    
    //Meeus 1991, page 61 and 62
    testDate(333, 1, 27.5, 1842713.0, cal);
    testDate(837, 4, 10.3, 2026871.8, cal);
    testDate(-1000, 7, 12.5, 1356001.0, cal);
    testDate(-1000, 2, 29.0, 1355866.5, cal);
    testDate(-1001, 8, 17.9, 1355671.4, cal); 
    testDate(-4712, 1, 1.5, 0.0, cal);
    //by extension:
    testDate(-4712, 1, 1.0, -0.5, cal); //1461 * -1178, exact cycle
    testDate(-4713, 12, 31.0, -1.5, cal);
    testDate(-4713, 12, 30.0, -2.5, cal);
    
    testDate(-98, 1, 1.5, 1684532.0 + 1.0 + LONG_YEAR + SHORT_YEAR, cal); 
    testDate(-99, 1, 1.5, 1684532.0 + 1.0 + LONG_YEAR, cal); 
    testDate(-100, 1, 1.5, 1684532.0 + 1.0, cal); 
    testDate(-101, 1, 1.5, 1684532.0 + 1.0 - SHORT_YEAR, cal);
    testDate(-102, 1, 1.5, 1684532.0 + 1.0 - 2*SHORT_YEAR, cal);
    
    testDate(-600, 1, 1.5, 1501907.0 + 1.0, cal); 
    testDate(-2000, 1, 1.5, 990557.0 + 1.0, cal);
    
    testDate(-1000, 1, 1.5, 1355807.0 + 1.0, cal);
    testDate(-1001, 1, 1.5, 1355807.0 + 1.0 - SHORT_YEAR, cal);
    
    testDate(100, 1, 1.5, 1757582.0 + 1.0, cal);
    testDate(200, 1, 1.5, 1794107.0 + 1.0, cal);
    testDate(300, 1, 1.5, 1830632.0 + 1.0, cal);
    testDate(400, 1, 1.5, 1867157.0 + 1.0, cal);
    testDate(500, 1, 1.5, 1903682.0 + 1.0, cal);
    
    testDate(1234, 5, 5.5, 2171901.0, cal);
    testDate(200, 1, 1.5, 1794108.0, cal);
    testDate(1500, 1, 1.5, 2268933.0, cal);
    testDate(1600, 1, 1.5, 2305458.0, cal);
    testDate(1800, 1, 1.5, 2378507.0 + 1.0, cal);
    testDate(1900, 1, 1.5, 2415032.0 + 1.0, cal);
  }
  
  @Test public void specificCasesGregorian() {
    Calendar cal = Calendar.GREGORIAN;
    
    //Meeus 1991
    testDate(1957, 10, 4.81, 2436116.31, cal);
    
    //From Vondrak, Wallace, Capitaine 2011
    // -1374 May 3, at 13:52:19.2 TT 
    testDate(-1374, 5, 3.578, 1219339.078, cal);    
    
    //https://legacy-www.math.harvard.edu/computing/javascript/Calendar/index.html
    testDate(-8, 1, 1.5, 1718138.0, cal);
    testDate(-101, 1, 1.5, 1684171.0, cal);
    testDate(-799, 1, 1.5, 1429232.0, cal);
    testDate(-800, 1, 1.5, 1428866.0, cal);
    testDate(-801, 1, 1.5, 1428501.0, cal);
    testDate(99, 12,31.5, 1757584.0, cal);
    testDate(100,1,1.5, 1757584.0 + 1.0, cal);
    testDate(100,1,31.5, 1757584.0 + 31.0, cal);
    testDate(100,2,1.5, 1757584.0 + 31.0 + 1.0, cal);
    testDate(100,2,28.5, 1757584.0 + 31.0 + 28.0, cal); //100 is not a leap year
    testDate(100,3,1.5, 1757584.0 + 31.0 + 28.0 + 1.0, cal);
    testDate(3000, 1, 1.5, 2816788, cal);
    testDate(30000, 1, 1.5, 12678335.0, cal);
        
    testDate(100,1,1.5, 1757585.0, cal);
    testDate(101,1,1.5, 1757950.0, cal); 
    testDate(200,1,1.5, 1794109.0, cal); 
    testDate(300,1,1.5, 1830633.0, cal); 
    testDate(400,1,1.5, 1867157, cal); 
    testDate(700,1,1.5, 1976730, cal);  
    testDate(800,1,1.5, 2013254, cal);
    
    //Explanatory Supplement 1961, p 438
    testYear(1920, 2422324 - 0.5, cal);
    testYear(1960, 2436934 - 0.5, cal);
    testYear(1964, 2438395 - 0.5, cal);
    testYear(1980, 2444239 - 0.5, cal);
    testYear(1990, 2447892 - 0.5, cal);
  }
  
  @Test public void deepPastGregorian() {
    Calendar cal = Calendar.GREGORIAN;
    testDate(-4712, 1, 1.5, 38.0, cal);
    testDate(-4713, 12, 31.5, 37.0, cal);
    testDate(-4713, 12, 1.5, 7.0, cal);
    testDate(-4713, 11, 30.5, 6.0, cal);
    testDate(-4713, 11, 24.5, 0.0, cal); //the JD=0 date
    testDate(-4713, 11, 24.0, -0.5, cal);
    testDate(-4713, 11, 23.0, -1.5, cal);
    testDate(-4713, 1, 1.5, -327.0, cal);
    testDate(-4714, 1, 1.5, -327.0 - 365 * 1.0, cal);
    testDate(-4715, 1, 1.5, -327.0 - 365 * 2.0, cal);
    testDate(-4716, 1, 1.5, -327.0 - 365 * 2.0 - 366 * 1.0, cal);
    testDate(-4717, 1, 1.5, -327.0 - 365 * 3.0 - 366 * 1.0, cal);
    testDate(-4718, 1, 1.5, -327.0 - 365 * 4.0 - 366 * 1.0, cal);
    testDate(-4719, 1, 1.5, -327.0 - 365 * 5.0 - 366 * 1.0, cal);
    testDate(-4720, 1, 1.5, -327.0 - 365 * 5.0 - 366 * 2.0, cal);
    testDate(-4721, 1, 1.5, -327.0 - 365 * 6.0 - 366 * 2.0, cal);
    
    testDate(-4800, 1, 1.5, -327.0 - 365 * 65.0 - 366 * 22.0, cal); //leap century year
    testDate(-4801, 1, 1.5, -327.0 - 365 * 66.0 - 366 * 22.0, cal);
    
    testDate(-4900, 1, 1.5, -327.0 - 365 * (75.0 + 66.0) - 366 * (24.0 + 22.0), cal); //not a leap year
  }
  
  @Test public void deepPastJulian() {
    Calendar cal = Calendar.JULIAN;
    testDate(-4711, 1, 1.5, 366 * 1.0, cal); 
    testDate(-4712, 1, 1.5, 0.0, cal); //the JD=0 date; a leap year
    testDate(-4712, 1, 31.5, 30.0, cal); 
    testDate(-4713, 1, 1.5, -365 * 1.0, cal); 
    testDate(-4714, 1, 1.5, -365 * 2.0, cal); 
    testDate(-4715, 1, 1.5, -365 * 3.0, cal); 
    testDate(-4716, 1, 1.5, -365 * 3.0 - 366 * 1.0, cal); 
    testDate(-4717, 1, 1.5, -365 * 4.0 - 366 * 1.0, cal);
    testDate(-4718, 1, 1.5, -365 * 5.0 - 366 * 1.0, cal); 
    testDate(-4719, 1, 1.5, -365 * 6.0 - 366 * 1.0, cal); 
    testDate(-4720, 1, 1.5, -365 * 6.0 - 366 * 2.0, cal);
    
    testDate(-4800, 1, 1.5, -365 * 66.0 - 366 * 22.0, cal); 
    testDate(-4900, 1, 1.5, -365 * (75.0 + 66.0) - 366 * (25.0 + 22.0), cal); 
  }
  
  
  private static final TimescaleImpl TIMESCALE = TimescaleImpl.TT;
  
  // It's easy to compute the JD manually.
  private void testSmallYears(Calendar calendar) {
    double base = calendar.julianDateJan0Year0().doubleValue();
    testYear(-9, base - 2*366 - 7*365, calendar);
    testYear(-8, base - 2*366 - 6*365, calendar);
    testYear(-7, base - 1*366 - 6*365, calendar);
    testYear(-6, base - 1*366 - 5*365, calendar);
    testYear(-5, base - 1*366 - 4*365, calendar);
    testYear(-4, base - 1*366 - 3*365, calendar);
    testYear(-3, base - 0*366 - 3*365, calendar);
    testYear(-2, base - 0*366 - 2*365, calendar);
    testYear(-1, base - 0*366 - 1*365, calendar);
    testYear(0, base + 0*366 + 0*365, calendar);
    testYear(1, base + 1*366 + 0*365, calendar);
    testYear(2, base + 1*366 + 1*365, calendar);
    testYear(3, base + 1*366 + 2*365, calendar);
    testYear(4, base + 1*366 + 3*365, calendar);
    testYear(5, base + 2*366 + 3*365, calendar);
    testYear(6, base + 2*366 + 4*365, calendar);
    testYear(7, base + 2*366 + 5*365, calendar);
    testYear(8, base + 2*366 + 6*365, calendar);
    testYear(9, base + 3*366 + 6*365, calendar);
    testYear(10, base + 3*366 + 7*365, calendar);
    testYear(11, base + 3*366 + 8*365, calendar);
    testYear(12, base + 3*366 + 9*365, calendar);
  }
  
  private void testYear(long year, double jan_0_in_given_year, Calendar calendar) {
    testDateToJdForYear(year, jan_0_in_given_year, calendar);
    testJdToDateForYear(year, jan_0_in_given_year, calendar);
  }
  
  private void testDate(long year, int month, double day, double jd, Calendar calendar) {
    testDateToJd(year, month, day, jd, calendar);
    testJdToDate(year,  month,  day, jd, calendar);
  }

  private void testDateToJdForYear(long year, double jan_0_given_year, Calendar calendar) {
    Date day = Date.from(year, 1,  1, calendar);
    for(int i = 0; i < calendar.numDaysIn(year); ++i) {
      testDateToJd(year, day.month(), day.day(), jan_0_given_year + 1 + i, calendar);
      day = day.next();
    }
  }

  private void testJdToDateForYear(long year, double jan_0_in_given_year, Calendar calendar) {
    Date day = Date.from(year, 1, 1, calendar); //Jan 1 of the given year
    for(int i = 1; i <= calendar.numDaysIn(year); ++i) {
      testJdToDate(day.year(), day.month(), day.day(), jan_0_in_given_year + i, calendar);
      day = day.next();
    }
  }
  
  private void testDateToJd(long year, int month, double day, double jd_expected, Calendar calendar) {
    JulianDateConverter convert = JulianDateConverter.using(calendar);
    JulianDate jd = convert.toJulianDate(year, month, BigDecimal.valueOf(day), TIMESCALE);
    assertTrue(BigDecimal.valueOf(jd_expected).compareTo(jd.jd()) == 0);
  }
  
  private void testJdToDate(long y_expected, int m_expected, double d_expected, double jd, Calendar calendar) {
    JulianDateConverter convert = JulianDateConverter.using(calendar);
    DateTime dt = convert.toDateTime(JulianDate.from(BigDecimal.valueOf(jd), TIMESCALE));
    assertEquals(dt.year(), y_expected);
    assertEquals(dt.month(), m_expected);
    assertEquals(dt.fractionalDay(), BigDecimal.valueOf(d_expected));
  }
}
