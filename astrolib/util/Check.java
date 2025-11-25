package astrolib.util;

import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;

/** Common boolean checks and validations. */
public final class Check {

  /** Return true only if the text has visible content. */
  public static boolean textHasContent(String text) {
    return text != null && text.trim().length() > 0;
  }
  
  /** Throw exception if given value is not in the range [min, max]. The max is included. */
  public static void range(Integer val, Integer min, Integer max) {
    if (val < min || val > max) {
      throw new IllegalArgumentException(val + " is not in range [" + min + ".." + max + "]");
    }
  }
  
  /** Throw exception if given value is not in range [min, max). The max is excluded. */
  public static void range(Double val, Double min, Double max) {
    if (val < min || val >= max) {
      throw new IllegalArgumentException(val + " is not in range [" + min + ".." + max + ")");
    }
  }
  
  /** Throw exception if given value is not in range [min, max). The max is excluded. */
  public static void range(BigDecimal val, Double min, Double max) {
    BigDecimal aMin = big(min);
    BigDecimal aMax = big(max);
    if (val.compareTo(aMin) < 0 || val.compareTo(aMax) >= 0) {
      throw new IllegalArgumentException(val + " is not in range [" + aMin.toPlainString() + ".." + aMax.toPlainString() + ")");
    }
  }

  public static void nonNegative(Double val) {
    if (val < 0.0) {
      throw new IllegalArgumentException(val + " is negative: " + val);
    }
  }

  /** More than 0. */
  public static void positive(int val) {
    if (val <= 0) {
      throw new IllegalArgumentException(val + " is not positive: " + val);
    }
  }

  public static void nonNegative(int val) {
    if (val < 0) {
      throw new IllegalArgumentException(val + " is negative: " + val);
    }
  }
  
}