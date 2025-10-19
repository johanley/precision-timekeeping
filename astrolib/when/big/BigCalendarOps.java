package astrolib.when.big;

/** Operations implemented by supported calendars. */
public interface BigCalendarOps {
  
  /** Determine if a given year is a leap year in this calendar. */
  public boolean isLeap(long year);

}
