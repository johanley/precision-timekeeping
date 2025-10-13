package astrolib.when;

import java.util.Objects;

/** 
 Data-carrier for date and time information.
 This class is meant for input and output, not for core calculations.
 
 <P>This class was initially created because the java.time classes have no support for leap seconds.
*/
public final class DateTime implements Comparable<DateTime> {
  
  /**
   Factory method for a date-time in the Gregorian calendar.
    
   @param year has no minimum or maximum value here, but the caller may choose to limit its range 
   @param month range [1,12]
   @param day range [1,31], with an extra check according to the month-year
   @param hour range [0,23]
   @param minute range [0,59]
   @param seconds range [0,61.0) for the UTC timescale, and [0,60.0) for all other timescales. The extra second exists because of leap seconds. 
  */
  public static DateTime gregorianCalendar(int year, int month, int day, int hour, int minute, double seconds, Timescale timescale) {
    return new DateTime(Date.gregorian(year, month, day), Time.from(hour, minute, seconds, timescale));
  }
  
  /**
   Factory method for a date-time in the Julian calendar.
   
   @param year has no minimum or maximum value here, but the caller may choose to limit its range 
   @param month range [1,12]
   @param day range [1,31], with an extra check according to the month-year
   @param hour range [0,23]
   @param minute range [0,59]
   @param seconds range [0,61.0) for the UTC timescale, and [0,60.0) for all other timescales. The extra second exists because of leap seconds. 
  */
  public static DateTime julianCalendar(int year, int month, int day, int hour, int minute, double seconds, Timescale timescale) {
    return new DateTime(Date.julian(year, month, day), Time.from(hour, minute, seconds, timescale));
  }

  /**
   Factory method for a date-time in the given calendar.
  
   @param year has no minimum or maximum value here, but the caller may choose to limit its range 
   @param month range [1,12]
   @param day range [1,31], with an extra check according to the month-year
   @param hour range [0,23]
   @param minute range [0,59]
   @param seconds range [0,61.0) for the UTC timescale, and [0,60.0) for all other timescales. The extra second exists because of leap seconds. 
  */
  public static DateTime from(int year, int month, int day, int hour, int minute, double seconds, Calendar calendar, Timescale timescale) {
    return new DateTime(Date.from(year, month, day, calendar), Time.from(hour, minute, seconds, timescale));
  }

  public static DateTime from(Date date, Time time) {
    return new DateTime(date, time);
  }

  /** Build a DateTime in the given calendar, using a Julian date. */
  public static DateTime from(JulianDate julianDate, Calendar calendar) {
    return JulianDateConverter.using(calendar).toDateTime(julianDate);
  }
  
  public Date date() { return date; }
  public Time time() { return time; }
  
  public int year() { return date.year(); }
  public int month() { return date.month(); }
  public int day() { return date.day(); }
  
  public int hour() { return time.hour(); }
  public int minute() { return time.minute(); }
  public double seconds() { return time.seconds(); }
  
  /** 
   This method is sensitive to the presence of possible leap seconds used by the UTC timescale.
   In the presence of a leap second, the number of seconds in the day is adjusted by +/- 1 second. 
  */
  public int secondsInDay() {
    int res = 24 * 60 * 60;
    if (Timescale.UTC == time.timescale()) {
      //find out if there's a leap second in the current day; if so, +/- 1s to the result
      //look up the Utc config using the DATE part
      //what about the rate changes?
    }
    return res;
  }
  
  /**
   The day of the month, plus the time represented as a decimal value in the range [0.0,1.0). 
   The returned value is sensitive to the presence of leap seconds in the day, if any. 
  */
  public double fractionalDay() {
    return date.day() + time.fraction(secondsInDay());
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
    //TODO IF UTC, CHECK THAT LEAP SECONDS, IF PRESENT, ARE ATTACHED TO LEAP-SECOND DAYS
  }
  
  private Object[] getSigFields() {
    Object[] res = {date, time};
    return res;
  }
}