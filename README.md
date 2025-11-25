## Precision Timekeeping 

References for context:
- The *Explanatory Supplement to the Astronomical Almanac*, various editions
- <a href='https://www.iausofa.org/'>SOFA</a> and its Cookbook called *SOFA Time Scale and Calendar Tools* 

This project is implemented in Java. 
Similar implementations in other languages would need 
<a href='https://en.wikipedia.org/wiki/List_of_arbitrary-precision_arithmetic_software'>arbitrary precision arithmetic</a>, which is widely implemented.

Quick view of <a href='https://github.com/johanley/precision-timekeeping/blob/master/bigtime/examples/ExampleCode.java'>example code</a> 
that uses this library.  

### Time Precision in Various Domains

|Precision|Description|
|---------|-----------|
|~0.000 002 ns|World's best clocks|
|~0.001 ns|ALMA telescope master clock|
|~1 ns|CPU access L1 cache|
|~2 ns|GPS satellite|
|~20 ns|GPS receiver|
|~100 ns|CPU access main memory|
|~130 ns|5G cell towers|
|~100,000 ns|Network Time Protocol on a LAN|


## Design Choices In This Library

This library is not very large or complex. 
If you disagree with any of the design decisions mentioned below, changing the code isn't difficult. 

### Gregorian and Julian Calendars
The Gregorian calendar and the Julian calendar are implemented.

### *Proleptic* Behaviour
Either calendar can be used for any date.
There's no restriction to the historical facts of when a calendar was adopted in any jurisdiction.

### Unrestricted Julian Dates 
The Julian date is **not restricted** to dates having Julian date >= 0.
This restriction is unfortunately found in many date-time libraries.

### Arbitrary Precision
The date-time and Julian date can be defined to **arbitrary precision** for seconds and fractional days.
This is implemented by using Java's <a href='https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/math/BigDecimal.html'>BigDecimal</a> class.
This is an unusual property. 
Most date-time libraries don't allow arbitrary precision for the time of day. For example:
- Java's <a href='https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/package-summary.html'>java.time</a> package stops at nanoseconds.
- <a href='https://www.iausofa.org/'>SOFA</a> implements Julian dates with a pair of `double`s. 
That library can represent a moment in time to an accuracy of <a href='https://aa.usno.navy.mil/downloads/novas/USNOAA-TN2011-02.pdf'>~20 microseconds</a>.

### Included Timescales
- TAI is the core timescale. Other timescales are defined using an offset from TAI.
- TT has a fixed offset from TAI.
- TDB, whose offset from TAI is modeled as a simple periodic function.
- GPS is modeled with a fixed offset from TAI.
- UT1, whose offset from TAI comes from data files from IERS. 
That data starts 1962-01-01. 
You need to update that file manually. 
Has a back-door to let you define a fixed override value.
- UTC, modeled as a fixed offset from TAI. 
Has a back-door to let you define a fixed override value.

### Conversions Between Timescales At Sub-Millisecond Level
Time can be represented to arbitrary precision in this library. 
But *conversions between* timescales is another story:
- some conversions are defined *by convention*, and can be taken to be "infinitely precise", so to speak
- for the remaining conversions, the precision of the conversion varies
 
In this library, the goals are:
- to **ensure timescale conversions are always accurate to sub-millisecond level from 1980-01-01 to the present day**, as a kind of minimal baseline 
- to execute timescale conversions in a **single method call**

The design of <a href='https://www.iausofa.org/'>SOFA</a> is different in this regard. 
In SOFA, specific conversions between timescales are implemented, each at the best possible precision. 
This is a good design for SOFA.
But in SOFA, to go from one timescale to another, you need to think about the specific chain of conversions that gets you from A to B.
In this library, it's always a single method call.


### UT1-TAI Data From 1962-01-01 Onward
For converting UT1 to other timescales, the difference UT1-TAI in seconds is taken from a downloaded snapshot of the <a href='https://hpiers.obspm.fr/eop-pc/index.php?index=C04&lang=en'>IERS EOP C04 series data set</a>.
You will need to manually update that snapshot to get the most recent data.
The sigma for the UT1-TAI value is generally below 1.0 milliseconds after 1980-01-01, and above 1.0 milliseconds before that date. 

If you use a date that comes after the range of the downloaded snapshot of the IERS data set, 
then the timescale conversion code will silently use the most recent value found in the snapshot. 
If you use a date before 1962-01-01, then the timescale conversion will fail.

You can **override** all of this logic by using a back-door System property that lets you manually set a specific value for UT1-TAI.  


### UTC-TAI Is Given A Fixed Value
UTC is the only timescale that uses leap seconds. **Leap seconds are problematic.**
 
Superficially they seem simple, but this is misleading.
The BIPM is <a href='https://www.bipm.org/en/cgpm-2022/resolution-4'>seeking to change things</a> because 

<em>"the consequent introduction of leap seconds creates discontinuities that risk causing serious malfunctions in critical digital infrastructure"</em>.

Here's an example of <a href='https://github.com/liberfa/erfa/issues/91'>a tricky leap second bug</a>.

From the <a href='https://www.iausofa.org/2023-10-11c'>SOFA Cookbook</a> on *Time Scales and Calendar Tools*:

<em>"Leap seconds pose tricky problems for software writers, and consequently there are concerns that these events put safety-critical systems at risk. 
The correct solution is for designers to base such systems on TAI or some other glitch-free time scale, not UTC, but this option is often overlooked 
until it is too late."</em>

Because of their complexity, it's likely that international standards bodies will add **no new leap seconds** in the future.
So, for modern dates and times, UTC will have a *fixed* offset from TAI.
The most recent (and likely the last) leap second was [2016-12-31 23:59:60.0, 2017-01-01 00:00:00.0).

In this library, UTC is implemented as having a *simple constant offset from TAI*, equal to its current value at the time of writing.
That constant offset is hard-coded.

You can **override** the hard-coded value by using a back-door System property that lets you manually set a specific value for UTC-TAI.  



### Also Notable
- in this library, a `Date` always has a `Calendar`, and a `Time` always has a `Timescale`
- time zones are not part of this library



## What I Learned On This Project

- clocks on the rotating geoid run all at the same rate! [link](https://www.gpsworld.com/inside-the-box-gps-and-relativity/)  
- standards bodies seem to be leaning towards not establishing any more leap seconds.
- UTC and leaps seconds are the most problematic aspect of timekeeping. They aren't uniform.
- UTC was introduced January 1, 1960. SOFA states that, strictly speaking, using UTC before this date is dubious.  
- the java.time package has no support for leap seconds.
- the amount of precision in an IEEE 754 double is not quite enough to model a Julian date precisely. 
A library that implements arbitrary precision arithmetic (BigDecimal in Java, for example) can solve that.
- in astronomy, the terms Julian date and Julian calendar are confusing. 
They refer to separate ideas. 
A Julian date can be related to different calendars. 
I had forgotten that.
- calculations with dates and times are much simplified when you have robust conversions from/to Julian dates. 
Adding days or seconds no longer needs to deal with calendar logic, because that's done by the robust conversion logic. 
This only works well when you have the arbitrary precision arithmetic at hand.
- I found an algorithm for Julian dates that has no restriction to positive Julian dates. 
This algorithm has been done before, but it doesn't seem to be as popular as it probably should be. 
- The older Network Time Protocal (NTP) has a target precision usually in the millisecond range.
The newer Precision Time Protocal (PTP) has a target precision in the nanosecond range, or even better.
- The second [might be re-defined](https://www.scientificamerican.com/article/worlds-most-accurate-clocks-could-redefine-time/) in the future. 
Newer, more precise clocks are obsoleting the old definition of the second. 
The <a href='https://a1120960.github.io/PAC-SWaP/'>new clocks</a> have optical frequencies (~4*10^14 Hz, ~0.000 002ns), 
not microwave frequencies (~10^10 Hz, ~0.1ns).
- The world's most accurate clocks can measure the gravitational time-dilation for differences in altitude on the order a 1mm! 
That precision is just *bonkers*.


<!--

## Sketchy Notes

- for leap seconds and Julian date, SOFA defines what they call a quasi-JD, in which the length-of-day is increased/decreased by 1 second.

Astropy seems to mirror lower level (?) implementations like SOFA, ERFA. 
 - they implement JDs as a pair of doubles. 
 - they limit seconds to nanoseconds (at least in one place). 
 - "The Time object maintains an internal representation of time as a pair of double precision numbers expressing Julian days. The sum of the two numbers is the Julian Date for that time relative to the given time scale. Users requiring no better than microsecond precision over human time scales (~100 years) can safely ignore the internal representation details and skip this section. This representation is driven by the underlying ERFA C-library implementation."
 - "Note that the limit of 9 digits [decimal seconds] is driven by the way that ERFA handles fractional seconds. In practice this should should not be an issue."

- SOFA hard-codes data for leap seconds, rate changes, and DUT1, and you are expected to re-compile when future data arrives. In this project, I just read data files. 


   Support TT, UT1, UTC. The last two need data files for best results. 1960..present? When UTC began. 1970, when leap seconds began?
   data files for leap seconds, TT-UT1
   TDB is almost the same as TT.
   
   java.time has no support for leap seconds!!
   https://www.threeten.org/threeten-extra/apidocs/org.threeten.extra/org/threeten/extra/scale/UtcInstant.html 
   - make a dumb data-carrier class for date-time and time, not meant for calculations?
   - When.timescale, When.datetime, When.jd, When.mjd. The jd's could be used to calc durations.

   TT as core, others as lazy?
   
   Should I use SOFA's idea of a 'quasi-JD' for JD attached to the UTC timescale?
   
   https://www.iausofa.org/
    https://www.iausofa.org/2023-10-11c#documentation
    https://static1.squarespace.com/static/68caa251ae552a6213e8764b/t/68d3e216e1cf0121bd4273a6/1758716438331/sofa_ts_c.pdf
    
    
   UTC began 1960 Jan 1.0 (JD 2436934.5)!
    https://barrettp.github.io/Astrometry.jl/dev/SOFA/timescales/#Astrometry.SOFA.d2dtf
    
   Leap seconds: 58, 59, 60, 0.  So: 60.01..60.99. The seconds don't follow the usual 0..59.99 constraint!
    
   Something called 'drift rates' 1960-1968, before leap seconds in 1972
    https://github.com/barrettp/Astrometry.jl/blob/61301b755333f2f7143962d0aeb688aa981e9fb0/src/sofa.jl#L21-L23
    Note all the tests:https://github.com/barrettp/Astrometry.jl/blob/61301b755333f2f7143962d0aeb688aa981e9fb0/test/sofatests.jl  
    
   1986.56 is called an 'epoch' - a decimal year.
    
   This C# port includes original SOFA code.
    https://github.com/starsbane/AstroRoutines
    
   ICRS replaces J2000
    https://static1.squarespace.com/static/68caa251ae552a6213e8764b/t/68d3e24f9815680fc3586889/1758716495718/sofa_misc_c.pdf
    However, since the IAU’s 2000 resolutions the role of the ecliptic in high precision applications has declined, while 
    J2000.0 mean [α,δ] as a standard reference system has given way to the ICRS. Consequently SOFA offers only mean equinox 
    and ecliptic of date for [λ,β ] and ICRS for [α,δ].
    
   Hatcher's paper 1983
    https://adsabs.harvard.edu/full/1985QJRAS..26..151H
    
   NOVAS
    https://aa.usno.navy.mil/software/novas_info
    NOVAS uses matrices only. No spherical trig.
    
   An effort to do something in Julia is interesting:
    https://github.com/kiranshila/NOVAS.jl
    
   The ERFA clone of SOFA:
    https://github.com/liberfa/erfa
    
   The accuracy of IEEE 754 in modeling jd:
    https://aa.usno.navy.mil/downloads/novas/USNOAA-TN2011-02.pdf
    COULD I use BigDecimal to retain larger precision?
    
   IAU 2005:
    https://aa.usno.navy.mil/downloads/Circular_179.pdf
    
   UT1
    The simplest impl is to have it as a fixed offset from TAI, with the latest value.
    There have been no leap seconds since 2017-01-01. 
    There's definitely a move afoot to have no more leap seconds for a long time (2022):
      https://www.bipm.org/en/cgpm-2022/resolution-4
      https://en.wikipedia.org/wiki/Leap_second#Problems
      
   [CASA Timescales](https://casadocs.readthedocs.io/en/stable/notebooks/memo-series.html#Time-Reference-Frames)
   
   Referenced in the SOFA cookbook, IERS Conventions (2003) - about IERS reference systems, tides
   https://iers-conventions.obspm.fr/archive/2003/tn32.pdf
   
   Java port of SOFA, relatively up to date, by an astronomer in the UK
   https://github.com/Javastro/jsofa?tab=readme-ov-file
   
   C99 nicer than C89; the big jump was this transition; there's also  C11, C23
      C Struct init with 'field' names
      <stdint.h> pushes types to be of fixed size
      //comments like this
      -std=c99 -Wall -Werror   -fsanitize=address
      unity build, single translation unit: main.c just includes everything (in the correct order); no header files.
   https://www.youtube.com/watch?v=9UIIMBqq1D4
   
   NASA no use heap!
   They also aren't crazy about directives. And function pointers.
   And they like: gcc   -Wall -Werror -Wpedantic
   https://www.youtube.com/watch?v=GWYhtksrmhE
   
   Scientists are stuck on doubles, instead of BigDecimal and friends.
   They use tricks like using pairs of doubles - 'integer' + fraction.
   There are tools in C for arbitrary precision values: https://gmplib.org/
   https://hea-www.cfa.harvard.edu/~arots/TimeWCS/WCSPaper-IV-v1.1A4.pdf          
   "For time, more than any other coordinate, precision may be a concern and naive use of double precision floating point parameters for time values (especially Julian Dates) will be inadequate in some cases. However, a judicious combination of keywords and their values, as described in the remainder of this section, will allow almost any required precision to be achieved without having to resort to anything beyond double precision data types in handling keywordvalues. We urgecreatorsof data productsto apply special care, so that clients can rely on this being the case. If and when, in addition to the 32-bit (E) and 64-bit (D) floating point types, a 128-bit floating point data type becomes available and supported, we envision that such a type will also be used for time values, removing the need for any special provisions."

   
Finals.data: 1992-01-01 to 2027-01-17, with predictions; quick-look weekly estimates
Finals.all:  1973-01-02 to 2027-01-17, can't see the difference other than the start-date

EOP C04 series has UT1-TAI, I think.
https://hpiers.obspm.fr/eop-pc/index.php?index=C04&lang=en
https://www.bipm.org/documents/20126/28429869/working-document-ID-7399/aed6f662-7a8a-64b3-3b70-f36d3c8ef037  - see slide #5
https://hpiers.obspm.fr/iers/eop/eopc04/eopc04.1962-now
https://hpiers.obspm.fr/eop-pc/products/combined/C04.php?date=1&eop=22&year1=1962&month1=1&day1=1&year2=2027&month2=1&day2=1&SUBMIT=Submit+Search
This data has the sigma for UT1-TAI under 1.0ms after 1980-01-01. Before that it's generally larger than 1.0ms.

-->