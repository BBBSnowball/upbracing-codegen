load_pins_from_eagle(
  #DEPENDS ON:
  "eagle_schematic_3.sch")
  
eagle_pins(/IC3/, /GEAR_[A-G]/)
eagle_pins(/IC3/, /^ERROR_[0-9]/)

eagle_pins(/IC.*/, "GEAR_ANODE")

pinAlias("LOW_FUEL", "ERROR_1")
pinAlias("ENGINE_FAILURE", "ERROR_2")
pinAlias("HEADLIGHT_NOT_WORKING", "ERROR_3")
pinAlias("