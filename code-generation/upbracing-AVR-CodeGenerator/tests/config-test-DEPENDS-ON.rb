#DEPENDS ON: ../../../ecu-list.xml
#DEPENDS ON:
dbc_file = "../../../can_final.dbc"
# This one should cause a warning
#DEPENDS ON

puts "Hello from config.rb"

#DEPENDS ON "../../../can_final.dbc"
# Another one with a warning:
#DEPENDS ON
puts "abc", "def"

# This one should cause a warning, as well
#DEPENDS ON

puts Dir.pwd

require 'parse-dbc.rb'
require 'parse-ecu-list.rb'

ecus = read_ecu_list("../../../ecu-list.xml")
$config.ecus = ecus

$config.can = parse_dbc(dbc_file)

$config.selectEcu("Cockpit")
