
class MyProject < ProjectWithCommon
	def initialize
		super
		add_common :rs232
	end
end

$project = MyProject.new

# remove class, so we can use the same name in other tests
Object.send(:remove_const, :MyProject)
