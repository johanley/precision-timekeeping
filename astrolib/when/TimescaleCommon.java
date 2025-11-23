package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;

import astrolib.util.Check;

/**
 Commonly used {@link Timescale}s.
 
 <P>Here's a sketch of the time-order of timescales in 2025, with time values increasing to the right:
 <pre>
    UTx      GPS       TAI               TT TDB        
 ----+--------+---------+-----------------++-------&gt;
     |  ~18s  |   19s   |      32.184s    ||    
 </pre>

 <P>The difference (TDB - TT) is very small and varies during the year; it can have either sign.

  <P>A notable instant in the past is January 1.0, 1977, related to the conventional definition of {@link TT}:
  <pre>
 TAI: 1977-01-01 00:00:00.0   
 TT : 1977-01-01 00:00:32.184 
 TDB: 1977-01-01 00:00:32.1839345   (32.184s - 65.5 microseconds)</pre>
  
 <P>Reference: the <a href='https://www.iausofa.org/2023-10-11c'>SOFA Timescale and Calendar Tools</a> cookbook.
*/
public enum TimescaleCommon implements Timescale {
  
  /** 
   International Atomic Time, the basis for modern time measurments. 
   Corresponds to atomic clocks on the rotating geoid. (The geoid is approximately an ideal surface at mean sea level.)
   
   <P>TAI: 
   <ul> 
    <li>is the best realization of the SI second.
    <li>was introduced as a standard in 1972, but began continuous use in July 1955.
    <li>coincided with the value of {@link UT1} at 1958 January 1 at 0.0h.
    <li>is a <em>proper time</em>, in the sense of the term used in general relativity.
   </ul>
   
   <P>Interesting to note: changes in relativistic effects cancel each other out when you move a clock 
   on the geoid in rotation. Changes in the speed of rotation about the Earth's axis (the Doppler effect), 
   are canceled out by changes in altitude (the effect of gravitational time dilation).
  */
  TAI,
  
  /** 
   Terrestrial Time is a dynamical time attached to apparent geocentric ephemerides in the solar system.
   
   <P>TT is a successor to Ephemeris Time (ET, introduced in 1952). From 1984 to 2000, TT was called TDT.
   
   <P>The unit of measurement of TT agrees with the SI second on the rotating geoid. 

   <P>Strictly speaking, the inputs to a solar system ephemeris should be {link TDB}, but the difference 
   is less than 1.8 milliseconds.
   This is small enough to ignore in most cases. 
   For the Moon, the error from using TT instead of {@link TDB} is less than 0.001 arcseconds.
  */
  TT {
    /** TT - TAI. Fixed value of +32.184s. This is an approximation. Deviations are on the order of 10 microseconds. */
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

   <P>UTC is nowadays an integer number of seconds from TAI.
   
   <P>UTC began in 1960. 
   Initially, <em>rate changes</em> were used instead of leap seconds, with the last rate change in February 1968.
   After that, only leap seconds have been used, the first in 1972-01-01, and the most recent 2017-01-01. 
   When leap seconds are used UTC differs from TAI by an integral number of seconds.
   
   <P><b>WARNING: This library has modest support for UTC.
   In short, it has no support for leap seconds. 
   It models UTC as a simple <em>fixed</em> offset from TAI, and that's it.</b> 
   The offset has a default value, hard-coded here.
   But that value can be overridden by setting a System property named <em>UTC-minus-TAI</em> to the desired value:
   <pre>-DUTC-minus-TAI=-38</pre>

   <P>In short, this library can only handle spans of UTC-time that contain no leap second.

   <P>Reasons for not fully supporting leap seconds here:
     <ul>
      <li><b>it allows all minutes in all timescales to have exactly 60 seconds</b>; this simplifies things greatly
      <li>leap seconds have complex logic, when you look at the details
      <li>it seems very likely that no new leap seconds will ever be decreed by international standards bodies
     </ul>
     
   <P>For dates previous to 2017-01-01, you can:
     <ul> 
      <li>use {@link UT1} as an approximation to UTC (since it differs from UTC by 0.9 seconds or less).
      <li>or, you can set a specific offset from {@link TAI} manually, using the System property mentioned above.
      This will let you use UTC <em>over the time span that corresponds to that specific offset from {@link TAI}</em>. 
    </ul>
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
   Barycentric dynamical time (TDB) is the independent variable of the equations of motion with respect 
   to the barycenter of the solar system.
   TDB is used for lookup in solar system ephemerides (but for most applications {@link TT} can be used instead).
   Keeps in step with {@link TT} on average, and can differ from {@link TT} by up to ~1.8 milliseconds.
   
   <P>Explanatory Supplement, 2006: <em>The fundamental epochs of celestial reference coordinate systems are properly on TBD. 
   Thus J2000.0 is 2000 January 1.5 TDB, which is 2451545.0 TDB.</em>
  */
  TDB {
    /** TDB - TAI. Reference: Explanatory Supplement, 2006, page 42, Equation 2.222-1. */
    @Override public BigDecimal secondsFromTAI(DateTime when) {
      //uses TT as a base
      //TDB - TT = 0.001658s * sin(g) + 0.000014s * sin(2g)
      //g = 357.53 + 0.9856003 * (JD - 2451545.0)
      //50 microsecond accuracy in 1980-2100 (SOFA Cookbook) - 0.05 msec
      JulianDate jd = JulianDateConverter.using(Calendar.GREGORIAN).toJulianDate(when);
      BigDecimal diff = jd.jd().subtract(JulianDate.J2000);
      double g = Math.toRadians(357.53 + 0.985_6003 * (diff.doubleValue()));
      BigDecimal tdbMinusTT = big(0.001_658 * Math.sin(g) + 0.000_014 * Math.sin(2*g)); //seconds
      return TT.secondsFromTAI(when).add(tdbMinusTT); 
    }
  };
  
  /** Used to set a System property, and override a default value for UTC - TAI: {@value}. */
  public static final String UTC_SYS_PROPERTY = "UTC-minus-TAI";
  
  /** Used to set a System property, and override a default value for UT1 - TAI: {@value}. */
  public static final String UT1_SYS_PROPERTY = "UT1-minus-TAI";
  
  /** {@value} seconds. */
  static final Double TT_MINUS_TAI = 32.184;
}
