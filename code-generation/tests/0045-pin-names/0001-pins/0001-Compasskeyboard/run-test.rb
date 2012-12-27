#erase the processor 
$helper.erase_processor

#show instructions to the user 
$toolkit.showInstruction("This test blinks some LEDs, and you must answer whether you saw the LED blink or not.
You will be asked to press a button/release a button during the test, and when you are done, you must press
ENTER. The buttons are N S E W C. \n The button layout:\n       North       \n West  Center  East\n       South       
Please press ENTER if you have understood the instructions."


#flash the program onto the microcontroller
$helper.flash_processor

#call the java method to do all the heavy work between the PC and microcontroller
run_tests = Java::execute.tests2::ExecuteTests2::executeTests2($toolkit)

#STDIN.readline