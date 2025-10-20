package astrolib.when.big;

import java.math.BigDecimal;
import astrolib.util.Check;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.Test;

/** Curt operations with BigDecimal objects. */
public final class BigDecimalHelper {
  
  public static final int INT_DIV = 0;
  public static final int REMAINDER = 1;

  public static BigDecimal big(String val) {
    return new BigDecimal(val);
  }
  
  public static BigDecimal big(long val) {
    return BigDecimal.valueOf(val);
  }

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
   Wrapper for the <em>divide</em> method.
   <P>In the case of a non-terminating decimal, the return value's precision is {@link #bigDecimalDivisionPrecision()}. 
  */
  public static BigDecimal divvy(BigDecimal a, BigDecimal b) {
    BigDecimal res = null;
    try {
      res = a.divide(b);
    }
    catch(ArithmeticException ex) {
      res = a.divide(b, new MathContext(bigDecimalDivisionPrecision(), RoundingMode.HALF_EVEN));
    }
    return res; 
  }

  /**
   Wrapper for the <em>divideAndRemainder</em> method.
   <P>In the case of a non-terminating decimal, the return value's precision is {@link #bigDecimalDivisionPrecision()}. 
  */
  public static BigDecimal[] divvyAndRemainder(BigDecimal a, BigDecimal b) {
    BigDecimal[] res = null;
    try {
      res = a.divideAndRemainder(b);
    }
    catch(ArithmeticException ex) {
      res = a.divideAndRemainder(b, new MathContext(bigDecimalDivisionPrecision(), RoundingMode.HALF_EVEN));
    }
    return res; 
  }

  /** 
   The number of digits to use when 'cutting off' infinite decimals.
   By default, returns the same precision as in MathContext.DECIMAL64. 
   This can be overridden, by setting a System property named <em>big-decimal-division-precision</em> to a positive integer.  
  */
  public static int bigDecimalDivisionPrecision() {
    String override = System.getProperty("big-decimal-division-precision");
    return Check.textHasContent(override) ? Integer.valueOf(override) : MathContext.DECIMAL64.getPrecision();
  }

}
