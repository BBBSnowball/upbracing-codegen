#erase the processor 
$helper.erase_processor

#show instructions to the user 
$toolkit.showInstruction("This test blinks some LEDs, and you must answer whether you saw the LED blink or not. Enter yes if
you understood the instructions")
puts "The LED layout is like this:"
puts " 8  7  6  5  4  3  2  1 "

#flash the program onto the microcontroller
$helper.flash_processor

#call the java method to do all the heavy work between the PC and microcontroller
run_tests = Java::execute.tests2::ExecuteTests2::executeTests2($toolkit)

#STDIN.readline