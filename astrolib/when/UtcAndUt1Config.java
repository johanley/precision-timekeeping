package astrolib.when;

import java.util.Map;

/** 
 Read configuration files for details related to UTC and UT1.
 The files are encoded using UTF-8.
 
 <P>This data changes often. 
 So, this class includes a mechanism to allow updates to the data without recompiling the code.
 It simply looks for an (optional) setting in the Java environment, which points to a file containing the complete data in an expected format. 
 Those files will be used <em>instead of</em> the default data files that accompany the source code.
 
 <P>(It's noteworthy that <a href='https://www.iausofa.org/2023-10-11c#documentation'>SOFA</a> has no such mechanism for pulling in such updates. 
 SOFA simply hard-codes the data directly in source files.)
*/
final class UtcAndUt1Config {
  
  static {
    //check env for pointer to a file
    //read in data from UTF8 text files.
  }
  
  
  /**
   Table of moments expressed in UTC (to an integral second) DIRECTLY AFTER WHICH which the number of leap seconds changes +/- 1s (or is scheduled to occur).
   The integer value is the new number of leap seconds between from UTC to TAI.
   
   DO I NEED TWO OF THESE? One with UTC the key, the other with TAI as the key? That might be simplest.
   That means that each moment-of-transition would need to have two values, UTC and TT.
  */
  Map<DateTime, Integer> leapSeconds;
  
  /*
   * The SOFA code has two entangled data structures: drift, changes.
   * It looks like the changes has leap second data (with year-month), and drift has rate-difference data only (mjd).
   */
  
  /**
   The table to use to convert between UTC and UT1. 
   A table of moments in UTC, in which ΔUT1 changed value.
   The absolute value of the <em>double</em> is less than 1.0. 
  */
  Map<DateTime, Double> ΔUT1;
}
