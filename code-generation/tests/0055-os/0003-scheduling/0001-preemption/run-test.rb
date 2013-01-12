# erase processor to make sure we get no further
# stuff on the serial line
$helper.erase_processor

# open serial line
# We do that before flashing the processor to get
# all the output. 
$helper.first_serial.ensure_baudrate 9600

# we need some delay between invocations of avrdude
sleep 3

# write the program on the processor
$helper.flash_processor

# Initialization
$helper.first_serial.expect_string "\nStarting preemption test.\n"

# wait some time, to prevent an "end of stream" error
sleep 3

# Test
test = $helper.start_test "Preemption"
begin
  
  started = false
  finished = false
  task2preempt = 0 # number of times task2 has preempted task1 
  
  for i in 1..10 # 10 lines should be enough for Start Task1, some instances of Task2 and Finish Task1
    matcher = $helper.first_serial.expectRegex "^(Start Task1|Finish Task1|Task2)(\n)" 
    str = matcher.group(1)
    
    if str == "Start Task1"
      if started
        $toolkit.messages.error "Task 1 started, when it was already started."
        raise Java::de::upbracing::code_generation::tests::TestFailedException.new
      end
      started = true
    end
    
    if str == "Finish Task1"
      if !started
        $toolkit.messages.error "Task 1 finished, but it was not started."
        raise Java::de::upbracing::code_generation::tests::TestFailedException.new
      end

      if task2preempt == 0
        $toolkit.messages.error "Test failed. Task 2 could not preempt task 1"
        raise Java::de::upbracing::code_generation::tests::TestFailedException.new
      end 
      
      finished = true
      break;
    end
    
    if str == "Task2"
      if started
        task2preempt = task2preempt + 1
      end
    end

  end
  
  if started && finished && task2preempt > 2
    test.succeed
  else
    if taskpreempt < 3
      $toolkit.messages.error "Test failed. Task 2 could not preempt Task 1 at least three times"
      raise Java::de::upbracing::code_generation::tests::TestFailedException.new
    else
      $toolkit.messages.error "Test failed. Task 1 did not start and finish"
      raise Java::de::upbracing::code_generation::tests::TestFailedException.new
    end
  end
  
 
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test.fail "Failed."
end
test.pop