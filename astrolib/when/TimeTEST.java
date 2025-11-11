package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

/** 
 Unit tests.
 Each test for a year is done with both the positive and negative value for the year. 
 Tests on the JulianDate methods are done elsewhere, on the helper class that does the calculation.
*/
public final class TimeTEST {

  @Test public void ctor() {
    Time t = Time.from(1, 2, big(3.14), TimescaleCommon.TAI);
    assertEquals(1, t.hour());
    assertEquals(2, t.minute());
    assertEquals(big(3.14), t.seconds());
  }
  
  @Test public void ctorFails() {
    ctorFails(-1, 0, BigDecimal.ZERO);
    ctorFails(24, 0, BigDecimal.ZERO);
    ctorFails(0, -1, BigDecimal.ZERO);
    ctorFails(0, 60, BigDecimal.ZERO);
    ctorFails(0, 0, big(-1));
    ctorFails(0, 0, big(60));
  }

  @Test public void ctorSucceeds() {
    ctorSucceeds(0, 0, BigDecimal.ZERO);
    ctorSucceeds(23, 0, BigDecimal.ZERO);
    ctorSucceeds(0, 1, BigDecimal.ZERO);
    ctorSucceeds(0, 59, BigDecimal.ZERO);
    ctorSucceeds(0, 0, big(1));
    ctorSucceeds(0, 0, big(59));
  }
  
  @Test public void fraction() {
    Time t = Time.from(big(0.25), TimescaleCommon.TAI);
    assertEquals(6, t.hour());
    assertEquals(0, t.minute());
    assertEquals(big("0.00"), t.seconds()); //I guess I'm okay with this; the precision varies with the data

    t = Time.from(12, 0, BigDecimal.ZERO, TimescaleCommon.TAI);
    assertEquals(big("0.5"), t.fraction()); 

    t = Time.from(12, 30, BigDecimal.ZERO, TimescaleCommon.TAI);
    // non-terminating decimals are tricky
    assertEquals(big("0.5208333333333333333333333333333333"), t.fraction()); 
  }
  
  @Test public void fractionFails() {
    fractionFails(BigDecimal.ONE.negate());
    fractionFails(BigDecimal.ONE);
    fractionFails(BigDecimal.TEN);
  }

  private void ctorFails(int hour, int minute, BigDecimal seconds) {
    assertThrows(IllegalArgumentException.class, () -> {Time.from(hour, minute, seconds, TimescaleCommon.TAI);} );
  }
  
  private void fractionFails(BigDecimal fraction) {
    assertThrows(IllegalArgumentException.class, () -> {Time.from(fraction, TimescaleCommon.TAI);} );
  }

  @SuppressWarnings("unused")
  private void ctorSucceeds(int hour, int minute, BigDecimal seconds) {
    Time t = Time.from(hour, minute, seconds, TimescaleCommon.TAI);
  }
}
