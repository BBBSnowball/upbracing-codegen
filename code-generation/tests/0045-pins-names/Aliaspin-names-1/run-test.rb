#erase the processor 
$helper.erase_processor

#show instructions to the user 
$toolkit.showInstruction("This test blinks some LEDs and you would be asked to answer whether you see the 
same LED pattern on the board as is shown in the console.);

$toolkit.showInstruction("X means that the LED is on and - means that the LED is off.");
$toolkit.showInstruction("The LED layout is like this : ");
$toolkit.showInstruction(" 8 7 6 5 4 3 2 1 ");
$toolkit.showInstruction("The leftmost LED on the board is the leftmost LED on the console and viceversa");
$toolkit.showInstruction("For example: If you see pattern -X------ on the console only the 7th LED on the board must
 be on");

#flash the program onto the microcontroller
$helper.flash_processor

#call the java method to do all the heavy work between the PC and microcontroller
run_tests = Java::executetests1::ExecuteTests::executeTests($toolkit)