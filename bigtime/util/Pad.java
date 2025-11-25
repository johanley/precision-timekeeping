package bigtime.util;

/** Simple padding with 0s. */
public final class Pad {
  
  /** Pad a number less than 10 with one zero on the left. */
  public static String zeroPad(int value) {
    return padding(value) + value;
  }
  
  /** Pad a number less than 10 with one zero on the left. */
  public static String zeroPad(double value) {
    return padding(value) + value;
  }

  private static String padding(double value) {
    return  value < 10 ? "0" : "";
  }
}
