s = state_machine "counter" do
  global_code :includes, :cfile do
    "#include <avr/io.h>\n#include <avr/wdt.h>"
  end
  
  state :stopped
  
  state :running do
    always "wdt_reset()"
    action "ENTER/DDRA = 0xff", "EXIT/DDRA = 0x00"
    
    transition_to(:running) { "wait(100ms) / PORTA++" }
    transition_to :stopped, :condition => "PORTA >= 128"
  end

  transition({:running => :stopped}, "startstop_pressed")
  transition :stopped => :running, :t_info => "startstop_pressed"
  transition(:stopped => :stopped) { "reset / PORTA = 0" }
  transition :running => :stopped, :transition_info => "ISR(INT0)"

  self[:stopped].action({ "enter" => "DDRB = 0xff" }, "ENTER / PORTB++", "ALWAYS / wdt_reset()")
  
  self.base_period = "1ms"
  
  initial :stopped, "PORTA = 0"
end

$config.statemachines << s
