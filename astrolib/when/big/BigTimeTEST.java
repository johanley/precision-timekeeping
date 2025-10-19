package astrolib.when.big;

import static org.junit.Assert.*;
import static astrolib.when.big.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.Test;

/** 
 Each test for a year is done with both the positive and negative value for the year. 
 Tests on the JulianDate methods are done elsewhere, on the helper class that does the calculation.
*/
public class BigTimeTEST {

  @Test public void ctor() {
    BigTime t = BigTime.from(1, 2, big(3.14), BigTimescaleCommon.TAI);
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
    BigTime t = BigTime.from(big(0.25), BigTimescaleCommon.TAI);
    assertEquals(6, t.hour());
    assertEquals(0, t.minute());
    assertEquals(big("0.00"), t.seconds()); //I guess I'm okay with this; the precision varies with the data

    t = BigTime.from(12, 0, BigDecimal.ZERO, BigTimescaleCommon.TAI);
    assertEquals(big("0.5"), t.fraction()); 

    t = BigTime.from(12, 30, BigDecimal.ZERO, BigTimescaleCommon.TAI);

   // non-terminating decimals are tricky; you need to provide a MathContext
    assertEquals(big("0.5208333333333333"), t.fraction()); 
  }
  
  @Test public void fractionFails() {
    fractionFails(BigDecimal.ONE.negate());
    fractionFails(BigDecimal.ONE);
    fractionFails(BigDecimal.TEN);
  }

  private void ctorFails(int hour, int minute, BigDecimal seconds) {
    assertThrows(IllegalArgumentException.class, () -> {BigTime.from(hour, minute, seconds, BigTimescaleCommon.TAI);} );
  }
  
  private void fractionFails(BigDecimal fraction) {
    assertThrows(IllegalArgumentException.class, () -> {BigTime.from(fraction, BigTimescaleCommon.TAI);} );
  }
  

  private void ctorSucceeds(int hour, int minute, BigDecimal seconds) {
    BigTime t = BigTime.from(hour, minute, seconds, BigTimescaleCommon.TAI);
  }
  
}
