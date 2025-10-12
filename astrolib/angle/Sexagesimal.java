package astrolib.angle;

import java.util.Objects;

import astrolib.util.Check;

import static astrolib.util.Mathy.*;

/** 
 Represent an angle in degrees-arcminutes-arcseconds, or hours-minutes-seconds.
 This way of representing an angle is usually meant for input-output purposes, not for core calculations.
 Instead, most core calculations use {@link Angle}. 
*/
public class Sexagesimal implements Comparable<Sexagesimal> {
  
  /** 
   Degree-arcminute-arcsecond factory method, to build from an {@link Angle}.
   @param θ the given angle. 
   @param numDecimals to be applied to the smallest value, for rounding. Pass null if no rounding desired. Non-negative.
  */
  public static Sexagesimal degMinSec(Angle θ, Integer numDecimals) {
    return new Sexagesimal(θ.degs(), numDecimals, false);
  }
  
  /** 
   Hour-minute-second factory method, to build from an {@link Angle}.
   @param θ the given angle. 
   @param numDecimals to be applied to the smallest value, for rounding. Pass null if no rounding desired. Non-negative.
  */
  public static Sexagesimal hourMinSec(Angle θ, Integer numDecimals) {
    return new Sexagesimal(θ.degs() / DEGREES_PER_HOUR, numDecimals, true);
  }

  /**
   Factory method, to build from existing parts in degrees etc.
   @param deg degrees.
   @param arcmin 0..59 arc-minutes.
   @param arcsec 0..60 (excluding 60) arc-seconds. 
   @param numDecimals to be applied to the smallest value, for rounding. Pass null if no rounding desired.
  */
  public static Sexagesimal fromDegMinSec(Integer deg, Integer arcmin, Double arcsec, Integer numDecimals) {
    return new Sexagesimal(deg, arcmin, arcsec, numDecimals, false);
  }
  
  /**
   Factory method, to build from existing parts in hours etc.
   @param hour degrees.
   @param min 0..59 minutes.
   @param sec 0..60 (excluding 60) seconds. 
   @param numDecimals to be applied to the smallest value, for rounding. Pass null if no rounding desired.
  */
  public static Sexagesimal fromHourMinSec(Integer hour, Integer min, Double sec, Integer numDecimals) {
    return new Sexagesimal(hour, min, sec, numDecimals, false);
  }

  /** Hours or degrees. */
  public Integer main() { return main; }
  /** Minutes or arc-minutes. */
  public Integer minutes() { return minutes; }
  /** Seconds or arc-seconds. */
  public Double seconds() { return seconds; }
  /** Hours or degrees. */
  public Double decimalValue() {
    return main + (minutes / MULTIPLIER) + (seconds / sqr(MULTIPLIER));
  }
  public Boolean isHours() { return isHours; } 

  /** Intended for logging only. */
  public String toStringDeg() {
    String res = isHours ? 
      main + "h " + minutes + "m " + seconds + "s" : 
      main + "° " + minutes + "' " + seconds + "\""
    ;
    return res;
  }
  
  /** Intended for logging only. */
  public String toStringTime() {
    return main + "h " + minutes + "m " + seconds + "s";  
  }
  
  @Override public int compareTo(Sexagesimal that) {
    final int EQUAL = 0;
    if (this == that) return EQUAL;

    int comparison = this.main.compareTo(that.main);
    if (comparison != EQUAL) return comparison;
    
    comparison = this.minutes.compareTo(that.minutes);
    if (comparison != EQUAL) return comparison;
    
    comparison = this.seconds.compareTo(that.seconds);
    if (comparison != EQUAL) return comparison;
    
    return EQUAL;
  }

  @Override public boolean equals(Object aThat) {
    if (this == aThat) return true;
    if (!(aThat instanceof Sexagesimal)) return false;
    Sexagesimal that = (Sexagesimal)aThat;
    for(int i = 0; i < this.getSigFields().length; ++i){
      if (!Objects.equals(this.getSigFields()[i], that.getSigFields()[i])){
        return false;
      }
    }
    return true;
  }  
  
  @Override public int hashCode() {
    return Objects.hash(getSigFields());
  }

  /** {@value}. */
  public static final double DEGREES_PER_HOUR = 15.0; //not int; avoid integer division
  private Integer main;
  private Integer minutes;
  private Double seconds;
  private boolean isHours;
  private static final double MULTIPLIER = 60; //not int; avoid integer division
  
  private Sexagesimal(double main, Integer numDecimals, boolean isHours) {
    Check.nonNegative(numDecimals);
    int sign = sign(main);
    //temporarily calc with a non-negative number; apply sign at end
    double m = Math.abs(main);
    
    this.main = (int)Math.floor(m); //need to be careful with the sign here
    double fracDeg = m - this.main;
    double min = fracDeg * MULTIPLIER;
    this.minutes = (int)Math.floor(min);
    double fracMin = min - this.minutes;
    this.seconds = rounded(fracMin * MULTIPLIER, numDecimals);
    
    this.main = sign * this.main; //finally, apply the sign
    this.isHours = isHours;
  }
  
  private Sexagesimal(Integer degrees, Integer minutes, Double seconds, Integer numDecimals, boolean isHours) {
    Check.range(minutes, 0, (int)MULTIPLIER);
    Check.range(seconds, 0.0, MULTIPLIER+1);
    Check.nonNegative(numDecimals);
    this.main = degrees;
    this.minutes = minutes;
    this.seconds = rounded(seconds, numDecimals);
    this.isHours = isHours;
  }
  
  private Double rounded(Double seconds, Integer numDecimals) {
    return numDecimals == null ? seconds : round(seconds, numDecimals);
  }
  
  private Object[] getSigFields(){
    Object[] result = {main, minutes, seconds};
    return result;
  }  
  
  public static void main(String... args) {
    Sexagesimal x = new Sexagesimal(+12.25321, 2, false);
    System.out.println(x.toStringDeg());
    System.out.println(x.toStringTime());
  }
}
