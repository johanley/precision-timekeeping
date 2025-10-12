package astrolib.when;

/** Operations implemented by supported calendars. */
public interface CalendarOps {
  
  /** Determine if a given year is a leap year in this calendar. */
  public boolean isLeap(int year);

  /** Convert a date-time in this calendar to a Julian date. */
  public double jd(int year, int month, double fractionalDay);
  
  /** Convert a Julien date to a date-time in this calendar. */
  //public Date date(JulianDate jd);
  
}
