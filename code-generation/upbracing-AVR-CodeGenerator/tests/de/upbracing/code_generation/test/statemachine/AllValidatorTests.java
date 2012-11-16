package de.upbracing.code_generation.test.statemachine;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ TestStateNameValidation.class,
		TestStateTransitions.class, TestCollapseFinalStates.class,
		TestAssignNames.class, TestStateMachineValidator.class, TestDuplicateNames.class })

public class AllValidatorTests {

}
