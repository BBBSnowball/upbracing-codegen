def should_build_project(name, directory, configuration)
	not name =~ /semaphore|queues|[Aa]lias/ and name != "pins"
end
