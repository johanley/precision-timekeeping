package astrolib.when;

import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static astrolib.when.BigDecimalHelper.*;

/**
 Unit tests.
 These tests are indirect.
 They test the method in {@link DateTime} that uses an {@link Odometer}.
*/
public final class OdometerTEST {
  
  @Test public void rollover() {
    Calendar cal = Calendar.GREGORIAN;
    Timescale ts = TimescaleCommon.TT;
    RoundingMode mode = RoundingMode.HALF_EVEN;
    BigDecimal zero = BigDecimal.ZERO;
    
    //rollover the minute only
    DateTime dt = DateTime.from(Date.from(2025, 12, 31, cal), Time.from(0, 0, big(59.999), ts));
    DateTime result = dt.roundSeconds(2, mode); //60, so next minute
    DateTime expected = DateTime.from(Date.from(2025, 12, 31, cal), Time.from(0, 1, zero, ts));
    assertEquals(expected, result);
    assertEquals(expected.date().calendar(), result.date().calendar());
    assertEquals(expected.time().timescale(), result.time().timescale());
    
    //rollover the minute and the hour
    dt = DateTime.from(Date.from(2025, 12, 31, cal), Time.from(0, 59, big(59.999), ts));
    result = dt.roundSeconds(2, mode); 
    expected = DateTime.from(Date.from(2025, 12, 31, cal), Time.from(1, 0, zero, ts));
    assertEquals(expected, result);

    //rollover the minute, hour, and day
    dt = DateTime.from(Date.from(2025, 12, 30, cal), Time.from(23, 59, big(59.999), ts));
    result = dt.roundSeconds(2, mode); 
    expected = DateTime.from(Date.from(2025, 12, 31, cal), Time.from(0, 0, zero, ts));
    assertEquals(expected, result);

    //rollover the minute, hour, day, and month
    dt = DateTime.from(Date.from(2025, 1, 31, cal), Time.from(23, 59, big(59.999), ts));
    result = dt.roundSeconds(2, mode); 
    expected = DateTime.from(Date.from(2025, 2, 1, cal), Time.from(0, 0, zero, ts));
    assertEquals(expected, result);

    //rollover the minute, hour, day, month, and year
    dt = DateTime.from(Date.from(2025, 12, 31, cal), Time.from(23, 59, big(59.999), ts));
    result = dt.roundSeconds(2, mode); 
    expected = DateTime.from(Date.from(2026, 1, 1, cal), Time.from(0, 0, zero, ts));
    assertEquals(expected, result);

    //rollover into a leap day
    dt = DateTime.from(Date.from(1960, 2, 28, cal), Time.from(23, 59, big(59.999), ts));
    result = dt.roundSeconds(2, mode); 
    expected = DateTime.from(Date.from(1960, 2, 29, cal), Time.from(0, 0, zero, ts));
    assertEquals(expected, result);

    //rollover out of a leap day
    dt = DateTime.from(Date.from(1960, 2, 29, cal), Time.from(23, 59, big(59.999), ts));
    result = dt.roundSeconds(2, mode); 
    expected = DateTime.from(Date.from(1960, 3, 1, cal), Time.from(0, 0, zero, ts));
    assertEquals(expected, result);
  }
}
