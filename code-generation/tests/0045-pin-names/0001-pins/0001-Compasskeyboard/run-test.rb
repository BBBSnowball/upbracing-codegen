# show instructions to the user 
$toolkit.showInstructions <<EOF
This is a semi-automated test. Most of the tests will be performed without your 
help, except for a few where you would be asked to press a button on the compass card keyboard. The layout
of the keyboard is like this :

          NORTH
 WEST     CENTER        EAST
          SOUTH

The abbreviations N (NORTH), S (SOUTH), W (WEST), E (EAST), C (CENTER) are
on the board next to the buttons.
EOF


# flash the program onto the microcontroller
$helper.flash_processor

# call the java method to do all the heavy work between the PC and microcontroller
Java::executetests2::ExecuteTests2::executeTests2($toolkit)
