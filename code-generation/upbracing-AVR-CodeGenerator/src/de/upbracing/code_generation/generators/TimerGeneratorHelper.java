package de.upbracing.code_generation.generators;

import de.upbracing.shared.timer.model.enums.CTCOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMDualSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMSingleSlopeOutputPinMode;

public class TimerGeneratorHelper {
	
	public static String getFilePrefix(Object generator_data) {
		if (generator_data != null && generator_data instanceof String)
			return (String)generator_data;
		return "timer";
	}
	
	public static String getPrescaleCode(int timer, int prescale) {
		
		if (timer == 2) {
			// Timer 2 has different Bitsettings
			switch (prescale) {
			case 1:
				return "(1<<CS20)";
			case 8:
				return "(1<<CS21)";
			case 64:
				return "(1<<CS22)";
			case 256:
				return "(1<<CS22) | (1<<CS21)";
			case 1024:
				return "(1<<CS22) | (1<<CS21) | (1<<CS20)";
			}
		}
		else {
			switch (prescale) {
			case 1:
				return "(1<<CS" + timer + "0)";
			case 8:
				return "(1<<CS" + timer + "1)";
			case 64:
				return "(1<<CS" + timer + "1) | (1<<CS" + timer + "0)";
			case 256:
				return "(1<<CS" + timer + "2)";
			case 1024:
				return "(1<<CS" + timer + "2) | (1<<CS" + timer + "0)";
			}
		}
		
		return "";
	}

	public static String getDataType(int timer) {
		if (timer % 2 == 0)
			return "uint8_t";
		else
			return "uint16_t";
	}
	
	public static String getCTCOutputModeCode(int timer, CTCOutputPinMode m, String channel) {
		if (m.equals(CTCOutputPinMode.CLEAR))
			return "(1<<COM" + timer + channel.toUpperCase() + "1)";
		if (m.equals(CTCOutputPinMode.SET))
			return "(1<<COM" + timer + channel.toUpperCase() + "1) | (1<<COM" + timer + channel.toUpperCase() + "0)";
		if (m.equals(CTCOutputPinMode.TOGGLE))
			return "(1<<COM" + timer + channel.toUpperCase() + "0)";
		return "";
	}
	
	public static String getPWMOutputModeCode(int timer, PWMSingleSlopeOutputPinMode m, String channel) {
		if (m.equals(PWMSingleSlopeOutputPinMode.CLEAR))
			return "(1<<COM" + timer + channel.toUpperCase() + "1)";
		if (m.equals(PWMSingleSlopeOutputPinMode.SET))
			return "(1<<COM" + timer + channel.toUpperCase() + "1) | (1<<COM" + timer + channel.toUpperCase() + "0)";
		if (m.equals(PWMSingleSlopeOutputPinMode.TOGGLE))
			return "(1<<COM" + timer + channel.toUpperCase() + "0)";
		return "";
	}
	
	public static String getPWMOutputModeCode(int timer, PWMDualSlopeOutputPinMode m, String channel) {
		if (m.equals(PWMDualSlopeOutputPinMode.CLEAR_SET))
			return "(1<<COM" + timer + channel.toUpperCase() + "1)";
		if (m.equals(PWMDualSlopeOutputPinMode.SET_CLEAR))
			return "(1<<COM" + timer + channel.toUpperCase() + "1) | (1<<COM" + timer + channel.toUpperCase() + "0)";
		if (m.equals(PWMDualSlopeOutputPinMode.TOGGLE))
			return "(1<<COM" + timer + channel.toUpperCase() + "0)";
		return "";
	}
	
	public static String getPrescaleSettingsRegisterSuffix(int timer) {
		if (timer % 2 == 1)
			return "B";
		return "A";
	}
}
