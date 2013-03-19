@echo off

java -cp "%~dp0\de.upbracing.code_generation.jar" org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader %*
