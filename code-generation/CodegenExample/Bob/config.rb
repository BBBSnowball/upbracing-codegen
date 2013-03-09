# load the DBC file
#DEPENDS ON:
$config.can = parse_dbc("can.dbc")
# select messages for Bob
$config.use_can_node = "Bob"

# tell the program about the buttons
# This is only for the DVK90CAN1 board.
# If you use a different board, you need
# to change this.
pin("BUTTON_CENTER",     "PE2")
pin("BUTTON_CENTER_ALT", "PD1")
pin("BUTTON_NORTH",      "PE4")
pin("BUTTON_EAST",       "PE5")
pin("BUTTON_WEST",       "PE6")
pin("BUTTON_SOUTH",      "PE7")
# and some LEDs on PA0 through PA7
port("LED", "PA")


# load Bob's statemachine
#DEPENDS ON:
bobs_statemachine = "bob.statemachine"
$config.statemachines.load("bob", bobs_statemachine)

# We need a few header files for our handlers.
$config.can.addDeclarationsInCFile <<-END
#include <common.h>
#include "pins.h"
#include "statemachines.h"
END

# We trigger statemachine actions for incoming messages.
add_code("msg(SuggestMeeting)", "after_rx", "event_suggested();")
add_code("msg(CancelMeeting)", "after_rx", "event_Alice_cancelled();")
