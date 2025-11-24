package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;
import static astrolib.when.TimescaleImpl.*;
import static org.junit.Assert.*;

import java.math.RoundingMode;

import org.junit.Test;

import astrolib.util.Mathy;

/** Unit test. */
public final class TimescaleImplTEST {
  
  @Test public void secondsFromTAI() {
    Date date = Date.gregorian(2025, 9, 7);
    test(TAI, "0", DateTime.from(date, Time.zero(TAI)));
    test(TT, "32.184", DateTime.from(date, Time.zero(TT)));
    test(GPS, "-19", DateTime.from(date, Time.zero(GPS)));
    test(UTC, "-37", DateTime.from(date, Time.zero(UTC)));
    
    //TDB for J2000, to 9 decimal places
    double g = Math.toRadians(357.53);
    int DECIMAL_PLACES = 9;
    Double val =  Mathy.round(TimescaleImpl.TT_MINUS_TAI + 0.001658 * Math.sin(g) + 0.000014 * Math.sin(2*g), DECIMAL_PLACES); //seconds
    test(TDB, val.toString(), 
      DateTime.from(Date.from(2000, 1, 1, Calendar.GREGORIAN), Time.from(big(0.5), TimescaleImpl.TT)), 
      DECIMAL_PLACES
    );
    
    //page 15 of the SOFA cookbook
    date = Date.gregorian(1977, 1, 1);
    Time time = Time.zero(TimescaleImpl.TAI);
    DateTime dt = DateTime.from(date, time);
    //System.out.println(dt.toJulianDate());
    //32.184 - 65.5 microseconds
    //32.184_000_0 - 0.000_065_5  = 32.183_934_5
    test(TDB, "32.1839", dt, 4); //0.1 msec
    
    //2016  9 20 -36265.3820  0.0227 
    date = Date.gregorian(2016, 9, 20);
    time = Time.zero(TimescaleImpl.UTC);
    test(UT1, "-36.2653820", DateTime.from(date, time));
  }
  
  @Test public void overrideForUTC() {
    overrideForUTC("0");
    overrideForUTC("1");
    overrideForUTC("-1");
    overrideForUTC("-40");
    overrideForUTC("40");
    
    overrideForUTCFails("blah");
    overrideForUTCFails("0.0");
    overrideForUTCFails("0.1");
    overrideForUTCFails("-40.0");
  }
  
  /** Assumes the conversion always works.  */
  private void test(Timescale timescale, String secondsFromTAI, DateTime when) {
    assertEquals(big(secondsFromTAI), timescale.secondsFromTAI(when).get());
  }
  
  /** Assumes the conversion always works.  */
  private void test(Timescale timescale, String expectedSecondsFromTAI, DateTime when, int numPlaces) {
    assertEquals(big(expectedSecondsFromTAI), round(timescale.secondsFromTAI(when).get(), numPlaces, RoundingMode.HALF_EVEN));
  }
  
  private void overrideForUTCFails(String val) {
    String key = TimescaleImpl.UTC_SYS_PROPERTY;
    System.setProperty(key, val);
    assertThrows(IllegalArgumentException.class, () -> UTC.secondsFromTAI(null));
    System.clearProperty(key);
  }
  
  private void overrideForUTC(String val) {
    String key = TimescaleImpl.UTC_SYS_PROPERTY;
    System.setProperty(key, val);
    test(UTC, val, null);
    System.clearProperty(key);
  }
}
