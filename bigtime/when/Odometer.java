package bigtime.when;

import static bigtime.util.Consts.*;

import java.math.BigDecimal;
import java.time.Month;

/** 
 Helper class for rounding seconds, specifically for the fence-post case when the rounding results in a value of 60 seconds exactly.
 For example, rounding 59.999 to 2 decimals results in a value of 60.
 Such a value is for the seconds is 'disallowed' by this library, and requires a 'rollover' of other time units (minutes, and possibly even 
 the hour, day, month, and year). 
 
 <P>The task of this class is to calculate a new date-time corresponding to a value of 60-seconds.
 <P>The caller uses this class after having already concluded that a rollover is indeed necessary.
*/
final class Odometer {
  
  /**
   Rollover the given {@link DateTime} to the next minute, with its seconds value set to 0.
   The returned object has the same {@link Calendar} and {@link Timescale} as the given {@link DateTime}.
   
   <P>A rollover can possibly change all higher units of time: minute-hour-day-month-year.
   Rounding the {@link DateTime}:
     <pre>2025-12-31 23:59:59.999</pre> 
   to 2 decimal places for the seconds results in the {@link DateTime}:
     <pre>2026-01-01 00:00:0</pre> 
  */
  static DateTime rollover(DateTime orig) {
    long year = orig.year();
    int month = orig.month();
    int day = orig.day();
    int hour = orig.hour();
    int minute = orig.minute();
    BigDecimal seconds = BigDecimal.ZERO;
    
    ++minute;
    if (minute == MINUTES_PER_HOUR) {
      minute = 0;
      ++hour;
    }
    if (hour == HOURS_PER_DAY) {
      hour = 0;
      ++day;
    }
    if (day > Month.of(month).length(orig.date().calendar().isLeap(year))) {
      day = 1;
      ++month;
    }
    if (month > Month.DECEMBER.getValue()) {
      month = Month.JANUARY.getValue();
      ++year;
    }
    return DateTime.from(year, month, day, hour, minute, seconds, orig.date().calendar(), orig.time().timescale());
  }
}