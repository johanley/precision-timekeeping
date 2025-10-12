package astrolib.when;

import static org.junit.Assert.*;
import org.junit.Test;

import astrolib.util.Consts;

import static astrolib.when.Calendar.*;
import static astrolib.when.JdAndJulianCalendar.*;

/** JUnit 4 tests.*/
public class JdAndJulianCalendarTEST {

  @Test public void dateToJdVariousYears() {
    testDateToJdForYear(0, JULIAN_BASE);
    testDateToJdForYear(1, JULIAN_BASE + 366);
    testDateToJdForYear(2, JULIAN_BASE + 366 + 365);
    testDateToJdForYear(3, JULIAN_BASE + 366 + 2*365);
    testDateToJdForYear(4, JULIAN_BASE + 366 + 3*365);
    testDateToJdForYear(5, JULIAN_BASE + 2*366 + 3*365);
    testDateToJdForYear(6, JULIAN_BASE + 2*366 + 4*365);
    testDateToJdForYear(7, JULIAN_BASE + 2*366 + 5*365);
    testDateToJdForYear(8, JULIAN_BASE + 2*366 + 6*365);
    testDateToJdForYear(9, JULIAN_BASE + 3*366 + 6*365);
    testDateToJdForYear(10, JULIAN_BASE + 3*366 + 7*365);
    testDateToJdForYear(11, JULIAN_BASE + 3*366 + 8*365);
    testDateToJdForYear(12, JULIAN_BASE + 3*366 + 9*365);
    
    testDateToJdForYear(-1, JULIAN_BASE - 365);
    testDateToJdForYear(-2, JULIAN_BASE - 2*365);
    testDateToJdForYear(-3, JULIAN_BASE - 3*365);
    testDateToJdForYear(-4, JULIAN_BASE - (3*365 + 366));
    testDateToJdForYear(-5, JULIAN_BASE - (4*365 + 366));
    testDateToJdForYear(-6, JULIAN_BASE - (5*365 + 366));
    testDateToJdForYear(-7, JULIAN_BASE - (6*365 + 366));
    testDateToJdForYear(-8, JULIAN_BASE - (6*365 + 2*366));
    testDateToJdForYear(-9, JULIAN_BASE - (7*365 + 2*366));
    testDateToJdForYear(-10, JULIAN_BASE - (8*365 + 2*366));
    testDateToJdForYear(-11, JULIAN_BASE - (9*365 + 2*366));
    testDateToJdForYear(-12, JULIAN_BASE - (9*365 + 3*366));
  }
  
  @Test public void jdToDateTimeVariousYears() {
    testJdToDateForYear(0, JULIAN_BASE);
    testJdToDateForYear(1, JULIAN_BASE + 366);
    testJdToDateForYear(2, JULIAN_BASE + 366 + 365);
    testJdToDateForYear(3, JULIAN_BASE + 366 + 2*365);
    testJdToDateForYear(4, JULIAN_BASE + 366 + 3*365);
    testJdToDateForYear(5, JULIAN_BASE + 2*366 + 3*365);
    testJdToDateForYear(6, JULIAN_BASE + 2*366 + 4*365);
    testJdToDateForYear(7, JULIAN_BASE + 2*366 + 5*365);
    testJdToDateForYear(8, JULIAN_BASE + 2*366 + 6*365);
    testJdToDateForYear(9, JULIAN_BASE + 3*366 + 6*365);
    testJdToDateForYear(10, JULIAN_BASE + 3*366 + 7*365);
    testJdToDateForYear(11, JULIAN_BASE + 3*366 + 8*365);
    testJdToDateForYear(12, JULIAN_BASE + 3*366 + 9*365);
    
    testJdToDateForYear(-1, JULIAN_BASE - 365);
    testJdToDateForYear(-2, JULIAN_BASE - 2*365);
    testJdToDateForYear(-3, JULIAN_BASE - 3*365);
    testJdToDateForYear(-4, JULIAN_BASE - (3*365 + 366));
    testJdToDateForYear(-5, JULIAN_BASE - (4*365 + 366));
    testJdToDateForYear(-6, JULIAN_BASE - (5*365 + 366));
    testJdToDateForYear(-7, JULIAN_BASE - (6*365 + 366));
    testJdToDateForYear(-8, JULIAN_BASE - (6*365 + 2*366));
    testJdToDateForYear(-9, JULIAN_BASE - (7*365 + 2*366));
    testJdToDateForYear(-10, JULIAN_BASE - (8*365 + 2*366));
    testJdToDateForYear(-11, JULIAN_BASE - (9*365 + 2*366));
    testJdToDateForYear(-12, JULIAN_BASE - (9*365 + 3*366));
  }
  
  /** Includes examples from various reliable sources.  */
  @Test public void specificCases() {
    //Explanatory Supplement tables, 1961
    testDateToJd(-5, 1, 1.5, 1719232.0);
    testDateToJd(1,  1, 1.5, 2415385.0 + 1.0 - 693962.0);
    testDateToJd(2,  1, 1.5, 2415750.0 + 1.0 - 693962.0);
    testDateToJd(3,  1, 1.5, 2416115.0 + 1.0 - 693962.0);
    testDateToJd(4,  1, 1.5, 2416480.0 + 1.0 - 693962.0);
    testDateToJd(5,  1, 1.5, 2416846.0 + 1.0 - 693962.0);
    
    //USNO https://aa.usno.navy.mil/data/JulianDate
    testDateToJd(399, 12, 31.0, 1867156.5);
    
    //Meeus 1991, page 61 and 62
    testDateToJd(333, 1, 27.5, 1842713.0);
    testDateToJd(837, 4, 10.3, 2026871.8);
    testDateToJd(-1000, 7, 12.5, 1356001.0);
    testDateToJd(-1000, 2, 29.0, 1355866.5);
    testDateToJd(-1001, 8, 17.9, 1355671.4); 
    testDateToJd(-4712, 1, 1.5, 0.0);
    //by extension:
    testDateToJd(-4712, 1, 1.0, -0.5);
    testDateToJd(-4713, 12, 31.0, -1.5);
    testDateToJd(-4713, 12, 30.0, -2.5);
    
    testDateToJd(-98, 1, 1.5, 1684532.0 + 1.0 + LEAP_YEAR_NUM_DAYS + NORMAL_YEAR_NUM_DAYS); 
    testDateToJd(-99, 1, 1.5, 1684532.0 + 1.0 + LEAP_YEAR_NUM_DAYS); 
    testDateToJd(-100, 1, 1.5, 1684532.0 + 1.0); 
    testDateToJd(-101, 1, 1.5, 1684532.0 + 1.0 - NORMAL_YEAR_NUM_DAYS);
    testDateToJd(-102, 1, 1.5, 1684532.0 + 1.0 - 2*NORMAL_YEAR_NUM_DAYS);
    
    testDateToJd(-600, 1, 1.5, 1501907.0 + 1.0); 
    testDateToJd(-2000, 1, 1.5, 990557.0 + 1.0);
    
    testDateToJd(-1000, 1, 1.5, 1355807.0 + 1.0);
    testDateToJd(-1001, 1, 1.5, 1355807.0 + 1.0 - NORMAL_YEAR_NUM_DAYS);
    
    testDateToJd(100, 1, 1.5, 1757582.0 + 1.0);
    testDateToJd(200, 1, 1.5, 1794107.0 + 1.0);
    testDateToJd(300, 1, 1.5, 1830632.0 + 1.0);
    testDateToJd(400, 1, 1.5, 1867157.0 + 1.0);
    testDateToJd(500, 1, 1.5, 1903682.0 + 1.0);
    
    testDateToJd(1234, 5, 5.5, 2171901.0);
    testDateToJd(200, 1, 1.5, 1794108.0);
    testDateToJd(1500, 1, 1.5, 2268933.0);
    testDateToJd(1600, 1, 1.5, 2305458.0);
    testDateToJd(1800, 1, 1.5, 2378507.0 + 1.0);
    testDateToJd(1900, 1, 1.5, 2415032.0 + 1.0);
  }
  
  /*@Test*/ public void sanityJDToDateTime() {
    JulianDate jd = JulianDate.from(JULIAN_BASE + 1, Timescale.TT);
    DateTime dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt);  //Jan 1 year 0

    jd = JulianDate.from(JULIAN_BASE + 1 + 0.5, Timescale.TT);
    dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt);  //Jan 1.5 year 0
    
    jd = JulianDate.from(JULIAN_BASE + 2, Timescale.TT);
    dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt);  //Jan 2

    jd = JulianDate.from(JULIAN_BASE + 30, Timescale.TT);
    dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt); //Jan 30 

    jd = JulianDate.from(JULIAN_BASE + 31, Timescale.TT);
    dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt);  //Jan 31

    jd = JulianDate.from(JULIAN_BASE + 32, Timescale.TT);
    dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt);  //Feb 1

    jd = JulianDate.from(JULIAN_BASE + 32 + 27, Timescale.TT);
    dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt); // Feb 28  

    jd = JulianDate.from(JULIAN_BASE + 32 + 27 + 1, Timescale.TT);
    dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt);  // Feb 29

    jd = JulianDate.from(JULIAN_BASE + 32 + 27 + 2, Timescale.TT);
    dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt); //March 1
    
    jd = JulianDate.from(JULIAN_BASE + 32 + 27 + 2 + 0.25, Timescale.TT);
    dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt); //March 1.25
    
    //dec 31 of year 0, a leap year
    jd = JulianDate.from(JULIAN_BASE + 366, Timescale.TT);
    dt = JdAndJulianCalendar.dateTime(jd);
    //System.out.println(dt);  //Dec 31
  }
  
  private void testDateToJd(int y, int m, double d, double expected) {
    double jd = JdAndJulianCalendar.jd(y, m, d);
    assertEquals(expected, jd, Consts.EPSILON);
  }
  
  private void testJdToDateForYear(int year, double julianBaseJan0) {
    Date day = Date.julian(year, 1, 1); //Jan 1 of the given year
    for(int i = 1; i <= Calendar.JULIAN.numDaysIn(year); ++i) {
      
      JulianDate jd = JulianDate.from(julianBaseJan0 + i, Timescale.TT);
      DateTime dt = JdAndJulianCalendar.dateTime(jd);
      //System.out.println("test for year"  + year + " i:" + i + " " + dt + " " + day);
      
      assertEquals(dt.day(), day.day());
      assertEquals(dt.month(), day.month());
      assertEquals(dt.year(), day.year());
      
      day = day.next();
    }
  }
  
  private void testDateToJdForYear(int year, double julianBaseJan0) {
    Date day = Date.julian(year, 1, 1); //Jan 1 of the given year
    for(int i = 1; i <= Calendar.JULIAN.numDaysIn(year); ++i) {
      
      JulianDate jd = JulianDate.from(julianBaseJan0 + i, Timescale.TT);
      double jd2 = JdAndJulianCalendar.jd(day.year(), day.month(), day.day());
      //System.out.println("test for year"  + year + " i:" + i + " " + dt + " " + day);
      
      assertEquals(jd2, jd.jd(), Consts.EPSILON);
      
      day = day.next();
    }
  }
  
}
