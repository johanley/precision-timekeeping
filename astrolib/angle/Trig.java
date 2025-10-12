package astrolib.angle;

public final class Trig {

  public static double sin(Angle θ) {
    return Math.sin(θ.rads());
  }
  
  public static double cos(Angle θ) {
    return Math.cos(θ.rads());
  }

  public static double tan(Angle θ) {
    return Math.tan(θ.rads());
  }

  /** -pi/2..+pi/2 */
  public static Angle asin(Double val) {
    return Angle.fromRads(Math.asin(val));
  }

  /** 0..pi */
  public static Angle acos(Double val) {
    return Angle.fromRads(Math.acos(val));
  }

  /** -pi/2..+pi/2. The atan2 method is usually preferred, since it gets the quadrant right. */
  public static Angle atan(Double val) {
    return Angle.fromRads(Math.atan(val));
  }

  /** -pi..+pi. */
  public static Angle atan2(Double y, Double x) {
    return Angle.fromRads(Math.atan2(y, x));
  }
  
}
