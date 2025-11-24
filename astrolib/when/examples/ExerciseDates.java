package astrolib.when.examples;

import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;

import astrolib.when.Calendar;
import astrolib.when.Date;
import astrolib.when.DateTime;
import astrolib.when.JulianDate;
import astrolib.when.Time;
import astrolib.when.Timescale;
import astrolib.when.TimescaleImpl;

public class ExerciseDates {
  
  public static void main(String[] args) {
    ExerciseDates ex = new ExerciseDates();
    ex.doubleConversion(2025, 1, 1, Calendar.GREGORIAN, Calendar.JULIAN);
    ex.doubleConversion(-4713, 11, 24, Calendar.GREGORIAN, Calendar.JULIAN);
    ex.doubleConversion(2025,  1, 1, Calendar.GREGORIAN);
    ex.doubleConversionJd(2025, 1, 1, 0, 0, big(13.05), Calendar.GREGORIAN); //repeating decimal; some rounding 
    ex.doubleConversionJd(2025, 1, 1, 0, 0, big(0), Calendar.GREGORIAN); 
    ex.doubleConversionJd(2025, 1, 1, 12, 0, big(0), Calendar.GREGORIAN); 
    ex.doubleConversionJd(2025, 1, 1, 1, 0, big(0), Calendar.GREGORIAN); //repeating decimal; some rounding 
    ex.doubleConversionJd(2025, 1, 1, 1, 30, big(0), Calendar.GREGORIAN);  
    ex.doubleConversionJd(2025, 1, 1, 17, 32, big(56.00000001), Calendar.GREGORIAN);  //repeating decimal; some rounding 
    ex.doubleConversionJd(1599, 10, 11, 19, 56, big(47.98123), Calendar.GREGORIAN);  //repeating decimal; some rounding
    Date date = Date.from(-4799, 1, 1, Calendar.GREGORIAN);
    System.out.println("-4799 Jan 1: " + date.jd(TimescaleImpl.TAI).jd());
    
    JulianDate jd = JulianDate.from(JulianDate.MODIFIED_JD_ORIGIN, TimescaleImpl.TAI);
    DateTime dt = jd.toDateTime(Calendar.GREGORIAN);
    System.out.println(dt);
  }
  
  private void doubleConversion(long year, int month, int day, Calendar fromCalendar, Calendar toCalendar) {
    Date d = Date.from(year, month, day, fromCalendar);
    Date dc = d.convertTo(toCalendar);
    Date dcc = dc.convertTo(fromCalendar);
    System.out.println(d + " to " + dc + " back to " + dcc);
  }
  
  private void doubleConversion(long year, int month, int day, Calendar calendar) {
    Date d = Date.from(year, month, day, calendar);
    JulianDate jd = d.jd(TimescaleImpl.TAI);
    DateTime d2 = jd.toDateTime(calendar);
    System.out.println(d + " " + jd + " " + d2);
  }
  
  private void doubleConversionJd(long year, int month, int day, int hour, int minute, BigDecimal seconds, Calendar calendar) {
    Date d = Date.from(year, month, day, calendar);
    Timescale timescale = TimescaleImpl.TAI;
    Time t = Time.from(hour, minute, seconds, timescale);
    DateTime dt = DateTime.from(d, t);
    JulianDate jd = dt.toJulianDate();
    DateTime dt2 = jd.toDateTime(calendar);
    System.out.println(dt + " " + jd + " " + dt2);
  }
  
  

}
