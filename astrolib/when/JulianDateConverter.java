package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Month;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/** 
 Convert a {@link DateTime} with a given {@link Calendar} into a {@link JulianDate}, and vice versa.

 <P>Here, there's no restriction on the input/output year.
 Most astronomical libraries implement this conversion by restricting the range of the year (and the Julian date) in some way.
 Usually, they restrict the date such that the Julian date is greater than or equal to 0. 
*/
final class JulianDateConverter {

  /** Factory method for a convert for the given {@link Calendar}. */
  static JulianDateConverter using(Calendar calendar) {
    return new JulianDateConverter(calendar);
  }
  
  /** 
   Return the Julian date corresponding to the given {@link DateTime} in the calendar.
   @param dt must be attached to the {@link Calendar} passed to the factory method.
  */
  JulianDate toJulianDate(DateTime dt) {
    if (calendar != dt.date().calendar() ) {
      throw new IllegalArgumentException("The supplied date-time is for the " + dt.date().calendar() + " calendar. Expecting " + calendar);
    }
    return toJulianDate(dt.year(), dt.month(), dt.fractionalDay(), dt.time().timescale());
  }
  
  /** 
   Return the {@JulianDate} corresponding to the given moment in the {@link Calendar} passed to the factory method..
   @param d is a fractional day. The value '15.5' corresponds to 12h on the 15th of the month, for example.  
  */
  JulianDate toJulianDate(long y, int m, BigDecimal d, Timescale timescale) {
    BigDecimal jd = cal_to_jd(y, m, d);
    return JulianDate.from(jd, timescale);
  }
  
  /** 
   Return the corresponding {@link DateTime} in {@link Calendar} passed to the factory method, 
   with the same {@link Timescale} as the given {@link JulianDate}. 
  */
  public DateTime toDateTime(JulianDate jd) {
    return jd_to_cal(jd);
  }
  
  private BigDecimal jan_0_year_0;
  private Calendar calendar;

  /** A converter for the given calendar. */
  private JulianDateConverter(Calendar calendar){
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
    return jan_0_year_0.add(big(fullCycles).add(big(remainderYears)).add(remainderDays)); 
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
    BigDecimal total = big(fullCycles).add(big(remainderYears)).add(remainderDays);
    return jan_0_year_0.add(OVERHANG).subtract(total);
  }
  
  
  private DateTime nonNegYears(JulianDate jd) {
    BigDecimal BASE = jan_0_year_0.add(BigDecimal.ONE); 
    
    //1. full cycles in the calendar  
    BigDecimal target = jd.jd().subtract(BASE); //the target value we'll match below
    BigDecimal fullCycleDays = big(calendar.fullCycleDays());
    long numFullCycles = floor(divide(target, fullCycleDays)).longValue();
    long year = numFullCycles * calendar.fullCycleYears(); //starting value for the year; can increase below
    
    //this temp value is less than the target value, and approaches it from below
    BigDecimal temp_target = big(numFullCycles * calendar.fullCycleDays()); 
    
    //2. remainder years: whole years left after the full cycles (not including the final year)
    long year_full_cycles = year; //simply to remember this value in the loop below 
    for(int remainderYearIdx = 0; remainderYearIdx < calendar.fullCycleYears(); ++remainderYearIdx ) {
      BigDecimal oneMoreYear = big(calendar.numDaysIn(year_full_cycles + remainderYearIdx));
      if (temp_target.add(oneMoreYear).compareTo(target) <= 0) {
        temp_target = temp_target.add(oneMoreYear);
        ++year;
      } else { break; }
    }

    //3. months and days in the final year
    int month = Month.JANUARY.getValue(); //starting point; can increase below
    for(Month m : Month.values()) {
      BigDecimal oneMoreMonth = big(m.length(calendar.isLeap(year)));
      if (temp_target.add(oneMoreMonth).compareTo(target) <= 0) {
        temp_target = temp_target.add(oneMoreMonth);
        ++month;
      } else { break; }
    }
    BigDecimal fractionalDays = target.subtract(temp_target).add(BigDecimal.ONE); //+1 since the base is Jan 1 0h, not Dec 31 0h
    return buildDateTimeFrom(year, month, fractionalDays, jd);
  }
  
  private DateTime negYears(JulianDate jd) {
    BigDecimal BASE = jan_0_year_0.add(BigDecimal.ONE); 

    //1. full cycles in the calendar  
    BigDecimal target = jd.jd().subtract(BASE); //the target value we'll match below
    BigDecimal fullCycleDays = big(calendar.fullCycleDays());
    long numFullCycles = floor(divide(target, fullCycleDays)).longValue() + 1 ;
    long year = numFullCycles * calendar.fullCycleYears(); //starting value for the year; can decrease below
    --year; //because going backwards through the calendar
    
    //this temp value is more than the target value, and approaches it from above
    BigDecimal temp_target = big(numFullCycles * calendar.fullCycleDays()); 

    //2. remainder years: whole years left after the full cycles (not including the final year)
    long year_full_cycles = year; //simply to remember this value in the loop below 
    for(int remainderYearIdx = 0; remainderYearIdx < calendar.fullCycleYears(); ++remainderYearIdx ) {
      BigDecimal oneLessYear = big(calendar.numDaysIn(year_full_cycles - remainderYearIdx));
      if (temp_target.subtract(oneLessYear).compareTo(target) > 0) {
        temp_target = temp_target.subtract(oneLessYear);
        --year;
      } else { break; }
    }
     
    //3. months and days in the final year
    int month = Month.DECEMBER.getValue(); //starting point; can decrease below
    for(Month m : Arrays.asList(Month.values()).reversed()) { //go backwards, Dec to Jan!
      BigDecimal oneLessMonth = big(m.length(calendar.isLeap(year)));
      if (temp_target.subtract(oneLessMonth).compareTo(target) > 0) {
        temp_target = temp_target.subtract(oneLessMonth);
        --month;
      } else { break; }
    }
    //count backwards from the end of the month
    int monthLen = Month.of(month).length(calendar.isLeap(year));
    //double fractionalDays = (monthLen + 1) + (target - temp_target);  //32 + (-0.5) = 31.5 for a time on Dec 31, for example 
    BigDecimal fractionalDays = big(monthLen + 1).add(target.subtract(temp_target));  //32 + (-0.5) = 31.5 for a time on Dec 31, for example 
    return buildDateTimeFrom(year, month, fractionalDays, jd);
  }
  
  private DateTime buildDateTimeFrom(long year, int month, BigDecimal fractionalDays, JulianDate jd) {
    BigInteger day = integer(fractionalDays);
    Date date = Date.from(year, month, day.intValue(), calendar);
    BigDecimal frac = divideAndRemainder(fractionalDays, BigDecimal.ONE)[1];
    Time time = Time.from(frac, jd.timescale());
    return DateTime.from(date, time);
  }
  
  
  
  
  
  
  static final double JAN_0_YEAR_0 = 1_721_058.5;
  private static final int SHORT_YR = 365;
  private static final int LONG_YR = 366;
  private static final int CYCLE_YEARS = 400;
  private static final Map<Month, Integer> DAYS_IN_PRECEDING_MONTHS = daysInPrecedingMonths();
  private static Map<Month, Integer> daysInPrecedingMonths() {
    int accumulator = 0;
    Map<Month, Integer> res = new LinkedHashMap<>();
    for(Month month : Month.values()) {
      res.put(month, accumulator);
      accumulator += month.length(false);
    }
    return res;
  }
  private BigDecimal cal_to_jd(long y, int m, BigDecimal d) {
    //completed years: small asymmetry between positive and negative years
    long y_p = (y >= 0) ? (y - 1) : y;  //y_p = y-prime
    long num_366yrs = (y_p/4);
    if (Calendar.GREGORIAN == calendar) {
      num_366yrs -= (y_p/100);  // should this part be in the calendar?
      num_366yrs += (y_p/CYCLE_YEARS);
    }
    if (y > 0) {
      num_366yrs += 1; //since year 0 is a leap year
    }
    long num_365yrs = y - num_366yrs;
    long res = num_365yrs * SHORT_YR + num_366yrs * LONG_YR;    
    //completed months
    res += DAYS_IN_PRECEDING_MONTHS.get(Month.of(m));   
    res += (calendar.isLeap(y) && (m - 1) >= 2 ? 1 : 0); //'correct' for leap years  
    //rebase to the usual origin of Julian date, and add the day
    return big(res).add(calendar.julianDateJan0Year0()).add(d);
  }
  
  
  private DateTime jd_to_cal(JulianDate jd) {
    BigDecimal JAN_1_YEAR_0 = calendar.julianDateJan0Year0().add(BigDecimal.ONE);
    BigDecimal target = jd.jd().subtract(JAN_1_YEAR_0);
    //1. find the closest base that PRECEDES the given moment
    BigDecimal fullCycleDays = big(calendar.fullCycleDays());
    //long num_cycles = (int)Math.floor((target.doubleValue())/calendar.fullCycleDays()); //rounds towards negative infinity: good!
    long num_cycles = floor(divide(target, fullCycleDays)).longValue(); //round towards neg infinity: good!
    BigDecimal base_jd = JAN_1_YEAR_0.add(big(num_cycles * calendar.fullCycleDays())); //a January 1.0  
    long year = num_cycles * calendar.fullCycleYears();
    
    BigDecimal jd_minus_base = jd.jd().subtract(base_jd);
    BigDecimal cursor = BigDecimal.ZERO; //points to a Jan 1.0 initially; approaches jd_minus_base from below!
    
    //2. remainder-years: whole, completed years after the base 
    //one big chunk of years: calculate a MINIMUM number of full remainder-years, to reduce loop iterations later
    int approx_days = floor(jd_minus_base).intValue();
    int more_years = (approx_days / LONG_YR) - 1; // at least this many
    if (more_years > 0) {
      int m_p = more_years - 1;
      int more_days = more_years * SHORT_YR + (m_p/4) + 1;
      if (Calendar.GREGORIAN == calendar) {
        more_days = more_days - (m_p/100) + (m_p/400);
      }
      cursor = cursor.add(big(more_days)); //still on a Jan 1.0!
      year += more_years;
    }
    //loop to find the rest of the remaining-years: at most 2 iterations here!
    long year_so_far = year; //for use in the loop 
    for(int more = 0; more < calendar.fullCycleYears(); ++more ) { 
      int year_length = calendar.isLeap(year_so_far + more) ? LONG_YR : SHORT_YR;
      if (cursor.add(big(year_length)).compareTo(jd_minus_base) <= 0) {
        cursor = cursor.add(big(year_length)); // Jan 1.0 of the next year
        ++year;
      } else { break; }
    }
    
    //3. months and days
    int month = 0; //both a loop index AND a result-value
    BigDecimal fractional_days = BigDecimal.ZERO;
    for(month = 1; month <= 12; ++month) {
      int month_length = Month.of(month).length(calendar.isLeap(year));
      if (cursor.add(big(month_length)).compareTo(jd_minus_base) <= 0) {
        cursor = cursor.add(big(month_length)); //1st day of the next month
      }
      else {
        fractional_days = jd_minus_base.subtract(cursor).add(BigDecimal.ONE); break;
      }
    }
    return buildDateTimeFrom(year, month, fractional_days, jd);
  }
  
  
  
}