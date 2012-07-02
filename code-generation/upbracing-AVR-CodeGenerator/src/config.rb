puts "Hello from config.rb"

$config.a = 42
$config.b = 7

puts Dir.pwd

require 'parse-dbc.rb'
require 'parse-ecu-list.rb'

ecus = read_ecu_list("../../ecu-list.xml")
$config.ecus = ecus

$config.can = parse_dbc("../../can_final.dbc")
