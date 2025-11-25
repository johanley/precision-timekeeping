package bigtime.when;

import static bigtime.when.BigDecimalHelper.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import org.junit.Test;

/** Unit test.*/
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
    
    a = big("9.0");
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
  
  @Test public void floor() {
    BigDecimal a = big(9.01);
    BigInteger b = BigDecimalHelper.floor(a);
    assertEquals(9, b.intValue());

    a = big(9.99);
    b = BigDecimalHelper.floor(a);
    assertEquals(9, b.intValue());
    
    a = big(-9.01);
    b = BigDecimalHelper.floor(a);
    assertEquals(-10, b.intValue());

    a = big(-9);
    b = BigDecimalHelper.floor(a);
    assertEquals(-9, b.intValue());
  }
  
  @Test public void divideThem() {
    BigDecimal a = big(1);
    BigDecimal b = big(3);
    BigDecimal c = divide(a, b);
    assertEquals("0.3333333333333333333333333333333333", c.toPlainString());
    
    try {
      System.setProperty(BigDecimalHelper.OVERRIDE_INFINITE_CUTOFF, "11");
      c = divide(a, b);
      assertEquals(c.toPlainString(), "0.33333333333");
    }
    finally {
      System.clearProperty(BigDecimalHelper.OVERRIDE_INFINITE_CUTOFF);
    }
    
    a = big(10);
    b = big(4);
    c = divide(a, b);
    assertEquals(c.toPlainString(), "2.5");
  }
  
  @Test public void divideAndRemainderThem() {
    BigDecimal a = big(10);
    BigDecimal b = big(7);
    BigDecimal[] c = divideAndRemainder(a, b);
    assertEquals("1", c[0].toPlainString());
    assertEquals("3", c[1].toPlainString(), "3");
    
    a = big(10);
    b = big(3);
    c = divideAndRemainder(a, b);
    assertEquals("3", c[0].toPlainString());
    assertEquals("1", c[1].toPlainString());
  }
  
  @Test public void cutoff() {
    int cutoff = BigDecimalHelper.infiniteCutoffPrecision();
    assertEquals(MathContext.DECIMAL128.getPrecision(), cutoff);
    
    try {
      System.setProperty(BigDecimalHelper.OVERRIDE_INFINITE_CUTOFF, "11");
      cutoff = BigDecimalHelper.infiniteCutoffPrecision();
      assertEquals(11, cutoff);
    }
    finally {
      System.clearProperty(BigDecimalHelper.OVERRIDE_INFINITE_CUTOFF);
    }
  }
}
