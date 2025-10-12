package astrolib.util;

public final class Mathy {
  
  /** Round the given value to the given number of (non-negative) decimals. */
  public static double round(double val, int numDecimals) {
    //start with 5.236, and round it to two decimals
    Double factor = Math.pow(10, numDecimals); //100
    Double temp = val * factor; //523.6
    Long result = Math.round(temp); //524
    return result.doubleValue() / factor; //5.24, this avoids integer division 
  }
  
  /** Zero is treated as positive. */
  public static int sign(double val) {
     return val < 0 ? -1 : +1; 
  }
  
  public static double sqr(double val) {
    return val * val;
  }
  
  /** Chop off the non-integral part of a number. For negative numbers, not the same as floor.*/
  public static double truncate(double value) {
    double result = Math.floor(value);
    if (value < 0) {
      result = result + 1;
    }
    return result;
  }
}
