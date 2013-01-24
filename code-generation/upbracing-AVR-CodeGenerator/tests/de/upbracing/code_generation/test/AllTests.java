package de.upbracing.code_generation.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.upbracing.code_generation.test.statemachine.AllValidatorTests;

@RunWith(Suite.class)
@SuiteClasses({ TestCanGenerator.class, TestEepromGenerator.class,
		TestGlobalVariableGenerator.class, TestPinGenerator.class,
		TestRTOSGenerator.class, TestStatemachineGenerator.class,
		TestTable.class,
		TestMessages.class, TestUtils.class, AllValidatorTests.class,
		TestCodeGeneratorConfigurations.class })
public class AllTests {

}
