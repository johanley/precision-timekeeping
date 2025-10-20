package astrolib.util;

/** 
 Simple utility logging methods.
 
 Centralizing these policies makes it easier to change them, 
 if the logging requirements change. 
*/
public final class LogUtil {
  
  public static void log(Object thing) {
    if (LOGGING_ON) {
      System.out.println(thing.toString());
    }
  }
  
  public static void warn(Object thing) {
    if (LOGGING_ON) {
      System.out.println("WARNING!!: " + thing.toString());
    }
  }
  
  /** Pad a number less than 10 with one zero on the left. */
  public static String zeroPad(int value) {
    return padding(value) + value;
  }
  
  /** Pad a number less than 10 with one zero on the left. */
  public static String zeroPad(double value) {
    return padding(value) + value;
  }

  /** In a servlet environment, it may be convenient to turn off all logging. */
  private static final boolean LOGGING_ON = true;
  
  private static String padding(double value) {
    return  value < 10 ? "0" : "";
  }


}
