package astrolib.when.big;

import java.math.BigDecimal;
import java.math.BigInteger;

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
  
  /** Discard the fractional part. */
  public static BigInteger integer(BigDecimal bd) {
    return bd.toBigInteger();
  }

  /** Return the fractional part. */
  public static BigDecimal decimals(BigDecimal bd) {
    BigDecimal[] parts = bd.divideAndRemainder(BigDecimal.ONE);
    return parts[1];
  }

}
