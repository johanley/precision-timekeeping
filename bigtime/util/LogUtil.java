package bigtime.util;

/** Simple utility logging methods. */
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
  
  /** In some environments, it may be convenient to turn off all logging. */
  private static final boolean LOGGING_ON = true;
  
}
