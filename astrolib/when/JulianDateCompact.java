package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;

public final class JulianDateCompact {

  static double convertOleary(long y, int m, double d) {
    //Robin O'Leary
    //https://pdc.ro.nu/jd-code.html
    //his algo gives the jd for noon on the given date
    y += 8000; 
    if (m < 3) {
      --y;
      m += 12;
    }
    return 
      (y*365) 
      + (y/4) 
      - (y/100) 
      + (y/400) 
      - 1200820
      + (m*153+3)/5-92
      + d
      - 1.5;
  }

  /**
   With help from Robin O'Leary's algorithm: https://pdc.ro.nu/jd-code.html
   
   I base the calculation on counting days from January 0, year 0.
   Then I simply re-base the result at the end, to reflect the usual origin-day for Julian dates.
   This exploits the (near) symmetry of the calendar cycles.
   
   (Making a verison for the Julian calendar is very similar.)
  */
  static double gregorianToJulianDate(long y, int m, double d) {
    //completed years: small asymmetry between positive and negative years
    long y_p = (y >= 0) ? (y - 1) : y;  //y_p = y-prime
    long num_366yrs = (y_p/4) - (y_p/100) + (y_p/CYCLE_YEARS); //Robin's clever trick
    if (y > 0) {
      num_366yrs += 1; //since year 0 is a leap year
    }
    long num_365yrs = y - num_366yrs;
    double res = num_365yrs * SHORT_YR + num_366yrs * LONG_YR;    
    
    //completed months
    //Explanatory Supplement 1961, page 434: 
    int[] DAYS_IN_PRECEDING_MONTHS = {0 /*Jan*/, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 /*Dec*/};
    res += DAYS_IN_PRECEDING_MONTHS[m-1];   
    res += (isLeap(y) && (m - 1) >= 2 ? 1 : 0); //'correct' for leap years  
    
    res += d;  // the day of the month
    
    //rebase to the usual origin of Julian date
    res += JAN_0_YEAR_0;   
    return res;
  }
  
  private static final double JAN_0_YEAR_0 = 1_721_058.5;
  private static final double JAN_1_YEAR_0 = JAN_0_YEAR_0 + 1.0;
  private static final int SHORT_YR = 365;
  private static final int LONG_YR = 366;
  private static final int CYCLE_YEARS = 400;
  private static final int CYCLE_DAYS = SHORT_YR*CYCLE_YEARS + CYCLE_YEARS/4 - CYCLE_YEARS/100 + CYCLE_YEARS/CYCLE_YEARS; //146_097 days
  private static final int[] MONTH_LEN = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  
  private static boolean isLeap(long y) {
    return (y % 100 == 0) ? (y % 400 == 0) : (y % 4 == 0);
  }
  
  static final class CalendarDate {
    CalendarDate(long y, int m, double d){
      this.y = y;
      this.m = m;
      this.d = d;
    }
    long y;
    int m;
    double d;
  }
  
  static CalendarDate julianDateToGregorianOBSOLETE(double jd) {
    double BASE = JAN_0_YEAR_0 + 1.0;  
    double target = jd - BASE;
    int CYCLE_DAYS = SHORT_YR*CYCLE_YEARS + CYCLE_YEARS/4 - CYCLE_YEARS/100 + CYCLE_YEARS/CYCLE_YEARS;
    long num_cycles = (long)Math.floor(target/CYCLE_DAYS); //this gives the desired behaviour for neg years
    long year = num_cycles * CYCLE_YEARS; //start value; usually changes 

    //the idea is to approach the given target from below, using this temp value 
    double cursor = num_cycles * CYCLE_DAYS;

    //2. remainder years: whole years left after the full cycles (not including the final year)
    long year_full_cycles = year; //to remember this value in the loop below 
    for(int yr = 0; yr < CYCLE_YEARS; ++yr ) {
      int another_year = isLeap(year_full_cycles + yr) ? LONG_YR : SHORT_YR;
      if (cursor + another_year <= target) {
        cursor = cursor + another_year;
        ++year;
      } else { break; }
    }

    //3. months and days in the final year
    int[] MONTH_LEN = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    int month = 1; //starting point; can increase below
    for(int m = 1; m <= 12; ++m) {
      int another_month = MONTH_LEN[m-1];
      if (isLeap(year) && m == 2) ++another_month;  
      if (cursor + another_month <= target) {
        cursor = cursor + another_month;
        ++month;
      } else { break; }
    }

    double fractionalDays = target - cursor + 1.0; 
    return new CalendarDate(year, month, fractionalDays);
  }

  /**
    Idea; use a 'base', a point in time occurring once every 400 years, at which the calendar cycle starts.
    Counting forward in time from such any such base exploits the symmetry of the calendar's cycle.
    Let's take a base as always falling on a N*400 years from January 1.0, year 0:
      JD of a base = 1_721_059.5 + N * 146_097  N = ...-2,-1,0,1,2,...
    There are 2 loops here, with a max number of 14 loop iterations (not much).
   */
  static CalendarDate julianDateToGregorian2(double jd) {
    //1. find the closest base that PRECEDES the given moment
    long num_cycles = (long)Math.floor((jd - JAN_1_YEAR_0)/CYCLE_DAYS); //rounds towards negative infinity: good!
    double base_jd = JAN_1_YEAR_0 + num_cycles * CYCLE_DAYS; //a January 1.0 in the years  ..., -800, -400, 0, 400, 800, ... 
    long year = num_cycles * CYCLE_YEARS; // ...,-400, 0, 400,... (the starting value)
    double jd_minus_base = jd - base_jd; //never neg
    double cursor = 0.0; //points to a Jan 1.0 initially; approaches jd_minus_base from below!
    
    //2. remainder-years: whole, completed years after the base 
    //one big chunk of years: calculate a MINIMUM number of full remainder-years, to reduce loop iterations later
    int approx_days = (int)Math.floor(jd_minus_base);
    int more_years = (approx_days / LONG_YR) - 1; // at least this many
    if (more_years > 0) {
      int m_p = more_years - 1;
      int more_days = more_years * SHORT_YR + (m_p/4) - (m_p/100) + (m_p/400) + 1;
      cursor += more_days; //still on a Jan 1.0!
      year += more_years;
    }
    //loop to find the rest of the remaining-years: at most 2 iterations here!
    long year_so_far = year; //for use in the loop 
    for(int more = 0; more < CYCLE_YEARS; ++more ) { 
      int year_length = isLeap(year_so_far + more) ? LONG_YR : SHORT_YR;
      if (cursor + year_length <= jd_minus_base) {
        cursor += year_length; // Jan 1.0 of the next year
        ++year;
      } else { break; }
    }
    
    //3. months and days
    int month = 0; //both a loop index AND a result-value
    double fractionalDays = 0.0;
    for(month = 1; month <= 12; ++month) {
      int month_length = MONTH_LEN[month - 1];
      if (isLeap(year) && month == 2) ++month_length;
      if (cursor + month_length <= jd_minus_base) {
        cursor += month_length; //1st day of the next month
      }
      else {
        fractionalDays = jd_minus_base - cursor + 1.0; break;
      }
    }
    return new CalendarDate(year, month, fractionalDays);
  }
    
  
  public static void main(String[] args) {
    // testDate(1957, 10, 4.81, 2436116.31, cal);

    /*
    double jd = convertOleary(1957, 10, 4.81);
    System.out.println(jd);
    jd = gregorianToJulianDate(1957, 10, 4.81);
    System.out.println(jd);
    
    jd = convertOleary(0, 1, 1.0);
    System.out.println(jd);
    jd = gregorianToJulianDate(0, 1, 1.0);
    System.out.println(jd);

    test(0, 12, 31.0);
    test(1, 1, 1.0);
    test(-4713, 11, 24.5); //the JD=0 date
    
    //400-logic
    test(400, 1, 1.5); //1867157 

    
    jd = gregorianToJulianDate(0, 1, 31.0);
    System.out.println(jd);
    jd = gregorianToJulianDate(0, 2, 1.0);
    System.out.println(jd);
    jd = gregorianToJulianDate(0, 2, 28.0);
    System.out.println(jd);
    jd = gregorianToJulianDate(0, 2, 29.0); //leap year!
    System.out.println(jd);
    
    jd = convertOleary(0, 3, 1.0);
    System.out.println(jd);
    jd = gregorianToJulianDate(0, 3, 1.0);
    System.out.println(jd);
    */
    
    testNonNegYears();
    testNegYears();
  }
  
  private static void test(long year, int month, double day) {
    double jd = convertOleary(year, month, day);
    System.out.println(jd);
    jd = gregorianToJulianDate(year, month, day);
    System.out.println(jd);
    Date date = Date.from(year, month, (int)day, Calendar.GREGORIAN);
    Time time = Time.from(big(day - (int)day), TimescaleCommon.TAI);
    JulianDate julianDate = JulianDateConverter.using(Calendar.GREGORIAN).toJulianDate(DateTime.from(date, time));
    System.out.println(julianDate);
    System.out.println("");
  }
  
  private static void testNonNegYears() {
    double JAN_0_YEAR_0 = 1_721_058.5; 
    testNonNegYear(0, 1, 1, JAN_0_YEAR_0 + 1);
    testNonNegYear(0, 1, 31, JAN_0_YEAR_0 + 1 + 30);
    testNonNegYear(0, 12, 31, JAN_0_YEAR_0 + 366);
    testNonNegYear(1, 1, 1, JAN_0_YEAR_0 + 366 + 1);
    testNonNegYear(1, 12, 31, JAN_0_YEAR_0 + 366 + 365);
    testNonNegYear(2, 12, 31, JAN_0_YEAR_0 + 366 + 365 * 2);
    testNonNegYear(3, 12, 31, JAN_0_YEAR_0 + 366 + 365 * 3);
    testNonNegYear(4, 12, 31, JAN_0_YEAR_0 + 366 * 2 + 365 * 3);

    testNonNegYear(99, 12, 31.5, 1757584.0);
    testNonNegYear(100, 1, 1.5, 1757585.0);
    testNonNegYear(101, 1, 1.5, 1757950.0);
    testNonNegYear(200, 1, 1.5, 1794109.0); 
    testNonNegYear(300, 1, 1.5, 1830633.0); 
    testNonNegYear(400, 1, 1.5, 1867157); 
    testNonNegYear(700, 1, 1.5, 1976730);  
    testNonNegYear(800, 1, 1.5, 2013254);
  }
  
  private static void testNegYears() {
    double JAN_0_YEAR_0 = 1_721_058.5; 
    testNegYear(-1, 1, 1, JAN_0_YEAR_0 - 364);
    testNegYear(-1, 12, 31, JAN_0_YEAR_0);
    testNegYear(-2, 12, 31, JAN_0_YEAR_0 - 365);
    testNegYear(-2, 12, 31.3, JAN_0_YEAR_0 - 365 + 0.3);
    testNegYear(-3, 12, 31, JAN_0_YEAR_0 - 365 * 2);
    testNegYear(-4, 12, 31, JAN_0_YEAR_0 - 365 * 3);
    testNegYear(-5, 12, 31, JAN_0_YEAR_0 - 365 * 3 - 366 * 1);
    testNegYear(-6, 12, 31, JAN_0_YEAR_0 - 365 * 4 - 366 * 1);
    testNegYear(-7, 12, 31, JAN_0_YEAR_0 - 365 * 5 - 366 * 1);
    testNegYear(-8, 12, 31, JAN_0_YEAR_0 - 365 * 6 - 366 * 1);
    testNegYear(-9, 12, 31, JAN_0_YEAR_0 - 365 * 6 - 366 * 2);
  }
  
  
  private static void testNonNegYear(long y, int m, double d, double expected) {
    double jd = gregorianToJulianDate(y, m, d);
    boolean success = (jd == expected);
    if (!success) {
      System.out.println("Error. Expected: " + expected + " result:" + jd + " Input " +  y+ "-" + m + "-" + d );
    }
    
    CalendarDate dt = julianDateToGregorian2(expected);
    success = (dt.y == y) && (dt.m == m) && (dt.d == d);
    if (!success) {
      System.out.println("Error. Expected: " +  y+"-"+m+"-"+d + " result:" + dt.y+"-"+dt.m+"-"+dt.d + " Input " + jd);
    }
  }
  
  private static void testNegYear(long y, int m, double d, double expected) {
    double jd = gregorianToJulianDate(y, m, d);
    boolean success = (jd == expected);
    if (!success) {
      System.out.println("Error. Expected: " + expected + " result:" + jd + " Input " +  y+ "-" + m + "-" + d );
    }
    CalendarDate dt = julianDateToGregorian2(expected);
    success = (dt.y == y) && (dt.m == m) && (Math.abs(dt.d - d) < 0.0000001);
    if (!success) {
      System.out.println("Error. Expected: " +  y+"-"+m+"-"+d + " result:" + dt.y+"-"+dt.m+"-"+dt.d + " Input " + jd);
    }
  }
}
