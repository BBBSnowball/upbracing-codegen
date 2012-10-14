package de.upbracing.code_generation.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestCanGenerator.class, TestEepromGenerator.class,
		TestGlobalVariableGenerator.class, TestPinGenerator.class,
		TestRTOSGenerator.class, TestStatemachineGenerator.class,
		TestTable.class, TestStateNameValidation.class,
		TestMessages.class, TestUtils.class,
		TestStateTransitions.class, TestCollapseFinalStates.class,
		TestAssignNames.class })
public class AllTests {

}
