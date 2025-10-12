package astrolib.when;

import java.time.Month;

/** 
 Supported calendars.
 
 <P> (SOFA supports the Gregorian calendar, but not the Julian calendar (not to be confused with the Julian date.) 
*/
public enum Calendar implements CalendarOps {
  
  /** Not used very often. Leap years are divisble by 4. */
  JULIAN { 
    @Override public boolean isLeap(int year) {
      return year % 4 == 0;
    }
    @Override public double jd(int year, int month, double fractionalDay) {
      return JdAndJulianCalendar.jd(year, month, fractionalDay);
    }
  }, 
  
  /** 
   The most commonly used calendar, and the basis of civil timekeeping.
   Leap years are divisible by 4. 
   If the year is a century-year (1900, 2000, 2100, etc.) then it must also be divisible by 400.  
  */
  GREGORIAN { 
    @Override public boolean isLeap(int year) {
      boolean res = (year % 4 == 0);
      if (year % 100 == 0) {
        res = (year % 400 == 0);
      }
      return res;
    }
    @Override public double jd(int year, int month, double fractionalDay) {
      return JdAndGregorianCalendar.jd(year, month, fractionalDay);
    }
  };

  /** {@value} */
  public static final int LEAP_YEAR_NUM_DAYS = 366;
  
  /** {@value} */
  public static final int NORMAL_YEAR_NUM_DAYS = 365;
  
  public int numDaysIn(int year) {
    return isLeap(year) ? LEAP_YEAR_NUM_DAYS : NORMAL_YEAR_NUM_DAYS;
  }
  
  /** 
   For the given (fractional) date, return the (fractional) number of days since Jan 0.0.
   Jan 0.0 is just an alias for December 31 of the previous year.
   Jan 1 is 1, Jan 2 is 2, etc. 
  */
  public double daysFromJan0(int year, int month, double day) {
    int completedMonths = 0;
    for(int before = Month.JANUARY.getValue(); before < month; ++before) {
      completedMonths = completedMonths + Month.of(before).length(isLeap(year)); 
    }
    double result = completedMonths + day;
    return result;
  }
  
  /** 
   Number of days in a full set of complete years.
   Includes the start-year, but excludes the end-year.
   Returns 0 if the start and end are the same year. 
  */
  public int daysInCompleteYears(int startYr, int endYr) {
    int result = 0;
    for(int year = startYr; year < endYr; ++year) {
      result = result + numDaysIn(year);
    }
    return result;
  }
}
