package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.util.Objects;

/** 
 Data-carrier for date and time information.
 This class is meant for input and output, not for core calculations.
*/
public final class DateTime implements Comparable<DateTime> {
  
  /** As in the general factory method, but for a date-time in the Gregorian calendar. */
  public static DateTime gregorianCalendar(int year, int month, int day, int hour, int minute, BigDecimal seconds, Timescale timescale) {
    return new DateTime(Date.gregorian(year, month, day), Time.from(hour, minute, seconds, timescale));
  }
  
  /** As in the general factory method, but for a date-time in the Julian calendar. */
  public static DateTime julianCalendar(int year, int month, int day, int hour, int minute, BigDecimal seconds, Timescale timescale) {
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
  public static DateTime from(int year, int month, int day, int hour, int minute, BigDecimal seconds, Calendar calendar, Timescale timescale) {
    return new DateTime(Date.from(year, month, day, calendar), Time.from(hour, minute, seconds, timescale));
  }

  public static DateTime from(Date date, Time time) {
    return new DateTime(date, time);
  }
  
  /**  @param fraction of a day [0.0,1.0) */
  public static DateTime from(Date date, BigDecimal fraction, Timescale timescale) {
    return new DateTime(date, Time.from(fraction, timescale));
  }

  /** Build a DateTime in the given calendar, using a Julian date. */
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
  
  /**  The day of the month, plus the time represented as a decimal value in the range [0.0,1.0).  */
  public BigDecimal fractionalDay() {
    return big(date.day()).add(time.fraction());
  }
  
  /** Convert this DateTime to a JulianDate. */
  public JulianDate toJulianDate() {
    return JulianDateConverter.using(date.calendar()).toJulianDate(this);
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
}