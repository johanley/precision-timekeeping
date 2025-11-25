package bigtime.when;

import static bigtime.when.BigDecimalHelper.*;
import static bigtime.when.Calendar.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import bigtime.util.Check;
import bigtime.util.DataFileReader;

/**
 Look up the value UT1-TAI from a data file.
 The source is a snapshot of the <a href='https://hpiers.obspm.fr/eop-pc/index.php?index=C04&lang=en'>IERS EOP C04 series</a>.
 The values in the source file are expressed in milliseconds.
 The source file needs to be updated manually.
*/
final class Ut1Helper {
  
  /** 
   Read in source data file. 
   The file is large.
   Its contents are in memory until this object is garbage-collected. 
  */
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
   {@link TimescaleImpl#UT1_SYS_PROPERTY} to a specific numeric value.
   In that case, only that specific numeric value is returned by this method.
   
   @param dt will be internally converted to use {@link Calendar#GREGORIAN} if 
   its not already in use. The result is only very weakly dependent on the {@link Timescale}.
   To a sub-millisecond accuracy, any {@link Timescale} may be used for the input {@link DateTime}.
  */
  Optional<BigDecimal> lookup(DateTime dt) {
    BigDecimal res = override();
    if (res != null) return Optional.of(res);

    DateTime dateTime = dt; 
    if (GREGORIAN != dateTime.date().calendar()) {
      dateTime = DateTime.from(dt.toJulianDate(), GREGORIAN);
    }
    //any concerns regarding the timescale here?
    
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
      else {
        res = interpolateUsingTimeOfDay(dateTime);
      }
    }
    
    //milliseconds to seconds
    if (res != null) {
      res = divide(res, big("1000"));
      res = rounded(res);
    }
    return res != null ? Optional.of(res) : Optional.empty();
  }
  
  /*
   Example of two lines of data:
     1980  9 30 -18967.5278  0.4000 
     1980 10  1 -18969.7139  0.4000
   Idea: use strings as keys. Avoid the creation of many date-time objects.
   */
  private Date earliestDate; 
  private Date mostRecentDate;
  
  /** 
   This map is large. 
   The keys and values are just raw strings from the file. 
   (This avoids the unnecessary creation of thousands of DateTime and BigDecimal objects.)
   To look up values in the table, just format the input date-time as a string in the same format. 
  */
  private Map<String /*1980  9 30*/, String /*-18967.5278*/> table = new LinkedHashMap<>();

  private BigDecimal override() {
    BigDecimal res = null; 
    String override = System.getProperty(TimescaleImpl.UT1_SYS_PROPERTY);
    if (Check.textHasContent(override)) {
      try {
        res = rounded(big(override));
      }
      catch(NumberFormatException ex) {
        throw new IllegalArgumentException("System property " + TimescaleImpl.UT1_SYS_PROPERTY + " should be a double, but isn't: " + override);
      }
    }
    return res;
  }
  
  /** Format a date in the same style as the underlying data file. */
  private String key(Date dt) {
    String sep = " ";
    return dt.year() + sep + padOnLeft(dt.month()) + sep + padOnLeft(dt.day());
  }
  
  private String padOnLeft(int n) {
    String padding = n < 10 ? " " : "";
    return padding + n;
  }

  /** Assumes the lookup succeeds! */
  private BigDecimal lookup(Date date) {
    return big(table.get(key(date)));
  }

  /** 
   Simple linear interpolation. 
   It might the case that a 'bigger' interpolation algorithm is more appropriate, given the data. 
  */ 
  private BigDecimal interpolateUsingTimeOfDay(DateTime dt) {
    BigDecimal fraction = dt.time().fraction();
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
    DataFileReader reader = new DataFileReader();
    List<String> lines = reader.readFileUTF8(this.getClass(), "UT1-TAI.txt");
    String first = "", last = "";
    for(String line : lines) {
      if (line.trim().startsWith(DataFileReader.COMMENT)) continue;
      if (!Check.textHasContent(line)) continue;
      
      String date = line.substring(0, 10);
      String[] parts = line.split("\\s+");
      String seconds = parts[3];
      table.put(date, seconds);
      
      if (!Check.textHasContent(first)) {
        first = date;
      }
      last = date;
    }
    this.earliestDate = buildDate(first);
    this.mostRecentDate = buildDate(last);
  }

  /**  '1980  9 30' */
  private Date buildDate(String text) {
    String[] parts = text.split("\\s+");
    int y = Integer.valueOf(parts[0]);
    int m = Integer.valueOf(parts[1]);
    int d = Integer.valueOf(parts[2]);
    return Date.from(y, m, d, GREGORIAN);
  }
  
  private BigDecimal rounded(BigDecimal res) {
    return round(res, 7, RoundingMode.HALF_EVEN);
  }
}
