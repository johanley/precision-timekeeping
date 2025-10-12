package astrolib.when;

import static astrolib.when.Calendar.*;
import static astrolib.when.JdAndGregorianCalendar.*;
import static org.junit.Assert.*;

import org.junit.Test;

import astrolib.util.Consts;

/** JUnit 4 tests.*/
public class JdAndGregorianCalendarTEST {

  @Test public void dateToJdVariousYears() {
    testDateToJdForYear(0, GREGORIAN_BASE);
    testDateToJdForYear(1, GREGORIAN_BASE + 366);
    testDateToJdForYear(2, GREGORIAN_BASE + 366 + 365);
    testDateToJdForYear(3, GREGORIAN_BASE + 366 + 2*365);
    testDateToJdForYear(4, GREGORIAN_BASE + 366 + 3*365);
    testDateToJdForYear(5, GREGORIAN_BASE + 2*366 + 3*365);
    testDateToJdForYear(6, GREGORIAN_BASE + 2*366 + 4*365);
    testDateToJdForYear(7, GREGORIAN_BASE + 2*366 + 5*365);
    testDateToJdForYear(8, GREGORIAN_BASE + 2*366 + 6*365);
    testDateToJdForYear(9, GREGORIAN_BASE + 3*366 + 6*365);
    testDateToJdForYear(10, GREGORIAN_BASE + 3*366 + 7*365);
    testDateToJdForYear(11, GREGORIAN_BASE + 3*366 + 8*365);
    testDateToJdForYear(12, GREGORIAN_BASE + 3*366 + 9*365);
    
    //Explanatory Supplement 1961, p 438
    testDateToJdForYear(1920, 2422324 - 0.5);
    testDateToJdForYear(1960, 2436934 - 0.5);
    testDateToJdForYear(1964, 2438395 - 0.5);
    testDateToJdForYear(1980, 2444239 - 0.5);
    testDateToJdForYear(1990, 2447892 - 0.5);

    testDateToJdForYear(-1, GREGORIAN_BASE - 365);
    testDateToJdForYear(-2, GREGORIAN_BASE - 2*365);
    testDateToJdForYear(-3, GREGORIAN_BASE - 3*365);
    testDateToJdForYear(-4, GREGORIAN_BASE - (3*365 + 366));
    testDateToJdForYear(-5, GREGORIAN_BASE - (4*365 + 366));
    testDateToJdForYear(-6, GREGORIAN_BASE - (5*365 + 366));
    testDateToJdForYear(-7, GREGORIAN_BASE - (6*365 + 366));
    testDateToJdForYear(-8, GREGORIAN_BASE - (6*365 + 2*366));
    testDateToJdForYear(-9, GREGORIAN_BASE - (7*365 + 2*366));
    testDateToJdForYear(-10, GREGORIAN_BASE - (8*365 + 2*366));
    testDateToJdForYear(-11, GREGORIAN_BASE - (9*365 + 2*366));
    testDateToJdForYear(-12, GREGORIAN_BASE - (9*365 + 3*366));
  }
  
  @Test public void dateToJdCenturyYears() {
    //Explanatory Supplement 1961, p.437
    testDateToJdForYear(1500, 2268923 - 0.5);
    testDateToJdForYear(1600, 2305447 - 0.5);
    testDateToJdForYear(1700, 2341972 - 0.5);
    testDateToJdForYear(1800, 2378496 - 0.5);
    testDateToJdForYear(1900, 2415020 - 0.5);
  }
  
  @Test public void jdToDateTimeVariousYears() {
    testJdToDateForYear(0, GREGORIAN_BASE);
    testJdToDateForYear(1, GREGORIAN_BASE + 366);
    testJdToDateForYear(2, GREGORIAN_BASE + 366 + 365);
    testJdToDateForYear(3, GREGORIAN_BASE + 366 + 2*365);
    testJdToDateForYear(4, GREGORIAN_BASE + 366 + 3*365);
    testJdToDateForYear(5, GREGORIAN_BASE + 2*366 + 3*365);
    testJdToDateForYear(6, GREGORIAN_BASE + 2*366 + 4*365);
    testJdToDateForYear(7, GREGORIAN_BASE + 2*366 + 5*365);
    testJdToDateForYear(8, GREGORIAN_BASE + 2*366 + 6*365);
    testJdToDateForYear(9, GREGORIAN_BASE + 3*366 + 6*365);
    testJdToDateForYear(10, GREGORIAN_BASE + 3*366 + 7*365);
    testJdToDateForYear(11, GREGORIAN_BASE + 3*366 + 8*365);
    testJdToDateForYear(12, GREGORIAN_BASE + 3*366 + 9*365);
    
    /*
    testJdToDateForYear(-1, GREGORIAN_BASE - 365);
    testJdToDateForYear(-2, GREGORIAN_BASE - 2*365);
    testJdToDateForYear(-3, GREGORIAN_BASE - 3*365);
    testJdToDateForYear(-4, GREGORIAN_BASE - (3*365 + 366));
    testJdToDateForYear(-5, GREGORIAN_BASE - (4*365 + 366));
    testJdToDateForYear(-6, GREGORIAN_BASE - (5*365 + 366));
    testJdToDateForYear(-7, GREGORIAN_BASE - (6*365 + 366));
    testJdToDateForYear(-8, GREGORIAN_BASE - (6*365 + 2*366));
    testJdToDateForYear(-9, GREGORIAN_BASE - (7*365 + 2*366));
    testJdToDateForYear(-10, GREGORIAN_BASE - (8*365 + 2*366));
    testJdToDateForYear(-11, GREGORIAN_BASE - (9*365 + 2*366));
    testJdToDateForYear(-12, GREGORIAN_BASE - (9*365 + 3*366));
    */
  }
  
  
  /** Includes examples from various reliable sources.  */
  @Test public void allTests() {
    test(0,1,1.0, GREGORIAN_BASE + 1.0);
    test(0,1,31.0, GREGORIAN_BASE + 31.0);
    test(0,2,1.0, GREGORIAN_BASE + 31.0 + 1.0);
    test(0,3,1.0, GREGORIAN_BASE + 31.0 + 29.0 + 1.0); // year 0 is a leap year
    test(1,1,1.0, GREGORIAN_BASE + 1.0 + LEAP_YEAR_NUM_DAYS);
    test(2,1,1.0, GREGORIAN_BASE + 1.0 + LEAP_YEAR_NUM_DAYS + 1*NORMAL_YEAR_NUM_DAYS);
    test(3,1,1.0, GREGORIAN_BASE + 1.0 + LEAP_YEAR_NUM_DAYS + 2*NORMAL_YEAR_NUM_DAYS);
    test(4,1,1.0, GREGORIAN_BASE + 1.0 + LEAP_YEAR_NUM_DAYS + 3*NORMAL_YEAR_NUM_DAYS);
    test(5,1,1.0, GREGORIAN_BASE + 1.0 + LEAP_YEAR_NUM_DAYS + 3*NORMAL_YEAR_NUM_DAYS + 1*LEAP_YEAR_NUM_DAYS);
    
    //https://legacy-www.math.harvard.edu/computing/javascript/Calendar/index.html
    test(-8, 1, 1.5, 1718138.0);
    test(-101, 1, 1.5, 1684171.0);
    test(-799, 1, 1.5, 1429232.0);
    test(-800, 1, 1.5, 1428866.0);
    test(-801, 1, 1.5, 1428501.0);
    test(99, 12,31.5, 1757584.0);
    test(100,1,1.5, 1757584.0 + 1.0);
    test(100,1,31.5, 1757584.0 + 31.0);
    test(100,2,1.5, 1757584.0 + 31.0 + 1.0);
    test(100,2,28.5, 1757584.0 + 31.0 + 28.0); //100 is not a leap year
    test(100,3,1.5, 1757584.0 + 31.0 + 28.0 + 1.0);
    test(3000, 1, 1.5, 2816788);
    test(30000, 1, 1.5, 12678335);
        
    test(100,1,1.5, 1757585.0);
    test(101,1,1.5, 1757950.0); 
    test(200,1,1.5, 1794109.0); 
    test(300,1,1.5, 1830633.0); 
    test(400,1,1.5, 1867157); 
    test(700,1,1.5, 1976730);  
    test(800,1,1.5, 2013254);
    
    //Explanatory Supplement 1961
    test(1500, 1, 1.5, 2268923.0 + 1.0);  
    test(1600, 1, 1.5, 2305447.0 + 1.0); 
    test(1700, 1, 1.5, 2341972.0 + 1.0); 
    test(1800, 1, 1.5, 2378496.0 + 1.0);  
    test(1900, 1, 1.5, 2415020.0 + 1.0);  
    test(1901, 1, 1.5, 2415020.0 + 1.0 + 365.0); 
    
    //Meeus
    test(1957, 10, 4.81, 2436116.31);
    
    //From Vondrak, Wallace, Capitaine 2011
    // -1374 May 3, at 13:52:19.2 TT 
    test(-1374, 5, 3.578, 1219339.078);
  }
  
  private void test(int y, int m, double d, double expected) {
    double jd = JdAndGregorianCalendar.jd(y, m, d);
    assertEquals(expected, jd, Consts.EPSILON);
  }
  
  private void testDateToJdForYear(int year, double julianBaseJan0) {
    Date day = Date.gregorian(year, 1, 1); //Jan 1 of the given year
    for(int i = 1; i <= Calendar.GREGORIAN.numDaysIn(year); ++i) {
      JulianDate jd = JulianDate.from(julianBaseJan0 + i, Timescale.TT);
      double jd2 = JdAndGregorianCalendar.jd(day.year(), day.month(), day.day());
      assertEquals(jd2, jd.jd(), Consts.EPSILON);
      day = day.next();
    }
  }

  private void testJdToDateForYear(int year, double julianBaseJan0) {
    Date day = Date.gregorian(year, 1, 1); //Jan 1 of the given year
    for(int i = 1; i <= Calendar.GREGORIAN.numDaysIn(year); ++i) {
      JulianDate jd = JulianDate.from(julianBaseJan0 + i, Timescale.TT);
      DateTime dt = JdAndGregorianCalendar.dateTime(jd);
      assertEquals(dt.day(), day.day());
      assertEquals(dt.month(), day.month());
      assertEquals(dt.year(), day.year());
      day = day.next();
    }
  }
  
  
}
