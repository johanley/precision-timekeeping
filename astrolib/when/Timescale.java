package astrolib.when;

import java.math.BigDecimal;

public interface Timescale {
  
  /** 
   The difference TAI - this timescale, in seconds.
   
   <P>In general, this difference changes at a slow rate. 
   @param date the date for which the difference is required.
  */
  default BigDecimal TAIminusThis(Date date) { return BigDecimal.ZERO; } 
  
  /** A convenient identifier for this timescale. Usually an abbreviation. Must have content. */
  default String id() { return this.toString(); }

}
