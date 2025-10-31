## astrolib


# What I Learned

- the IAU seems to be leaning towards not establishing any more leap seconds.
- UTC and leaps seconds are the most problematic aspect of timekeeping.
- UTC was introduced January 1, 1960. SOFA states that, strictly speaking, using UTC before this date is dubious.  
- SOFA hard-codes data for leap seconds, rate changes, and DUT1, and you are expected to re-compile when future data arrives. In this project, I just read data files. 
- for leap seconds and Julian date, SOFA defines what they call a quasi-JD, in which the length-of-day is increased/decreased by 1 second.
- the java.time package has no support for leap seconds. 
- the ideas of timescale and calendar are logically independent. In this library, I explicitly attach a Calendar to a Date, and a Timescale to a Time.
- the Angle class is a pleasing abstraction.
- in astronomy, the terms Julian date and Julian calendar are confusing. They refer to separate ideas. A Julian date can be related to different calendars.
- most astronomical libraries that create a Julian date from a given date have restrictions in the range of accepted years. This seems increasingly problematic. 
For example, modern theories of long-term precession can be used over timescales of tens of thousands of years. In this library, I avoid such restrictions on the year.
- the amount of precision in an IEEE 754 double is not quite enough to model a Julian date precisely. Using BigDecimal in Java can solve that.
- cursory look: SOFA's tests don't seem to be very extensive
- NOVAS: its julian_date function doesn't document any conditions on the input year, but I think there is one
- I think the idea of 'tagging' every date with a calendar, and every time with a timescale, is a sound one.
- Network Time Protocal (NTP) is based on UTC within a few msecs. Precision Time Protocal (PTP) is based on TAI within a few nanoseconds (?).
- Global Navigation Satellite System GNSS = (GPS | Galileo | GLONASS | ...) All have atomic clocks. Precision ~2ns. Receivers are ~20ns, but can combine signals to get better results.
- GNSS clocks and relativity: 7,000ns/day for speed (slower), but 45,000ns/day for gravity well (faster) [link](https://www.gpsworld.com/inside-the-box-gps-and-relativity/)
- Clocks on the geoid run all at the same rate! [link](https://www.gpsworld.com/inside-the-box-gps-and-relativity/)  
- The second [might be re-defined](https://www.scientificamerican.com/article/worlds-most-accurate-clocks-could-redefine-time/) in the future. Newer, more precise clocks are obsoleting the old definition of the second. The new clocks have optical frequencies (~10^10 Hz, ~0.1ns), not microwave frequencies (~4*10^14 Hz, ~0.000002ns).
- The world's most accurate clocks can measure the GR time-dilation for differences in altitude on the order a 1mm!
- Cell phone networks: [5G needs 130ns precision](https://www.gpsworld.com/gnss-clocks-prove-to-be-invisible-and-indispensable/), while 6G needs 5ns (2030s release?).
- Financial markets: [100,000 ns](https://www.gpsworld.com/gnss-clocks-prove-to-be-invisible-and-indispensable/)


# Sketchy Notes

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
      
   DAY 0.0 - I need a policy for this!      
