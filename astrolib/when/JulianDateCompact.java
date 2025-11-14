package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;

import java.math.BigDecimal;
import java.time.Month;

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
   Inspired by Robin O'Leary's algorithm.
   
   I base the calculation on counting days from January 0, year 0.
   Then I simply re-base the result at the end, to reflect the usual origin-day for Julian dates.
   This exploits the (near) symmetry of the calendar cycles.
   
   (Making a verison for the Julian calendar is very similar.)
  */
  static double gregorianToJulianDate(long y, int m, double d) {
    //completed years: small asymmetry between positive and negative years
    long y_p = (y >= 0) ? (y - 1) : y;  //y_p = y-prime
    long num_366yrs = (y_p/4) - (y_p/100) + (y_p/400); //Robin's clever trick
    if (y > 0) {
      num_366yrs += 1; //since year 0 is a leap year
    }
    long num_365yrs = y - num_366yrs;
    double res = num_365yrs * 365 + num_366yrs * 366;    
    
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
  private static final int SHORT_YR = 365;
  private static final int LONG_YR = 366;
  
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
  
  static CalendarDate julianDateToGregorian(double jd) {
    double BASE = JAN_0_YEAR_0 + 1.0;  
    double target = jd - BASE;
    int CYCLE_YEARS = 400;
    int CYCLE_DAYS = SHORT_YR*CYCLE_YEARS + CYCLE_YEARS/4 - CYCLE_YEARS/100 + CYCLE_YEARS/CYCLE_YEARS;
    long num_cycles = (long)Math.floor(target /CYCLE_DAYS);
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
  
  
  public static void main(String[] args) {
    // testDate(1957, 10, 4.81, 2436116.31, cal);

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
    testNegYear(-1, 12, 31, JAN_0_YEAR_0);
    testNegYear(-1, 1, 1, JAN_0_YEAR_0 - 364);
    testNegYear(-2, 12, 31, JAN_0_YEAR_0 - 365);
    testNegYear(-3, 12, 31, JAN_0_YEAR_0 - 365 * 2);
    testNegYear(-4, 12, 31, JAN_0_YEAR_0 - 365 * 3);
    testNegYear(-5, 12, 31, JAN_0_YEAR_0 - 365 * 3 - 366 * 1);
  }
  
  
  private static void testNonNegYear(long y, int m, double d, double expected) {
    double jd = gregorianToJulianDate(y, m, d);
    boolean success = (jd == expected);
    if (!success) {
      System.out.println("Error. Expected: " + expected + " result:" + jd + " Input " +  y+ "-" + m + "-" + d );
    }
  }
  
  private static void testNegYear(long y, int m, double d, double expected) {
    double jd = gregorianToJulianDate(y, m, d);
    boolean success = (jd == expected);
    if (!success) {
      System.out.println("Error. Expected: " + expected + " result:" + jd + " Input " +  y+ "-" + m + "-" + d );
    }
  }
}
