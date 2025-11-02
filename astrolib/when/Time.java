package astrolib.when;

import static astrolib.util.Consts.*;
import static astrolib.util.LogUtil.*;
import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.util.Objects;

import astrolib.util.Check;

/** Data-carrier for time information. */
public final class Time implements Comparable<Time> {
  
  /**
   Factory method.
   @param hour range [0,23]
   @param minute range [0,59]
   @param seconds range [0,60.0). Leap seconds are not supported in this library. 
  */
  public static Time from(int hour, int minute, BigDecimal seconds, Timescale timescale) {
    return new Time(hour, minute, seconds, timescale);
  }
  
  /**
   Factory method.
   @param fraction of a day [0.0,1.0)
  */
  public static Time from(BigDecimal fraction, Timescale timescale) {
    return new Time(fraction, timescale);
  }
  
  /** 00:00:00 in the given timescale. */
  public static Time zero(Timescale timescale) {
    return new Time(0, 0, BigDecimal.ZERO, timescale);
  }
  
  public int hour() { return hour; }
  public int minute() { return minute; }
  public BigDecimal seconds() { return seconds; }
  public Timescale timescale() { return timescale; }
  
  /**  This time as a fraction of a full 24 hour day. Return a value in the range [0.0 to 1.0). */
  public BigDecimal fraction() {
    if (fraction == null) {
      BigDecimal hr = big(hour * SECONDS_PER_HOUR);
      BigDecimal min = big(minute * SECONDS_PER_MINUTE);
      BigDecimal totalSeconds = hr.add(min).add(seconds);
      fraction = divide(totalSeconds, big(SECONDS_PER_DAY));
    }
    return fraction;
  }
  
  /** Intended for logging only. Example: <em>01:09:02.0 TT</em> */
  @Override public String toString() {
    String colon = ":";
    String padding = seconds.doubleValue() < 10 ? "0" : "";
    return zeroPad(hour) + colon + zeroPad(minute) + colon + padding + seconds.toString() + " " + timescale;  
  }
  
  @Override public boolean equals(Object aThat) {
    if (this == aThat) return true;
    if (!(aThat instanceof Time)) return false;
    Time that = (Time)aThat;
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
  
  @Override public int compareTo(Time that) {
    if (this == that) return EQUAL;

    int comparison = this.timescale.id().compareTo(that.timescale.id());
    if (comparison != EQUAL) return comparison;
    
    comparison = this.hour.compareTo(that.hour);
    if (comparison != EQUAL) return comparison;

    comparison = this.minute.compareTo(that.minute);
    if (comparison != EQUAL) return comparison;

    comparison = this.seconds.compareTo(that.seconds);
    if (comparison != EQUAL) return comparison;
    
    return EQUAL;
  }
  
  private Timescale timescale; 
  private Integer hour; 
  private Integer minute; 
  private BigDecimal seconds;
  private BigDecimal fraction;
  
  private static final int SECONDS_PER_MINUTE = 60;
  private static final int SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;
  private static final int SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR;
  
  private Time(int hour, int minute, BigDecimal seconds, Timescale timescale) {
    init(hour, minute, seconds, timescale);
  }
  
  private Time(BigDecimal fraction, Timescale timescale) {
    Check.range(fraction, 0.0, 1.0);
    BigDecimal totalSeconds = fraction.multiply(big(SECONDS_PER_DAY));
    
    BigDecimal[] hourAndRemainder = divideAndRemainder(totalSeconds, big(SECONDS_PER_HOUR));
    int hours = hourAndRemainder[INT_DIV].intValue();
    
    BigDecimal remainder = hourAndRemainder[REMAINDER]; //seconds
    BigDecimal[] minutesAndRemainder = divideAndRemainder(remainder, big(SECONDS_PER_MINUTE));
    int minutes = minutesAndRemainder[INT_DIV].intValue();
    
    remainder = minutesAndRemainder[REMAINDER]; //seconds
    
    init(hours, minutes, remainder, timescale);
    this.fraction = fraction; //preserve the given value; we don't want to recalculate it later
  }
  
  private void init(int hour, int minute, BigDecimal seconds, Timescale timescale) {
    Check.range(hour, 0, 23);
    Check.range(minute, 0, 59); 
    Check.range(seconds, 0.0, 60.0);
    this.timescale = timescale;
    this.hour = hour;
    this.minute = minute;
    this.seconds = seconds;
  }
  
  private Object[] getSigFields() {
    Object[] res = {timescale.id(), hour, minute, seconds};
    return res;
  }
}
