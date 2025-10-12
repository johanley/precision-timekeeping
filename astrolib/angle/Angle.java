package astrolib.angle;

import java.util.Objects;

import astrolib.util.Mathy;

/** 
 Various aliases for the same angle: radians, degrees, and so on.
 
 Builds an angle using one unit of measure, and (lazily) returns multiple units of measure.
 
 <P>This class does simple conversions so that the caller doesn't have to.
 This is pleasing, since the caller can just use a simple method to get the desired representation.
 This class is at a higher level than a plain <em>double</em> value.
*/
public final class Angle implements Comparable<Angle> {
  
  public static Angle fromRads(double rads) {
    return new Angle(rads);
  }
  
  public static Angle fromDegs(Double degs) {
    Angle res = Angle.fromRads(Math.toRadians(degs));
    res.degs = degs; //simply to reflect original value, without change
    return res;
  }
  
  public static Angle fromSexagesimal(Sexagesimal sexagesimal) {
    double multiplier = sexagesimal.isHours() ? Sexagesimal.DEGREES_PER_HOUR : 1.0; 
    Angle res = Angle.fromDegs(sexagesimal.decimalValue() * multiplier);
    res.sexagesimal = sexagesimal; //simply to reflect original value, without change
    return res;
  }
  
  /** Radians. */
  public double rads() { 
    return rads; 
  }
  
  /** Degrees. */
  public Double degs() {
    if (degs == null) {
      degs = Math.toDegrees(rads);
    }
    return degs;
  }

  /** 
   The angle in degrees, minutes, and seconds OR in hours, minutes, and seconds. 
   Intended mostly for output/display purposes.
   @param isHours true for hour-min-sec, false for deg-arcmin-arcsec.
   @param numDecimals applied to the arc-seconds value. 
  */
  public Sexagesimal sexagesimal(Boolean isHours, Integer numDecimals) {
    if (sexagesimal == null) {
      sexagesimal = isHours ? Sexagesimal.hourMinSec(this, numDecimals) : Sexagesimal.degMinSec(this, numDecimals);
    }
    return sexagesimal;
  }

  /*
   * Coercion to one-revolution, 0..2pi, 0..360.
   * 
   * https://github.com/mattwelsh/Urania
   */
  
  /** Intended for logging only. Decimal degrees and deg-min-sec. */ 
  @Override public String toString() {
    return Mathy.round(degs, 4) + "° " + sexagesimal(false, 2) ;
  }

  @Override public boolean equals(Object aThat) {
    if (this == aThat) return true;
    if (!(aThat instanceof Angle)) return false;
    Angle that = (Angle)aThat;
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
  
  @Override public int compareTo(Angle that) {
    final int EQUAL = 0;
    if (this == that) return EQUAL;
    
    //I only need to compare on one value
    int comparison = this.degs().compareTo(that.degs());
    if (comparison != EQUAL) return comparison;
    
    return EQUAL;
  }
  
  private double rads; //primitive, to avoid boxing/unboxing the core data
  private Double degs; //possibly lazy
  private Sexagesimal sexagesimal; //possibly lazy

  /** All construction passes through here. */
  private Angle(double rads) {
    this.rads = rads;
  }
  
  private Object[] getSigFields(){
    Object[] result = {degs()}; //just the one suffices
    return result;
  }  
}