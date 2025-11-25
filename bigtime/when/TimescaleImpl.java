package bigtime.when;

import static bigtime.when.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.util.Optional;

import bigtime.util.Check;

/**
 Implementations of commonly used {@link Timescale}s.
 
 <P>Here's a sketch of the time-order of these timescales in 2025, with time values 
 increasing to the right:
 <pre>
    UTx      GPS       TAI               TT TDB        
 ----+--------+---------+-----------------++-------&gt;
     |  ~18s  |   19s   |      32.184s    ||    
 </pre>

 <P>The difference (TDB - TT) is very small and varies during the year; it can have either sign.
 
 <P>UTx stands for both UT1 and UTC, which are never far apart.

  <P>A notable instant in the past is January 1.0, 1977, related to the conventional 
  definition of {@link TT}:
  <pre>
 TAI: 1977-01-01 00:00:00.0   
 TT : 1977-01-01 00:00:32.184 
 TDB: 1977-01-01 00:00:32.1839345   (32.184s - 65.5 microseconds)</pre>
  
 <P>Reference: the <a href='https://www.iausofa.org/2023-10-11c'>SOFA Timescale and Calendar Tools</a> cookbook.
*/
public enum TimescaleImpl implements Timescale {
  
  /** 
   International Atomic Time, the basis for modern time measurments. 
   Corresponds to atomic clocks on the rotating geoid. 
   The geoid is an ideal surface near mean sea level on the Earth.
   
   <P>Here, TAI is taken as the base timescale. 
   Other timescales have small differences with respect to TAI.
   
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
   
   <P>TT is a successor to Ephemeris Time (ET, introduced in 1952). 
   From 1984 to 2000, TT was called TDT.
   
   <P>The unit of measurement of TT agrees with the SI second on the rotating geoid. 

   <P>Strictly speaking, the inputs to a solar system ephemeris should be {link TDB}, but the difference 
   is less than 1.8 milliseconds.
   This is small enough to ignore in most cases. 
   For the Moon, the error from using TT instead of {@link TDB} is less than 0.001 arcseconds.
  */
  TT {
    /** 
     TT - TAI. 
     Fixed value of +32.184s at all times. 
     This is an approximation. Deviations are on the order of 10 microseconds. 
    */
    @Override public Optional<BigDecimal> secondsFromTAI(DateTime when) {
      return Optional.of(big(TT_MINUS_TAI));
    }
  },
  
  /** 
   The timescale used by the Global Positioning System.
   Began 1980-01-06 00:00:00 UTC? But it was first launched ~ Feb 1978?
  */
  GPS {
    /** 
     GPS - TAI. 
     Fixed value of -19.0s for times after 1980-01-06 00:00:00 in any timescale (otherwise empty).
     This value is precise to sub-microsecond accuracy (according to SOFA). 
    */
    @Override public Optional<BigDecimal> secondsFromTAI(DateTime when) {
      return when.date().lt(Date.gregorian(1980, 1, 6)) ? Optional.empty() : Optional.of(big("-19"));
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
   See below for how to override the default value.

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
      <li>or, you can set a specific offset from {@link TAI} manually, using the System property mentioned below.
      This will let you use UTC <em>over the time span that corresponds to that specific offset from {@link TAI}</em>. 
    </ul>
  */
  UTC {
    /** 
     UTC - TAI. 
     <b>Fixed value for all times.</b> 
     Default is -37s.  
     An empty value is returned if the date is before 2017-01-01 (the date of the most recent leap second),
     unless the caller has set an override.
     
     <P>An override is set with a System property named <em>UTC-minus-TAI</em> to the desired value:
     <pre>-DUTC-minus-TAI=-38</pre>
     Override values must be an integral number of seconds.
    */
    @Override public Optional<BigDecimal> secondsFromTAI(DateTime when) {
      String override = System.getProperty(TimescaleImpl.UTC_SYS_PROPERTY);
      if (Check.textHasContent(override)) {
        try {
          @SuppressWarnings("unused")
          Integer overrideSeconds = Integer.valueOf(override);
          return Optional.of(big(override));
        }
        catch(NumberFormatException ex) {
          throw new IllegalArgumentException("System property " + UTC_SYS_PROPERTY + " should be an integer, but isn't: " + override);
        }
      }
      
      if (when.date().lt(Date.gregorian(2017, 1, 1))) {
        return Optional.empty();
      }
      else {
        return Optional.of(big("-37"));
      }
    }
  },
  
  /** 
   Universal Time, reflecting the rotation of the Earth. 
   Sometimes referred to as <em>UT</em>. 
   Always within 0.9 seconds of UTC.
  */
  UT1 {
    /** 
     UT1 - TAI in seconds.
     <P>Uses an underlying table of values from a text file. 
     The text file is placed in the same directory as this class.
     The text file contains a snapshot of the <a href='https://hpiers.obspm.fr/eop-pc/index.php?index=C04&lang=en'>IERS EOP C04 series</a>.
     Manually updates to this file are needed in order to stay current. 
 
     <P>An empty value is returned for dates preceding 1962-01-01, unless the caller has specified an override.
     <P>Override values can be set using a System property named <em>UT1-minus-TAI</em> to the desired value:
     <pre>-DUT1-minus-TAI=27</pre>
    */
    @Override public Optional<BigDecimal> secondsFromTAI(DateTime when) {
      Ut1Helper helper = new Ut1Helper();
      return helper.lookup(when);
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
    /** 
     TDB - TAI. 
     Never empty. Modeled as a simple periodic function. 
     Has 50 microsecond accuracy in years 1980-2100 (SOFA Cookbook). 
     Reference: Explanatory Supplement (2006), page 42, Equation 2.222-1. 
    */
    @Override public Optional<BigDecimal> secondsFromTAI(DateTime when) {
      //uses TT as a base
      //TDB - TT = 0.001658s * sin(g) + 0.000014s * sin(2g)
      //g = 357.53 + 0.9856003 * (JD - 2451545.0)
      //50 microsecond accuracy in 1980-2100 (SOFA Cookbook) - 0.05 msec
      JulianDate jd = JulianDateConverter.using(Calendar.GREGORIAN).toJulianDate(when);
      BigDecimal diff = jd.jd().subtract(JulianDate.J2000);
      double g = Math.toRadians(357.53 + 0.985_6003 * (diff.doubleValue()));
      BigDecimal tdbMinusTT = big(0.001_658 * Math.sin(g) + 0.000_014 * Math.sin(2*g)); //seconds
      return Optional.of(TT.secondsFromTAI(when).get().add(tdbMinusTT)); 
    }
  };
  
  /** Used to set a System property, and override a default value for UTC - TAI: {@value}. */
  public static final String UTC_SYS_PROPERTY = "UTC-minus-TAI";
  
  /** Used to set a System property, and override a default value for UT1 - TAI: {@value}. */
  public static final String UT1_SYS_PROPERTY = "UT1-minus-TAI";
  
  /** {@value} seconds. */
  static final Double TT_MINUS_TAI = 32.184;
}
