load_pins_from_eagle(
  #DEPENDS ON:
  "eagle-schematic-2.sch")
  
eagle_pins(/IC3/, /ERROR_[0-9]/)
eagle_pins(/IC3/, /LED_[0-9]/)

pinAlias("LOW_FUEL", "ERROR_1")
pinAlias("ENGINE_FAILURE", "ERROR_2")
pinAlias("HEADLIGHT_NOT_WORKING", "ERROR_3")
pinAlias("FAULTY_EXHAUST", "ERROR_4")
pinAlias("HIGH_TEMP", "ERROR_5")
pinAlias("CHANGE_GEAR", "ERROR_6")
