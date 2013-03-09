# load the DBC file
#DEPENDS ON:
$config.can = parse_dbc("can.dbc")
# select messages for Alice
$config.use_can_node = "Alice"

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


# only for Alice

# We need a few header files for our handlers.
$config.can.addDeclarationsInCFile <<-END
#include <common.h>
#include "pins.h"
END

# We don't want to decode any signals, so we completely
# replace the code for the RequestMeeting message.
add_code("msg(RequestMeeting)", "rx_handler", "HIGH(LED7);")

# We need to know whether Bob has accepted the meeting, but
# we don't want to store that in a global variable. Therefore,
# we supply some custom code to handle the value.
add_code("signal(accepted, AcceptMeeting)", "put_value", <<-END_CODE)
  // We have an answer -> turn on LED4
  HIGH(LED4);
  // Show the status with LED5. The value of the
  // signal is stored in the local variable value.
  if (value)
    HIGH(LED5);
  else
    LOW(LED5);
END_CODE

# We treat CancelMeeting like a meeting that is not accepted.
# We might be interested in the reason, so we let the code
# generator produce its usual code which stores the reason
# in a global variable. Our code will be executed after the
# messages has been decoded and all values have been stored.
add_code("msg(CancelMeeting)", "after_rx", "HIGH(LED4); LOW(LED5);")
