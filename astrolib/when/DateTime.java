package astrolib.when;

import static astrolib.util.Consts.*;
import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** 
 Data-carrier for date and time information.
 This class is meant for input and output, not for core calculations.
*/
public final class DateTime implements Comparable<DateTime> {
  
  /** As in the general factory method, but for a date-time in the Gregorian calendar. */
  public static DateTime gregorianCalendar(long year, int month, int day, int hour, int minute, BigDecimal seconds, Timescale timescale) {
    return new DateTime(Date.gregorian(year, month, day), Time.from(hour, minute, seconds, timescale));
  }
  
  /** As in the general factory method, but for a date-time in the Julian calendar. */
  public static DateTime julianCalendar(long year, int month, int day, int hour, int minute, BigDecimal seconds, Timescale timescale) {
    return new DateTime(Date.julian(year, month, day), Time.from(hour, minute, seconds, timescale));
  }

  /**
   Factory method for a date-time in the given calendar.
  
   @param year has no minimum or maximum value here, but the caller may choose to limit its range 
   @param month range [1,12]
   @param day range [1,31], with an extra check according to the month-year
   @param hour range [0,23]
   @param minute range [0,59]
   @param seconds range [0,60.0) for all timescales. Leap seconds are not implemented here. 
  */
  public static DateTime from(long year, int month, int day, int hour, int minute, BigDecimal seconds, Calendar calendar, Timescale timescale) {
    return new DateTime(Date.from(year, month, day, calendar), Time.from(hour, minute, seconds, timescale));
  }

  public static DateTime from(Date date, Time time) {
    return new DateTime(date, time);
  }
  
  /**  @param fraction of a day [0.0,1.0) */
  public static DateTime from(Date date, BigDecimal fraction, Timescale timescale) {
    return new DateTime(date, Time.from(fraction, timescale));
  }

  /** Build a {@link DateTime} in the given {@link Calendar}, using a {@link JulianDate}. */
  public static DateTime from(JulianDate julianDate, Calendar calendar) {
    return JulianDateConverter.using(calendar).toDateTime(julianDate);
  }
  
  public Date date() { return date; }
  public Time time() { return time; }
  
  public long year() { return date.year(); }
  public int month() { return date.month(); }
  public int day() { return date.day(); }
  
  public int hour() { return time.hour(); }
  public int minute() { return time.minute(); }
  public BigDecimal seconds() { return time.seconds(); }
  
  /**  The day of the month, plus the time-of-day represented as a decimal value in the range [0.0,1.0).  */
  public BigDecimal fractionalDay() {
    return big(date.day()).add(time.fraction());
  }
  
  /** Convert this {@link DateTime} to a {@link JulianDate}. */
  public JulianDate toJulianDate() {
    return JulianDateConverter.using(date.calendar()).toJulianDate(this);
  }

  /** 
   Round the seconds to the given number of places, and return a new {@link DateTime}.
   Retains the {@link Calendar} and {@link Timescale} attached to {@link #date()} and {@link #time()}, respectively. 
  */
  public DateTime roundSeconds(int numPlaces, RoundingMode roundingMode) {
    DateTime res = null;
    RoundSeconds rounder = RoundSeconds.to(numPlaces);
    RoundSeconds.Result rounded = rounder.apply(seconds());
    if (rounded.overflows()) {
      res = Odometer.rollover(this);
    }
    else {
      res = new DateTime(date(), Time.from(time.hour(), time.minute(), rounded.val(), time.timescale()));
    }
    return res;
  }

  /** 
   Return a new {@link DateTime} which is a given number of days from this {@link DateTime}.
   Retains the {@link Calendar} and {@link Timescale} attached to {@link #date()} and {@link #time()}, respectively. 
   @param numPlaces number of decimal places to which the seconds field is to be rounded.
   @param roundingMode used when rounding the seconds field. 
  */
  public DateTime plusMinusDays(BigDecimal days, int numPlaces, RoundingMode roundingMode) {
    JulianDate jd = JulianDateConverter.using(this.date().calendar()).toJulianDate(this);
    JulianDate jdNew = JulianDate.from(jd.jd().add(days), this.time().timescale());
    return JulianDateConverter.using(this.date().calendar()).toDateTime(jdNew).roundSeconds(numPlaces, roundingMode);
  }
  
  /** 
   Return a new {@link DateTime} which is a given number of seconds from this {@link DateTime}.
   Similar to {@link #plusMinusDays(BigDecimal)}.
  */
  public DateTime plusMinusSeconds(BigDecimal seconds, int numPlaces, RoundingMode roundingMode) {
    BigDecimal days = divide(seconds, big(SECONDS_PER_DAY));
    return plusMinusDays(days, numPlaces, roundingMode);
  }

  /**
   Return the number of (fractional) days between this {@link DateTime} and the given {@link DateTime}.
   @param that can use a different calendar than the one used by this {@link DateTime}!
   @param numPlaces used for rounding the result to the given number of decimal places.
   @return a positive result if this date is after <em>that</em> date, negative if this date is before <em>that</em> date. 
  */
  public BigDecimal daysFrom(DateTime that, int numPlaces, RoundingMode roundingMode) {
    BigDecimal res = daysFromUnrounded(that);
    return round(res, numPlaces, roundingMode);
  }
  
  /** As in {@link #daysFrom(DateTime, int, RoundingMode)}, but return fractional seconds. */
  public BigDecimal secondsFrom(DateTime that, int numPlaces, RoundingMode roundingMode) {
    BigDecimal daysFrom = daysFromUnrounded(that);
    BigDecimal res = daysFrom.multiply(big(SECONDS_PER_DAY));
    return round(res, numPlaces, roundingMode);
  }

  /** Intended for logging only. 2025-01-01 01:01:01 */
  @Override public String toString() {
    return date.toString() + " " + time.toString(); 
  }
  
  @Override public boolean equals(Object aThat) {
    if (this == aThat) return true;
    if (!(aThat instanceof DateTime)) return false;
    DateTime that = (DateTime)aThat;
    for(int i = 0; i < this.getSigFields().length; ++i){
      if (!Objects.equals(this.getSigFields()[i], that.getSigFields()[i])){
        return false;
      }
    }
    return true;
  }  
  
  @Override public int hashCode() {
    return Objects.hash(getSigFields());
  }
  
  @Override public int compareTo(DateTime that) {
    final int EQUAL = 0;
    if (this == that) return EQUAL;

    int comparison = this.date.compareTo(that.date);
    if (comparison != EQUAL) return comparison;
    
    comparison = this.time.compareTo(that.time);
    if (comparison != EQUAL) return comparison;
    
    return EQUAL;
  }

  private Date date;
  private Time time;
  
  private DateTime(Date date, Time time) {
    this.date = date;
    this.time = time;
  }
  
  private Object[] getSigFields() {
    Object[] res = {date, time};
    return res;
  }
  
  private BigDecimal daysFromUnrounded(DateTime that) {
    JulianDate jdThis = JulianDateConverter.using(this.date().calendar()).toJulianDate(this);
    JulianDate jdThat = JulianDateConverter.using(that.date().calendar()).toJulianDate(that);
    return jdThis.jd().subtract(jdThat.jd());
  }
}