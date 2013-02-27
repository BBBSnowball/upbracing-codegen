
def snake_case(name)
  name.gsub(/[a-z0-9][A-Z]/) { |a| a[0] + "_" + a[1].downcase }
end

def upcase_first(name)
  name[0].upcase + name[1..-1]
end

def downcase_first(name)
  name[0].downcase + name[1..-1]
end

# add configuration extensions (methods and properties) to the configuration object (for use by JRuby)
class RubyConfigurationExtender
  include ConfigurationExtender
  # add a property that you can get and set by name (you must first add it as state)
  def addProperty(name, state)
    snake_name = snake_case name
    getters = [ name, snake_name, "get" + upcase_first(name) ].uniq
    setters = [ name+"=", snake_name+"=", "set" + upcase_first(name) ].uniq
    CodeGeneratorConfigurations.class_eval do
      getters.each do |mname|
        define_method(mname.intern) { self.getProperty name }
      end
      setters.each do |mname|
        define_method(mname.intern) { |*args| self.setProperty name, *args }
      end
    end
  end
  
  # add a read-only property that you can get by name (you must first add it as state)
  def addReadonlyProperty(name, state)
    snake_name = snake_case name
    getters = [ name, snake_name, "get" + upcase_first(name) ].uniq
    CodeGeneratorConfigurations.class_eval do
      getters.each do |mname|
        define_method(mname.intern) { self.getProperty name }
      end
    end
  end
  
  # add invisible state
  def addState(state, cls)
    # we don't care about this
  end
  
  # add a method
  def addMethod(name, method)
    CodeGeneratorConfigurations.class_eval do
      names = [name, snake_case(name)]
      if name =~ /^set/
        # It is a setter, so we add the usual aliases.
        name2 = downcase_first(name.sub(/^set/, ""))
        names << name2+"="
        names << snake_case(name2)+"="
      end
      names.uniq.each do |mname|
        define_method(mname.intern) do |*args|
          # get parameter types (without the first one which is the config)
          param_types = method.parameter_types[1..-1].to_a
          if method.isVarArgs
            # Find out whether the user tries to call it variadic (not with an array)
            #var_arg_type = param_types[-1]
            var_arg = args[-1]
            # If the uses passed too many arguments, we obviously use the variadic variant.
            uses_var_arg = args.length > param_types.length
            if !uses_var_arg
              # It might be a single variadic argument or an array.
              #  May not work for all cases, but at least for the ones in my test - that should be enough.
              is_array = var_arg.is_a?(Array) || (var_arg.respond_to?(:java_class) && var_arg.java_class.array?)
              uses_var_arg = !is_array
            end

            if uses_var_arg
              # Get the type of the variadic items
              var_arg_type = param_types[-1].component_type
              
              # Replace the variadic param type by
              # some copies of the array item type.
              # (and make sure it is a Ruby list)
              param_types = [*param_types[0..-2]]
              
              (args.length - param_types.length).times do
                param_types << var_arg_type
              end
            end
          end
          if param_types.length != args.length
            raise "I need #{param_types.length} arguments#{method.isVarArgs ? " (or more)" : ""}, but you gave me #{args.length}!"
          end
          
          # tell JRuby to cast the values appropiately
          args = param_types.zip(args).map do |type,value|
            #puts "#{value.inspect} -> #{type}"
            if type.interface?
              # JRuby doesn't seem to be able to convert this
              value
            elsif !type.isArray
              value.to_java(type)
            else
              # to_java of (JRuby) Array needs the item type
              value.to_a.to_java(type.component_type)
            end
          end
          
          if uses_var_arg
            # pack them into an array
            
            # non-variadic arguments (not counting config and the variadic array argument)
            non_var_args = method.parameter_types.length - 2
            
            non_variadic = args[0...non_var_args]
            vardiac_arg = args[non_var_args..-1].to_java(var_arg_type)
            
            args = non_variadic + [vardiac_arg]
          end
          
          method.invoke(nil, [self.to_java(CodeGeneratorConfigurations), *args].to_java)
          #self.call name, *args
        end
      end
    end
  end
end

CodeGeneratorConfigurations.add_extension_listener RubyConfigurationExtender.new
