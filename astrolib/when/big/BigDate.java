package astrolib.when.big;

import static astrolib.util.Consts.*;
import static astrolib.util.LogUtil.*;
import static astrolib.when.big.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.Objects;

import astrolib.util.Check;

/** 
 Immutable data-carrier for date information.
 
 <P>The astronomer's convention is used here: the year A.D. 1 is preceded by the year 0, not 1 B.C. 
 The year 2 B.C. is the year -1 in this convention, and so on.
 
 <P>Here, comparison and equality operations treat the underlying {@link BigCalendar} as the most significant item.
 If you wish to compare dates from different calendars, you can do so using {@link #jd(BigTimescale)}.
*/
public final class BigDate implements Comparable<BigDate> {
  
  /**
   Factory method for a date in the given calendar.
 
   @param year has no minimum or maximum value here, but the caller may choose to limit its range 
   @param month range [1,12]
   @param day range [1,31], with an extra check according to the month-year, which accounts for leap years
  */
  public static BigDate from(long year, int month, int day, BigCalendar calendar) {
    return new BigDate(year, month, day, calendar);
  }
  
  /** As in the full factory method, but specifically for a date in the Gregorian calendar.  */
  public static BigDate gregorian(long year, int month, int day) {
    return new BigDate(year, month, day, BigCalendar.GREGORIAN);
  }

  /** As in the full factory method, but specifically for a date in the Julian calendar.  */
  public static BigDate julian(long year, int month, int day) {
    return new BigDate(year, month, day, BigCalendar.JULIAN);
  }
  
  public long year() { return year; }
  public int month() { return month; }
  public int day() { return day; }
  public BigCalendar calendar() { return calendar; }
  
  /** The day of the week corresponding to this date. */
  public DayOfWeek weekday() {
    //Meeus 1991, page 65
    BigDateTime dt = BigDateTime.from(this, BigTime.zero(BigTimescaleCommon.TAI));
    BigDecimal jd = dt.toJulianDate().jd().add(big(1.5));
    BigDecimal[] div = divideAndRemainder(jd, big(7));
    int index = div[REMAINDER].intValue(); //0..6 = Sunday to Monday
    return DayOfWeek.of(index + 1).minus(1); //Monday..Sunday = 1..7
  }

  /** 
   Convert this date to a Julian date, using the given timescale.
   <p>This class doesn't do the reverse operation, to create a date from a {@link BigJulianDate}, because that conversion usually loses 
   information about the time. For that operation, please use {@link BigDateTime} instead.
  */  
  public BigJulianDate jd(BigTimescale timescale) {
    BigJulianDateConverter converter = BigJulianDateConverter.using(this.calendar);
    return converter.toJulianDate(BigDateTime.from(this, BigTime.zero(timescale)));
  }

  /** Convert this date to a date in a different calendar. */
  public BigDate convertTo(BigCalendar toCalendar) {
    if (this.calendar == toCalendar) {
      throw new IllegalArgumentException("Calendar conversion aborted. Trying to convert to the same calendar: " + toCalendar);
    }
    //to avoid possible hard-to-spot rounding differences near 0h, temporarily add a bit of time to this date:
    BigTime weeTime = BigTime.from(0, 5, BigDecimal.ZERO, BigTimescaleCommon.TT);
    BigDateTime nonce = BigDateTime.from(this, weeTime);
    BigJulianDate jd = BigJulianDateConverter.using(this.calendar).toJulianDate(nonce);
    BigDateTime converted = BigJulianDateConverter.using(toCalendar).toDateTime(jd);
    return converted.date();
  }

  /*** The first day of the month corresponding to this date and calendar. */
  public BigDate startOfMonth() {
    return new BigDate(year, month, 1, calendar);
  }
  
  /*** The last day of the month corresponding to this date and calendar. */
  public BigDate endOfMonth() {
    int lastDay = Month.of(month).length(calendar.isLeap(year));
    return new BigDate(year, month, lastDay, calendar);
  }
  
  /** The first day of the year corresponding to this date and calendar. */
  public BigDate startOfYear() {
    return new BigDate(year, 1, 1, calendar);
  }
  
  /** The last day of the year corresponding to this date and calendar. */
  public BigDate endOfYear() {
    return new BigDate(year, 12, 31, calendar);
  }
  
  /** With January 1 being day 1, and so on. */
  public int dayOfYear() {
    return integer(calendar.daysFromJan0(year, month, big(day))).intValue();
  }
  
  /** Less-than comparison. */
  public boolean lt(BigDate that) {
    return this.compareTo(that) < EQUAL;
  }
  
  /** Less-than-or-equal-to comparison. */
  public boolean lteq(BigDate that) {
    return compareTo(that) < EQUAL || equals(that);
  }
  
  /** Greater-than comparison. */
  public boolean gt(BigDate that) {
    return compareTo(that) > EQUAL;
  }

  /** Greater-than-or-equal-to comparison. */
  public boolean gteq(BigDate that) {
    return compareTo(that) > EQUAL || equals(that);
  }
  
  /** A convenient synonym for the <em>equals</em> method. */
  public boolean eq(BigDate that) {
    return equals(that);
  }

  
  /*
  This needs the Julian day number for both calendars.
  This could even work across calendars.
  public int daysFrom(Date that) () {}
  */


  /** Synonym for <em>plusDays(1)</em>. */
  public BigDate next() {
    return plusDays(1);
  }
  
  /** Synonym for <em>minusDays(1)</em>. */
  public BigDate previous() {
    return minusDays(1);
  }

  /**
   Add or subtract the given number of days from this date.
   
   @param days can be either sign. If non-negative, then returns a later date. 
   If negative, then returns an earlier date. 
  */
  public BigDate plusOrMinusDays(int days) {
    return days >= 0 ? plusDays(days) : minusDays(-days);
  }
  
  /**
   Add the given number of days to this date.
   @param days is non-negative. 
  */
  public BigDate plusDays(int days) {
    return plusDaysImpl(days); 
  }
  
  /**
   Subtract the given number of days from this date.
   @param days is non-negative. 
  */
  public BigDate minusDays(int days) {
    return minusDaysImpl(days); 
  }
  /** Intended for logging only. Example: <em>2025-01-01 GR</em> */
  @Override public String toString() {
    String sep = "-";
    return year + sep + zeroPad(month) + sep + zeroPad(day) + " " + calendar.toString().substring(0, 2);  
  }

  /** Two dates must share the same calendar in order to be equal. */
  @Override public boolean equals(Object aThat) {
    if (this == aThat) return true;
    if (!(aThat instanceof BigDate)) return false;
    BigDate that = (BigDate)aThat;
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

  /** This implementation treats the calendar as being the most significant item in the comparison. */
  @Override public int compareTo(BigDate that) {
    if (this == that) return EQUAL;

    int comparison = this.calendar.compareTo(that.calendar);
    if (comparison != EQUAL) return comparison;

    comparison = this.year.compareTo(that.year);
    if (comparison != EQUAL) return comparison;
    
    comparison = this.month.compareTo(that.month);
    if (comparison != EQUAL) return comparison;
    
    comparison = this.day.compareTo(that.day);
    if (comparison != EQUAL) return comparison;

    return EQUAL;
  }
  
  private BigCalendar calendar;
  private Long year;
  private Integer month, day;
  
  private BigDate(long year, int month, int day, BigCalendar calendar) {
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
    Struct(long y, int m, int d){
      this.y = y; 
      this.m = m; 
      this.d = d;
    }
    void setTo(Struct that) {
      y = that.y; 
      m = that.m;
      d = that.d;
    }
    long y;
    int m, d;
  }
  
  private BigDate plusDaysImpl(int days) {
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
    return new BigDate(temp.y, temp.m, temp.d, this.calendar);
  }

  private BigDate minusDaysImpl(int days) {
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
    return new BigDate(temp.y, temp.m, temp.d, this.calendar);
  }
}
