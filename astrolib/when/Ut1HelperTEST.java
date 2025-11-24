package astrolib.when;

import java.math.BigDecimal;
import java.util.Optional;

import static astrolib.when.BigDecimalHelper.*;
import static org.junit.Assert.*;
import org.junit.Test;

/** Unit tests. */
public final class Ut1HelperTEST {
  
  @Test public void override() {
    System.setProperty(TimescaleImpl.UT1_SYS_PROPERTY, "-88.123");
    Date date = Date.gregorian(1852, 4, 12);
    Time time = Time.zero(timescale);
    Optional<BigDecimal> seconds = lookup(DateTime.from(date, time));
    assertEquals("-88.1230000", seconds.get().toString());
    System.clearProperty(TimescaleImpl.UT1_SYS_PROPERTY);
  }
  
  @Test public void beforeFirstDate() {
    Date date = Date.gregorian(1961, 12, 31);
    Time time = Time.zero(timescale);
    Optional<BigDecimal> seconds = lookup(DateTime.from(date, time));
    assertTrue(seconds.isEmpty());
  }
  
  @Test public void firstDate() {
    Date date = Date.gregorian(1962, 1, 1);
    Time time = Time.zero(timescale);
    Optional<BigDecimal> seconds = lookup(DateTime.from(date, time));
    assertEquals("-1.8132242", seconds.get().toString());
  }
  
  @Test public void intermediateExactDate() {
    //2016  9 20 -36265.3820  0.0227 
    Date date = Date.gregorian(2016, 9, 20);
    Time time = Time.zero(timescale);
    Optional<BigDecimal> seconds = lookup(DateTime.from(date, time));
    assertEquals("-36.265382", seconds.get().doubleValue() + "");
  }
  
  @Test public void interpolate() {
    /*
    1962  1  1 -1813.2242  2.0000 
    1962  1  2 -1814.9265  2.0000 
    */
    Date date = Date.gregorian(1962, 1, 1);
    Time time = Time.from(big(0.5), timescale);
    Optional<BigDecimal> seconds = lookup(DateTime.from(date, time));
    assertEquals("-1.8140754", seconds.get().doubleValue() + "");
  }
  
  /** THIS TEST IS BRITTLE, and breaks when the data is updated. */
  @Test public void lastDate() {
    Date date = Date.gregorian(2025, 10, 23);
    Time time = Time.zero(timescale);
    Optional<BigDecimal> seconds = lookup(DateTime.from(date, time));
    assertEquals("-36.9066007", seconds.get().doubleValue() + "");
  }
  
  /** THIS TEST IS BRITTLE, and breaks when the data is updated. */
  @Test public void afterLastDate() {
    Date date = Date.gregorian(2500, 1, 1);
    Time time = Time.zero(timescale);
    Optional<BigDecimal> seconds = lookup(DateTime.from(date, time));
    assertEquals("-36.9066007", seconds.get().doubleValue() + "");
  }

  private Optional<BigDecimal> lookup(DateTime dt) {
    return ut1.lookup(dt);
  }
  
  /** This reads in the large data file into memory. */
  private Ut1Helper ut1 = new Ut1Helper();
  private Timescale timescale = TimescaleImpl.UTC;
  
}
