package bigtime.when;

import static bigtime.when.BigDecimalHelper.*;
import static bigtime.when.Calendar.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.Map;

/** 
 Convert a {@link DateTime} with a given {@link Calendar} into a {@link JulianDate}, 
 and vice versa.

 <P>Here, there's no restriction on the input/output year.
 Most astronomical libraries implement this conversion by restricting the range of the 
 year (and the Julian date) in some way. Usually, they restrict the date such that the 
 Julian date is greater than or equal to 0. 
*/
final class JulianDateConverter {

  /** Factory method to convert using given {@link Calendar}. */
  static JulianDateConverter using(Calendar calendar) {
    return new JulianDateConverter(calendar);
  }
  
  /** 
   Return the Julian date corresponding to the given {@link DateTime} in the calendar.
   @param dt must be attached to same {@link Calendar} passed to the factory method.
  */
  JulianDate toJulianDate(DateTime dt) {
    if (calendar != dt.date().calendar() ) {
      throw new IllegalArgumentException("The supplied date-time is for the " + dt.date().calendar() + " calendar. Expecting " + calendar);
    }
    return toJulianDate(dt.year(), dt.month(), dt.fractionalDay(), dt.time().timescale());
  }
  
  /** 
   Return the {@JulianDate} corresponding to the given moment in the {@link Calendar} 
   passed to the factory method..
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
  
  private Calendar calendar;

  /** A converter for the given calendar. */
  private JulianDateConverter(Calendar calendar){
    this.calendar = calendar;
  }
  
  private DateTime buildDateTimeFrom(long year, int month, BigDecimal fractionalDays, JulianDate jd) {
    BigInteger day = integer(fractionalDays);
    Date date = Date.from(year, month, day.intValue(), calendar);
    BigDecimal frac = divideAndRemainder(fractionalDays, BigDecimal.ONE)[REMAINDER];
    Time time = Time.from(frac, jd.timescale());
    return DateTime.from(date, time);
  }
  
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
      num_366yrs += (y_p/calendar.fullCycleYears());
    }
    if (y > 0) {
      num_366yrs += 1; //since year 0 is a leap year
    }
    long num_365yrs = y - num_366yrs;
    long res = num_365yrs * SHORT_YEAR + num_366yrs * LONG_YEAR;    
    //completed months
    res += DAYS_IN_PRECEDING_MONTHS.get(Month.of(m));   
    res += (calendar.isLeap(y) && (m - 1) >= 2 ? 1 : 0); //'correct' for leap years  
    //rebase to the usual origin of Julian date, and add the day
    return big(res).add(calendar.julianDateJan0Year0()).add(d);
  }
  
  private DateTime jd_to_cal(JulianDate jd) {
    BigDecimal jan_1_yr_0 = calendar.julianDateJan0Year0().add(BigDecimal.ONE);
    BigDecimal target = jd.jd().subtract(jan_1_yr_0);
    //1. find the closest base that PRECEDES the given moment
    BigDecimal fullCycleDays = big(calendar.fullCycleDays());
    //long num_cycles = (int)Math.floor((target.doubleValue())/calendar.fullCycleDays()); //rounds towards negative infinity: good!
    long num_cycles = floor(divide(target, fullCycleDays)).longValue(); //round towards neg infinity: good!
    BigDecimal base_jd = jan_1_yr_0.add(big(num_cycles * calendar.fullCycleDays())); //a January 1.0  
    long year = num_cycles * calendar.fullCycleYears();
    
    BigDecimal jd_minus_base = jd.jd().subtract(base_jd);
    BigDecimal cursor = BigDecimal.ZERO; //points to a Jan 1.0 initially; approaches jd_minus_base from below!
    
    //2. remainder-years: whole, completed years after the base 
    //one big chunk of years: calculate a MINIMUM number of full remainder-years, to reduce loop iterations later
    int approx_days = floor(jd_minus_base).intValue();
    int more_years = (approx_days / LONG_YEAR) - 1; // at least this many
    if (more_years > 0) {
      int m_p = more_years - 1;
      int more_days = more_years * SHORT_YEAR + (m_p/4) + 1;
      if (Calendar.GREGORIAN == calendar) {
        more_days = more_days - (m_p/100) + (m_p/400); //should this be in the calendar?
      }
      cursor = cursor.add(big(more_days)); //still on a Jan 1.0!
      year += more_years;
    }
    //loop to find the rest of the remaining-years: at most 2 iterations here!
    long year_so_far = year; //for use in the loop 
    for(int more = 0; more < calendar.fullCycleYears(); ++more ) { 
      int year_length = calendar.isLeap(year_so_far + more) ? LONG_YEAR : SHORT_YEAR;
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