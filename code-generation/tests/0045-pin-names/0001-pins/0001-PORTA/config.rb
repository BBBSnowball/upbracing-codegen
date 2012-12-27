load_pins_from_eagle(
  #DEPENDS ON:
  "eagle-schematic-1.sch")
  
eagle_pins(/IC3/, /GEAR_[A-G]/)
eagle_pins(/IC3/, /^ERROR[0-9]/)

eagle_pins(/IC.*/, "GEAR_ANODE", /OC3B/)
eagle_pins(/IC.*/, "RPM_ANODE")

pinAlias("ERROR_TEMPERATURE", "ERROR1")
pinAlias("ERROR_BATTERY", "ERROR2")
pinAlias("ERROR_OIL_LEVEL", "ERROR3")
pinAlias("FIRST_GEAR", "ERROR4")
pinAlias("SECOND_GEAR", "ERROR5")
pinAlias("THIRD_GEAR", "ERROR6")
pinAlias("FOURTH_GEAR", "ERROR7")
pinAlias("FIFTH_GEAR", "ERROR8")