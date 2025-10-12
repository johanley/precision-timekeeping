package astrolib.when;

/**
 Supported timescales. 
 
 <P>Reference: <a href='https://static1.squarespace.com/static/68caa251ae552a6213e8764b/t/68d3e216e1cf0121bd4273a6/1758716438331/sofa_ts_c.pdf'>SOFA Cookbook</a> on timescales.
 
 <P>The supported timescales are currently related as follows:
 <pre>
    UT1     UTC   leap seconds   TAI                TT
 ----+-------+--------------------+------------------+-----&gt;
     | -ΔUT1 |        ΔAT         |    32.184s       |
 </pre>
 
 <P>From 1960-01-01 (when UTC started) to 1971-12-30, <em>rate changes</em> were combined with leap seconds.
 
 <P>Additional conventions:
 <ul>
   <li>ΔTT = TT - UTC = 32.184s + ΔAT 
   <li>ΔT =  TT - UT1 = 32.184s + ΔAT - ΔUT1
   <li>ΔUT1 is also called DUT1
 </ul>
 
 <P>Data files needed to implement the above (the <a href='https://hpiers.obspm.fr/eop-pc/index.php'>IERS</a>):
 <ul>
   <li><a href='https://hpiers.obspm.fr/iers/bul/bulc/Leap_Second.dat'>leap second file</a>, 1972..present
   <li><a href='https://hpiers.obspm.fr/eop-pc/index.php?index=TAI-UTC_tab&lang=en'>leap seconds and rate changes</a>, 1960..present
   <li>ΔUT1 ?
   <li>ΔT ?
 </ul>  

 
 <P>The <em>coordinate</em> timescales based on General Relativity are not supported.
 Thhey are of interest only to professional astronomers and physicists:
 <ul>
  <li>TCG, Geocentric Coordinate Time. Used for calculations centered on the Earth in space.
  <li>TCB, Barycentric Coordinate Time. Used for calculations beyond Earth orbit.
  <li>TDB, Barycentric Dynamical Time. A scaled form of TCB that keeps in step with TT on the average.
 </ul>
 
*/
public enum Timescale {
  
  /** 
   Terrestrial Time. Used for solar system ephemeris lookup.
    
   <P>Strictly speaking, the inputs to a solar system ephemeris should be TDB, but the difference is less than 2 milliseconds.
   For most users this is small enough to ignore. 
   For the Moon, the error from using TT instead of TDB is less than 1 milliarcsecond.
   
   <P>TT is a successor to Ephemeris Time (ET).
   
   <P>TT was called TDT from 1984 to 2000.
  */
  TT,
  
  /** International Atomic Time. The official timekeeping standard. */
  TAI,
  
  /** 
   Coordinated Universal Time. 
   The basis of civil time. 
   
   <P>UTC was introduced January 1, 1960. 
   At that time, UTC was kept near UT1 by using a combination of rate changes and leap seconds.
   Since December 30 1971, it has been kept near UT1 by using leap seconds only.

   <P>Leap seconds introduce unusual behaviour in UTC.
   When a leap second occurs, the sequence of UTC seconds lies outside the usual numeric range: 
   <pre>... 57.0, 58.0, 59.0, 60.0, 0.0, 1.0, ...</pre>
   
   <P>It's theoretically possible for leap second to be negative. 
   In that case, the sequence of UTC seconds is: 
   <pre>... 57.0, 58.0, 0.0, 1.0, ...</pre>
  */
  UTC,
  
  /** Universal Time. Within +/- 0.9 seconds of UTC. Also referred to as 'UT', in some contexts. */
  UT1;

}
