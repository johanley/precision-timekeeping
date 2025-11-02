package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;

import astrolib.util.Check;

/**
 Commonly used timescales.
 
 <P>If a timescale is not listed here, you'll need to create a class that implements the {@link Timescale} interface. 
 
 <P>Reference: <a href='https://static1.squarespace.com/static/68caa251ae552a6213e8764b/t/68d3e216e1cf0121bd4273a6/1758716438331/sofa_ts_c.pdf'>SOFA Cookbook</a> on timescales.
 
 <P>Major timescales are related as follows:
 <pre>
    UT1     UTC   leap seconds   TAI                TT
 ----+-------+--------------------+------------------+-----&gt;
     | -ΔUT1 |        ΔAT         |    32.184s       |
 </pre>
 
 <P>
 Leap seconds are the trickiest part.
 In addition, from 1960-01-01 (when UTC started) to 1971-12-30, <em>rate changes</em> were combined with leap seconds.
 
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
 
 <P>Here's the order in 2025:
 <pre>
    UTx      GPS       TAI               TT TCG          TCB
 ----+--------+---------+-----------------++-------------+-----&gt;
     |  ~18s  |   19s   |      32.184s    ||     ~32s    |  
 </pre>
 
 
 <P>TT, TCG, and TCB coincided in value on 1977 January 1, 00:00:00. 
 At that moment, they were all exactly 32.184s ahead of TAI.
 
   <P>See <a href='https://articles.adsabs.harvard.edu//full/1992A%26A...265..833S/0000835.000.html'>link</a>, especially Table 1, and Figure 1.
   
*/
public enum TimescaleCommon implements Timescale {
  
  /** 
   International Atomic Time, the basis for all time modern time measurments.
   <P>Explanatory Supplement: 
   Conforms as closely as possible to the definition of the SI second.
   Used as the basis for other timescales. 
   Introduced as standard since January 1972, but in use since July 1955.
   "TAI is a coordinate timescale defined at a geocentric datum line and having as its unit one SI second as obtained on the geoid in rotation."
   
   <P>
   <a href='https://www.bipm.org/documents/20126/41489667/SI-App2-second.pdf/3c76fec8-04d9-f484-5c3c-a2e280a0f248?version=1.12&t=1643724477633&download=true'>BIPM</a>: 
   Defined using a conventional value for the frequency of a transition in a Cs133 atom at rest, and in the absence of an electromagnetic field or radiation.
   Realized with a set of world wide clocks and an algorithm for combining them.
   Proper time (in the sense of General Relativity) on the Earth's geoid in rotation.
   Weird: changes to relativistic effects cancel each other out when you move about on the geoid in rotation: changes in speed of rotation  (Doppler effect),  
   versus changes in altitude (gravitational time dilation).
   
   <P>"The definition of the second should be understood as the definition of the unit of proper time: it applies in a small spatial domain which shares
    the motion of the caesium atom used to realize the definition."
    
    <P>"In a laboratory sufficiently small to allow the effects of the non-uniformity of the gravitational field to be neglected when 
    compared to the uncertainties of the realization of the second, the proper second is obtained after application of the special 
    relativistic correction for the velocity of the atom in the laboratory. It is wrong to correct for the local gravitational field."  
    
    <P>"International Atomic Time (TAI) is a continuous time scale produced by the BIPM based on the best realizations of the SI second. 
    TAI is a realization of Terrestrial Time (TT) with the same rate as that of TT, as defined by the IAU Resolution B1.9 (2000)."
    
    <P>"Coordinated Universal Time (UTC) is a time scale produced by the BIPM with the same rate as TAI, but differing from TAI only by an integral number of seconds."
    
    <P>"In addition, each of the GNSS de facto serves as a means for disseminating a prediction of UTC, with deviations from UTC by a few ten nanoseconds or better."
    
    <P><a href='https://www.bipm.org/documents/d/guest/si-brochure-9-en-pdf'>BIPM Brochure</a>:
    "The reference to an unperturbed atom is intended to make it clear that the definition of the SI second is based on an isolated caesium atom that is 
    unperturbed by any external field, such as ambient black-body radiation."
    
    <P>"The second, so defined, is the unit of proper time in the sense of the general theory of relativity. To allow the provision of a coordinated time scale, 
    the signals of different primary clocks in different locations are combined, which have to be corrected for relativistic caesium frequency shifts." 
  */
  TAI,
  
  /** 
   Terrestrial Time is a dynamical time attached to apparent geocentric ephemerides in the solar system.
   TT has a given form for the space-time metric, while TAI does not.

  <P><a href='https://articles.adsabs.harvard.edu//full/1992A%26A...265..833S/0000835.000.html'>Link</a>: 

   <P>"TT is the time reference for the apparent geocentric ephemerides, such that it differs from TCG by a constant rate, the unit of measurement 
   of TT agrees with the SI second on the geoid, and at the instant 1977 January 1, 0h0m0s TAI exactly, TT has the reading 1977 January 1, 0h0m32.184s exactly."
   
   <P>"Secondly, TDT, while defined as an idealized form of TAI, which is a coordinate time (CCDS 1980), was in some cases misinterpreted to be a proper time at 
   the geocenter."
   
   <P>"TCG is defined in terms of TT, and TCB is defined in terms of TCG."
   
    <P>"TT may be viewed as an idealized version of atomic time. It is the unit of measurement that the TAI unit of measurement attempts to equal."
    
   <P>TT is a successor to Ephemeris Time (ET, introduced in 1952). 
   TT was called TDT from 1984 to 2000.
   
   <P>Strictly speaking, the inputs to a solar system ephemeris should be TDB, but the difference is less than 2 milliseconds.
   This is small enough to ignore in most cases. 
   For the Moon, the error from using TT instead of TDB is less than 1 milliarcsecond.
  */
  TT {
    /** A fixed offset of +32.184s. */
    @Override public BigDecimal secondsFromTAI(DateTime when) {
      return big(TT_MINUS_TAI);
    }
    @Override public Boolean isDynamicalTimescale() { return Boolean.TRUE; }
  },
  
  
  /** 
   Coordinated Universal Time, the standard for all civil time systems.
   
   <P>UTC is an integer number of seconds from TAI.
   
   <P>This library has modest support for UTC. Here, <b>UTC is modeled as a simple fixed offset from TAI.</b> 
   The offset has a default value, hard-coded here; that value can be overridden 
   by setting a System property named <em>UTC-minus-TAI</em> to the desired value:
   
   <P>Example: <pre>-DUTC-minus-TAI=-38</pre> 
  */
  UTC {
    /**  UTC - TAI. Default is -37s.  */
    @Override public BigDecimal secondsFromTAI(DateTime when) {
      String delta = "-37";
      String override = System.getProperty(TimescaleCommon.UTC_SYS_PROPERTY);
      if (Check.textHasContent(override)) {
        try {
          @SuppressWarnings("unused")
          Integer overrideSeconds = Integer.valueOf(override);
          delta = override;
        }
        catch(NumberFormatException ex) {
          throw new IllegalArgumentException("System property " + UTC_SYS_PROPERTY + " should be an integer, but isn't: " + override);
        }
      }
      return big(delta);
    }
  },
  
  /** The timescale used by the Global Positioning System. */
  GPS {
    /** GPS - TAI. Fixed value of -19s. */
    @Override public BigDecimal secondsFromTAI(DateTime when) {
      return big("-19");
    }
  },

  /** Universal Time. Within +/- 0.9 seconds of UTC. Also referred to as 'UT', in some contexts. */
  UT1 {
    /** UT1 - TAI. */
    @Override public BigDecimal secondsFromTAI(DateTime when) {
      //INCORRECT. This needs to use a table of values. See IERS.
      /* read in a text file, for past values only. */
      return big("-37");
    }
  }, 
  
  /** 
   Geocentric Coordinate Time. Used for calculations centered on the Earth in space.
   "The Geocentric Coordinate Time (TCG) is a time-like coordinate to go with space coordinates centered at the 
   geocenter of the Earth as determined by the relativistic conversions from the geoid to the geocenter." 
   No rotation.
   Not influenced by the gravitational field of the Earth. 
   Adopted in 1991 by the IAU.
  */
  TCG {
    @Override public BigDecimal secondsFromTAI(DateTime when) {
      /*
       TCG - TT = 6.969291×10−10 * (JD - 2443144.5) * 86400 seconds - Explanatory Supplement 2006, page 47
       TCG - TT + 32.184  
      */
      JulianDate jd0 = JulianDate.from(big("2443144.5"), when.time().timescale());
      JulianDate jd = when.toJulianDate();
      BigDecimal tcg_minus_tt = jd.jd().subtract(jd0.jd()).multiply(big(86400)).multiply(big("6.969291E-10")); //a small positive amount after 1977
      return tcg_minus_tt.add(big(TT_MINUS_TAI));
    }
  };
  
  /*
  TCG, TCB and TDB are called coordinate timescales.
  TCB, Barycentric Coordinate Time. Used for calculations beyond Earth orbit. This is the one having periodic terms. No rotation. Adopted in 1991.
  
  TDB, Barycentric Dynamical Time. A scaled form of TCB that keeps in step with TT on the average.
   */

  /** Used to set a System property, and override a default value for UTC - TAI: {@value}. */
  public static final String UTC_SYS_PROPERTY = "UTC-minus-TAI";
  
  /** {@value} seconds. */
  private static final Double TT_MINUS_TAI = 32.184;
}
