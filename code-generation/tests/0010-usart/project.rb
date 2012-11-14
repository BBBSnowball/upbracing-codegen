
class MyProject < ProjectWithCommon
	def initialize
		super
		add_common :rs232
	end
end

$project = MyProject.new
