package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import astrolib.util.Consts;

import static astrolib.util.Consts.*;

/**
 The Julian date is a simple decimal number that increases monotonically with time.
 <b>A Julian date can be applied to different calendars and different time scales.</b>

 <P>Don't confuse the term <em>Julian date</em> with <em>dates in the Julian calendar</em>. 
 Those are two separate ideas, with names that are annoyingly similar. (They are named after two different Julians!) 
 
 <P>The Julian <em>day number</em> is a integral count of days.

  <P>The Julian <em>date</em> adds a fraction of a day to the <em>day number</em>, corresponding to the time elapsed since the 
 <em>preceding noon</em> in some calendar and timescale. In this implementation, the number of decimals is arbitrary, since this 
 implementation uses {@link BigDecimal} instead of <em>double</em>.
 
 <P>At 0h, the Julian date always ends in <em>xxx.5</em>.
 
 <P>From the <em>Explanatory Supplement</em>, 2006 <a href='https://archive.org/details/explanatorysuppl00pken/page/54/mode/2up'>page 55</a>:
  
 <P><em>The number assigned to a day in this continuous count is the Julian Day Number which is 
 defined to be 0 for the day starting at Greenwich mean noon on 1 January 4713 B.C., Julian proleptic calendar.</em>
 
 <P>To me, this seems slightly misleading. 
 The statement should be more clearly generic with respect to the idea of <em>Greenwich mean noon </em>.
 Julian dates can be used with different time scales, as stated later in the same text:
 
 <P><em>Julian dates can be expressed in Universal Time or dynamical time, though the timescale should be specified if it is a concern.</em>
*/
public final class JulianDate implements Comparable<JulianDate> {

  /** {@value} corresponds to 1858 November 17 at 0h. */
  public static final BigDecimal MODIFIED_JD_ORIGIN = BigDecimal.valueOf(2400000.5);
  
  /** {@value} The Julian epoch J2000.0 corresponds to 2000 January 1 at 12h. */
  public static final BigDecimal J2000 = BigDecimal.valueOf(2451545.0);

  
  /** Factory method. */
  public static JulianDate from(BigDecimal jd, Timescale timescale) {
    return new JulianDate(jd, timescale);
  }

  /** Return the date-time corresponding to this Julian date, using the given calendar. */
  public DateTime toDateTime(Calendar calendar) {
    JulianDateConverter convert = JulianDateConverter.using(calendar);
    return convert.toDateTime(this);
  }

  public Timescale timescale() { return timescale; }
  
  public BigDecimal jd() { return jd; }

  /** The Julian date minus {@link #MODIFIED_JD_ORIGIN}. */
  public BigDecimal modifiedJd() { 
    return jd.subtract(MODIFIED_JD_ORIGIN); 
  }

  /** 
   The number of (fractional) days since some other {@link JulianDate}.
   Positive if this Julian date is after the other Julian date, negative if before. 
  */
  public BigDecimal daysSince(JulianDate other) { 
    return this.jd.subtract(other.jd); 
  }
  
  /** 
   The number of (fractional) Julian centuries since some other {@link JulianDate}.
   See {@link Consts#DAYS_IN_JULIAN_CENTURY}. 
  */
  public BigDecimal julianCenturiesSince(JulianDate that) {
    return divide(daysSince(that), JULIAN_CENTURY_DAYS);
  }
  
  /** The fraction part of this {@link JulianDate}. Has the same sign as this {@link JulianDate}. */
  public BigDecimal fraction() {
    BigDecimal integerPart = new BigDecimal(noFraction(), 0);
    return jd.subtract(integerPart);
  }
  
  /** The ingteger part of this {@link JulianDate}. Has the same sign as this {@link JulianDate}. */
  public BigInteger noFraction() {
    return jd.toBigInteger();
  }

  /** For debugging/logging only. */
  @Override public String toString() { return jd + " " + timescale; }
  
  @Override public boolean equals(Object aThat) {
    if (this == aThat) return true;
    if (!(aThat instanceof JulianDate)) return false;
    JulianDate that = (JulianDate)aThat;
    for(int i = 0; i < this.getSigFields().length; ++i){
      if (!Objects.equals(this.getSigFields()[i], that.getSigFields()[i])){
        return false;
      }
    }
    return true;
  }  
  
  @Override public int hashCode() {
    return Objects.hash(getSigFields());
  }
  
  @Override public int compareTo(JulianDate that) {
    if (this == that) return EQUAL;

    int comparison = this.timescale.id().compareTo(that.timescale.id());
    if (comparison != EQUAL) return comparison;

    comparison = this.jd.compareTo(that.jd);
    if (comparison != EQUAL) return comparison;
    
    return EQUAL;
  }
  
  /** {@value} */
  private static final BigDecimal JULIAN_CENTURY_DAYS = BigDecimal.valueOf(DAYS_IN_JULIAN_CENTURY);

  private BigDecimal jd;
  private Timescale timescale;
  
  private JulianDate(BigDecimal jd, Timescale timescale) {
    this.jd = jd;
    this.timescale = timescale;
  }
  
  private Object[] getSigFields() {
    Object[] res = {jd, timescale};
    return res;
  }
}