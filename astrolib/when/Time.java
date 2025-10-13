package astrolib.when;

import java.util.Objects;

import astrolib.util.Check;
import astrolib.util.Mathy;
import static astrolib.util.LogUtil.zeroPad;

/** 
 Data-carrier for time information.
 This class is meant for input and output, not for core calculations.
*/
public final class Time implements Comparable<Time> {
  
  /**
   Factory method.
    
   @param hour range [0,23]
   @param minute range [0,59]
   @param seconds range [0,61.0) for the UTC timescale, and [0,60.0) for all other timescales. The extra second exists because of leap seconds. 
  */
  public static Time from(int hour, int minute, double seconds, Timescale timescale) {
    return new Time(hour, minute, seconds, timescale);
  }
  
  /**
   Factory method.
   
   @param fraction of a day [0.0,1.0)
   @param numSecondsInDay the number of seconds in a given day. 86400 for most timescales, 
   but the UT1 timescale can be in a range plus-minus 1 second from this value.
  */
  public static Time from(double fraction, int numSecondsInDay, Timescale timescale) {
    return new Time(fraction, numSecondsInDay, timescale);
  }
  
  public int hour() { return hour; }
  public int minute() { return minute; }
  public double seconds() { return seconds; }
  public Timescale timescale() { return timescale; }
  
  /** 
   Return a value in the range [0.0 to 1.0).
   <P>Because of leap seconds, the number of seconds in a day can vary.  
  */
  public double fraction(int numSecondsInDay) {
    if (fraction == null) {
      double totalSeconds = hour * SECONDS_PER_HOUR + minute * SECONDS_PER_MINUTE + seconds;
      fraction = totalSeconds / numSecondsInDay; //never int div
    }
    return fraction;
  }
  
  /** Intended for logging only. Example: <em>01:09:02.0 TT</em> */
  @Override public String toString() {
    String colon = ":";
    return zeroPad(hour) + colon + zeroPad(minute) + colon + zeroPad(seconds) + " " + timescale;  
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
    final int EQUAL = 0;
    if (this == that) return EQUAL;

    int comparison = this.timescale.compareTo(that.timescale);
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
  private Double seconds; 
  private Double fraction;
  private static final int SECONDS_PER_HOUR = 60 * 60;
  private static final int SECONDS_PER_MINUTE = 60;
  private static final int SECONDS_REGULAR_DAY = 24 * SECONDS_PER_HOUR;
  
  private Time(int hour, int minute, double seconds, Timescale timescale) {
    init(hour, minute, seconds, timescale);
  }
  
  private Time(double fraction, int numSecondsInDay, Timescale timescale) {
    Check.range(fraction, 0.0, 1.0);
    if (Timescale.UT1 == timescale) {
      Check.range(numSecondsInDay, SECONDS_REGULAR_DAY - 1 , SECONDS_REGULAR_DAY + 1);
    }
    else {
      if (numSecondsInDay != SECONDS_REGULAR_DAY) {
        throw new IllegalArgumentException("Should be " + SECONDS_REGULAR_DAY + " :" + numSecondsInDay);
      }
    }
    double manySeconds = fraction * numSecondsInDay;
    //THE NUMBER OF SECONDS FLOW REGULARLY, EXCEPT POSSIBLY FOR THE LAST MINUTE OF THE DAY
    int hours = Mathy.truncate(manySeconds / SECONDS_PER_HOUR );
    double remainder = manySeconds - hours * SECONDS_PER_HOUR;
    int minutes = (int)Mathy.truncate(remainder / SECONDS_PER_MINUTE);
    //the last minute of the day can have oddball seconds
    remainder = remainder - minutes * SECONDS_PER_MINUTE;
    init(hours, minutes, remainder, timescale);
    this.fraction = fraction; //preserve the given value; we don't want to recalculate it later
  }
  
  private void init(int hour, int minute, double seconds, Timescale timescale) {
    Check.range(hour, 0, 23);
    Check.range(minute, 0, 59); 
    double max = Timescale.UTC == timescale ? 61.0 : 60.0;  //because of leap seconds
    Check.range(seconds, 0.0, max); //max is excluded here
    this.timescale = timescale;
    this.hour = hour;
    this.minute = minute;
    this.seconds = seconds;
  }
  
  private Object[] getSigFields() {
    Object[] res = {timescale, hour, minute, seconds};
    return res;
  }
}
