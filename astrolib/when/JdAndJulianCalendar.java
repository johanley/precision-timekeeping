package astrolib.when;

import static astrolib.when.Calendar.*;

import java.time.Month;
import java.util.Arrays;

import astrolib.util.Mathy;

/** 
 Convert a date from the Julian proleptic calendar into a Julian date, and vice versa.
  
 <P>For this class, there are no restrictions on the input/output date.
 (All astronomical libraries that I've seen restrict range of the year in some way. This class does not share that defect.)
*/
final class JdAndJulianCalendar {
  
  /*
   Description of the implementation:
   The Julian calendar has a full cycle lasting just 4 years, and is simpler than the Gregorian calendar.
   
   Starting from year 0, separate into 3 blocks of time, from largest to smallest: 
     - 1. full Julian cycles of [0..N] * 4 years
     - 2. [0..3] remainder-years, having complete years not part of a full Julian cycle
     - 3. remainder-days of the final year
     
   Then just add the three parts.
        
   When the year is negative, the above logic is similar, but modified in order to 
   count backwards through the calendar.
   
   The year 0 is a leap year in both the Julian calendar and the Gregorian calendar.
  */

  /** 
   Return the Julian date corresponding to the given date-time in the Julian calendar.
   @param dt must be attached to the Julian calendar.
  */
  static double jd(DateTime dt) {
    if (JULIAN != dt.date().calendar() ) {
      throw new IllegalArgumentException("The supplied date-time is not attached to the Julian calendar.");
    }
    return jd(dt.year(), dt.month(), dt.fractionalDay());
  }
  
  /** 
   Return the Julian date corresponding to the given moment (fractional day!) in the Julian calendar.
   Passing '15.5', for the day corresponds to 12h, for example.  
  */
  static double jd(int year, int month, double day) {
    double result = 0.0;
    if (Mathy.sign(year) >= 0) {
      result = nonNegativeYears(year, month, day);
    }
    else {
      result = negativeYears(year, month, day);
    }
    return result;
  }
  
  /** Return the corresponding date-time in the Julian calendar, with the same timescale as the given jd. */
  public static DateTime dateTime(JulianDate jd) {
    double JAN_1_YEAR_0 = JULIAN_BASE + 1; //0h Jan 1 year 0; Explanatory Supplement 1961, p437, table 14.14
    return jd.jd() >= JAN_1_YEAR_0 ? dateTimeNonNegYears(jd) : dateTimeNegYears(jd);
  }
  
  /**
   A full cycle of the Julian calendar starts at this moment = {@value}. 
   It is the Julian date as of Jan 0.0 in the year 0, in the Julian Calendar.
   This is really 0h December 31 in the year -1.
   This is chosen as the basis for the internal calculation of Julian date, plus/minus so many days with respect to this moment.
   See the Explanatory Supplement 1961, p437.
  */
  static final double JULIAN_BASE = 1_721_056.5;
  
  /** Number of years in a complete Julian cycle of 4 years = {@value}. */
  static final int CYCLE_LEN_YEARS = 4;
  
  /** Number of days in a complete Julian cycle of 4 years = {@value}. */
  static final int CYCLE_LEN_DAYS = 1 * LEAP_YEAR_NUM_DAYS + 3 * NORMAL_YEAR_NUM_DAYS; // 1461
  
  /** Return the number of days until Dec 32.0. */
  static double daysFromDec32(int month, double day, boolean isLeap) {
    int monthAccumulator = 0;
    //count backwards in time
    for(int after = Month.DECEMBER.getValue(); after > month; --after) {
      monthAccumulator = monthAccumulator + Month.of(after).length(isLeap); 
    }
    return monthAccumulator + daysRemainingInMonth(month, day, isLeap);
  }
  
  /** 
   Calculation for non-negative years.
   
   <P>For years 0,1,... the cycles have this pattern:
   <pre>
          L*********  L*********  L..  cycles start with a leap year
    y:    0  1  2  3  4  5  6  7  8.. 
    y/4:  0  0  0  0  1  1  1  1  2..  integer division
   </pre>  
  */
  private static double nonNegativeYears(int year, int month, double day) {
    //1. full cycles 
    //here, cycles start with a leap year and have a fixed length in days; the years in a cycle share the same value of (year/4)
    int numFullCycles = year / CYCLE_LEN_YEARS;  // N, integer division!
    int fullCycles = numFullCycles * CYCLE_LEN_DAYS; // N * 1461  
    
    //2. remainder-years, 0..3 complete years left after the cycles, but excluding the final year
    int startYr = numFullCycles * CYCLE_LEN_YEARS;
    int remainderYears = JULIAN.daysInCompleteYears(startYr, year); //excluded end!
    
    //3. remainder-days in the final-year
    double remainderDays = JULIAN.daysFromJan0(year, month, day);
    return JULIAN_BASE + fullCycles + remainderYears + remainderDays;
  }
  
  /**
   Calculation for negative years.
   
   <P>For years -1,-2,... the cycles have this pattern:
    <pre>
            .. *  L*********  L*********  cycles start with a leap year
    y       ..-9 -8 -7 -6 -5 -4 -3 -2 -1    
    y + 1   ..-8 -7 -6 -5 -4 -3 -2 -1  0  
    (y+1)/4 ..-2 -1 -1 -1 -1  0  0  0  0  integer division
    </pre>
   */
  private static double negativeYears(int year, int month, double day) {
    //The zero point is for Dec 31 in the year -1. 
    //In the negative years, it's convenient to use (year + 1) for some items, per the diagram above.
    
    //1. full cycles
    //here, cycles start END with a leap year and have a fixed length in days; the years in a cycle share the same value of (year+1/4)
    int numFullCycles = (year + 1) / CYCLE_LEN_YEARS;  // N, integer division!
    int fullCycles = Math.abs(numFullCycles) * CYCLE_LEN_DAYS; // N * 1461  
    
    //2. remainder-years, 0..3 complete years left after the cycles, but excluding the final year
    int startYr = year + 1;  // avoid touching the final-year
    int endYr = numFullCycles * CYCLE_LEN_YEARS; 
    int remainderYears = JULIAN.daysInCompleteYears(startYr, endYr); //excluded end!
    
    //3. remainder-days in the final-year (counting backwards from year-end)
    double remainderDays = daysFromDec32(month, day, JULIAN.isLeap(year));
    int OVERHANG = 1; // Jan 0.0 is already impinging onto the negative years, by 1 day
    return JULIAN_BASE + OVERHANG - (fullCycles + remainderYears + remainderDays);
  }
  
  /** The number of days remaining in the given month, from the given day. */
  private static double daysRemainingInMonth(int month, double day, boolean isLeap) {
    int length = Month.of(month).length(isLeap);
    return (length + 1) - day;
  }

  /** 
   For years greater than or equal to 0.
    
   <P>Account for:
   <pre> 
     1) full cycles of N*4 years
     2) full remainder-years, and then 
     3) the final year
   </pre>     
   <P>Calculate as an offset from a convenient base that corresponds to the start of a complete 4-year Julian cycle. 
  */
  private static DateTime dateTimeNonNegYears(JulianDate jd) {
    double BASE = JULIAN_BASE + 1; //0h Jan 1 year 0; Explanatory Supplement 1961, p437, table 14.14

    //1) full cycles (4 years in the Julian calendar)
    double target = jd.jd() - BASE; //the target value we'll match below
    int numFullCycles = (int)Mathy.truncate(target / CYCLE_LEN_DAYS); //int division
    int year = numFullCycles * CYCLE_LEN_YEARS; //starting value for the year; can increase below
    
    //this temp value is less than the target value, and approaches it from below
    int temp_target = numFullCycles * CYCLE_LEN_DAYS; 

    //2) full 'remainder years' after the full cycles (but not including the final year)   
    int year_full_cycles = year; //simply to remember this value in the loop below 
    for(int remainderYearIdx = 0; remainderYearIdx < CYCLE_LEN_YEARS; ++remainderYearIdx ) {
      int oneMoreYear = JULIAN.numDaysIn(year_full_cycles + remainderYearIdx);
      if (temp_target + oneMoreYear <= target) {
        temp_target = temp_target + oneMoreYear;
        ++year;
      } else { break; }
    }
     
    //3) months and days in the final year
    int month = Month.JANUARY.getValue(); //starting point; can increase below
    for(Month m : Month.values()) {
      int oneMoreMonth = m.length(JULIAN.isLeap(year));
      if (temp_target + oneMoreMonth <= target) {
        temp_target = temp_target + oneMoreMonth;
        ++month;
      } else { break; }
    }
    double fractionalDays = target - temp_target + 1; //+1 since the base is Jan 1 0h, not Dec 31 0h
    return buildDateTimeFrom(year, month, fractionalDays, jd);
  }
  
  /** For year -1 and previous. */
  private static DateTime dateTimeNegYears(JulianDate jd) {
    double BASE = JULIAN_BASE + 1; //0h Jan 1 year 0; Explanatory Supplement 1961, p437, table 14.14

    //1) full cycles (4 years in the Julian calendar)
    double target = jd.jd() - BASE; //the target value we'll match below
    int numFullCycles = (int)Mathy.truncate(target / CYCLE_LEN_DAYS); //int division
    int year = numFullCycles * CYCLE_LEN_YEARS; //starting value for the year; can decrease below
    --year;  //because going backwards through the calendar
    
    //this temp value is more than the target value, and approaches it from above
    int temp_target = numFullCycles * CYCLE_LEN_DAYS; 

    //2) full 'remainder years' after the full cycles (but not including the final year)   
    int year_full_cycles = year; //simply to remember this value in the loop below 
    for(int remainderYearIdx = 0; remainderYearIdx < CYCLE_LEN_YEARS; ++remainderYearIdx ) {
      int oneLessYear = JULIAN.numDaysIn(year_full_cycles - remainderYearIdx);
      if (temp_target - oneLessYear > target) { //not >= here, because going backwards
        temp_target = temp_target - oneLessYear;
        --year;
      } else { break; }
    }
     
    //3) months and days in the final year
    int month = Month.DECEMBER.getValue(); //starting point; can decrease below
    for(Month m : Arrays.asList(Month.values()).reversed()) { //go backwards, Dec to Jan!
      int oneLessMonth = m.length(JULIAN.isLeap(year));
      if (temp_target - oneLessMonth > target) {
        temp_target = temp_target - oneLessMonth;
        --month;
      } else { break; }
    }
    //count backwards from the end of the month
    int monthLen = Month.of(month).length(JULIAN.isLeap(year));
    double fractionalDays = (monthLen + 1) + (target - temp_target);  //32 + (-0.5) = 31.5 for a time on Dec 31, for example 
    return buildDateTimeFrom(year, month, fractionalDays, jd);
  }

  private static DateTime buildDateTimeFrom(int year, int month, double fractionalDays, JulianDate jd) {
    int day = (int)Mathy.truncate(fractionalDays);
    Date date = Date.julian(year, month, day);
    DateTime midnight = DateTime.julian(year, month, day, 0, 0, 0.0, jd.timescale());
    int numSecondsInDay = midnight.secondsInDay(); //this lets us interpret the fractional day in the case of UTC
    double frac = fractionalDays - Mathy.truncate(fractionalDays);
    Time time = Time.from(frac, numSecondsInDay, jd.timescale());
    return DateTime.dateTime(date, time);
  }
  
  public static void main(String[] args) {
    DateTime dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE, Timescale.TT));
    System.out.println(dt);

    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 1, Timescale.TT));
    System.out.println(dt);

    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 45, Timescale.TT));
    System.out.println(dt);
    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 179, Timescale.TT));
    System.out.println(dt);
    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 179.5, Timescale.TT));
    System.out.println(dt);
    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 179.90, Timescale.TT));
    System.out.println(dt);
    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 180, Timescale.TT));
    System.out.println(dt);

    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 180, Timescale.TT));
    System.out.println(dt);
    
    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 364, Timescale.TT));
    System.out.println(dt);
    
    for(int i = 0; i < 360; ++i) {
      dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 364 + i, Timescale.TT));
      System.out.println(" testing loop " + dt);
    }
    
    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 365, Timescale.TT));
    System.out.println(dt);

    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 2*365, Timescale.TT));
    System.out.println(dt);
    
    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 3*365, Timescale.TT));
    System.out.println(dt);
    
    dt = JdAndJulianCalendar.dateTime(JulianDate.from(JULIAN_BASE - 3*365 - 366, Timescale.TT));
    System.out.println(dt);
    
  }
}