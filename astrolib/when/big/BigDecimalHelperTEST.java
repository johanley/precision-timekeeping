package astrolib.when.big;

import static astrolib.when.big.BigDecimalHelper.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public final class BigDecimalHelperTEST {
  
  
  @Test public void build() {
    BigDecimal a = big(2);
    assertEquals(BigInteger.valueOf(2), a.unscaledValue());
    assertEquals(1, a.precision());
    assertEquals(0, a.scale());
    
    a = big(9.0);
    assertEquals(BigInteger.valueOf(90), a.unscaledValue());
    assertEquals(2, a.precision());
    assertEquals(1, a.scale());
  }
  
  @Test public void integerPart() {
    BigDecimal a = big(9.01);
    assertEquals(BigInteger.valueOf(901), a.unscaledValue());
    assertEquals(3, a.precision());
    assertEquals(2, a.scale());

    BigInteger b = integer(a);
    assertEquals(BigInteger.valueOf(9), b);
  }
  
  @Test public void fractionalPart() {
    BigDecimal a = big(9.01);
    assertEquals(BigInteger.valueOf(901), a.unscaledValue());
    assertEquals(3, a.precision());
    assertEquals(2, a.scale());

    BigDecimal b = decimals(a);
    assertEquals(BigInteger.valueOf(1), b.unscaledValue());
    assertEquals(1, b.precision());
    assertEquals(2, b.scale());
  }
  

}
