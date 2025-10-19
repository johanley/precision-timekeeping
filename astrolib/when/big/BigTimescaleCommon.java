package astrolib.when.big;

import static astrolib.when.big.BigDecimalHelper.*;

import java.math.BigDecimal;

import astrolib.util.Check;

/**
 Commonly used timescales.
 
 <P>If a timescale is not listed here, you'll need to create a class that implements the {@link BigTimescale} interface. 
 
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
public enum BigTimescaleCommon implements BigTimescale {
  
  /** 
   Terrestrial Time. Used for solar system ephemeris lookup.
    
   <P>Strictly speaking, the inputs to a solar system ephemeris should be TDB, but the difference is less than 2 milliseconds.
   For most users this is small enough to ignore. 
   For the Moon, the error from using TT instead of TDB is less than 1 milliarcsecond.
   
   <P>TT is a successor to Ephemeris Time (ET).
   
   <P>TT was called TDT from 1984 to 2000.
  */
  TT {
    /** A fixed offset of -32.184s. */
    @Override public BigDecimal TAIminusThis(BigDate date) {
      return big(TAI_MINUS_TT);
    }
  },
  
  /** International Atomic Time. The official timekeeping standard. */
  TAI,
    
  
  /** 
   Coordinated Universal Time, the basis of civil time. 
   
   <P>This library has modest support for UTC.
   <b>It models UTC as a simple fixed offset from TAI.</b> 
   The offset has a default value, hard-coded here; that value can be overridden 
   by setting a System property named <em>TAI-minus-UTC</em> to the desired value:
   
   <P>Example: <pre>-DTAI-minus-UTC=42</pre> 
  */
  UTC {
    /** TAI - UTC. Default is +37s. */
    @Override public BigDecimal TAIminusThis(BigDate date) {
      String delta = "37";
      String propName = "TAI-minus-UTC";
      String override = System.getProperty(propName);
      if (Check.textHasContent(override)) {
        try {
          @SuppressWarnings("unused")
          Integer overrideSeconds = Integer.valueOf(override);
          delta = override;
        }
        catch(NumberFormatException ex) {
          throw new IllegalArgumentException("Systemp property " + propName + " should be an integer, but isn't: " + override);
        }
      }
      return big(delta);
    }
  },
  
  /** Universal Time. Within +/- 0.9 seconds of UTC. Also referred to as 'UT', in some contexts. */
  UT1 {
    /** TAI - UT1. */
    @Override public BigDecimal TAIminusThis(BigDate date) {
      //INCORRECT. This needs to use a table of values. See IERS.
      /* read in a text file, for past values only. */
      return big("37");
    }
  }, 
  
  GPS {
    /** TAI - GPS. Fixed value of +19s. */
    @Override public BigDecimal TAIminusThis(BigDate date) {
      return big("19");
    }
  },
  
  TCG {
    @Override public BigDecimal TAIminusThis(BigDate date) {
      /*
       * TCG = TAI + 32.184s + Lg * (Tai - T0)
       * Lg = 6.969290134×10−10  (dimensionless)
       * T0 = 1977, Jan 1 00:00:00
       */
      return super.TAIminusThis(date);
    }
  };
  
  /** {@value} seconds. */
  private static final Double TAI_MINUS_TT = -32.184;
}
