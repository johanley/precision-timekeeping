package astrolib.when;

import java.time.Month;

/** 
 Supported calendars.
 
 <P> (SOFA supports the Gregorian calendar, but not the Julian calendar (not to be confused with the Julian date.) 
*/
public enum Calendar implements CalendarOps {

  /** Not used very often. Leap years are divisble by 4. */
  JULIAN (4, 3*365 + 366){ 
    @Override public boolean isLeap(int year) {
      return year % 4 == 0;
    }
    @Override public JulianDate jd(int year, int month, double fractionalDay, Timescale timescale) {
      return JulianDateConverter.forJulianCalendar().toJulianDate(year, month, fractionalDay, timescale);
    }
    @Override public DateTime date(JulianDate jd) {
      return JulianDateConverter.forJulianCalendar().toDateTime(jd);
    }
  }, 
  
  /** 
   The most commonly used calendar, and the basis of civil timekeeping.
   Leap years are divisible by 4. 
   In addition, if the year is a century-year (1900, 2000, 2100, etc.) then it must also be divisible by 400.  
  */
  GREGORIAN (400, (3*365 + 366) * 25 * 4 /*centuries*/ - 3 /*oddball century-years with no leap day*/){ 
    @Override public boolean isLeap(int year) {
      boolean res = (year % 4 == 0);
      if (year % 100 == 0) {
        res = (year % 400 == 0);
      }
      return res;
    }
    @Override public JulianDate jd(int year, int month, double fractionalDay, Timescale timescale) {
      return JulianDateConverter.forGregorianCalendar().toJulianDate(year, month, fractionalDay, timescale);
    }
    @Override public DateTime date(JulianDate jd) {
      return JulianDateConverter.forGregorianCalendar().toDateTime(jd);
    }
  };

  /** {@value} */
  public static final int LEAP_YEAR_NUM_DAYS = 366;
  
  /** {@value} */
  public static final int NORMAL_YEAR_NUM_DAYS = 365;
  
  public int numDaysIn(int year) {
    return isLeap(year) ? LEAP_YEAR_NUM_DAYS : NORMAL_YEAR_NUM_DAYS;
  }
  
  /** Number of years in a full cycle of this calendar. */
  public int cycleYears() { return cycleYears; }
  
  /** Number of days in a full cycle of this calendar. */
  public int cycleDays() { return cycleDays; }
  
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
  
  /** Return the number of days until Dec 32.0. */
  public double daysFromDec32(int year, int month, double day) {
    int monthAccumulator = 0;
    //count backwards in time
    boolean isLeap = isLeap(year);
    for(int after = Month.DECEMBER.getValue(); after > month; --after) {
      monthAccumulator = monthAccumulator + Month.of(after).length(isLeap); 
    }
    return monthAccumulator + daysRemainingInMonth(month, day, isLeap);
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

  private Calendar(int cycleYears, int cycleDays) {
    this.cycleYears = cycleYears;
    this.cycleDays = cycleDays;
  }
  private int cycleYears;
  private int cycleDays;
  
  /** The number of days remaining in the given month, from the given day. */
  private static double daysRemainingInMonth(int month, double day, boolean isLeap) {
    int length = Month.of(month).length(isLeap);
    return (length + 1) - day;
  }

  

}
