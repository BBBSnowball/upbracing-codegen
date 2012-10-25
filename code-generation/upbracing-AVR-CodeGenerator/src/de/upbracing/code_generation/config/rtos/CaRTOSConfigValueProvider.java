package de.upbracing.code_generation.config.rtos;

import java.util.Arrays;
import java.util.Collection;

import de.upbracing.code_generation.config.rtos.RTOSConfigValue.ConfigFile;

public class CaRTOSConfigValueProvider implements RTOSConfigValueProvider {
	@Override
	public Collection<RTOSConfigValue> getRTOSConfigValues() {
		return Arrays.<RTOSConfigValue>asList(
				new IntegerRTOSConfigValue(RTOSConfigValueType.DefineValue,
						ConfigFile.APPLICATION, "drivers/usart",
						"USART_TRANSMIT_QUEUE_LENGTH", "10",
						"length of the usart transmit buffer",
						0, 255),

				new RTOSConfigValue(RTOSConfigValueType.DefineFlag,
						ConfigFile.FEATURES, "drivers/usart",
						"USART_ENABLE_DRIVER", "true",
						"enable USART driver"),
		
				new RTOSConfigValue(
						RTOSConfigValueType.DefineEnum("BCC1", "BCC2", "ECC1", "ECC2"),
						ConfigFile.FEATURES, "os/core",
						"OS_CFG_CC", "BCC1",
						"conformance mode (task features)")
			);
	}
}
