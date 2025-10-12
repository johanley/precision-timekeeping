package astrolib.util;

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