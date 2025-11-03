package astrolib.when;

import java.math.BigDecimal;
import static astrolib.when.BigDecimalHelper.*;
import static astrolib.when.TimescaleCommon.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class TimescaleTEST {
  
  @Test public void convertOneMomentToManyTimescales() {
    int year = 2025;
    int month = 11;
    int day = 15;
    int hour = 12;
    int minute = 30;
    BigDecimal seconds = big(30);
    convert(year, month, day, hour, minute, seconds, Calendar.GREGORIAN, TAI, TAI, year, month, day, hour, minute, seconds);
    convert(year, month, day, hour, minute, seconds, Calendar.GREGORIAN, TAI, TT, year, month, day, hour, minute + 1, big(2.184));
  }
  
  private void convert(
    int year, int month, int day, int hour, int minute, BigDecimal seconds, Calendar calendar, Timescale from, 
    Timescale toTimescale,
    int yearE, int monthE, int dayE, int hourE, int minuteE, BigDecimal secondsE 
  ) {
    DateTime when = DateTime.from(year, month, day, hour, minute, seconds, calendar, from);
    DateTime convertedWhen = Timescale.convertTo(toTimescale, when); //many decimal places
    DateTime whenExpected = DateTime.from(yearE, monthE, dayE, hourE, minuteE, secondsE, calendar, toTimescale);
    assertEquals(whenExpected, convertedWhen);
  }

}
