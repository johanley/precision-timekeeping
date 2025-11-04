package astrolib.when;

import static astrolib.util.Consts.*;
import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** 
 Round seconds in the range [0, 60) to a certain number of decimal places.

 <P>Arithmetic with {@link BigDecimal} can often result in objects that carry too many decimal places for a given context.
 This class is meant to reduce the effort needed to work with seconds modeled as a {@link BigDecimal}. 
 
 <P>It's important to note that <b>such rounding operations can result in a value that's greater than 59.</b>
 For example, rounding 59.99 to 1 decimal place gives 60.
 This case needs usually needs special handling by the caller.
*/
final class RoundSeconds {
  
  /** 
   Factory method.
   @param numPlaces must be -1, 0, or a positive number.
   When numPlaces is -1, then the BigDecimal value is rounded to the nearest 10 seconds.
  */
  static RoundSeconds to(int numPlaces, RoundingMode roundingMode) {
    return new RoundSeconds(numPlaces, roundingMode);
  }

  /** As in the other factory method, but using the HALF_EVEN rounding mode. */
  static RoundSeconds to(int numPlaces) {
    return new RoundSeconds(numPlaces, RoundingMode.HALF_EVEN);
  }
  
  /** 
   Apply a rounding to the given BigDecimal value representing seconds in the range [0, 60).
   
   @param seconds is less than 60.
   @return a result that can be 60 (after rounding 59.99999, for example). 
   The result's overflow flag will be set to true only if the result is 60.
   That flag should almost always be interrogated by the caller.
  */
  Result apply(BigDecimal seconds) {
    if (seconds.abs().compareTo(SIXTY) >= 0) {
      throw new IllegalArgumentException("Can't round a seconds value whose absolute value is 60.0 or more: " + seconds);
    }
    BigDecimal res = BigDecimalHelper.round(numPlaces, seconds, roundingMode);
    return new Result(res);
  }
  
  /**
   The result of a rounding operation.
   The overflow method returns true only if the absolute value of the result is 60.
   The overflow method should almost always be interrogated by the caller. 
  */
  static final class Result {
    private Result(BigDecimal val) {
      this.value = val;
      this.overflows = val.abs().compareTo(SIXTY) >= 0;
    }
    BigDecimal val() { return value; }
    Boolean overflows() { return overflows; }
    private BigDecimal value;
    private Boolean overflows;
  }
  
  private int numPlaces;
  private RoundingMode roundingMode;
  private RoundSeconds(int places, RoundingMode roundingMode) {
    if (places < -1) {
      throw new IllegalArgumentException("Num places should be -1 or more: " + places);
    }
    this.numPlaces = places;
    this.roundingMode = roundingMode;
  }
  
  private static final BigDecimal SIXTY = big(SECONDS_PER_MINUTE);
}