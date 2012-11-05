# hardware on the DVK90CAN1 eval board

# LEDs on port A, non-inverting (page 26)
port("LED", "A");

# buttons on port E (page 25)
# (CENTER jumper set to configuration PortE.2,
#  see page 25 in DVK90CAN1 datasheet)
pins(
  "BUTTON_CENTER" => "PE2",
  "BUTTON_NORTH"  => "PE4",
  "BUTTON_NORTH"  => "PE4",
  "BUTTON_EAST"   => "PE5",
  "BUTTON_WEST"   => "PE6",
  "BUTTON_SOUTH"  => "PE7")
#TODO: define BUTTON_MASK 0xf4
