## astrolib


# What I learned

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
- the amount of precision in an IEEE 754 double is not quite enough to model a Julian date precisely. Could a BigDecimal be used in Java, to address that?
- cursory look: SOFA's tests don't seem to be very extensive
- NOVAS: its julian_date function doesn't document any conditions on the input year, but I think there is one