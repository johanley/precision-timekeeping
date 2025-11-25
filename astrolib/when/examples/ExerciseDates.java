package astrolib.when.examples;

import static astrolib.when.BigDecimalHelper.*;
import static astrolib.when.Calendar.*;
import static astrolib.when.TimescaleImpl.*;
import static java.math.RoundingMode.*;

import java.math.BigDecimal;

import astrolib.when.Calendar;
import astrolib.when.Date;
import astrolib.when.DateTime;
import astrolib.when.JulianDate;
import astrolib.when.Time;

/** 
 Basic examples of using the library.
 This is not a complete set of examples.
 
 Note the use of BigDecimal to represent seconds/days.
 The 'big(...)' methods are simple helper methods defined in BigDecimalHelper, 
 for making BigDecimal objects. 
*/
public class ExerciseDates {
  
  private static void buildDates() {
    Date a = Date.from(1987, 12, 25, GREGORIAN);
    Date b = Date.gregorian(1987, 12, 25);
    Date c = Date.julian(1095, 11, 12);
    //the year is a 'long' data type
    Date d = Date.gregorian(3213132131321313131L, 12, 31);
  }
  
  private static void buildTimes() {
    //12:30:15.123_456_789_012
    Time a = Time.from(12, 30, big(15.123_456_789_012), GPS);
    //using fraction of a day
    Time b = Time.from(big(0.123_456_789_012), TAI);
    Time startOfDay = Time.zero(TAI);
  }
  
  private static void buildDatesAndTimes() {
    Date date = Date.gregorian(1987, 12, 25);
    Time time = Time.from(12, 30, big(15.123), UT1);
    DateTime dt = DateTime.from(date, time);
    
    //using a fraction of a day
    dt = DateTime.from(date, big(0.123_465_789_012), TDB);
    
    dt = DateTime.from(1999, 12, 31, 23, 59, big(59.999), GREGORIAN, UTC);
    
    JulianDate jd = JulianDate.from(big(2_545_321.5), TT);
    dt = DateTime.from(jd, GREGORIAN);
    JulianDate jd2 = dt.toJulianDate();
  }
  
  private static void calendarConversion() {
    Date a = Date.gregorian(1400, 12, 1);
    Date b = a.convertTo(JULIAN);
  }
  
  private static void compareDates() {
    Date a = Date.gregorian(1400, 12, 1);
    Date b = Date.julian(1095, 11, 16);
    boolean isAfter = a.gt(b);
    boolean isSame = a.eq(b);
    //and so on...
  }

  private static void daysFrom() {
    Date a = Date.gregorian(1400, 12, 1);
    Date b = Date.julian(1095, 11, 16);
    long days = a.daysFrom(b);
  }
  
  private static void nextPreviousDayEtc() {
    Date a = Date.gregorian(2023, 12, 1);
    Date b = a.next();
    b = a.previous();
    b = a.plusMinusDays(10);
    b = a.plusMinusDays(-10);
  }

  /* 
   You can build a DateTime from a JulianDate, but not a Date.
   A JulianDate object carries fractional day information, but Date does not. 
  */
  private static void dateToJulianDate() {
    Date a = Date.gregorian(2023, 12, 1);
    JulianDate jd = a.jd(TT); //for 0h that day
    DateTime b = DateTime.from(jd, Calendar.GREGORIAN);
    
    //no restriction to JD >= 0
    Date ancient = Date.gregorian(-15000, 1, 1);
    jd = a.jd(TT); 
  }
  
  private static void dateTimeMethods() {
    Date date = Date.gregorian(1987, 12, 25);
    Time time1 = Time.from(12, 30, big(15.123_456), TDB);
    Time time2 = Time.from(12, 30, big(15.000_002), TDB);
    DateTime a = DateTime.from(date, time1);
    DateTime b = DateTime.from(date, time2);
    
    int comparison = a.compareTo(b);
    BigDecimal days = a.daysFrom(b, 3, HALF_EVEN); //round to three decimals
    BigDecimal seconds = a.secondsFrom(b, 3, HALF_EVEN); //round to three decimals
    BigDecimal frac = a.fractionalDay(); //day and time-of-day as one number
    DateTime c = a.plusMinusDays(big(10), 4, HALF_EVEN);
    DateTime d = a.plusMinusSeconds(big(-62.132), 4, HALF_EVEN);
    DateTime e = a.roundSeconds(3, HALF_EVEN);
    JulianDate jd = a.toJulianDate();
    long year = a.year();
    int month = a.month();
    //and so on...
  }
  
  /* The Calendar and Timescale are explicit in toString(). */
  private static void dateTimeString() {
    Date date = Date.gregorian(1987, 12, 25);
    Time time = Time.from(12, 30, big(15.123_456), TDB);
    DateTime a = DateTime.from(date, time);
    a.toString(); //1987-12-25 GR 12:30:15.123456 TDB
  }
}
