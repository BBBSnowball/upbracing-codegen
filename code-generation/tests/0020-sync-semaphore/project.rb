
class MyProject < ProjectWithCommon
	def initialize
		super
		add_common :rs232, :os_errors_on_usart
	end
end

$project = MyProject.new
