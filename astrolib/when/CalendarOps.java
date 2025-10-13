package astrolib.when;

/** Operations implemented by supported calendars. */
public interface CalendarOps {
  
  /** Determine if a given year is a leap year in this calendar. */
  public boolean isLeap(int year);

  /** Convert a date-time in this calendar to a Julian date. */
  public JulianDate jd(int year, int month, double fractionalDay, Timescale timescale);
  
  /** Convert a Julian date to a date-time in this calendar. */
  public DateTime date(JulianDate jd);
  
}
