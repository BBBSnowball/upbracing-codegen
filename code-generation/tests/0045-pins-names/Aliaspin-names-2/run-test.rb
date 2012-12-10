#erase the processor 
$helper.erase_processor

#show instructions to the user 
$toolkit.showInstruction("This test blinks some LEDs and you must answer whether you saw the LED blink or not. During the 
test you will be required to answer questions related to them.
Enter yes if you understood the instructions")
$toolkit.showInstruction("The LED layout : ");
$toolkit.showInstruction(" 7 6 5 4 3 2 1 0 ");

#flash the program onto the microcontroller
$helper.flash_processor

#call the java method to do all the heavy work between the PC and microcontroller
run_tests = Java::execute.tests3::ExecuteTests3::executeTests3($toolkit)

#STDIN.readline