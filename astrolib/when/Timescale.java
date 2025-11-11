package astrolib.when;

import static astrolib.util.Consts.*;
import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;

/** 
 A timescale is a precise and well-defined way of measuring time.
 Timescales can differ in their time of origin, the rate of their clocks, or in other ways.
 
 <P>This library allows for modeling moments in time with arbitrary precision.
 <b>But transformations between timescales are a different story.</b>
 Transformations between timescales are <em>sometimes</em> known with complete precision because of how they 
 are conventionally defined, but this is not always the case. 

 <P>For example, the difference {@link TimescaleCommon#TAI} - {@link TimescaleCommon#GPS} is 
 modeled here as exactly 19 seconds. But that's not precisely correct at the nanosecond level. 

 <P><b>The target policy adopted here is to model all transformations between timescales to least sub-millisecond precision.</b>
 This allows one to implement a transformation of timescale from a source to a target in a single step, and always ensure 
 at least a modest precision (sub-millisecond), accurate for most purposes. 
 
 <P>This policy is different from the policies of the <a href='https://www.iausofa.org/'>SOFA library</a>.
 SOFA is both more precise and more fine-grained with respect to the transformations of timescales. 
 For example, the transformation between TCG and TCB is implemented by SOFA to nanosecond accuracy, and uses a large number of terms. 
 On the other hand, transforming from one timescale to another in SOFA often involves multiple steps.
 
  <P>In addition, the TCG and TCB timescales found in SOFA are not included here. 
  The reason is that they are rather specialized timescales, and not used very often.
  
  <P>(Another difference between SOFA and this library is that this library <b>always</b> models your timestamps 
  with the maximum precision, without ever losing precision. 
  SOFA's technique of using two doubles to model a Julian date allows for greater precision, 
  but that policy is not compulsory for the caller.) 
*/
public interface Timescale {
  
  /** 
   The difference between this {@link Timescale} and {@link TimescaleCommon#TAI}, in seconds.
   Positive if this timescale is ahead of TAI, negative otherwise.
   In this library, TAI is taken as the base timescale.
   
   <P>In general, this difference changes at a slow rate. 
   @param when the moment for which the difference is required. 
   This {@link DateTime} uses the {@link Calendar#GREGORIAN}.
   This {@link DateTime} can use any {@link Timescale}, because in practice the transformations are only 
   very weakly dependent on time (if at all).
   @return default is 0.
  */
  default BigDecimal secondsFromTAI(DateTime when) {
    return BigDecimal.ZERO;
  } 
  
  /** A convenient identifier for this {@link Timescale}, usually an abbreviation. */
  default String id() { return this.toString(); }

  /** 
   Convert a {@link DateTime} from its (implicit) {@link Timescale} to some other target {@link Timescale}.
   The caller will often want to do a rounding operation on the result, to reflect a desired precision, 
   but no rounding is applied here.
   
   @param toTimescale the target {@link Timescale}.
   @param fromWhen the moment to convert. It contains the source {@link Timescale}, attached to its {@link Time}.  
   If the source {@link Timescale} is the same as the target {@link Timescale}, then <tt>fromWhen</tt> is simply returned unchanged.
   @return a {@link DateTime} whose date and time reflects the target {@link Timescale}.  
  */
  public static DateTime convertTo(Timescale toTimescale, DateTime fromWhen) {
    if (toTimescale == fromWhen.time().timescale()) {
      return fromWhen; //early exit; no conversion is possible
    }
    BigDecimal jd = JulianDateConverter.using(fromWhen.date().calendar()).toJulianDate(fromWhen).jd();

    //the calc is simple since BigDecimal handles any number of decimal places
    //any 'rollover' effects into another minute-hour-day-year are already handled by other classes
    BigDecimal seconds = jd.multiply(big(SECONDS_PER_DAY)); 
    BigDecimal toMinusTAI = toTimescale.secondsFromTAI(fromWhen);
    BigDecimal fromMinusTAI = fromWhen.time().timescale().secondsFromTAI(fromWhen);
    seconds = seconds.add(toMinusTAI).subtract(fromMinusTAI);
    
    BigDecimal days = divide(seconds, big(SECONDS_PER_DAY));
    JulianDate jdConverted = JulianDate.from(days, toTimescale);
    DateTime result = JulianDateConverter.using(fromWhen.date().calendar()).toDateTime(jdConverted);
    return result;
  }
}
