package astrolib.when;

/** How a calendar determines if a year is a leap year. */
public interface CalendarLeapYear {
  
  /** Determine if a given year is a leap year in this calendar. */
  public boolean isLeap(long year);

}
