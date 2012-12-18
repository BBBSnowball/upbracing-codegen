load_pins_from_eagle(
  #DEPENDS ON:
  "eagle_schematic_1.sch")
  
eagle_pins(/IC3/, /GEAR_[A-G]/)
eagle_pins(/IC3/, /^ERROR[0-9]+$/)

eagle_pins(/IC.*/, "GEAR_ANODE", /OC3B/)
eagle_pins(/IC.*/, "RPM_ANODE")

pinAlias("ERROR_OIL_PRESSURE", "ERROR1")
pinAlias("ERROR_TEMPERATURE", "ERROR2")
pinAlias("ERROR_BATTERY", "ERROR3")