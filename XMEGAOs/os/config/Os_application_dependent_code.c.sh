#!/bin/sh

DEST="$(readlink -f "$(dirname "$0")/Os_application_dependent_code.c")"

cat >"$DEST" <<EOF
/*
 * Os_application_dependent_code.c
 *
 *  Created on: Jul 26, 2012
 *      Author: benny
 */

// This file contains code that should be part of the OS, but depends on the
// application configuration (e.g. count of TCBs). It must be compiled for
// each application.

#include "Os_cfg_application.h"

// You can mark application specific code like this:


// will be compiled in Os_application_dependent_code.c:
#ifdef APPLICATION_DEPENDENT_CODE
// your code
#endif	// end of APPLICATION_DEPENDENT_CODE

// This file can be generated with the shell script Os_application_dependent_code.c.sh
EOF

cd "$(dirname "$0")/../../"
find os -iname "*.c" -exec sed -ne '/^#ifdef\s*APPLICATION_DEPENDENT_CODE\($\|\s\)/,/^#endif\s*\/\/\s*end of APPLICATION_DEPENDENT_CODE\($\|\s\)/ { s§^#ifdef\s*APPLICATION_DEPENDENT_CODE\($\|\s.*$\)§\n\n\/\/ from file {}:\n§ ; /^#endif\s*\/\/\s*end of APPLICATION_DEPENDENT_CODE\($\|\s\)/ !p }' {} \; >>"$DEST"
