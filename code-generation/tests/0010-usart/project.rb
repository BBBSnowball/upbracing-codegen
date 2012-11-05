
class MyProject < Project
	def additional_source_files(source_dir, name, configuration)
		["#/common/rs232.c", "#/common/rs232-helpers.c"] + super
	end

	def additional_include_dirs(source_dir, source_file, target_dir, configuration)
		["#/common"] + super
	end
end

$project = MyProject.new
