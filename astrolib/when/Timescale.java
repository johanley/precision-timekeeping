package astrolib.when;

import java.math.BigDecimal;

public interface Timescale {
  
  /** 
   The difference between this timescale and TAI, in seconds.
   Positive if this timescale is ahead of TAI, negative otherwise.
   TAI is taken as the base timescale.
   
   <P>In general, this difference changes at a slow rate. 
   @param when the moment for which the difference is required, using the Gregorian Calendar, and this timescale.
   @return default is 0.
  */
  default BigDecimal secondsFromTAI(DateTime when) {
    return BigDecimal.ZERO;
  } 
  
  /** A convenient identifier for this timescale. Usually an abbreviation. */
  default String id() { return this.toString(); }

  /** 
   A <em>dynamical</em> timescale represents the independent time variable in theories of the motion of solar system objects.
   According to the theory of relativity, this independent variable depends on the coordinate system being used as the system of reference.
   
   <P>A non-dynamical timescale is referred to as a <em>coordinate</em> timescale.
   <P>Default is false.
  */
  default Boolean isDynamicalTimescale() { return Boolean.FALSE; }

}
