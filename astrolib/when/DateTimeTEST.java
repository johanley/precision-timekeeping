package astrolib.when;

import org.junit.Test;
import static org.junit.Assert.*;

import java.math.RoundingMode;

import static astrolib.when.BigDecimalHelper.*;

public class DateTimeTEST {
  
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
    //plusMinusSeconds(dt, "60", 0, DateTime.from(date, Time.from(0,1,big(0),ts)));
    plusMinusSeconds(dt, "61", 0, DateTime.from(date, Time.from(0,1,big(1),ts)));
    //plusMinusSeconds(dt, "59.999", DateTime.from(date, Time.from(0,0,big(59.999),ts)));
    plusMinusSeconds(dt, "1.8", 1, DateTime.from(date, Time.from(0,0,big(1.8),ts)));
    plusMinusSeconds(dt, "9.123456", 6, DateTime.from(date, Time.from(0,0,big(9.123456),ts)));
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
