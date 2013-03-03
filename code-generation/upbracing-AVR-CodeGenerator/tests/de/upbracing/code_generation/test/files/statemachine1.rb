$config.statemachines << statemachine("counter") do
  self.base_period = "1ms"
  
  initial :stopped, "PORTA = 0"
  
  state :stopped, :normal do
    enter "DDRB = 0xff", "PORTB++"
    action :always => "wdt_reset()"
  end
  
  state :running, :normal do
    always { "wdt_reset()" }
    
    transition_to(:running) { "wait(100ms) / PORTA++" }
    transition_to :stopped, :condition => "PORTA >= 128"
    
    action "ENTER/DDRA = 0xff", "EXIT/DDRA = 0x00"
  end

  transition({:running => :stopped}, "startstop_pressed")
  transition :stopped => :running, :t_info => "startstop_pressed"
  transition(:stopped => :stopped) { "reset / PORTA = 0" }
  transition :running => :stopped, :transition_info => "ISR(INT0)"
  
  global_code :includes, <<-EOF
    #include <avr/io.h>
    #include <avr/wdt.h>
EOF
end
