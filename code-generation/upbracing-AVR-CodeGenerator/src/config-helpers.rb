
def port(*args) ; $config.pins.addPort(*args) ; end
def pin(*args)  ; $config.pins.add(*args)     ; end
def pinAlias(*args) ; $config.pins.addAlias(*args) ; end

def pins(pins)
  pins.each do |name,pin|
    if pin.start_with? "="
      pinAlias(name, pin[1..-1])
    elsif pin.length == 3
      pin(name, pin)
    elsif pin.length == 2
      port(name, pin)
    else
      raise "Don't know what I should do with pin name: " + pin
    end
  end
end
