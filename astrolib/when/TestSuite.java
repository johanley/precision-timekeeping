package astrolib.when;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/** Run all tests in this package. */
@RunWith(Suite.class)
@SuiteClasses({
  BigDecimalTEST.class, //not really a test; more a demo of its behaviour
  BigDecimalHelperTEST.class,
  CalendarTEST.class,
  DateTEST.class,
  DateTimeTEST.class,
  JulianDateConverterTEST.class,
  OdometerTEST.class,
  RoundSecondsTEST.class,
  TimescaleImplTEST.class,
  TimescaleTEST.class,
  TimeTEST.class,
  Ut1HelperTEST.class
})
public final class TestSuite {
  //an empty abyss of non-code
}
