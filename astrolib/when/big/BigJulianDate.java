package astrolib.when.big;

import java.math.BigDecimal;
import java.math.BigInteger;
import static astrolib.when.big.BigDecimalHelper.*;

/**
 The Julian date is a simple decimal number that increases monotonically with time.
 <b>A Julian date can be applied to different calendars and different time scales.</b>
 
 <P>The Julian <em>day number</em> is a count of days.

  <P>The Julian <em>date</em> adds a fraction of a day to the <em>day number</em>, corresponding to the time elapsed since the 
 preceding noon in some calendar and timescale. In this implementation, the number of decimals is arbitrary, since this 
 implementation uses {@link BigDecimal} instead of <em>double</em>.
 
 <P>At 0h, the Julian date always ends in <em>xxx.5</em>.

 <P>Don't confuse the term <em>Julian date</em> with <em>dates in the Julian calendar</em>. 
 Those are two separate ideas, with names that are annoyingly similar. 
 
 <P>From the <em>Explanatory Supplement</em>, 2006 <a href='https://archive.org/details/explanatorysuppl00pken/page/54/mode/2up'>page 55</a>:
  
 <P><em>The number assigned to a day in this continuous count is the Julian Day Number which is 
 defined to be 0 for the day starting at Greenwich mean noon on 1 January 4713 B.C., Julian proleptic calendar.</em>
 
 <P>To me, this seems misleading. 
 The statement should be more clearly generic with respect to the idea of <em>Greenwich mean noon </em>.
 Julian dates can be used with different time scales, as stated later in the same text:
 
 <P><em>Julian dates can be expressed in Universal Time or dynamical time, though the timescale should be specified if it is a concern.</em>
*/
public final class BigJulianDate {
  
  /** Factory method. */
  public static BigJulianDate from(BigDecimal jd, BigTimescale timescale) {
    return new BigJulianDate(jd, timescale);
  }

  /** Return the date-time corresponding to this Julian date, using the given calendar. */
  public BigDateTime toDateTime(BigCalendar calendar) {
    BigJulianDateConverter convert = BigJulianDateConverter.using(calendar);
    return convert.toDateTime(this);
  }

  public BigTimescale timescale() { return timescale; }
  
  public BigDecimal jd() { return jd; }
  
  public BigDecimal modifiedJd() { 
    return jd.subtract(MODIFIED_JD_ORIGIN); 
  }
  
  public BigDecimal daysSince(BigJulianDate that) { 
    return this.jd.subtract(that.jd); 
  }
  
  public BigDecimal julianCenturiesSince(BigJulianDate that) {
    return divvy(daysSince(that), JULIAN_CENTURY_DAYS);
  }
  
  /** The fraction of the day.  Has the same sign as this Julian date. */
  public BigDecimal fraction() {
    BigDecimal integerPart = new BigDecimal(noFraction(), 0);
    return jd.subtract(integerPart);
  }
  
  /** With no fractional part. Has the same sign as this Julian date. */
  public BigInteger noFraction() {
    return jd.toBigInteger();
  }

  /** For debugging/logging only. */
  @Override public String toString() { return jd + " " + timescale; }

  /** {@value} */
  private static final BigDecimal MODIFIED_JD_ORIGIN = BigDecimal.valueOf(2400000.5);
  
   /** {@value} */
  private static final BigDecimal JULIAN_CENTURY_DAYS = BigDecimal.valueOf(36525);

  private BigDecimal jd;
  private BigTimescale timescale;
  
  private BigJulianDate(BigDecimal jd, BigTimescale timescale) {
    this.jd = jd;
    this.timescale = timescale;
  }
}