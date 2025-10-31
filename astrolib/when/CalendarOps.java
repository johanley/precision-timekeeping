package astrolib.when;

/** Operations implemented by supported calendars. */
public interface CalendarOps {
  
  /** Determine if a given year is a leap year in this calendar. */
  public boolean isLeap(long year);

}
