package bigtime.when;

import static bigtime.when.BigDecimalHelper.*;
import static bigtime.when.Calendar.*;
import static bigtime.when.TimescaleImpl.*;
import static java.math.RoundingMode.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Test;

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
    
    //1997  1  1 -30111.0800  0.0364   UT1 is 30.11108 seconds behind TAI: 
    convert(year, month, day, hour, minute, seconds, GREGORIAN, TAI, UT1, year, month, day, hour, minute - 1, big(60-30.11108), 3);
    
    //UTC is supported by default only back to 2017-01-01
  }
  
  /** Going in a circle a..b..a gets you back to the starting point, up to sub-millisecond precision. */
  @Test public void circularConversion() {
    circularConversion(3); //seconds to 3 decimal places
    circularConversion(4);
    circularConversion(5);
    circularConversion(6);
    
    //breaks for UT1, when 7 decimals used
    //circularConversion(7); 
    //  expected:<2018-01-01 GR 00:02:01.0000000 TAI> but was:<2018-01-01 GR 00:02:00.9999996 TAI>
    //since the goal here is to have all conversions valid to sub-millisecond level only (4 decimals)
    //this is deemed acceptable
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
  
  private void circularConversion(int numDecimals) {
    //all timescales are defined for this date
    int year = 2018; 
    int month = 1;
    int day = 1;
    int hour = 0;
    int minute = 2;
    String decimals = ".";
    for(int i=1; i<=numDecimals; ++i) {
      decimals = decimals + "0";
    }
    BigDecimal seconds = big("1" + decimals);
    Calendar cal = GREGORIAN;
    DateTime when1 = DateTime.from(year, month, day, hour, minute, seconds, cal, TAI);
    for(Timescale to : TimescaleImpl.values()) {
      Optional<DateTime> when2 = Timescale.convertTo(to, when1);
      Optional<DateTime> when3 = Timescale.convertTo(TAI, when2.get());
      assertEquals(when1, when3.get().roundSeconds(numDecimals, HALF_EVEN));
    }
  }
}