load_pins_from_eagle(
  #DEPENDS ON:
  "eagle_schematic_2.sch")
  
eagle_pins(/IC3/, /GEAR_[A-G]/)
eagle_pins(/IC3/, /^SPEED[0-9]+$/)

eagle_pins(/IC.*/, "GEAR_ANODE", /OC3B/)
eagle_pins(/IC.*/, "RPM_ANODE")

pinAlias("FIRST_GEAR", "SPEED1")
pinAlias("SECOND_GEAR", "SPEED2")
pinAlias("THIRD_GEAR", "SPEED3")
pinAlias("FOURTH_GEAR", "SPEED4")