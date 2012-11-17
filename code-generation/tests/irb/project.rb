$project = Project.new

class << $project
    def get_test_dependencies(project_dir, test_helper)
    	# don't build anything
    	# (super method would return the hex file)
        []
    end

    def add_to_test_all(dir, configuration, test)
    	# we don't want to run this for test-all
    	# -> do nothing
    end
end
