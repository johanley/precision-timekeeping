package astrolib.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import astrolib.util.Check;

/** Helper methods for {@link BigDecimal} objects. */
public final class BigDecimalHelper {
  
  /** The integer part of a {@link BigDecimal#divideAndRemainder(BigDecimal)} operation. */
  public static final int INTEGER_PART = 0;
  /** The remainder part of a {@link BigDecimal#divideAndRemainder(BigDecimal)} operation. */
  public static final int REMAINDER = 1;

  /** Make a {@link BigDecimal} from a String. */
  public static BigDecimal big(String val) {
    return new BigDecimal(val);
  }
  
  /** Make a {@link BigDecimal} from a long. */
  public static BigDecimal big(long val) {
    return BigDecimal.valueOf(val);
  }

  /** Make a {@link BigDecimal} from a double. */
  public static BigDecimal big(double val) {
    return BigDecimal.valueOf(val);
  }
  
  /** Discard the fractional part. -9.2 yields -9 (rounds toward 0). */
  public static BigInteger integer(BigDecimal bd) {
    return bd.toBigInteger();
  }
  
  /** Return the fractional part. */
  public static BigDecimal decimals(BigDecimal bd) {
    BigDecimal[] parts = bd.divideAndRemainder(BigDecimal.ONE);
    return parts[1];
  }

  /** Rounds towards negative infinity. */
  public static BigInteger floor(BigDecimal bd) {
    return bd.setScale(0, RoundingMode.FLOOR).toBigInteger();
  }

  /** 
   Wrapper for the {@link BigDecimal#divide(BigDecimal)} method.
   <P>In the case of a non-terminating decimal, the return value's precision is {@link #infiniteCutoffPrecision()}. 
  */
  public static BigDecimal divide(BigDecimal a, BigDecimal b) {
    BigDecimal res = null;
    try {
      res = a.divide(b);
    }
    catch(ArithmeticException ex) {
      res = a.divide(b, new MathContext(infiniteCutoffPrecision(), RoundingMode.HALF_EVEN));
    }
    return res; 
  }

  /**
   Wrapper for the {@link BigDecimal#divideAndRemainder(BigDecimal)} method.
   <P>In the case of a non-terminating decimal, the return value's precision is {@link #infiniteCutoffPrecision()}. 
  */
  public static BigDecimal[] divideAndRemainder(BigDecimal a, BigDecimal b) {
    BigDecimal[] res = null;
    try {
      res = a.divideAndRemainder(b);
    }
    catch(ArithmeticException ex) {
      res = a.divideAndRemainder(b, new MathContext(infiniteCutoffPrecision(), RoundingMode.HALF_EVEN));
    }
    return res; 
  }

  /** 
   The number of digits to use when 'cutting off' infinite decimals.
   By default, returns the same precision as in {@link MathContext#DECIMAL128}. 
   This can be overridden, by setting a System property named <em>big-decimal-division-precision</em> to a positive integer.  
  */
  public static int infiniteCutoffPrecision() {
    String override = System.getProperty(OVERRIDE_INFINITE_CUTOFF);
    return Check.textHasContent(override) ? Integer.valueOf(override) : MathContext.DECIMAL128.getPrecision();
  }

  /** 
   Round the given {@link BigDecimal} to the given number of decimal places.
   To round '123' to '120', for example, pass '-1' as the value for the number of places.
   This implementation uses {@link RoundingMode#HALF_EVEN}.
   
   @param places can be positive, 0, or negative.  
  */
  public static BigDecimal round(BigDecimal val, int places, RoundingMode roundingMode) {
    return val.setScale(places, roundingMode);
  }
  
  /**
   {@value}. 
   To override the value returned by {@link #infiniteCutoffPrecision()}, set a System property using this name.
   Use a positive integer. 
  */
  public static final String OVERRIDE_INFINITE_CUTOFF = "big-decimal-division-precision";
  
}
