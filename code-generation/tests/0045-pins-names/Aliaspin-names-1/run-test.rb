#erase the processor 
$helper.erase_processor

#show instructions to the user 
$toolkit.showInstruction("This test blinks some LEDs and you must answer whether you saw the LED blink or not. During the 
test you will be required to also press several buttons and will also be required to answer questions related to them.
Enter yes if you understood the instructions")
puts "The button layout:"
puts "       North       "
puts " West  Center  East"
puts "       South       "

#flash the program onto the microcontroller
$helper.flash_processor

#call the java method to do all the heavy work between the PC and microcontroller
run_tests = Java::execute.tests::ExecuteTests::executeTests($toolkit)

#STDIN.readline