
ws ::= {%whitespace%}

positive-number ::= (%digit% { %digit% })
sign ::= ("+" | "-")
simple-unsigned-float-number ::= (positive-number ["." positive-number])
float-number ::= (["+" | "-"] ((positive-number ["." {%digit%}]) | ("." positive-number)) [("e"|"E") [sign] positive-number])
ratio-number ::= (positive-number "/" float-number)

// parse a clock time like 1:30:02.7
clock-time ::= (positive-number {":" positive-number} [":" simple-unsigned-float-number])
// parses a time like 100ms
// time-suffix is defined externally
time-with-suffix ::= ((ratio-number | float-number) ws time-suffix)

time ::= (time-with-suffix | clock-time)


no-newline ::= ~<\n>
