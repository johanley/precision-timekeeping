package astrolib.when;

import static astrolib.util.Consts.*;
import static astrolib.util.LogUtil.*;
import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.Objects;

import astrolib.util.Check;

/** 
 Immutable data-carrier for date information.
 
 <P>The astronomer's convention is used here: the year A.D. 1 is preceded by the year 0, not 1 B.C. 
 The year 2 B.C. is the year -1 in this convention, and so on.
 
 <P>Here, comparison and equality operations treat the underlying {@link Calendar} as the most significant item.
 If you wish to compare dates from different calendars, you can do so using {@link #jd(Timescale)}.
*/
public final class Date implements Comparable<Date> {
  
  /**
   Factory method for a date in the given calendar.
 
   @param year has no minimum or maximum value here, but the caller may choose to limit its range 
   @param month range [1,12]
   @param day range [1,31], with an extra check according to the month-year, which accounts for leap years
  */
  public static Date from(long year, int month, int day, Calendar calendar) {
    return new Date(year, month, day, calendar);
  }
  
  /** As in the full factory method, but specifically for a date in the Gregorian calendar.  */
  public static Date gregorian(long year, int month, int day) {
    return new Date(year, month, day, Calendar.GREGORIAN);
  }

  /** As in the full factory method, but specifically for a date in the Julian calendar.  */
  public static Date julian(long year, int month, int day) {
    return new Date(year, month, day, Calendar.JULIAN);
  }
  
  public long year() { return year; }
  public int month() { return month; }
  public int day() { return day; }
  public Calendar calendar() { return calendar; }
  
  /** The day of the week corresponding to this {@link Date}, in its given {@link Calendar}. */
  public DayOfWeek weekday() {
    //Meeus 1991, page 65
    DateTime dt = DateTime.from(this, Time.zero(TimescaleCommon.TAI));
    BigDecimal jd = dt.toJulianDate().jd().add(big(1.5));
    BigDecimal[] div = divideAndRemainder(jd, big(7));
    int index = div[REMAINDER].intValue(); //0..6 = Sunday to Monday
    return DayOfWeek.of(index + 1).minus(1); //Monday..Sunday = 1..7
  }

  /** 
   Convert this {@link Date} to a {@link JulianDate}, using the given {@link Timescale}.
   <p>This class doesn't do the reverse operation, to create a {@link Date} from a {@link JulianDate}, 
   because that conversion can lose information about the time. For that operation, 
   please use {@link DateTime} instead.
  */  
  public JulianDate jd(Timescale timescale) {
    JulianDateConverter converter = JulianDateConverter.using(this.calendar);
    return converter.toJulianDate(DateTime.from(this, Time.zero(timescale)));
  }

  /** Convert this {@link Date} to a {@link Date} in a different {@link Calendar}. */
  public Date convertTo(Calendar toCalendar) {
    if (this.calendar == toCalendar) {
      throw new IllegalArgumentException("Calendar conversion aborted. Trying to convert to the same calendar: " + toCalendar);
    }
    //to avoid possible hard-to-spot rounding differences near 0h, temporarily add a bit of time to this date:
    Time weeTime = Time.from(0, 5, BigDecimal.ZERO, TimescaleCommon.TT);
    DateTime nonce = DateTime.from(this, weeTime);
    JulianDate jd = JulianDateConverter.using(this.calendar).toJulianDate(nonce);
    DateTime converted = JulianDateConverter.using(toCalendar).toDateTime(jd);
    return converted.date();
  }

  /*** The first day of the month corresponding to this {@link Date} and {@link Calendar}. */
  public Date startOfMonth() {
    return new Date(year, month, 1, calendar);
  }
  
  /*** The last day of the month corresponding to this {@link Date} and {@link Calendar}. */
  public Date endOfMonth() {
    int lastDay = Month.of(month).length(calendar.isLeap(year));
    return new Date(year, month, lastDay, calendar);
  }
  
  /** The first day of the year corresponding to this {@link Date} and {@link Calendar}. */
  public Date startOfYear() {
    return new Date(year, 1, 1, calendar);
  }
  
  /** The last day of the year corresponding to this {@link Date} and {@link Calendar}. */
  public Date endOfYear() {
    return new Date(year, 12, 31, calendar);
  }
  
  /** With January 1 being day 1, and so on. */
  public int dayOfYear() {
    return integer(calendar.daysFromJan0(year, month, big(day))).intValue();
  }
  
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

  /*
    This needs the Julian day number for both calendars.
    This could even work across calendars.
    public long daysFrom(Date that) () {}
    
    change both this, that to jd
    do the math: the diff in jd
  */

  /**
   Add the given number of days to this {@link Date}.
   If days is negative, then a subtraction occurs.
  */
  public Date plusMinusDays(int days) {
    //just borrow the full implementation of this, in {@link DateTime}
    DateTime dt = DateTime.from(this, Time.zero(TimescaleCommon.TT));
    DateTime dtNew = dt.plusMinusDays(big(days), 0, RoundingMode.HALF_EVEN);
    return dtNew.date();
  }
  
  /** Synonym for <em>plusMinusDays(1)</em>. */
  public Date next() {
    return plusMinusDays(1);
  }
  
  /** Synonym for <em>plusMinusDays(-1)</em>. */
  public Date previous() {
    return plusMinusDays(-1);
  }
  
  /** Intended for logging only. Example: <em>2025-01-01 GR</em> */
  @Override public String toString() {
    String sep = "-";
    return year + sep + zeroPad(month) + sep + zeroPad(day) + " " + calendar.toString().substring(0, 2);  
  }

  /** Here, two {@link Date}s must share the same {@link Calendar} in order to be equal. */
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

  /** This implementation treats the {@link Calendar} as being the most significant item in the comparison. */
  @Override public int compareTo(Date that) {
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
  
  private Calendar calendar;
  private Long year;
  private Integer month, day;
  
  private Date(long year, int month, int day, Calendar calendar) {
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
}
