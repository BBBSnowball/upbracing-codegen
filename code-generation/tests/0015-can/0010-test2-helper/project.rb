$project = ProjectWithCommon.new

# include simple rs232 driver
$project.add_common :rs232

# link to can library
$project.add_common :can
