package astrolib.when;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/** Run all test classes in this package. */
@RunWith(Suite.class)
@SuiteClasses({
  BigDecimalHelperTEST.class,
  CalendarTEST.class,
  DateTEST.class,
  DateTimeTEST.class,
  JulianDateConverterTEST.class,
  OdometerTEST.class,
  RoundSecondsTEST.class,
  TimescaleCommonTEST.class,
  TimescaleTEST.class,
  TimeTEST.class
})
public final class TestSuite {
  //an empty abyss of non-code
}
