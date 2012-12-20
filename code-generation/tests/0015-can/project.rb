
class MyProject < ProjectWithCommon
	def initialize
		super
		add_common :rs232
    @custom_source_files = ["#/../../avr-programs/upbracing-common/can_at90.c"]
	end
	
  def additional_source_files(source_dir, name, configuration)
    @custom_source_files + super
  end
end

$project = MyProject.new
