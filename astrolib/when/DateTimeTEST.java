package astrolib.when;

import org.junit.Test;
import static org.junit.Assert.*;

import java.math.RoundingMode;

import static astrolib.when.BigDecimalHelper.*;

/** Unit tests. */
public final class DateTimeTEST {
  
  @Test public void plusMinusDays() {
    int year = 2026; //not a leap year, nor next to a leap year
    int month = 1;
    Timescale ts = TimescaleCommon.UTC;
    Date date = Date.gregorian(year, month, 1);
    Time time = Time.zero(TimescaleCommon.UTC);
    DateTime dt = DateTime.from(date, time);
    plusMinusDays(dt, "1", DateTime.from(Date.gregorian(year, month, 2), Time.zero(ts)));
    plusMinusDays(dt, "30", DateTime.from(Date.gregorian(year, month, 31), Time.zero(ts)));
    plusMinusDays(dt, "31", DateTime.from(Date.gregorian(year, month+1, 1), Time.zero(ts)));
    plusMinusDays(dt, "-1", DateTime.from(Date.gregorian(year-1, 12, 31), Time.zero(ts)));
    
    plusMinusDays(dt, "1.5", DateTime.from(Date.gregorian(year, month, 2), Time.from(12, 0, big(0), ts)));
    plusMinusDays(dt, "365", DateTime.from(Date.gregorian(year+1, month, 1), Time.zero(ts)));
    plusMinusDays(dt, "-365", DateTime.from(Date.gregorian(year-1, month, 1), Time.zero(ts)));
  }
  
  @Test public void plusMinusSeconds() {
    int year = 2026; //not a leap year, nor next to a leap year
    int month = 1;
    Timescale ts = TimescaleCommon.UTC;
    Date date = Date.gregorian(year, month, 1);
    Time time = Time.zero(TimescaleCommon.UTC);
    DateTime dt = DateTime.from(date, time);
    plusMinusSeconds(dt, "1", 0, DateTime.from(date, Time.from(0,0,big(1),ts)));
    plusMinusSeconds(dt, "59", 0, DateTime.from(date, Time.from(0,0,big(59),ts)));
    
    plusMinusSeconds(dt, "60", 0, DateTime.from(date, Time.from(0,1,big(0),ts)));
    
    plusMinusSeconds(dt, "61", 0, DateTime.from(date, Time.from(0,1,big(1),ts)));
    
    plusMinusSeconds(dt, "59.999", 3, DateTime.from(date, Time.from(0,0,big(59.999),ts)));
    plusMinusSeconds(dt, "59.999", 2, DateTime.from(date, Time.from(0,1,big(0),ts)));
    plusMinusSeconds(dt, "59.999", 1, DateTime.from(date, Time.from(0,1,big(0),ts)));
    plusMinusSeconds(dt, "59.999", 0, DateTime.from(date, Time.from(0,1,big(0),ts)));
    plusMinusSeconds(dt, "59.9", 0, DateTime.from(date, Time.from(0,1,big(0),ts)));
    
    plusMinusSeconds(dt, "1.8", 1, DateTime.from(date, Time.from(0,0,big(1.8),ts)));
    plusMinusSeconds(dt, "9.123456", 6, DateTime.from(date, Time.from(0,0,big(9.123456),ts)));
  }
  
  @Test public void daysFrom() {
    Date date = Date.gregorian(2025, 1, 1);
    Time time = Time.zero(TimescaleCommon.TT);
    DateTime start = DateTime.from(date, time);
    
    int numPlaces = 0;
    DateTime end = DateTime.from(Date.gregorian(2025, 1, 1), time);
    daysFrom(start, end, "0", numPlaces);
    
    end = DateTime.from(Date.gregorian(2025, 1, 2), time);
    daysFrom(start, end, "1", numPlaces);
    end = DateTime.from(Date.gregorian(2024, 12, 31), time);
    daysFrom(start, end, "-1", numPlaces);
    end = DateTime.from(Date.gregorian(2025, 2, 1), time);
    daysFrom(start, end, "31", numPlaces);
    end = DateTime.from(Date.gregorian(2025, 12, 31), time);
    daysFrom(start, end, "364", numPlaces);
    end = DateTime.from(Date.gregorian(2026, 1, 1), time);
    daysFrom(start, end, "365", numPlaces);
    end = DateTime.from(Date.gregorian(2024, 1, 1), time);
    daysFrom(start, end, "-366", numPlaces); //2024 is a leap year
    
    //Explanatory Supplement 1961, page 437 (large intervals)
    date = Date.gregorian(1800, 1, 1);
    start = DateTime.from(date, time);
    end = DateTime.from(Date.gregorian(1900, 1, 1), time);
    Integer res = 2415020 - 2378496;
    daysFrom(start, end, res.toString(), numPlaces);
    
    end = DateTime.from(Date.gregorian(1700, 1, 1), time);
    res = 2341972 - 2378496;
    daysFrom(start, end, res.toString(), numPlaces);
    
    end = DateTime.from(Date.gregorian(1600, 1, 1), time);
    res = 2305447 - 2378496;
    daysFrom(start, end, res.toString(), numPlaces);

    end = DateTime.from(Date.gregorian(1500, 1, 1), time);
    res = 2268923 - 2378496;
    daysFrom(start, end, res.toString(), numPlaces);
    
    //mix Julian calendar and Gregorian calendar:
    end = DateTime.from(Date.julian(1400, 1, 1), time);  
    res = 2232407 - 2378496;
    daysFrom(start, end, res.toString(), numPlaces);
    
    end = DateTime.from(Date.julian(-2000, 1, 1), time);  
    res = 990557 - 2378496;
    daysFrom(start, end, res.toString(), numPlaces);
  }
  
  @Test public void secondsFrom() {
    Date date = Date.gregorian(2025, 1, 1);
    Time time = Time.zero(TimescaleCommon.TT);
    DateTime start = DateTime.from(date, time);
    Timescale ts = TimescaleCommon.TT;
    int numPlaces = 0;
    
    DateTime end = DateTime.from(date, time);
    secondsFrom(start, end, "0", numPlaces);
    end = DateTime.from(date, Time.from(0, 0, big(1), ts));
    secondsFrom(start, end, "1", numPlaces);
    end = DateTime.from(date, Time.from(0, 0, big(59), ts));
    secondsFrom(start, end, "59", numPlaces);
    end = DateTime.from(date, Time.from(0, 1, big(0), ts));
    secondsFrom(start, end, "60", numPlaces);
    end = DateTime.from(date, Time.from(0, 1, big(1), ts));
    secondsFrom(start, end, "61", numPlaces);
    end = DateTime.from(date, Time.from(1, 1, big(1), ts));
    secondsFrom(start, end, "3661", numPlaces);
    end = DateTime.from(Date.gregorian(2025, 1, 2), time);
    secondsFrom(start, end, "86400", numPlaces);
    end = DateTime.from(Date.gregorian(2024, 12, 31), time);
    secondsFrom(start, end, "-86400", numPlaces);
  }
  
  private void daysFrom(DateTime start, DateTime end, String expected, int numPlaces) {
    RoundingMode mode = RoundingMode.HALF_EVEN;
    assertEquals(big(expected), end.daysFrom(start, numPlaces, mode));
  }
  
  private void secondsFrom(DateTime start, DateTime end, String expected, int numPlaces) {
    RoundingMode mode = RoundingMode.HALF_EVEN;
    assertEquals(big(expected), end.secondsFrom(start, numPlaces, mode));
  }
  
  private void plusMinusDays(DateTime input, String days, DateTime expected) {
    DateTime result = input.plusMinusDays(big(days), 0, MODE);
    assertEquals(expected, result);
  }

  private void plusMinusSeconds(DateTime input, String seconds, int numPlaces, DateTime expected) {
    DateTime result = input.plusMinusSeconds(big(seconds), numPlaces, MODE);
    assertEquals(expected, result);
  }
  
  private static RoundingMode MODE = RoundingMode.HALF_EVEN;
}
