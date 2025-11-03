package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;
import static astrolib.util.Consts.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import astrolib.util.Check;

/** 
 Round seconds to 0 or more decimal places.
 
 <P>It's important to note that this operation can result in a value that's greater than 59.
 
 <P>Arithmetic with {@link BigDecimal} can often result in objects that carry 'too many' decimal places.
 This class is meant to reduce the effort needed to work with seconds modeled as a {@link BigDecimal}. 
*/
final class RoundSeconds {
  
  /** 
   Factory method.
   @param places must be non-negative.
  */
  static RoundSeconds to(int places, RoundingMode roundingMode) {
    return new RoundSeconds(places, roundingMode);
  }
  
  /** 
   Apply a rounding to the given value.
   @param seconds is less than 60.0.
   @return a result that can be 60.0 (after rounding 59.99999, for example). 
   The result's overflow flag will be set to true only if the result is 60.0 or more.
   That flag should almost always be interrogated by the caller.
  */
  Result apply(BigDecimal seconds) {
    if (seconds.compareTo(SIXTY) >= 0) {
      throw new IllegalArgumentException("Can't round a seconds value that is 60.0 or more: " + seconds);
    }
    int precision = desiredPrecisionFor(seconds);
    BigDecimal rounded = seconds.round(new MathContext(precision, roundingMode));
    return new Result(rounded);
  }
  
  /**
   The result of a rounding operation.
   The overflow method returns true only if the result is 60.0 or more.
   The overflow method should almost always be interrogated by the caller. 
  */
  static final class Result {
    private Result(BigDecimal val) {
      this.value = val;
      this.overflows = val.compareTo(SIXTY) >= 0;
    }
    BigDecimal val() { return value; }
    Boolean overflows() { return overflows; }
    private BigDecimal value;
    private Boolean overflows;
  }
  
  private int places;
  private RoundingMode roundingMode;
  private RoundSeconds(int places, RoundingMode roundingMode) {
    Check.nonNegative(places);
    this.places = places;
    this.roundingMode = roundingMode;
  }
  
  private static final BigDecimal SIXTY = big(SECONDS_PER_MINUTE);
  
  private int desiredPrecisionFor(BigDecimal val) {
    int res = places; //0.123
    BigDecimal nominal = val.abs();
    if (nominal.compareTo(BigDecimal.ONE) >= 0) {
      res = res + 1; //1.123
    }
    if (nominal.compareTo(BigDecimal.TEN) >= 0) {
      res = res + 1; //10.123
    }
    return res;
  }
}