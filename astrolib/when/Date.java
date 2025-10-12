package astrolib.when;

import java.time.Month;
import java.util.Objects;

import astrolib.util.Check;
import astrolib.util.Mathy;
import static astrolib.util.LogUtil.zeroPad;

/** 
 Immutable data-carrier for date information.
 This class is meant for input and output, not for core calculations.
 
 <P>The astronomer's convention is used here: the year A.D. 1 is preceded by the year 0, not 1 B.C. 
 The year 2 B.C. is the year -1 in this convention, and so on.
*/
public final class Date implements Comparable<Date> {
  
  /**
   Factory method for a date in the Gregorian calendar.
   
   @param year has no minimum or maximum value here, but the caller may choose to limit its range 
   @param month range [1,12]
   @param day range [1,31], with an extra check according to the month-year, which accounts for leap years
  */
  public static Date gregorian(int year, int month, int day) {
    return new Date(year, month, day, Calendar.GREGORIAN);
  }

  /**
   Factory method for a date in the Julian calendar.
  
   @param year has no minimum or maximum value here, but the caller may choose to limit its range 
   @param month range [1,12]
   @param day range [1,31], with an extra check according to the month-year, which accounts for leap years
  */
  public static Date julian(int year, int month, int day) {
    return new Date(year, month, day, Calendar.JULIAN);
  }
  
  /**
   Factory method for a date in the given calendar.
 
   @param year has no minimum or maximum value here, but the caller may choose to limit its range 
   @param month range [1,12]
   @param day range [1,31], with an extra check according to the month-year, which accounts for leap years
  */
  public static Date from(int year, int month, int day, Calendar calendar) {
    return new Date(year, month, day, calendar);
  }
  
  public int year() { return year; }
  public int month() { return month; }
  public int day() { return day; }
  public Calendar calendar() { return calendar; }

  /** Less-than comparison. */
  public boolean lt(Date that) {
    return this.compareTo(that) < EQUAL;
  }
  
  /** Less-than-or-equal-to comparison. */
  public boolean lteq(Date that) {
    return compareTo(that) < EQUAL || equals(that);
  }
  
  /** Greater-than comparison. */
  public boolean gt(Date that) {
    return compareTo(that) > EQUAL;
  }

  /** Greater-than-or-equal-to comparison. */
  public boolean gteq(Date that) {
    return compareTo(that) > EQUAL || equals(that);
  }
  
  /** A convenient synonym for the <em>equals</em> method. */
  public boolean eq(Date that) {
    return equals(that);
  }

  /*** The first day of the month corresponding to this date and calendar. */
  public Date startOfMonth() {
    return new Date(year, month, 1, calendar);
  }
  
  /*** The last day of the month corresponding to this date and calendar. */
  public Date endOfMonth() {
    int lastDay = Month.of(month).length(calendar.isLeap(year));
    return new Date(year, month, lastDay, calendar);
  }
  
  /** The first day of the year corresponding to this date and calendar. */
  public Date startOfYear() {
    return new Date(year, 1, 1, calendar);
  }
  
  /** The last day of the year corresponding to this date and calendar. */
  public Date endOfYear() {
    return new Date(year, 12, 31, calendar);
  }
  
  /** With January 1 being day 1, and so on. */
  public int dayOfYear() {
    return (int)Mathy.truncate(calendar.daysFromJan0(year, month, day));
  }
  
  /*
  These need the Julian day number for both calendars.
    
  public int weekday() { }
  
  public int daysFrom(Date that) () {}
  */

  /** Convenient synonym for <em>plusDays(1)</em>. */
  public Date next() {
    return plusDays(1);
  }
  
  /** Convenient synonym for <em>minusDays(1)</em>. */
  public Date previous() {
    return minusDays(1);
  }

  /**
   Add or subtract the given number of days from this date.
   This implementation uses a simple <em>odometer</em> model; the caller may find  it too slow if the number of days is VERY large.
   If days is non-negative, then return a later date. 
   If days is negative, then return an earlier date. 
  */
  public Date plusOrMinusDays(int days) {
    return days >= 0 ? plusDays(days) : minusDays(-days);
  }
  
  /**
   Add the given number of days to this date.
   This implementation uses a simple <em>odometer</em> model; the caller may find  it too slow if the number of days is VERY large. 
   @param days is non-negative. 
  */
  public Date plusDays(int days) {
    return plusDaysImpl(days); 
  }
  
  /**
   Subtract the given number of days from this date.
   This implementation uses a simple <em>odometer</em> model; the caller may find  it too slow if the number of days is VERY large. 
   @param days is non-negative. 
  */
  public Date minusDays(int days) {
    return minusDaysImpl(days); 
  }

  /** Intended for logging only. Example: <em>2025-01-01 GR</em> */
  @Override public String toString() {
    String sep = "-";
    return year + sep + zeroPad(month) + sep + zeroPad(day) + " " + calendar.toString().substring(0, 2);  
  }
  
  @Override public boolean equals(Object aThat) {
    if (this == aThat) return true;
    if (!(aThat instanceof Date)) return false;
    Date that = (Date)aThat;
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
  
  @Override public int compareTo(Date that) {
    if (this == that) return EQUAL;

    int comparison = this.year.compareTo(that.year);
    if (comparison != EQUAL) return comparison;
    
    comparison = this.month.compareTo(that.month);
    if (comparison != EQUAL) return comparison;
    
    comparison = this.day.compareTo(that.day);
    if (comparison != EQUAL) return comparison;

    return EQUAL;
  }
  
  private Calendar calendar;
  private Integer year, month, day;
  private static final int EQUAL = 0;
  
  private Date(int year, int month, int day, Calendar calendar) {
    Check.range(month, 1, 12);
    Check.range(day, 1, Month.of(month).length(calendar.isLeap(year)));
    this.year = year;
    this.month = month;
    this.day = day;
    this.calendar = calendar;
  }
  
  private Object[] getSigFields() {
    Object[] res = {calendar, year, month, day};
    return res;
  }
  
  /** For behind-the-scenes calculations, and avoiding object creation. */
  private class Struct {
    Struct(int y, int m, int d){
      this.y = y; 
      this.m = m; 
      this.d = d;
    }
    void setTo(Struct that) {
      y = that.y; 
      m = that.m;
      d = that.d;
    }
    int y, m, d;
  }
  
  private Date plusDaysImpl(int days) {
    Check.nonNegative(days);
    Struct temp = new Struct(year, month, day);
    for(int i=1; i<=days; ++i) {
      //model as an odometer, incrementing one day at a time
      Struct overFlowDay = new Struct(year, month + 1, 1);
      if (overFlowDay.m > Month.DECEMBER.getValue()) {
        overFlowDay.m = Month.JANUARY.getValue();
        overFlowDay.y = overFlowDay.y + 1;
      }
      int lastDayOfMonth = Month.of(temp.m).length(calendar.isLeap(temp.y));
      temp.d = temp.d + 1;
      if (temp.d > lastDayOfMonth) {
        temp.setTo(overFlowDay);
      }
    }
    return new Date(temp.y, temp.m, temp.d, this.calendar);
  }

  private Date minusDaysImpl(int days) {
    Check.nonNegative(days);
    Struct temp = new Struct(year, month, day);
    for(int i=1; i<=days; ++i) {
      //model as an odometer, decrementing one day at a time
      Struct underFlowDay = new Struct(year, month - 1, 1);
      if (underFlowDay.m < Month.JANUARY.getValue()) {
        underFlowDay.m = Month.DECEMBER.getValue();
        underFlowDay.y = underFlowDay.y - 1;
      }
      //finally, we can set it to the last day of the month
      underFlowDay.d = Month.of(underFlowDay.m).length(calendar.isLeap(underFlowDay.y)); 
      temp.d = temp.d - 1;
      if (temp.d < 1) {
        temp.setTo(underFlowDay);
      }
    }
    return new Date(temp.y, temp.m, temp.d, this.calendar);
  }
}
