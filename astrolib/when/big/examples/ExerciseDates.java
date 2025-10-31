package astrolib.when.big.examples;

import java.math.BigDecimal;
import static astrolib.when.big.BigDecimalHelper.*;

import astrolib.when.big.BigCalendar;
import astrolib.when.big.BigDate;
import astrolib.when.big.BigDateTime;
import astrolib.when.big.BigJulianDate;
import astrolib.when.big.BigTime;
import astrolib.when.big.BigTimescale;
import astrolib.when.big.BigTimescaleCommon;

public class ExerciseDates {
  
  public static void main(String[] args) {
    ExerciseDates ex = new ExerciseDates();
    ex.doubleConversion(2025, 1, 1, BigCalendar.GREGORIAN, BigCalendar.JULIAN);
    ex.doubleConversion(-4713, 11, 24, BigCalendar.GREGORIAN, BigCalendar.JULIAN);
    ex.doubleConversion(2025,  1, 1, BigCalendar.GREGORIAN);
    ex.doubleConversionJd(2025, 1, 1, 0, 0, big(13.05), BigCalendar.GREGORIAN); //repeating decimal; some rounding 
    ex.doubleConversionJd(2025, 1, 1, 0, 0, big(0), BigCalendar.GREGORIAN); 
    ex.doubleConversionJd(2025, 1, 1, 12, 0, big(0), BigCalendar.GREGORIAN); 
    ex.doubleConversionJd(2025, 1, 1, 1, 0, big(0), BigCalendar.GREGORIAN); //repeating decimal; some rounding 
    ex.doubleConversionJd(2025, 1, 1, 1, 30, big(0), BigCalendar.GREGORIAN);  
    ex.doubleConversionJd(2025, 1, 1, 17, 32, big(56.00000001), BigCalendar.GREGORIAN);  //repeating decimal; some rounding 
    ex.doubleConversionJd(1599, 10, 11, 19, 56, big(47.98123), BigCalendar.GREGORIAN);  //repeating decimal; some rounding
  }
  
  private void doubleConversion(long year, int month, int day, BigCalendar fromCalendar, BigCalendar toCalendar) {
    BigDate d = BigDate.from(year, month, day, fromCalendar);
    BigDate dc = d.convertTo(toCalendar);
    BigDate dcc = dc.convertTo(fromCalendar);
    System.out.println(d + " to " + dc + " back to " + dcc);
  }
  
  private void doubleConversion(long year, int month, int day, BigCalendar calendar) {
    BigDate d = BigDate.from(year, month, day, calendar);
    BigJulianDate jd = d.jd(BigTimescaleCommon.TAI);
    BigDateTime d2 = jd.toDateTime(calendar);
    System.out.println(d + " " + jd + " " + d2);
  }
  
  private void doubleConversionJd(long year, int month, int day, int hour, int minute, BigDecimal seconds, BigCalendar calendar) {
    BigDate d = BigDate.from(year, month, day, calendar);
    BigTimescale timescale = BigTimescaleCommon.TAI;
    BigTime t = BigTime.from(hour, minute, seconds, timescale);
    BigDateTime dt = BigDateTime.from(d, t);
    BigJulianDate jd = dt.toJulianDate();
    BigDateTime dt2 = jd.toDateTime(calendar);
    System.out.println(dt + " " + jd + " " + dt2);
  }
  
  

}
