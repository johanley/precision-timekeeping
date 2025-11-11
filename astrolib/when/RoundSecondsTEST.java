package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;
import static org.junit.Assert.*;

import java.math.RoundingMode;

import org.junit.Test;

/** Unit tests. */
public final class RoundSecondsTEST {

  @Test public void rounding() {
    roundingNoOverflow("0.123456", 3, "0.123");
    roundingNoOverflow("1.123456", 3, "1.123");
    roundingNoOverflow("10.123456", 3, "10.123");
    roundingNoOverflow("59.123456", 3, "59.123");
    
    roundingNoOverflow("0.1239", 3, "0.124");
    roundingNoOverflow("1.1239", 3, "1.124");
    roundingNoOverflow("10.1239", 3, "10.124");
    roundingNoOverflow("59.1239", 3, "59.124");
    
    roundingNoOverflow("0.123456", 2, "0.12");
    roundingNoOverflow("0.123456", 1, "0.1");
    
    roundingNoOverflow("32.123456", -1, "30");
    
    roundingNoOverflow("59.994", 2, "59.99");
  }
  
  @Test public void fails() {
    roundingFails("60.123456", 2); //60 or more will fail
    roundingFails("12.123456", -2); //max num places is -1
  }
  
  @Test public void roundingOverflow() {
    roundingOverflow("59.999", 2, "60");
    roundingOverflow("59.998", 2, "60");
    roundingOverflow("59.996", 2, "60");
    roundingOverflow("59.995", 2, "60");
    roundingOverflow("59.99", 1, "60");
    roundingOverflow("59.99", 0, "60");
    roundingOverflow("59.99", -1, "60");
    roundingOverflow("59.9", 0, "60");
  }
  
  private void roundingNoOverflow(String input, int numPlaces, String expected) {
    RoundSeconds rounder = RoundSeconds.to(numPlaces, RoundingMode.HALF_EVEN);
    RoundSeconds.Result result = rounder.apply(big(input));
    assertFalse(result.overflows());
    //assertEquals(big(expected), result.val());
    assertTrue(big(expected).compareTo(result.val()) == 0); //same 'cohort'; avoid equals

    //check the negative values at the same time
    result = rounder.apply(big(input).negate());
    assertFalse(result.overflows());
    //assertEquals(big(expected).negate(), result.val());
    assertTrue(big(expected).negate().compareTo(result.val()) == 0);
  }

  private void roundingFails(String input, int numPlaces) {
    assertThrows(
      IllegalArgumentException.class, 
      () -> {
        RoundSeconds.to(numPlaces).apply(big(input));
      }
    );
  }
  
  private void roundingOverflow(String input, int numPlaces, String expected) {
    RoundSeconds rounder = RoundSeconds.to(numPlaces, RoundingMode.HALF_EVEN);
    RoundSeconds.Result result = rounder.apply(big(input));
    assertTrue(result.overflows());
    assertTrue(big(expected).compareTo(result.val()) == 0); //same 'cohort'; avoid equals

    //check the negative values at the same time
    result = rounder.apply(big(input).negate());
    assertTrue(result.overflows());
    assertTrue(big(expected).negate().compareTo(result.val()) == 0);
  }
}
