$ruby_helpers_package = "de/upbracing/code_generation/ruby"

require "#$ruby_helpers_package/parse-dbc.rb"
require "#$ruby_helpers_package/parse-ecu-list.rb"

JRubyHelpers                = Java::de::upbracing::code_generation::JRubyHelpers
CodeGeneratorConfigurations = Java::de::upbracing::code_generation::config::CodeGeneratorConfigurations
ConfigurationExtender       = Java::de::upbracing::code_generation::config::ConfigurationExtender

# include all helpers
["config-object", "pins", "pins-from-eagle", "statemachine", "rtos", "can"].each do |name|
  require "#$ruby_helpers_package/#{name}.rb"
end
