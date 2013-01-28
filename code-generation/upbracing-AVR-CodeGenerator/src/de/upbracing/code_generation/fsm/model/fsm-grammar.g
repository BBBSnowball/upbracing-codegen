// needs common.g and time.g
// provided by external code: state-action-type

// parse a wait term like "wait(1ms)" or "after 1/3 hour"
wait-event ::= (wait-event-type ws (("(" ws time ws ")" ws) | (time ws)))
wait-event-type ::= ("wait" | "at" | "before" | "after")


// an item in parentheses
// Parentheses can be anything that deactivates the special meaning of delimiters within
// a well defined region, e.g. (...) or "..."
parenthesised-text ::= (("(" parenthesised-text-inner ")") | ("[" parenthesised-text-inner "]")
	 | ("{" parenthesised-text-inner "}") | double-quoted-string | single-quoted-char)
parenthesised-text-inner ::= { parenthesised-text | ~<{[()]}"'> }

condition-text         ::= { parenthesised-text | ~<{[(]\\>  | quoted-nl | ("\\" ~<\n>) }
action-text-without-nl ::= { parenthesised-text | ~<{[(\n\\> | quoted-nl | ("\\" ~<\n>) }
action-text-with-nl    ::= { parenthesised-text | ~<{[(\\>   | quoted-nl | ("\\" ~<\n>) }
quoted-nl ::= "\\\n"


// parser for transition information
transition-info ::= (ws [<event-name-and-wait> ws] [condition ws] ["/" ws transition-action])
isr-event ::= ("ISR" ws "(" ws identifier ws ")")
normal-event-name ::= identifier
event-name ::= (normal-event-name | isr-event)
event-name-and-wait ::= ((event-name ws [":" ws wait-event]) | ([":" ws] wait-event))
condition ::= ("[" condition-text "]")
transition-action ::= action-text-with-nl

// parser for state actions
// state-action-type is defined externally
state-actions ::= (ws {[state-action] "\n" ws})
state-action ::= (state-action-type ws "/" { "\n\t" } state-action-text)
state-action-text ::= action-text-without-nl
