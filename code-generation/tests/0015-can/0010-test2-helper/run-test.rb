if $helper.has_second_programmer
  # we can program the second board without user intervention
  # -> great :-)
  $helper.flash_processor2
else

answer = $helper.ask_yes_no <<EOF
This is not a test but a helper program for the following
test. It must be flashed on the auxiliary board which is
connected to the DUT (device under test) via the CAN bus.

If you want to flash it to the auxiliary board, please
connect it to the programmer and say 'yes'. If you are
sure that the auxiliary board already has the right
software, press 'no'. Please note that this helper is
different from the helper you need for test1!

Do you want to flash the helper program to the
auxiliary board now?
EOF

if answer
  $helper.flash_processor
else
  $helper.warn "Not flashing helper program. I hope you know what you are doing ;-)"
end

end # no second programmer

#TODO remember that we have flashed the other processor, so the following tests
#     can print targeted messages
