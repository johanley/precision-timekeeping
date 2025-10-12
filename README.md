## astrolib


# What I learned

- the SOFA function that creates a JD from a date in the Gregorian calendar has an undesirable restriction in the accepted dates.
- UTC and leaps seconds are the most problematic aspect of timekeeping.
- UTC was introduced January 1, 1960. SOFA states that, strictly speaking, using UTC before this date is dubious.  
- the java.time package has no support for leap seconds. I had to make my own data-carrier classes.
- SOFA hard-codes data for leap seconds, rate changes, and dut1, and you are expected to re-compile when future data arrives. In this project, I just read files. 
- for leap seconds and Julian date, SOFA defines what they call a quasi-JD, in which the length-of-day is increased/decreased by 1 second.
- the ideas of timescale and calendar are logically independent.
- the Angle class is a pleasing abstraction.
- range checks for doubles: equal to or greater than from, but less than to.
- confusing: the zero-point for Julian day number is defined using a moment stated in the Julian calendar. But Julian day numbers can be found for dates in different calendars.
- the IAU seems to be leaning towards not establishing any more leap seconds.
