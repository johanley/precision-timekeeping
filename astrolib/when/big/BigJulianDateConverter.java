package astrolib.when.big;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Arrays;

/** 
 Convert a date-time in a given Calendar into a {@link BigJulianDate}, and vice versa.

 <P>Here, there is no restriction on the input/output year.
 (Most astronomical libraries implement this conversion by restricting the range of the year in some way.) 
*/
final class BigJulianDateConverter {

  static BigJulianDateConverter using(BigCalendar calendar) {
    return new BigJulianDateConverter(calendar);
  }
  
  /** 
   Return the Julian date corresponding to the given date-time in the calendar.
   @param dt must be attached to the given calendar.
  */
  BigJulianDate toJulianDate(BigDateTime dt) {
    if (calendar != dt.date().calendar() ) {
      throw new IllegalArgumentException("The supplied date-time is for the " + dt.date().calendar() + " calendar. Expecting " + calendar);
    }
    return toJulianDate(dt.year(), dt.month(), dt.fractionalDay(), dt.time().timescale());
  }
  
  /** 
   Return the Julian date corresponding to the given moment in the calendar.
   @param d is a fractional day. The value '15.5' corresponds to 12h, for example.  
  */
  BigJulianDate toJulianDate(long y, int m, BigDecimal d, BigTimescale timescale) {
    BigDecimal jd =  (y >= 0)  ?   nonNegYears(y, m, d)  :   negYears(y, m, d);
    return BigJulianDate.from(jd, timescale);
  }
  
  /** Return the corresponding date-time in the given calendar, with the same timescale as the given Julian date. */
  public BigDateTime toDateTime(BigJulianDate jd) {
    BigDecimal jan_1_year_0 = jan_0_year_0.add(BigDecimal.ONE); 
    return jd.jd().compareTo(jan_1_year_0) >= 0 ? nonNegYears(jd) : negYears(jd);
  }
  
  private BigDecimal jan_0_year_0;
  private BigCalendar calendar;

  /** A converter for the given calendar. */
  private BigJulianDateConverter(BigCalendar calendar){
    this.jan_0_year_0 = calendar.julianDateJan0Year0();
    this.calendar = calendar;
  }
  
  private BigDecimal nonNegYears(long year, int month, BigDecimal day) {
    //1. full cycles in the calendar  
    long numCycles = year / calendar.fullCycleYears();
    long fullCycles = numCycles * calendar.fullCycleDays();
    
    //2. remainder-years: whole years left after the full cycles
    int remainderYears = calendar.daysInCompleteYears(numCycles * calendar.fullCycleYears(), year); 
    
    //3. remainder-days in the final year
    BigDecimal remainderDays = calendar.daysFromJan0(year, month, day);
    return jan_0_year_0.add(BigDecimal.valueOf(fullCycles).add(BigDecimal.valueOf(remainderYears)).add(remainderDays)); 
  }
  
  private BigDecimal negYears(long year, int month, BigDecimal day) {
    //In the negative years, it's convenient to use (year + 1) as the base from which to track cycles.
    //This is because we're counting backwards through the calendar
    long y_biased = year + 1;

    //1. full cycles in the calendar  
    long numCycles = y_biased / calendar.fullCycleYears(); 
    long fullCycles = Math.abs(numCycles * calendar.fullCycleDays());
    
    //2. remainder years: whole years left after the full cycles
    int remainderYears = calendar.daysInCompleteYears(y_biased, numCycles * calendar.fullCycleYears()); 
    
    //3. remainder days in the final year
    BigDecimal remainderDays = calendar.daysFromDec32(year, month, day);
    BigDecimal OVERHANG = BigDecimal.ONE; // Jan 0.0 is already impinging onto the negative years, by 1 day
    BigDecimal total = BigDecimal.valueOf(fullCycles).add(BigDecimal.valueOf(remainderYears)).add(remainderDays);
    return jan_0_year_0.add(OVERHANG).subtract(total);
  }
  
  private BigDateTime nonNegYears(BigJulianDate jd) {
    BigDecimal BASE = jan_0_year_0.add(BigDecimal.ONE); 
    
    //1. full cycles in the calendar  
    BigDecimal target = jd.jd().subtract(BASE); //the target value we'll match below
    BigDecimal fullCycleDays = BigDecimal.valueOf(calendar.fullCycleDays());
    long numCycles = target.divideAndRemainder(fullCycleDays)[0].longValue(); 
    long year = numCycles * calendar.fullCycleYears(); //starting value for the year; can increase below
    
    //this temp value is less than the target value, and approaches it from below
    BigDecimal temp_target = BigDecimal.valueOf(numCycles * calendar.fullCycleDays()); 
    
    //2. remainder years: whole years left after the full cycles (not including the final year)
    long year_full_cycles = year; //simply to remember this value in the loop below 
    for(int remainderYearIdx = 0; remainderYearIdx < calendar.fullCycleYears(); ++remainderYearIdx ) {
      BigDecimal oneMoreYear = BigDecimal.valueOf(calendar.numDaysIn(year_full_cycles + remainderYearIdx));
      if (temp_target.add(oneMoreYear).compareTo(target) <= 0) {
        temp_target = temp_target.add(oneMoreYear);
        ++year;
      } else { break; }
    }

    //3. months and days in the final year
    int month = Month.JANUARY.getValue(); //starting point; can increase below
    for(Month m : Month.values()) {
      BigDecimal oneMoreMonth = BigDecimal.valueOf(m.length(calendar.isLeap(year)));
      if (temp_target.add(oneMoreMonth).compareTo(target) <= 0) {
        temp_target = temp_target.add(oneMoreMonth);
        ++month;
      } else { break; }
    }
    BigDecimal fractionalDays = target.subtract(temp_target).add(BigDecimal.ONE); //+1 since the base is Jan 1 0h, not Dec 31 0h
    return buildDateTimeFrom(year, month, fractionalDays, jd);
  }
  
  private BigDateTime negYears(BigJulianDate jd) {
    BigDecimal BASE = jan_0_year_0.add(BigDecimal.ONE); 

    //1. full cycles in the calendar  
    BigDecimal target = jd.jd().subtract(BASE); //the target value we'll match below
    BigDecimal fullCycleDays = BigDecimal.valueOf(calendar.fullCycleDays());
    long numFullCycles = target.divideAndRemainder(fullCycleDays)[0].longValue(); 
    long year = numFullCycles * calendar.fullCycleYears(); //starting value for the year; can decrease below
    --year; //because going backwards through the calendar
    
    //this temp value is more than the target value, and approaches it from above
    BigDecimal temp_target = BigDecimal.valueOf(numFullCycles * calendar.fullCycleDays()); 

    //2. remainder years: whole years left after the full cycles (not including the final year)
    long year_full_cycles = year; //simply to remember this value in the loop below 
    for(int remainderYearIdx = 0; remainderYearIdx < calendar.fullCycleYears(); ++remainderYearIdx ) {
      BigDecimal oneLessYear = BigDecimal.valueOf(calendar.numDaysIn(year_full_cycles - remainderYearIdx));
      if (temp_target.subtract(oneLessYear).compareTo(target) > 0) {
        temp_target = temp_target.subtract(oneLessYear);
        --year;
      } else { break; }
    }
     
    //3. months and days in the final year
    int month = Month.DECEMBER.getValue(); //starting point; can decrease below
    for(Month m : Arrays.asList(Month.values()).reversed()) { //go backwards, Dec to Jan!
      BigDecimal oneLessMonth = BigDecimal.valueOf(m.length(calendar.isLeap(year)));
      if (temp_target.subtract(oneLessMonth).compareTo(target) > 0) {
        temp_target = temp_target.subtract(oneLessMonth);
        --month;
      } else { break; }
    }
    //count backwards from the end of the month
    int monthLen = Month.of(month).length(calendar.isLeap(year));
    BigDecimal fractionalDays = BigDecimal.valueOf(monthLen + 1).add(target.subtract(temp_target));  //32 + (-0.5) = 31.5 for a time on Dec 31, for example 
    return buildDateTimeFrom(year, month, fractionalDays, jd);
  }
  
  private BigDateTime buildDateTimeFrom(long year, int month, BigDecimal fractionalDays, BigJulianDate jd) {
    int day = fractionalDays.intValue();
    BigDate date = BigDate.from(year, month, day, calendar);
    BigDecimal frac = fractionalDays.divideAndRemainder(BigDecimal.ONE)[1];
    BigTime time = BigTime.from(frac, jd.timescale());
    return BigDateTime.from(date, time);
  }
}