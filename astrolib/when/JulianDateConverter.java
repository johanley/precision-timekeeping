package astrolib.when;

import java.time.Month;
import java.util.Arrays;

import astrolib.util.Mathy;

/** 
 Convert a date-time in a given Calendar into a {@link JulianDate}, and vice versa.

 <P>Here, there is no restriction on the input/output year.
 (Most astronomical libraries implement this conversion by restricting the range of the year in some way.) 
*/
final class JulianDateConverter {

  static JulianDateConverter using(Calendar calendar) {
    return new JulianDateConverter(calendar);
  }
  
  /** 
   Return the Julian date corresponding to the given date-time in the calendar.
   @param dt must be attached to the given calendar.
  */
  JulianDate toJulianDate(DateTime dt) {
    if (calendar != dt.date().calendar() ) {
      throw new IllegalArgumentException("The supplied date-time is for the " + dt.date().calendar() + " calendar. Expecting " + calendar);
    }
    return toJulianDate(dt.year(), dt.month(), dt.fractionalDay(), dt.time().timescale());
  }
  
  /** 
   Return the Julian date corresponding to the given moment in the calendar.
   @param d is a fractional day. The value '15.5' corresponds to 12h, for example.  
  */
  JulianDate toJulianDate(int y, int m, double d, Timescale timescale) {
    double jd = y >= 0 ? nonNegYears(y, m, d) : negYears(y, m, d);
    return JulianDate.from(jd, timescale);
  }
  
  /** Return the corresponding date-time in the given calendar, with the same timescale as the given Julian date. */
  public DateTime toDateTime(JulianDate jd) {
    double jan_1_year_0 = jan_0_year_0 + 1; 
    return jd.jd() >= jan_1_year_0 ? nonNegYears(jd) : negYears(jd);
  }
  
  private double jan_0_year_0;
  private Calendar calendar;

  /** A converter for the given calendar. */
  private JulianDateConverter(Calendar calendar){
    this.jan_0_year_0 = calendar.julianDateJan0Year0();
    this.calendar = calendar;
  }
  
  private double nonNegYears(int year, int month, double day) {
    //1. full cycles in the calendar  
    int numCycles = year / calendar.cycleYears();
    int fullCycles = numCycles * calendar.cycleDays();
    
    //2. remainder-years: whole years left after the full cycles
    int remainderYears = calendar.daysInCompleteYears(numCycles * calendar.cycleYears(), year); 
    
    //3. remainder-days in the final year
    double remainderDays = calendar.daysFromJan0(year, month, day);
    return jan_0_year_0 + fullCycles + remainderYears + remainderDays; 
  }
  
  private double negYears(int year, int month, double day) {
    //In the negative years, it's convenient to use (year + 1) as the base from which to track cycles.
    //This is because we're counting backwards through the calendar
    int y_biased = year + 1;

    //1. full cycles in the calendar  
    int numCycles = y_biased / calendar.cycleYears(); 
    int fullCycles = Math.abs(numCycles * calendar.cycleDays());
    
    //2. remainder years: whole years left after the full cycles
    int remainderYears = calendar.daysInCompleteYears(y_biased, numCycles * calendar.cycleYears()); 
    
    //3. remainder days in the final year
    double remainderDays = calendar.daysFromDec32(year, month, day);
    int OVERHANG = 1; // Jan 0.0 is already impinging onto the negative years, by 1 day
    return jan_0_year_0 + OVERHANG - (fullCycles +  remainderYears + remainderDays);
  }
  
  private DateTime nonNegYears(JulianDate jd) {
    double BASE = jan_0_year_0 + 1; 
    
    //1. full cycles in the calendar  
    double target = jd.jd() - BASE; //the target value we'll match below
    int numCycles = Mathy.truncate(target / calendar.cycleDays()); 
    int year = numCycles * calendar.cycleYears(); //starting value for the year; can increase below
    
    //this temp value is less than the target value, and approaches it from below
    int temp_target = numCycles * calendar.cycleDays(); 
    
    //2. remainder years: whole years left after the full cycles (not including the final year)
    int year_full_cycles = year; //simply to remember this value in the loop below 
    for(int remainderYearIdx = 0; remainderYearIdx < calendar.cycleYears(); ++remainderYearIdx ) {
      int oneMoreYear = calendar.numDaysIn(year_full_cycles + remainderYearIdx);
      if (temp_target + oneMoreYear <= target) {
        temp_target = temp_target + oneMoreYear;
        ++year;
      } else { break; }
    }

    //3. months and days in the final year
    int month = Month.JANUARY.getValue(); //starting point; can increase below
    for(Month m : Month.values()) {
      int oneMoreMonth = m.length(calendar.isLeap(year));
      if (temp_target + oneMoreMonth <= target) {
        temp_target = temp_target + oneMoreMonth;
        ++month;
      } else { break; }
    }
    double fractionalDays = target - temp_target + 1; //+1 since the base is Jan 1 0h, not Dec 31 0h
    return buildDateTimeFrom(year, month, fractionalDays, jd);
  }
  
  private DateTime negYears(JulianDate jd) {
    double BASE = jan_0_year_0 + 1; 

    //1. full cycles in the calendar  
    double target = jd.jd() - BASE; //the target value we'll match below
    int numFullCycles = Mathy.truncate(target / calendar.cycleDays()); 
    int year = numFullCycles * calendar.cycleYears(); //starting value for the year; can decrease below
    --year; //because going backwards through the calendar
    
    //this temp value is more than the target value, and approaches it from above
    int temp_target = numFullCycles * calendar.cycleDays(); 

    //2. remainder years: whole years left after the full cycles (not including the final year)
    int year_full_cycles = year; //simply to remember this value in the loop below 
    for(int remainderYearIdx = 0; remainderYearIdx < calendar.cycleYears(); ++remainderYearIdx ) {
      int oneLessYear = calendar.numDaysIn(year_full_cycles - remainderYearIdx);
      if (temp_target - oneLessYear > target) {
        temp_target = temp_target - oneLessYear;
        --year;
      } else { break; }
    }
     
    //3. months and days in the final year
    int month = Month.DECEMBER.getValue(); //starting point; can decrease below
    for(Month m : Arrays.asList(Month.values()).reversed()) { //go backwards, Dec to Jan!
      int oneLessMonth = m.length(calendar.isLeap(year));
      if (temp_target - oneLessMonth > target) {
        temp_target = temp_target - oneLessMonth;
        --month;
      } else { break; }
    }
    //count backwards from the end of the month
    int monthLen = Month.of(month).length(calendar.isLeap(year));
    double fractionalDays = (monthLen + 1) + (target - temp_target);  //32 + (-0.5) = 31.5 for a time on Dec 31, for example 
    return buildDateTimeFrom(year, month, fractionalDays, jd);
  }
  
  private DateTime buildDateTimeFrom(int year, int month, double fractionalDays, JulianDate jd) {
    int day = Mathy.truncate(fractionalDays);
    Date date = Date.from(year, month, day, calendar);
    Time midnightTime = Time.from(0,0,0.0, jd.timescale());
    DateTime midnight = DateTime.from(date, midnightTime);
    //TODO DO I REALLY WANT TO DEAL WITH LEAPSECONDS??
    int numSecondsInDay = midnight.secondsInDay(); //this lets us interpret the fractional day in the case of UTC
    double frac = fractionalDays - Mathy.truncate(fractionalDays);
    Time time = Time.from(frac, numSecondsInDay, jd.timescale());
    return DateTime.from(date, time);
  }
}