
ws ::= {%whitespace%}

positive-number ::= (%digit% { %digit% })
sign ::= ("+" | "-")
simple-unsigned-float-number ::= (positive-number ["." positive-number])
float-number ::= (["+" | "-"] ((positive-number ["." {%digit%}]) | ("." positive-number)) [("e"|"E") [sign] positive-number])
ratio-number ::= (positive-number "/" float-number)

double-quoted-string ::= ("\"" {string-or-char-contents | "\'"} "\"")
single-quoted-char   ::= ("\'" {string-or-char-contents | "\""} "\'")
string-or-char-contents ::= (~<\\\"\'> | ("\\" ~<>))

identifier ::= ((%alpha% | "_") {%alpha% | %digit% | "_"})

// parse a clock time like 1:30:02.7
clock-time ::= (positive-number {":" positive-number} [":" simple-unsigned-float-number])
// parses a time like 100ms
// time-suffix is defined externally
time-with-suffix ::= ((ratio-number | float-number) ws time-suffix)

time ::= (time-with-suffix | clock-time)


// parse a wait term like "wait(1ms)" or "after 1/3 hour"
wait-event-type ::= ("wait" | "at" | "before" | "after")
wait-event ::= (wait-event-type ws (("(" ws time ws ")" ws) | (time ws)))


// an item in parentheses
// Parentheses can be anything that deactivates the special meaning of delimiters within
// a well defined region, e.g. (...) or "..."
parenthesised-text ::= (("(" parenthesised-text-inner ")") | ("[" parenthesised-text-inner "]")
	 | ("{" parenthesised-text-inner "}") | double-quoted-string | single-quoted-char)
parenthesised-text-inner ::= { parenthesised-text | ~<{[()]}"'> }

condition-text         ::= { parenthesised-text | ~<{[(]>  }
action-text-without-nl ::= { parenthesised-text | ~<{[(\n> }
action-text-with-nl    ::= { parenthesised-text | ~<{[(>   }


// parser for transition information
event-name ::= identifier
event-name-and-wait ::= ((event-name [":" wait-event]) | ([":"] wait-event))
condition ::= ("[" condition-text "]")
transition-action ::= action-text-with-nl
transition-info ::= (ws [event-name-and-wait ws] [condition ws] ["/" ws transition-action ws])
