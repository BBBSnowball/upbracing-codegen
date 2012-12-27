#!/bin/sh

if [ -z "$1" ] ; then
	find -name "*.statecharts" -exec "$0" {} \;
else
	OLD="$1"
	NEW="$(echo "$OLD" | sed "s/statecharts$/statemachine/")"

	sed -e 's#xmlns:Statecharts="http://statechart"#xmlns:statemachine="http://www.upbracing.de/code_generation/statemachine"#' -e 's#Statecharts:#statemachine:#' <"$OLD" >"$NEW"
	git rm --cached "$OLD" && git add "$NEW"
fi
