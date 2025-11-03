package astrolib.when;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static astrolib.when.BigDecimalHelper.*;
import static astrolib.util.Consts.*;

public interface Timescale {
  
  /** 
   The difference between this timescale and TAI, in seconds.
   Positive if this timescale is ahead of TAI, negative otherwise.
   TAI is taken as the base timescale.
   
   <P>In general, this difference changes at a slow rate. 
   @param when the moment for which the difference is required, using the Gregorian Calendar, and this timescale.
   @return default is 0.
  */
  default BigDecimal secondsFromTAI(DateTime when) {
    return BigDecimal.ZERO;
  } 
  
  /** A convenient identifier for this timescale. Usually an abbreviation. */
  default String id() { return this.toString(); }

  /** 
   A <em>dynamical</em> timescale represents the independent time variable in theories of the motion of solar system objects.
   According to the theory of relativity, this independent variable depends on the coordinate system being used as the system of reference.
   
   <P>A non-dynamical timescale is referred to as a <em>coordinate</em> timescale.
   <P>Default is false.
  */
  default Boolean isDynamicalTimescale() { return Boolean.FALSE; }
  
  /** 
   Convert a date-time into a from one timescale to another.
   The caller will often want to do a rounding operation on the result, to reflect a desired precision.
   
   @param toTimescale the target timescale.
   @param when contains the source timescale, attached to its {@link Time}.  
   If the source timescale is the same as the target timescale, then <tt>when</tt> is simply returned unchanged.
   @return a date-time whose date and time reflects the target timescale.  
  */
  public static DateTime convertTo(Timescale toTimescale, DateTime when) {
    if (toTimescale == when.time().timescale()) {
      return when; //early exit; no conversion is possible
    }
    BigDecimal jd = JulianDateConverter.using(when.date().calendar()).toJulianDate(when).jd();

    //the calc is simple since BigDecimal handles any number of decimal places
    //any 'rollover' effects into another minute-hour-day-year are already handled by other classes
    BigDecimal seconds = jd.multiply(big(SECONDS_PER_DAY)); 
    BigDecimal toMinusTAI = toTimescale.secondsFromTAI(when);
    BigDecimal fromMinusTAI = when.time().timescale().secondsFromTAI(when);
    seconds = seconds.add(toMinusTAI).subtract(fromMinusTAI);
    
    BigDecimal days = divide(seconds, big(SECONDS_PER_DAY));
    JulianDate jdConverted = JulianDate.from(days, toTimescale);
    DateTime result = JulianDateConverter.using(when.date().calendar()).toDateTime(jdConverted);
    return result;
  }
}
