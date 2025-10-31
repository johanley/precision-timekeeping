package astrolib.when;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.Test;

/** 
 Explore the behaviour of 
 <a href='https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/math/BigDecimal.html'>BigDecimal</a>.
 
 <P>The intent here is not to test the class in the normal way, but to demonstrate its behavior and lingo.
 
 <P><b>The most important thing to know about BigDecimal is that you shouldn't use the constructor that takes a double value.</b>
  Instead, use the constructor that accepts a String, or perhaps the <em>valueOf(double)</em> factory method.
  
 <P>The second most important thing is to be careful with division. If you don't handle it correctly, it blow up
 when there's a non-terminating decimal - 1/3, for example. 
  
 <P>Here's a good description of 
 <a href='https://blogs.oracle.com/javamagazine/post/four-common-pitfalls-of-the-bigdecimal-class-and-how-to-avoid-them'>pitfalls to avoid</a>. 

 <P>A good mental model for BigDecimal is to picture it as two things:
 <ul> 
  <li>a BigInteger representing a string of digits without a decimal place. 
  This is called the <em>unscaled value</em>. It can end with zeros.
  <li>an integer which defines where to put the decimal point, starting from the right. 
  This number is called the <em>scale</em>.
 </ul>
 
 <P><b>The important point is that the underlying number represented by a BigDecimal is 
 <pre>(unscaled value) x Math.pow(10, -scale)</pre></b>
 
 
 <P>Examples:
 <pre>
 unscaled value: 12345
 scale: 1
 corresponds to: 1234.5
 
 unscaled value: 12345
 scale: 3
 corresponds to: 12.345
 
 unscaled value: 12345
 scale: 0
 corresponds to: 12345 
  
 unscaled value: 12345
 scale: -3 (negative!)
 corresponds to: 12345000 

 unscaled value: 12010
 scale: 3
 corresponds to: 12.010 
</pre>
 
 <P>Lingo (personally, I find these names to be ill-chosen): 
 <ul>
  <li><em>unscaled value</em>: the string of digits (a BigInteger). Allowed to end with zeros.
  <li><em>precision</em>: the (non-negative) number of digits in the unscaled value (the length of the string of digits).  
  Magic value: if the precision is set to 0, then arithmetic operations are exact; in the case of 1/3 and similar (infinite decimals), an exception is thrown.
  <li><em>scale</em>: an integer that defines where to place the decimal point. 
  If negative, then the string of digits is padded on the right with 0s.
 </ul>
 
  <P>Other lingo: 
 <ul>
  <li><em>cohort</em>: 12.345 = (12345 + scale 3 | 123450 + scale 4). Different representations of the same number.
 </ul>
 The <em>equals</em> method distinguishes at the finest level (unscaled value and scale), while the <em>compareTo</em> method does not.
*/
public class BigDecimalTEST {

  @Test public void basics() {
    BigDecimal a = new BigDecimal("12.789");
    assertEquals(3, a.scale());
    assertEquals(5, a.precision());
    assertEquals(12789, a.unscaledValue().intValue());
    assertEquals("12.789", a.toString());
    
    a = new BigDecimal("12.01000"); //trailing 0s
    assertEquals(5, a.scale());
    assertEquals(7, a.precision());
    assertEquals(1201000, a.unscaledValue().intValue());
    assertEquals("12.01000", a.toPlainString());
    
    a = new BigDecimal("120"); 
    assertEquals(0, a.scale());
    assertEquals(3, a.precision());
    assertEquals(120, a.unscaledValue().intValue());
    assertEquals("120", a.toPlainString());
    
    //same number 120, but represented in a different way, with a negative scale
    a = new BigDecimal(BigInteger.valueOf(12), -1); 
    assertEquals(-1, a.scale());
    assertEquals(2, a.precision());
    assertEquals(12, a.unscaledValue().intValue());
    assertEquals("120", a.toPlainString());
  }
  
  @Test public void avoidPassingDoubleToCtor() {
    //this is the bad way; don't use the ctor that accepts a double, because unexpected things happen
    BigDecimal x = new BigDecimal(0.1);
    BigInteger wow = new BigInteger("1000000000000000055511151231257827021181583404541015625");
    assertEquals(55, x.scale());
    assertEquals(wow, x.unscaledValue());
    
    //this way is better: the valueOf(double) factory method; 
    //warning: the passed double can't represent more than 15-17 significant digits
    x = BigDecimal.valueOf(0.1);
    assertEquals(1, x.scale());
    assertEquals(BigInteger.ONE, x.unscaledValue());

    //this works best: the ctor that takes a String is never problematic
    //it's also very convenient when source data is read from a file, since it's already a simple String
    //this is often the preferred method of construction
    x = new BigDecimal("0.1");
    assertEquals(1, x.scale());
    assertEquals(BigInteger.ONE, x.unscaledValue());
  }
  
 @Test public void additionMaxScale() {
   BigDecimal a = new BigDecimal("3.14");
   BigDecimal b = new BigDecimal("3.14100");
   BigDecimal c = a.add(b);
   assertEquals(5, b.scale());
   assertEquals(6, b.precision());
   assertEquals(b.scale(), c.scale());
   assertEquals(b.precision(), c.precision());
   assertEquals("6.28100", c.toString());
 }
 
 @Test public void addVastlyDifferentOrdersOfMagnitue() {
   BigDecimal a = new BigDecimal(BigInteger.valueOf(1_000_000_000), 0);
   BigDecimal b = new BigDecimal(BigInteger.valueOf(1), 12);
   BigDecimal c = a.add(b);
   assertEquals(12, c.scale());
   assertEquals(22, c.precision());
   assertEquals("1000000000.000000000001", c.toPlainString());
   
   //the double data-type doesn't do the same operation correctly, 
   //because double's internal representation has too few significant digits
   //System.out.println(1000000000 + 0.000000000001); // emits '1.0E9'
 }
 
 @Test public void customisePrecisionOrRounding() {
   //use MathContext to customise precision and/or rounding
   BigDecimal a = new BigDecimal(BigInteger.valueOf(314), 2);
   BigDecimal b = new BigDecimal(BigInteger.valueOf(273), 2);
   BigDecimal c = a.multiply(b); //3.14 * 2.73 = 8.5722
   assertEquals(4, c.scale());
   assertEquals(BigInteger.valueOf(85722), c.unscaledValue());
   assertEquals(5, c.precision());
   assertEquals("8.5722", c.toPlainString());
   
   //if you want the result of a*b rounded to 8.57, with less precision, 
   //then use a MathContext:
   BigDecimal d = a.multiply(b, new MathContext(3, RoundingMode.HALF_EVEN));
   assertEquals(2, d.scale());
   assertEquals(BigInteger.valueOf(857), d.unscaledValue());
   assertEquals(3, d.precision());
   assertEquals("8.57", d.toPlainString());
 }
 
 @Test public void divisionCanBlowUp() {
   BigDecimal a = new BigDecimal("1");
   BigDecimal b = new BigDecimal("3");
   assertThrows(ArithmeticException.class, () -> a.divide(b));
 }
 
 @Test public void divisionNoBlowUp() {
   BigDecimal a = new BigDecimal("1");
   BigDecimal b = new BigDecimal("3");
   //to avoid blowup, specify the precision (number of digits) in the result
   BigDecimal c = a.divide(b, new MathContext(5, RoundingMode.HALF_EVEN));
   assertEquals("0.33333", c.toPlainString());
   assertEquals(BigInteger.valueOf(33333), c.unscaledValue());
   assertEquals(5, c.scale());
   assertEquals(5, c.precision());
 }
 
 @Test public void integerDiv() {
    BigDecimal a = new BigDecimal("9");
    BigDecimal b = new BigDecimal("4");
    //this rounds towards zero, for both positive and negative values; this is usually desirable
    BigDecimal c = a.divideToIntegralValue(b);
    assertEquals(2, c.intValue());
    a = new BigDecimal("-9");
    c = a.divideToIntegralValue(b);
    assertEquals(-2, c.intValue());
  }
 
  @Test public void toBigInt() {
    // toBigInteger() rounds towards zero, which is usually desirable
    BigDecimal a = new BigDecimal("9.2");
    assertEquals(9, a.toBigInteger().intValue());
    a = new BigDecimal("-9.2");
    assertEquals(-9, a.toBigInteger().intValue());
  }
  
  @Test public void floor() {
    BigDecimal a = new BigDecimal("-4.2");
    BigDecimal b = a.setScale(0, RoundingMode.FLOOR);
    System.out.println(b.toPlainString());
  }
}
