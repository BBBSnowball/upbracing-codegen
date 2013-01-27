
load_pins_from_eagle($schematic_file)

eagle_pins(/IC.*/, /^GEAR_[A-G]$/)
eagle_pins(/IC.*/, /^ERROR[0-9]+$/)

eagle_pins(/IC.*/, "GEAR_ANODE", /OC3B/)
eagle_pins(/IC.*/, "RPM_ANODE")

pinAlias("ERROR_OIL_PRESSURE", "ERROR3")
pinAlias("ERROR_BATTERY", "ERROR2")
pinAlias("ERROR_TEMPERATURE", "ERROR1")

eagle_port("RPM", /IC.*/, /^RPM[1-8]$/)
