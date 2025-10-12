package astrolib.when;

import static astrolib.when.Calendar.*;

import java.time.Month;

import astrolib.util.Mathy;

/** 
 Convert a date from the Gregorian proleptic calendar into a Julian date, and vice versa.
 There are no restrictions on the input date.

 <P>For this class, there are no restrictions on the input/output date.
 (All astronomical libraries that I've seen restrict range of the year in some way. This class does not share that defect.)
 
 <P>See {@link JdAndJulianCalendar} for important information, which is not repeated here. 
 The implementation of that class is simpler and easier to understand.
*/
final class JdAndGregorianCalendar {
  
  /*
   Description of the implementation:
   The Gregorian calendar has a full cycle lasting 400 years, and is not as simple as the Julian calendar.
  
   Starting from year 0, separate into 3 blocks of time, from largest to smallest: 
    - 1. big cycles: full Gregorian cycles of [0..N] * 400 years
    - 2. small cycles: full cycles of 4 years each 
    - 3. [0..3] complete remainder-years
    - 4. remainder-days of the final year, 1..366
    
   Then just add the three parts.
       
   When the year is negative, the above logic is similar, but modified in order to 
   count backwards through the calendar.
  
   The year 0 is a leap year in both the Julian calendar and the Gregorian calendar.
 */

  /** 
   Return the Gregorian date corresponding to the given date-time in the Gregorian calendar.
   @param dt must be attached to the Gregorian calendar.
  */
  static double jd(DateTime dt) {
    if (GREGORIAN != dt.date().calendar() ) {
      throw new IllegalArgumentException("The supplied date-time is not attached to the Gregorian calendar.");
    }
    return jd(dt.year(), dt.month(), dt.fractionalDay());
  }
  
  /** 
   Return the Julian date corresponding to the given moment (fractional day!) in the Gregorian calendar.
   Passing '15.5', for the day corresponds to 12h, for example.  
  */
  static double jd(int y, int m, double d) {
    double result = 0.0;
    int sign = y < 0 ? -1 : 1;
    /* 
      Chop into blocks of years, from largest to smallest, and count the days in each block.
      There is asymmetry between + and - years; they aren't handled in the same way.
      There are 4 parts to consider:
        1. big cycles (complete) - N * 400 years 
        2. small cycles (complete) - M * 4 years 
        3. remainder years - complete (0..3)
        4. remainder days - in the last year 1..366
     */
    if (sign > 0) {
      result = nonNegativeYears(y, m, d);
    }
    else {
      result = negativeYears(y, m, d);
    }
    return result;
  }
  
  /** Return the corresponding date-time in the Gregorian calendar, with the same timescale as the given jd. */
  public static DateTime dateTime(JulianDate jd) {
    double JAN_1_YEAR_0 = GREGORIAN_BASE + 1; //0h Jan 1.0 year 0
    return jd.jd() >= JAN_1_YEAR_0 ? dateTimeNonNegYears(jd) : dateTimeNegYears(jd);
  }
  
  /**
   The Julian date as of Jan 0.0 (midnight) in the year 0, according to the Gregorian calendar (not the Julian).
   At this time, the the Gregorian calendar was 2 days behind the Julian calendar (Explanatory Supplement 1961, p417).
   
   <P>Treat this as the basis. Calculate the number of days +/- from this date. Value - {@value}.
   
   <P>"The year 0 is a leap year in the Gregorian proleptic calendar." Explanatory Supplement 1961, p416.
  */
  public static final double GREGORIAN_BASE = 1_721_058.5;

  private static final int SMALL_CYCLE_DAYS = JdAndJulianCalendar.CYCLE_LEN_DAYS; //1461
  private static final int SMALL_CYCLE_YRS = JdAndJulianCalendar.CYCLE_LEN_YEARS; //4
  
  private static final int LONG_CENTURY = 25*SMALL_CYCLE_DAYS; // 36525
  private static final int SHORT_CENTURY = 24*SMALL_CYCLE_DAYS + 4*Calendar.NORMAL_YEAR_NUM_DAYS; // 36524 
  private static final int BIG_CYCLE_DAYS = 3*SHORT_CENTURY + 1*LONG_CENTURY; // 146097
  private static final int BIG_CYCLE_YRS = 400;

  private static double nonNegativeYears(int year, int month, double day) {
    //1. big cycles - no need to track the exact years because their length is fixed 
    int numBig = year / BIG_CYCLE_YRS;  //integer division!
    int bigCycles = numBig * BIG_CYCLE_DAYS;
    
    //2. small cycles - track the exact years (variable numbers of leap years in the block)
    int bigRemainder = year % BIG_CYCLE_YRS; //0..399
    int numSmall = bigRemainder / SMALL_CYCLE_YRS; //0..99, integer division
    int startYrSmall = numBig * BIG_CYCLE_YRS;
    int endYrSmall = startYrSmall + numSmall * SMALL_CYCLE_YRS;
    int smallCycles = GREGORIAN.daysInCompleteYears(startYrSmall, endYrSmall); //excluded end
    
    //3. remainder years - whole years left after the small cycles
    int remainderYears = GREGORIAN.daysInCompleteYears(endYrSmall, year); //excluded end
    
    //4. remainder days in the final year
    double remainderDays = GREGORIAN.daysFromJan0(year, month, day);
    return GREGORIAN_BASE + bigCycles + smallCycles + remainderYears + remainderDays; 
  }
  
  private static double negativeYears(int year, int month, double day) {
    //The zero point is for Dec 31 in the year -1. 
    //In the negative years, it's convenient to use (year + 1) as the base from which to track cycles.
    int y_biased = year + 1;

    //1. big cycles - no need to track the exact years because their length is fixed 
    int numBig = Math.abs((y_biased) / BIG_CYCLE_YRS);  //integer division!
    int bigCycles = numBig * BIG_CYCLE_DAYS;
    
    //2. small cycles - track the exact years (variable numbers of leap years in this block)
    int bigRemainder = Math.abs(y_biased) % BIG_CYCLE_YRS; //0..399
    int numSmall = bigRemainder / SMALL_CYCLE_YRS; //0..99, integer division
    int endYrSmall = -numBig * BIG_CYCLE_YRS;
    int startYrSmall = endYrSmall - numSmall * SMALL_CYCLE_YRS;
    int smallCycles = GREGORIAN.daysInCompleteYears(startYrSmall, endYrSmall); //excluded end
    
    //3. remainder years - 0..3 whole years left after the small cycles
    int start = y_biased;
    int end = startYrSmall; 
    int remainderYears = GREGORIAN.daysInCompleteYears(start, end); //excluded endpoint
    
    //4. remainder days in the first year
    double remainderDays = JdAndJulianCalendar.daysFromDec32(month, day, GREGORIAN.isLeap(year));
    
    int OVERHANG = 1; // Jan 0.0 is already impinging onto the negative years, by 1 day
    return GREGORIAN_BASE + OVERHANG - (bigCycles + smallCycles + remainderYears + remainderDays);
  }
  
  private static DateTime dateTimeNonNegYears(JulianDate jd) {
    double BASE = GREGORIAN_BASE + 1; //0h Jan 1.0 year 0; Explanatory Supplement 1961, p437, table 14.14
    
    //1) big cycles of 400 years
    double target = jd.jd() - BASE; //the target value we'll match below
    int numBigCycles = (int)Mathy.truncate(target / BIG_CYCLE_DAYS); //int division
    int year = numBigCycles * BIG_CYCLE_YRS; //starting value for the year; can increase below
    
    //this temp value is less than the target value, and approaches it from below
    int temp_target = numBigCycles * BIG_CYCLE_DAYS; 
    
    //2) small cycles of 4 years
    int numSmallCycles = (int)Mathy.truncate((target - temp_target)/SMALL_CYCLE_DAYS);
    temp_target = temp_target + numSmallCycles * SMALL_CYCLE_DAYS;
    year = year + numSmallCycles * SMALL_CYCLE_YRS;
    
    //3) full remainder-years
    int year_full_cycles = year; //simply to remember this value in the loop below 
    for(int remainderYearIdx = 0; remainderYearIdx < SMALL_CYCLE_YRS; ++remainderYearIdx ) {
      int oneMoreYear = GREGORIAN.numDaysIn(year_full_cycles + remainderYearIdx);
      if (temp_target + oneMoreYear <= target) {
        temp_target = temp_target + oneMoreYear;
        ++year;
      } else { break; }
    }

    //4) months and days in the final year
    int month = Month.JANUARY.getValue(); //starting point; can increase below
    for(Month m : Month.values()) {
      int oneMoreMonth = m.length(GREGORIAN.isLeap(year));
      if (temp_target + oneMoreMonth <= target) {
        temp_target = temp_target + oneMoreMonth;
        ++month;
      } else { break; }
    }
    double fractionalDays = target - temp_target + 1; //+1 since the base is Jan 1 0h, not Dec 31 0h
    return buildDateTimeFrom(year, month, fractionalDays, jd);
  }
  
  private static DateTime dateTimeNegYears(JulianDate jd) {
    return null;
  }
  
  /** CODE REPETITION. ALSO IN THE OTHER CLASS, except for one line. */
  private static DateTime buildDateTimeFrom(int year, int month, double fractionalDays, JulianDate jd) {
    int day = (int)Mathy.truncate(fractionalDays);
    Date date = Date.gregorian(year, month, day);
    DateTime midnight = DateTime.julian(year, month, day, 0, 0, 0.0, jd.timescale());
    int numSecondsInDay = midnight.secondsInDay(); //this lets us interpret the fractional day in the case of UTC
    double frac = fractionalDays - Mathy.truncate(fractionalDays);
    Time time = Time.from(frac, numSecondsInDay, jd.timescale());
    return DateTime.dateTime(date, time);
  }
  
  
}
