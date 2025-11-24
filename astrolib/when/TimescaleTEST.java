package astrolib.when;

import java.math.BigDecimal;
import static java.math.RoundingMode.*;

import static astrolib.when.BigDecimalHelper.*;
import static astrolib.when.TimescaleImpl.*;
import static astrolib.when.Calendar.*;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit test. */
public final class TimescaleTEST {
  
  @Test public void convertOneMomentToManyTimescales() {
    int year = 1997;
    int month = 1;
    int day = 1;
    int hour = 0;
    int minute = 2;
    BigDecimal seconds = big(0);
    convert(year, month, day, hour, minute, seconds, GREGORIAN, TAI, TT, year, month, day, hour, minute, big(32.184), 3);
    convert(year, month, day, hour, minute, seconds, GREGORIAN, TAI, TDB, year, month, day, hour, minute, big(32.184).subtract(big("0.0000655")),4);
    convert(year, month, day, hour, minute, seconds, GREGORIAN, TAI, GPS, year, month, day, hour, minute - 1, big(41), 3);
    //1997  1  1 -30111.0800  0.0364   30.11108 seconds behind TAI 
    convert(year, month, day, hour, minute, seconds, GREGORIAN, TAI, UT1, year, month, day, hour, minute - 1, big(60-30.11108), 3);
    //convert(year, month, day, hour, minute, seconds, GREGORIAN, TAI, UTC, year, month, day, hour, minute - 1, big(23), 3);
  }
  
  private void convert(
    int year, int month, int day, int hour, int minute, BigDecimal seconds, Calendar calendar, Timescale from, 
    Timescale toTimescale,
    int yearE, int monthE, int dayE, int hourE, int minuteE, BigDecimal secondsE,
    int decimals
  ) {
    DateTime when = DateTime.from(year, month, day, hour, minute, seconds, calendar, from);
    DateTime convertedWhen = Timescale.convertTo(toTimescale, when).get().roundSeconds(decimals, HALF_EVEN); 
    DateTime whenExpected = DateTime.from(yearE, monthE, dayE, hourE, minuteE, secondsE, calendar, toTimescale).roundSeconds(decimals, HALF_EVEN);
    assertEquals(whenExpected, convertedWhen);
  }

}
