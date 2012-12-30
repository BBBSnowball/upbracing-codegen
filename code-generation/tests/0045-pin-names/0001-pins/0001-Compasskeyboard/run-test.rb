#erase the processor 
$helper.erase_processor

#show instructions to the user 
$toolkit.showInstructions("This test blinks some LEDs, and you must answer whether you saw the LED blink or not.\r\n
The led pattern is like this : \r\n
7 6 5 4 3 2 1 0 \r\n
A X means that the LED is on and a - means that the LED is off \r\n
For example : In ------XX the last two leds are on, and the rest are off\r\n ");


#flash the program onto the microcontroller
$helper.flash_processor

#call the java method to do all the heavy work between the PC and microcontroller
Java::executetests2::ExecuteTests2::executeTests2($toolkit)

#STDIN.readline