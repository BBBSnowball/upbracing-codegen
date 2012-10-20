
ws ::= <{%whitespace%}>
ws-no-nl ::= <{" " | "\t"}>

positive-number ::= (%digit% { %digit% })
sign ::= ("+" | "-")
simple-unsigned-float-number ::= (positive-number ["." positive-number])
float-number ::= (["+" | "-"] ((positive-number ["." {%digit%}]) | ("." positive-number)) [("e"|"E") [sign] positive-number])
ratio-number ::= (positive-number "/" float-number)

double-quoted-string ::= ("\"" {string-or-char-contents | "\'"} "\"")
single-quoted-char   ::= ("\'" {string-or-char-contents | "\""} "\'")
string-or-char-contents ::= (~<\\\"\'> | ("\\" ~<>))

identifier ::= ((%alpha% | "_") {%alpha% | %digit% | "_"})
