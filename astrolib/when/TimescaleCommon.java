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
   "The origin of TAI was arbitrarily chosen so the the TAI and UT1 readings at 1958 January 1 0h were the same."
   
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
    /** A fixed offset of +32.184s. This is an approximation. Deviations are on the order of 10 microseconds. */
    @Override public BigDecimal secondsFromTAI(DateTime when) {
      return big(TT_MINUS_TAI);
    }
  },
  
  /** The timescale used by the Global Positioning System. */
  GPS {
    /** GPS - TAI. Fixed value of -19s. This value is precise to sub-microsecond accuracy (according to SOFA). */
    @Override public BigDecimal secondsFromTAI(DateTime when) {
      return big("-19");
    }
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
  
  /** 
   Universal Time. Within +/- 0.9 seconds of UTC. Also referred to as 'UT', in some contexts.
   SHOULD THIS BE HERE? OR SHOULD IT BE SEPARATE?
   IT'S IRREGULAR. SEEMS FUNDAMENTALLY DIFFERENT FROM THE OTHERS.
   
   ES 2006:
   "Apparent sidereal time, because of its variable rate, is used only as a measure of epoch; it is not used as a measure of time interval."
   "Owing to precession, the mean sidereal day of 24 hours of mean sidereal time is shorter than the actual period of rotation of Earth 
   by about 0.0084s, the amount of precession in right ascension in one day."
   "Universal Time is directly related to sidereal time by means of a numerical formula."
   Formula 2.24-1 for GMST1 and UT1, for 0hUT1
   "Optical observations can determine UT to about 5ms of time." More advanced methods: 0.1ms or 0.05ms.
   
   Precession, nutation. 
  */
  UT1 {
    /** UT1 - TAI. */
    @Override public BigDecimal secondsFromTAI(DateTime when) {
      //INCORRECT. This needs to use a table of values. See IERS.
      /*
      https://bitbucket.org/psrsoft/tempo2/src/master/T2runtime/clock/ut1.dat  source of this file?
      https://github.com/astropy/astropy  has the data file ut1.dat?
      https://www.iers.org/IERS/EN/Science/EarthRotation/UT1-TAI.html?nn=12932
      https://www.bipm.org/documents/20126/270183862/1-+Stamatakos+BIPM_IERS_v4/08643617-307f-09ee-78ae-2aaa9b043eda   slide show
      https://www.bipm.org/en/time-metrology
      https://webtai.bipm.org/api/index.html
      https://webtai.bipm.org/api/v1.0/index.html  seems to focus on UTC-GNSS systems; no UT1.
      
      https://www.iers.org/IERS/EN/DataProducts/EarthOrientationData/eop.html
      
      astropy:
      https://github.com/search?q=repo%3Aastropy%2Fastropy%20ut1&type=code
      https://github.com/astropy/astropy/blob/137b86f98804f7197988ee8e0bb142e6ac64c51f/astropy/utils/iers/data/ReadMe.eopc04_IAU2000#L31
      https://github.com/astropy/astropy/blob/137b86f98804f7197988ee8e0bb142e6ac64c51f/astropy/utils/iers/iers.py
      it apparently, by default, downloads the most recent data automagically IERS-A
      they say that IERS-B (monthly) has some weird data issues?
      https://docs.astropy.org/en/stable/time/index.html#convert-time-scale
      Guido seems out to lunch regarding dates: https://discuss.python.org/t/bc-date-support/582
      https://github.com/astropy/astropy/issues/9231  - complaint about no support for early dates BC 
      
      This has the data, as UT1-UTC(s), once a day (about 1 ms per day), but its stale by about 30 days.
      https://datacenter.iers.org/data/latestVersion/EOP_20u23_C04_12h_dPsi_dEps_1984-now.txt  
      https://datacenter.iers.org/data/latestVersion/bulletinA.txt  - present and future (1 year) IERS-A, Bulletin A
      
      */
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
   @param when should use the {@link TimescaleCommon#TAI} timescale, but this is not enforced here, since any differences
   are very small.
  */
  TCG {
    @Override public BigDecimal secondsFromTAI(DateTime when) {
      /*
       TCG - TT = 6.969291×10−10 * (JD - 2443144.5) * 86400 seconds - Explanatory Supplement 2006, page 47
       TCG - TT + 32.184
       
       IERS Conventions (2003), section 10.1
       TCG−TT = LG x (MJD−43144.0) x 86400s,  with LG = 6.969290134E-10
      */
      JulianDate jd0 = JulianDate.from(big("2443144.5"), when.time().timescale());
      JulianDate jd = when.toJulianDate();
      //after 1977, the result is a small positive amount:
      BigDecimal tcg_minus_tt = jd.jd().subtract(jd0.jd()).multiply(big(86400)).multiply(big("6.969290134E-10"));
      return tcg_minus_tt.add(big(TT_MINUS_TAI));
    }
  };
  
  /*
  TCG, TCB and TDB are called coordinate timescales.
  TCB, Barycentric Coordinate Time. Used for calculations beyond Earth orbit. This is the one having periodic terms. No rotation. Adopted in 1991.
      I don't understand why TCB-TCG has a dependence of the observer's position! No clue. I'm missing something.
      The periodic terms for TCB have amplitude of order 1.6ms.
      This one seems more complex to calculate.
      There is a 'time ephemeris' TE405 with about 500 terms in it that does the job to near nanosecond level (1999) TCG-TCG. It's derived from DE405.
      https://iopscience.iop.org/article/10.1086/378909
      https://iopscience.iop.org/article/10.1086/378909/pdf
      github data (fortran):
      https://github.com/varenius/ascot/blob/15db0cb0cd1db8fa5bf8dd1ba5b9335db2b737ab/src/iers/HF2002_IERS.F
      
      There's a science tool called Tempo2, used for pulsar timing.
        Hobbes, Edward, Manchester 2006, MNRAS, 369, 655
        https://github.com/zhuww/tempo2/tree/master
        https://github.com/mattpitkin/tempo2
        https://bitbucket.org/psrsoft/tempo2/src/master/   
  
  TDB, Barycentric Dynamical Time. A scaled form of TCB that keeps in step with TT on the average.
  "TDB is essentially the same as T_eph, the time argument for the JPL ephemerides."
  ES 2006 equ. 2.222-1 relates TDB to TT in a simple way. Periodic term of ~2ms.
  Since 2006, TDB is defined as linear transform of TCB (with L_b).
  
  TDB(T_eph) -> TCB (definition) -> TCG (TE405 big, but to ns) -> TT  ?? is this correct? with the hard part being the 2nd step, from the Sun to the Earth?
  I could compare the above with the simple formula 2.222-1. 
  Target of at least 1ms over all timescales. TT/TAI variations of microseconds. 
  
  "The fundamental epochs of celestial reference coordinate systems are properly on TBD. Thus J2000.0 is 
  2000 January 1.5 TDB, which is 2451545.0 TDB." - ES 2006
  
  IERS Conventions 2003:
  "However, since no precise definition of TDB exists, there is no definitive value of LB and such an expression should be used with caution."
  (This is the simple relation between TDB and TCB.)
  "Note that in this section on the computation of TCB−TCG, TT is used as a time argument while the actual argument of the different 
  realizations is Teph (see Chapter 3). The resulting error in TCB−TCG is at most approximately 20 ps."
  
  Is the observer lat/long dependency in (TCB - TCG) from the difference between Earth's center and the Earth-Moon barycenter??

  Klioner summary https://syrte.obspm.fr/iauJD16/klioner.pdf  TCB-TCG (at the *geocenter*) as linear + periodic(0.1ms)
    "T_eph is de facto defined by a fixed relation to TT: by the Fairhead-Bretagnon formula based on VSOP-87"
  Klioner https://syrte.obspm.fr/jsr/journees2008/pdf/Klioner.pdf
  transformation code in 
  Klioner, S.A., Soffel, M., Le Poncin-Lafitte, Chr., 2008, “Towards the relativistic theory of precession and nutation”, 
  In: The Celestial Reference Frame for the Future (Proc. of Journes’2007), N. Capitaine (ed.), Paris Observatory, Paris, 139-142
     https://syrte.obspm.fr/journees2007/pdf/s3_04_Klioner.pdf
     "Let us recall that the transformation between TCB and TCG is a 4-dimensional one that involves the spatial location of an event."
     
    
  Pulsar timing:
  Pint, tempo, tempo2
  https://github.com/nanograv/PINT
  https://github.com/nanograv/tempo
  https://bitbucket.org/psrsoft/tempo2/src/master/
    interesting chart relating timescales in comment:   https://bitbucket.org/psrsoft/tempo2/src/master/sofa/dtdb.for
    mirrors this is SOFA: https://github.com/liberfa/erfa/blob/master/src/dtdb.c
  


- SOFA docs argue against supplying a general-purpose timescale conversion method. They argue that the TBD-TT conversion requires the 
observer's location, which isn't needed for the other transformations, and that it requires choosing a source for the delta TCB-TGB value, 
or for the Earth's rotation. In short, they don't want to commit the user to specific policies. For example, the TCB-TCG policy with ~800 
terms (accurate to a few ns in the modern era) is provided, but the user doesn't have to use it; they have the option of using some 
alternate implementation. (If the location is set to geocentric, the error is under ~2 microseconds. They note a simple model with a few 
terms gives 50-microsecond accuracy.)
The SOFA style of implementation has the further advantage that it maximizes accuracy: in my style, it's a lowest-common-denominator 
approach, and the accuracy is dominated by the worst transformation in the group.

PROLEPTIC: SHOULD I barf if I attempt to make a date-time before the inception of the timescale?

JNI: as an experiment, it would likely be interesting to call a SOFA routine from Java-land.
  https://www.youtube.com/watch?v=Hw7563ojRbU
  https://www.youtube.com/watch?v=dT3836cVVh4
  https://www.youtube.com/watch?v=pyXnX2SEaFc 

SOFA
tai - atomic
ut1 - solar
utc - atomic/solar hybrid
tt, tcg, tcb, tdb - dynamical
    
    C unit testing
    https://www.youtube.com/watch?v=z-uWt5wVVkU
    youtube.com/watch?v=z-uWt5wVV  kU
    
   */

  /** Used to set a System property, and override a default value for UTC - TAI: {@value}. */
  public static final String UTC_SYS_PROPERTY = "UTC-minus-TAI";
  
  /** {@value} seconds. */
  private static final Double TT_MINUS_TAI = 32.184;
}
