package astrolib.when;

import static astrolib.when.BigDecimalHelper.*;
import static astrolib.when.TimescaleCommon.*;
import static org.junit.Assert.*;
import org.junit.Test;

/** Unit test. */
public final class TimescaleCommonTEST {
  
  @Test public void secondsFromTAI() {
    test(TAI, "0", null);
    test(TT, "32.184", null);
    test(GPS, "-19", null);
    test(UTC, "-37", null);
  }
  
  @Test public void overrideForUTC() {
    overrideForUTC("0");
    overrideForUTC("1");
    overrideForUTC("-1");
    overrideForUTC("-40");
    overrideForUTC("40");
    
    overrideForUTCFails("blah");
    overrideForUTCFails("0.0");
    overrideForUTCFails("0.1");
    overrideForUTCFails("-40.0");
  }
  
  private void test(Timescale timescale, String secondsFromTAI, DateTime when) {
    assertEquals(big(secondsFromTAI), timescale.secondsFromTAI(when));
  }
  
  private void overrideForUTCFails(String val) {
    String key = TimescaleCommon.UTC_SYS_PROPERTY;
    System.setProperty(key, val);
    assertThrows(IllegalArgumentException.class, () -> UTC.secondsFromTAI(null));
    System.clearProperty(key);
  }
  
  private void overrideForUTC(String val) {
    String key = TimescaleCommon.UTC_SYS_PROPERTY;
    System.setProperty(key, val);
    test(UTC, val, null);
    System.clearProperty(key);
  }
}
