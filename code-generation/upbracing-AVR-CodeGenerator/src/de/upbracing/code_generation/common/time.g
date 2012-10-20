// needs common.g
// provided by external code: time-with-suffix

// a clock time like 1:30:02.7
clock-time ::= (positive-number {":" positive-number} [":" simple-unsigned-float-number])

// a time like 100ms
// time-suffix is defined externally
time-with-suffix ::= ((ratio-number | float-number) ws time-suffix)

// a time like "1.7ms", "1:30:02.7" or even "1/3 day"
time ::= (time-with-suffix | clock-time)
