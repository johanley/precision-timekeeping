package bigtime.when;

import static bigtime.when.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.time.Month;

/** 
 Supported calendars.
  
 <P><b>This set of supported calendars will never change in this library.</b> 
*/
public enum Calendar implements CalendarLeapYear {

  /** 
   The Julian calendar. 
   Leap years are evenly divisble by 4.
   Note the confusing language adopted by astronomers: the Julian date is not specifically attached to the Julian calendar.
   (They were simply named after two different Julians!) 
  */
  JULIAN (4, 3*365 + 366, big(1_721_056.5)){ 
    @Override public boolean isLeap(long year) {
      return year % 4 == 0;
    }
  }, 
  
  /** 
   The Gregorian calendar.
   The worldwide basis of civil timekeeping.
   Century-years are leap years only if they are evenly divisible by 400; otherwise, leap years are evenly divisible by 4. 
   For example, 1900 is not a leap year, but 2000 is a leap year.
  */
  GREGORIAN (400, (3*365 + 366) * 25 * 4 /*centuries*/ - 3 /*oddball century-years with no leap day*/, big(1_721_058.5)){ 
    @Override public boolean isLeap(long year) {
      boolean res = (year % 4 == 0);
      if (year % 100 == 0) {
        res = (year % 400 == 0);
      }
      return res;
    }
  };

  /** Number of days in a leap year: {@value} */
  public static final int LONG_YEAR = 366;
  
  /** Number of days in a non-leap year: {@value} */
  public static final int SHORT_YEAR = 365;

  /** Return the number of days in the given year. */
  public int numDaysIn(long year) {
    return isLeap(year) ? LONG_YEAR : SHORT_YEAR;
  }

  /** Number of years in one complete cycle of this calendar. */
  public int fullCycleYears() { return fullCycleYears; }
  
  /** Number of days in one complete cycle of this calendar. */
  public int fullCycleDays() { return fullCycleDays; }

  /** 
   The Julian date for January 0.0, year 0, for this calendar.
   This corresponds to December 31 of the year -1. 
  */
  public BigDecimal julianDateJan0Year0() {  return jdJan0Year0; }
  
  /** 
   For the given (fractional) date, return the (fractional) number of days since Jan 0.0.
   Jan 0.0 is just an alias for December 31 of the previous year.
   Jan 1 is day 1.0, Jan 2 is day 2.0, etc.
   @param day can be a fractional day, as in <em>5.27</em>. 
  */
  public BigDecimal daysFromJan0(long year, int month, BigDecimal day) {
    int daysInCompletedMonths = 0;
    for(int before = Month.JANUARY.getValue(); before < month; ++before) {
      daysInCompletedMonths = daysInCompletedMonths + Month.of(before).length(isLeap(year)); 
    }
    return day.add(big(daysInCompletedMonths));
  }
  
  /** 
   Return the number of days until Dec 32.0 in this calendar, for the given year and month.
   Dec 32.0 is an alias for January 1.0 of the following year. 
  */
  public BigDecimal daysFromDec32(long year, int month, BigDecimal day) {
    int monthAccumulator = 0;
    //count backwards in time
    boolean isLeap = isLeap(year);
    for(int after = Month.DECEMBER.getValue(); after > month; --after) {
      monthAccumulator = monthAccumulator + Month.of(after).length(isLeap); 
    }
    return big(monthAccumulator).add(daysRemainingInMonth(month, day, isLeap));
  }

  /**
   Constructor. 
   @param fullCycleYears the number of years in a complete cycle of the calendar 
   @param fullCycleDays the number of days in a complete cycle of the calendar
   @param jdJan0Year0 the Julian date for January 0.0, year 0, for the calendar
  */
  private Calendar(int fullCycleYears, int fullCycleDays, BigDecimal jdJan0Year0) {
    this.fullCycleYears = fullCycleYears;
    this.fullCycleDays = fullCycleDays;
    this.jdJan0Year0 = jdJan0Year0;
  }
  private int fullCycleYears;
  private int fullCycleDays;
  private BigDecimal jdJan0Year0; //with no timescale
  
  /** The number of days remaining in the given month, from the given day. */
  private static BigDecimal daysRemainingInMonth(int month, BigDecimal day, boolean isLeap) {
    int length = Month.of(month).length(isLeap);
    return big(length + 1).subtract(day);
  }
}