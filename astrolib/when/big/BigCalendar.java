package astrolib.when.big;

import java.math.BigDecimal;
import java.time.Month;

/** 
 Supported calendars. 
 (It's very likely that this set of implemented calendars will never change.) 
*/
public enum BigCalendar implements BigCalendarOps {

  /** Not used very often. Leap years are divisble by 4. */
  JULIAN (4, 3*365 + 366, BigDecimal.valueOf(1_721_056.5)){ 
    @Override public boolean isLeap(long year) {
      return year % 4 == 0;
    }
  }, 
  
  /** 
   The most commonly used calendar, and the basis of civil timekeeping.
   Leap years are divisible by 4. 
   However, if the year is a century-year (1900, 2000, 2100, etc.) then it must also be divisible by 400.  
  */
  GREGORIAN (400, (3*365 + 366) * 25 * 4 /*centuries*/ - 3 /*oddball century-years with no leap day*/, BigDecimal.valueOf(1_721_058.5)){ 
    @Override public boolean isLeap(long year) {
      boolean res = (year % 4 == 0);
      if (year % 100 == 0) {
        res = (year % 400 == 0);
      }
      return res;
    }
  };

  /** Number of days in a leap year: {@value} */
  public static final int LEAP_YEAR_NUM_DAYS = 366;
  
  /** Number of days in a non-leap year: {@value} */
  public static final int NORMAL_YEAR_NUM_DAYS = 365;

  /** Return the number of days in the given year. */
  public int numDaysIn(long year) {
    return isLeap(year) ? LEAP_YEAR_NUM_DAYS : NORMAL_YEAR_NUM_DAYS;
  }

  /** Number of years in a full cycle of this calendar. */
  public int fullCycleYears() { return fullCycleYears; }
  
  /** Number of days in a full cycle of this calendar. */
  public int fullCycleDays() { return fullCycleDays; }

  /** The Julian date for January 0.0, year 0, for this calendar. */
  public BigDecimal julianDateJan0Year0() {  return julianDateJan0Year0; }
  
  /** 
   For the given (fractional) date, return the (fractional) number of days since Jan 0.0.
   Jan 0.0 is just an alias for December 31 of the previous year.
   Jan 1 is 1, Jan 2 is 2, etc. 
  */
  public BigDecimal daysFromJan0(long year, int month, BigDecimal day) {
    int daysInCompletedMonths = 0;
    for(int before = Month.JANUARY.getValue(); before < month; ++before) {
      daysInCompletedMonths = daysInCompletedMonths + Month.of(before).length(isLeap(year)); 
    }
    return day.add(BigDecimal.valueOf(daysInCompletedMonths));
  }
  
  /** Return the number of days until Dec 32.0 in this calendar. */
  public BigDecimal daysFromDec32(long year, int month, BigDecimal day) {
    int monthAccumulator = 0;
    //count backwards in time
    boolean isLeap = isLeap(year);
    for(int after = Month.DECEMBER.getValue(); after > month; --after) {
      monthAccumulator = monthAccumulator + Month.of(after).length(isLeap); 
    }
    return BigDecimal.valueOf(monthAccumulator).add(daysRemainingInMonth(month, day, isLeap));
  }

  /** 
   Number of days in a full set of complete years.
   Includes the start-year, but excludes the end-year.
   Returns 0 if the start and end are the same year. 
  */
  public int daysInCompleteYears(long startYr, long endYr) {
    int result = 0;
    for(long year = startYr; year < endYr; ++year) {
      result = result + numDaysIn(year);
    }
    return result;
  }

  /**
   Constructor. 
   @param fullCycleYears the number of years in a complete cycle of the calendar 
   @param fullCycleDays the number of days in a complete cycle of the calendar
   @param julianDateJan0Year0 the Julian date for January 0.0, year 0, for the calendar
  */
  private BigCalendar(int fullCycleYears, int fullCycleDays, BigDecimal julianDateJan0Year0) {
    this.fullCycleYears = fullCycleYears;
    this.fullCycleDays = fullCycleDays;
    this.julianDateJan0Year0 = julianDateJan0Year0;
  }
  private int fullCycleYears;
  private int fullCycleDays;
  
  private BigDecimal julianDateJan0Year0; //with no timescale
  
  /** The number of days remaining in the given month, from the given day. */
  private static BigDecimal daysRemainingInMonth(int month, BigDecimal day, boolean isLeap) {
    int length = Month.of(month).length(isLeap);
    return BigDecimal.valueOf(length + 1).subtract(day);
  }
}