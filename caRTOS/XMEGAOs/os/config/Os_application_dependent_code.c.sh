#!/bin/sh

if ruby --version &>/dev/null ; then
    # We have Ruby :-)
    # => use the Ruby script instead, as it works on Windows
    #    and has better error messages
    ruby "$(dirname "$0")/Os_application_dependent_code.c.rb"
fi

if avr-gcc --version | grep -q WinAVR ; then
    echo "This script doesn't work on Windows because WinAVR neither has 'sed' nor a"     >&2
    echo "working 'which'."                                                               >&2
    echo "WARN: Os_application_dependent_code.c won't be updated. If you have changed or" >&2
    echo "      updated any code that should go into it, you must update it manually."    >&2
    echo "NOTE: You can use the Ruby script instead. If ruby.exe was on the PATH, I"      >&2
    echo "      would call it for you."                                                   >&2

    # exit with a success status, so we don't break the build
    exit 0
fi

# WinAVR has a broken 'which' - it doesn't find sed and breaks, if executed by the Unix shell
# => We try to run the program.
if gsed --version 2>/dev/null | grep -q "GNU sed" ; then
    # This is for Mac OS X. The default 'sed' is BSD-like.
    SED=gsed
elif sed --version 2>/dev/null | grep -q "GNU sed" ; then
    # On Linux or Windows we have GNU tools by default.
    SED=sed
else
    echo "We need a GNU sed !" >&2
    exit 1
fi

DEST="$(cd "$(dirname "$0")" ; pwd)/Os_application_dependent_code.c"

if diff --version 2>/dev/null | grep -q "GNU diffutils" ; then
    DIFF=diff
else
    DIFF=
fi
if [ -n "$DIFF" -a -e "$DEST" ] ; then
    REAL_DEST="$DEST"
    DEST="$DEST.tmp"
fi

cat >"$DEST" <<EOF
/*
 * Os_application_dependent_code.c
 *
 * generated by script
 */

// This file contains code that should be part of the OS, but depends on the
// application configuration (e.g. count of TCBs). It must be compiled for
// each application.

// This file is generated by the shell script Os_application_dependent_code.c.sh
// or the Ruby script Os_application_dependent_code.c.rb
// DO NOT MODIFY IT !!!

// You can mark application specific code like this:


// will be compiled in Os_application_dependent_code.c:
#ifdef APPLICATION_DEPENDENT_CODE
// your includes (including the directory name!)
// your code
#endif	// end of APPLICATION_DEPENDENT_CODE


#include "config/Os_cfg_application.h"

EOF

PROJECT_DIR_NAME="$(basename "$(cd "$(dirname "$0")/../" ; pwd)")"
cd "$(dirname "$0")/../../"
find "$PROJECT_DIR_NAME" -iname "*.c" -exec "$SED" -ne '/^#\s*ifdef\s\+APPLICATION_DEPENDENT_CODE\($\|\s\)/,/^#\s*\(else\|endif\)\s*\/\/\s*end of APPLICATION_DEPENDENT_CODE\($\|\s\)/ { s�^#\s*ifdef\s*APPLICATION_DEPENDENT_CODE\($\|\s.*$\)�\n\n\/\/ from file {}:\n� ; /^#\s*\(else\|endif\)\s*\/\/\s*end of APPLICATION_DEPENDENT_CODE\($\|\s\)/ !p ; }' {} \; >>"$DEST"

if [ -n "$DEST" -a -n "$REAL_DEST" ] ; then
    if "$DIFF" "$DEST" "$REAL_DEST" >/dev/null ; then
        # not changed -> don't touch the file
        rm "$DEST"
    else
        # really change
        mv "$DEST" "$REAL_DEST"
    fi
fi

