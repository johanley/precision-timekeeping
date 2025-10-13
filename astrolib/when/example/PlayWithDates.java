package astrolib.when.example;

import static astrolib.util.LogUtil.*;

import astrolib.when.Calendar;
import static astrolib.when.Calendar.*;
import astrolib.when.Date;
import astrolib.when.DateTime;
import astrolib.when.JulianDate;
import astrolib.when.Time;
import astrolib.when.Timescale;
import static astrolib.when.Timescale.*;
import static astrolib.util.Consts.*;

public final class PlayWithDates {
  
  public static void main(String[] args) {
    log("Convert twice to view rounding differences: ");
    convertTwice(0, 1, 1,  0, 0, 0.0,  GREGORIAN, TT);
    convertTwice(0, 1, 1,  0, 0, 0.0,  JULIAN, TT);
    convertTwice(1952, 7, 11,  0, 0, 0.0,  GREGORIAN, TT);
    convertTwice(2029, 7, 11,  6, 30, 15.0,  GREGORIAN, TT); 
    convertTwice(2029, 7, 11,  6, 30, 15.1234,  GREGORIAN, TT); 
    convertTwice(2029, 7, 11,  6, 30, 15.12345,  GREGORIAN, TT); //last decimal changes, nearing ~0.01ms 

    //Explanatory Supplement 1961, page 417
    log(NL + "Convert from the Gregorian calendar to the Julian calendar: ");
    convertBetweenCalendars(200, 3, 1);
    convertBetweenCalendars(300, 3, 1);
    convertBetweenCalendars(300, 3, 2);
    convertBetweenCalendars(1800, 3, 11);
  }

  /** Convert in one direction, then in the reverse direction. */
  private static void convertTwice(int y, int mon, int d,   int h, int min, double s,   Calendar calendar, Timescale timescale) {
    DateTime dt = DateTime.from(y, mon, d, h, min, s, calendar, timescale);
    JulianDate jd = dt.toJulianDate();
    DateTime dtAgain = jd.toDateTime(calendar);
    log(dt + " => " + jd + " => " + dtAgain);
  }
  
  private static void convertBetweenCalendars(int year, int month, int day) {
    Date dt = Date.from(year, month, day, GREGORIAN);
    Date dtConverted = dt.convertTo(JULIAN);
    log(dt + " = " + dtConverted);
  }
}
