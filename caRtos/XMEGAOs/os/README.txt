For each application, you have to do the following (for Eclipse):

1. Create the configuration file config.rb. You can use config.rb.skeleton
   as a starting point.

2. some settings (for all configurations, unless noted otherwise):
C/C++ Build / Build Variables:
	CARTOS_DIRECTORY_VARIABLE (type directory) = ${workspace_loc:/XMegaOS}
	(XMegaOS is the name of the OS project)
	CODE_GENERATOR_DIR_VARIABLE = ${workspace_loc:/upbracing-AVR-CodeGenerator}
C/C++ Build / Environment:
	CARTOS_DIRECTORY = ${CARTOS_DIRECTORY_VARIABLE}
	CODE_GENERATOR_DIR = ${CODE_GENERATOR_DIR_VARIABLE}
C/C++ Build / Settings / AVR Compiler / Directories / Include paths:
	add ${CARTOS_DIRECTORY_VARIABLE}
C/C++ Build / Settings / AVR Linker / Libraries / Libraries Path:
	add ${CARTOS_DIRECTORY}/${ConfigName}

3. Make sure that the generators are run at an appropiate time. You can use the Makefile
   hooks of Eclipse. Put an include line in makefile.defs and makefile.targets:
makefile.defs: include $(CARTOS_DIRECTORY)/makefile.defs-application
makefile.targets: include $(CARTOS_DIRECTORY)/makefile.targets-application

4. Add the OS library. If you include makefile.defs-application (see (3)), it will
   add the right library for you. Otherwise, you must determine the MD5 hash of
   gen/Os_cfg_features.h and then add the appropiate library. Make sure you update
   it, whenever Os_cfg_features.h changes.
