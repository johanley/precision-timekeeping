package astrolib.when;

import astrolib.util.Mathy;

/**
 The Julian date is a simple floating point number that increases monotonically with time.
 <b>A Julian date can be applied to different calendars and different time scales.</b>
 
 <P>The Julian <em>day number</em> is a count of days.

  <P>The Julian <em>date</em> adds a fraction of a day to the <em>day number</em>, corresponding to the time elapsed since the 
 preceding noon in some calendar and timescale.
 
 <P>At 0h, the Julian date always ends in <em>xxx.5</em>.

 <P>Don't confuse the term <em>Julian date</em> with <em>dates in the Julian calendar</em>. 
 Those are two separate ideas, with names that are annoyingly similar. 
 
 <P>From the <em>Explanatory Supplement</em>, 2006 <a href='https://archive.org/details/explanatorysuppl00pken/page/54/mode/2up'>page 55</a>:
  
 <P><em>The number assigned to a day in this continuous count is the Julian Day Number which is 
 defined to be 0 for the day starting at Greenwich mean noon on 1 January 4713 B.C., Julian proleptic calendar.</em>
 
 <P>To me, this seems misleading. 
 The statement should be more clearly generic with respect to what <em>0h / Greenwich mean noon </em> refers to.
 Julian dates can be used with different time scales, as stated later in the same text:
 
 <P><em>Julian dates can be expressed in Universal Time or dynamical time, though the timescale should be specified if it is a concern.</em>
*/
public final class JulianDate {
  
  /**
   Factory method using two parts that sum to the Julian day number.
   
   <P>This follows the style of SOFA, quoted here:
    
   <P><em>The Julian Date is apportioned in any convenient way between the two arguments. 
   For example, JD=2450123.7 could be expressed in any of these ways, among others:
   <pre>
    a                     b
    2450123.7           0.0    JD method
    2451545.0       -1421.3    J2000 method
    2400000.5       50123.2    MJD method
    2450123.5           0.2    date-and-time method
    </pre></em>
  */
  public static JulianDate from(double a, double b, Timescale timescale) {
    return new JulianDate(a, b, timescale);
  }

  /** Factory method. */
  public static JulianDate from(double jd, Timescale timescale) {
    return new JulianDate(jd, 0.0, timescale);
  }

  public Timescale timescale() { return timescale; }
  
  public double jd() { return jd; }
  
  public double modifiedJd() { return jd - MODIFIED_JD_ORIGIN; }
  
  public double daysSince(JulianDate that) { 
    return this.jd - that.jd; 
  }
  
  public double julianCenturiesSince(JulianDate that) { 
    return (this.jd - that.jd) / JULIAN_CENTURY_DAYS; 
  }
  
  /** The fraction of the day. Can be negative, but only if JD is negative. */
  public double fraction() {
    Double positive = Math.abs(jd);
    double frac = positive - Math.floor(positive);
    return Mathy.sign(jd) * frac;
  }
  
  /** With no fractional part. Can be negative, but only if JD is negative. */
  public int noFraction() {
    Double positive = Math.abs(jd);
    int dayNum = (int)Math.floor(positive);
    return Mathy.sign(jd) * dayNum;
  }

  /** {@value} */
  public static final double MODIFIED_JD_ORIGIN = 2400000.5;
  
  /** {@value} */
  public static final double J2000_JD = 2451545.0;

  /** {@value} */
  public static final double JULIAN_CENTURY_DAYS = 36525.0;

  private Timescale timescale;
  private Double jd;
  
  private JulianDate(double a, double b, Timescale timescale) {
    this.jd = a + b;
    this.timescale = timescale;
  }
}