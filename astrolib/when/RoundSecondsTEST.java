package astrolib.when;

import org.junit.Test;
import java.math.RoundingMode;
import org.junit.Assert;
import static astrolib.when.BigDecimalHelper.*;
import static org.junit.Assert.*;

public class RoundSecondsTEST {

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

  }
  
  private void roundingNoOverflow(String input, int places, String expected) {
    RoundSeconds rounder = RoundSeconds.to(places, RoundingMode.HALF_EVEN);
    RoundSeconds.Result result = rounder.apply(big(input));
    assertFalse(result.overflows());
    assertEquals(big(expected), result.val());

    //check the negative values at the same time
    result = rounder.apply(big(input).negate());
    assertFalse(result.overflows());
    assertEquals(big(expected).negate(), result.val());
  }

  
}
