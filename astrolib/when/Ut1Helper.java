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
  The source is <a href='https://hpiers.obspm.fr/eop-pc/index.php?index=C04&lang=en'>IERS EOP C04 series</a>.
  The values in the source file are expressed in milliseconds.
*/
final class Ut1Helper {
  
  /**
   Look up the value of UT1 - TAI from an IERS data file (EOP C04 Series).
   <ul>
    <li>before the file's earliest date: return an empty value. 
    <li>after the file's most recent date: use the file's most recent value, with no extrapolation
    <li>otherwise, interpolate between dates in the file 
   </ul> 
   
   <P>The above logic can be overridden by setting a System property named {@link TimescaleCommon#UT1_SYS_PROPERTY} to a specific numeric value.
   In that case, only that specific numeric value is returned by this method.
  */
  Optional<BigDecimal> lookup(DateTime dt) {
    BigDecimal res = override();
    if (res != null) return Optional.of(res);

    if (dt.date().lt(earliestDate)) {
      //do nothing: null
    }
    else if (dt.date().gteq(mostRecentDate)) {
      res = lookup(mostRecentDate);
    }
    else if (dt.date().gteq(earliestDate) && dt.date().lt(mostRecentDate)) {
      res = interpolate(dt);
    }
    return Optional.of(res);
  }
  
  @SuppressWarnings("unused")
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

  private BigDecimal interpolate(DateTime dt) {
    return null;
  }

  /*
   Example of two lines of data:
     1980  9 30 -18967.5278  0.4000 
     1980 10  1 -18969.7139  0.4000
  */
  private void readInSourceData() {
    //the file is assumed to be in this directory
    //it's big; should it be streamed into memory?
    //chop using white space? or fixed width
    //add to the map
    //the keys and values are strings! The conversions come when needed, later
  }
  


}
