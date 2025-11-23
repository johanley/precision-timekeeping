package astrolib.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import astrolib.util.Check;

/**
 Look up the value UT1-TAI from a data file.
 The source is a snapshot of the <a href='https://hpiers.obspm.fr/eop-pc/index.php?index=C04&lang=en'>IERS EOP C04 series</a>.
 The values in the source file are expressed in milliseconds.
*/
final class Ut1Helper {
  
  /** Read in source data file. */
  Ut1Helper(){
    readInSourceData();
  }
  
  /**
   Return the value of UT1 - TAI in seconds using an IERS data file (EOP C04 Series).
   <ul>
    <li>before the file's earliest date: return an empty value. 
    <li>after the file's most recent date: use the file's most recent value, with no extrapolation
    <li>otherwise, look up the value for the given date; interpolate using time-of-day if non-zero
   </ul> 
   
   <P>The above logic can be overridden by setting a System property named 
   {@link TimescaleCommon#UT1_SYS_PROPERTY} to a specific numeric value.
   In that case, only that specific numeric value is returned by this method.
   
   @param dt will be internally converted to use {@link Calendar#GREGORIAN} and 
   {@link TimescaleCommon#UTC}, if they are not already in use. (In practice, the 
   timescale is of little significance in this context.) 
  */
  Optional<BigDecimal> lookup(DateTime dt) {
    BigDecimal res = override();
    if (res != null) return Optional.of(res);

    DateTime dateTime = dt; 
    if (Calendar.GREGORIAN != dateTime.date().calendar()) {
      dateTime = DateTime.from(dt.toJulianDate(), Calendar.GREGORIAN);
    }
    if (TimescaleCommon.UTC != dateTime.time().timescale()) {
      dateTime = Timescale.convertTo(TimescaleCommon.UTC, dateTime);
    }
    
    if (dateTime.date().lt(earliestDate)) {
      //do nothing: null
    }
    else if (dateTime.date().gteq(mostRecentDate)) {
      res = lookup(mostRecentDate);
    }
    else if (dateTime.date().gteq(earliestDate) && dateTime.date().lt(mostRecentDate)) {
      if (dateTime.time().equals(Time.zero(dateTime.time().timescale()))) {
        res = lookup(dateTime.date());
      }
      res = interpolateUsingTimeOfDay(dateTime);
    }
    
    //milliseconds to seconds
    if (res != null) {
      res = res.multiply(new BigDecimal("1000"));
    }
    return Optional.of(res);
  }
  
  private BigDecimal override() {
    BigDecimal res = null; 
    String override = System.getProperty(TimescaleCommon.UT1_SYS_PROPERTY);
    if (Check.textHasContent(override)) {
      try {
        res = new BigDecimal(override);
      }
      catch(NumberFormatException ex) {
        throw new IllegalArgumentException("System property " + TimescaleCommon.UT1_SYS_PROPERTY + " should be a double, but isn't: " + override);
      }
    }
    return res;
  }

  /*
   Example of two lines of data:
     1980  9 30 -18967.5278  0.4000 
     1980 10  1 -18969.7139  0.4000
   Idea: use strings as keys. Avoid the creation of many date-time objects.
   Format the input date-time as a string in the same format, in order to look up values in the table.
   */
  private Date earliestDate; 
  private Date mostRecentDate;
  /** 
   This map is large. 
   It's lifetime is the lifetime of this object. 
   The keys and values are just raw strings from the file. 
   (This avoids the unnecessary creation of thousands of DateTime and BigDecimal objects.)
   To look up values in the table, just format the input date-time as a string in the same format. 
  */
  private Map<String /*1980  9 30*/, String /*-18967.5278*/> table = new LinkedHashMap<>();

  /** Format a date in the same style as the underlying data file. Note the extra space. */
  private String key(Date dt) {
    LocalDate localDt = LocalDate.of((int)dt.year(), dt.month(), dt.day());
    return localDt.format(DateTimeFormatter.ofPattern("yyyy  M  d"));
  }

  /** Assumes the lookup succeeds! */
  private BigDecimal lookup(Date date) {
    return new BigDecimal(table.get(key(date)));
  }

  /** 
   Simple linear interpolation. 
   It might the case that a 'bigger' interpolation algorithm is more appropriate, given the data. 
  */ 
  private BigDecimal interpolateUsingTimeOfDay(DateTime dt) {
    BigDecimal fraction = dt.fractionalDay();
    BigDecimal d0 = lookup(dt.date());
    BigDecimal d1 = lookup(dt.date().plusMinusDays(1));
    BigDecimal diff = d1.subtract(d0);
    return d0.add(diff.multiply(fraction));
  }

  /**
   Build a map containing the data in the file.
   
   Example of two lines of data, and the header:
   
   #   date    ut1-tai    sig      (ms)                    
   1980  9 30 -18967.5278  0.4000 
   1980 10  1 -18969.7139  0.4000
   
   The sigma value at the end is discarded here.
  */
  private void readInSourceData() {
    //the file is assumed to be in this directory
    //Util.DataFileReader is needed
    //it's big; should it be streamed into memory?
    //use its fixed-width character
    //add to the map
    //the keys and values are strings! The conversions come when needed, later
  }
}
