package executetests2;

import java.io.IOException;
import java.io.OutputStream;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.tests.RichToolkit;
import de.upbracing.code_generation.tests.RichToolkit.SimpleTestContext;
import de.upbracing.code_generation.tests.TestFailedException;
import de.upbracing.code_generation.tests.serial.SerialHelper;

public class ExecuteTests2 {
	public static void executeTests2(RichToolkit rich_tool) throws IOException,
			TestFailedException {
		// create instance of messages
		Messages msg = rich_tool.getMessages();

		// obtain instance of the serial helper
		SerialHelper serial_help = rich_tool.getSerial();

		// ensure a baudrate for the serial communication
		serial_help.ensureBaudrate(9600);

		// get output streams for communication
		OutputStream out = serial_help.getOutputStream();

		// setup to begin testing phase
		rich_tool.start();

		SimpleTestContext test_temp = rich_tool
				.startTest("Compass keyboard test\r\n");

		// begin the first test
		out.write('L');

		// read the response from the MCU
		serial_help.expectString("Compass keyboard test started\r\n");

		// begin HIGH test
		out.write('a');

		if (!rich_tool.askYesNo("-------X?"))
			msg.error("The -------X  test failed.");

		out.write('b');

		if (!rich_tool.askYesNo("------XX?"))
			msg.error("The ------XX test failed.");

		out.write('c');

		if (!rich_tool.askYesNo("-----XXX?"))
			msg.error("The -----XXX test failed.");

		out.write('d');

		if (!rich_tool.askYesNo("----XXXX?"))
			msg.error("The ----XXXX test failed.");

		out.write('e');

		if (!rich_tool.askYesNo(" ---XXXXX?"))
			msg.error("The ---XXXXX test failed.");

		out.write('f');

		if (!rich_tool.askYesNo("--XXXXXX?"))
			msg.error("The --XXXXXX test failed.");

		out.write('g');

		if (!rich_tool.askYesNo("-XXXXXXX?"))
			msg.error("The -XXXXXXX test failed.");

		out.write('h');

		if (!rich_tool.askYesNo("XXXXXXXX?"))
			msg.error("The XXXXXXXX test failed.");

		out.write('i');

		if (!rich_tool.askYesNo("------X-?"))
			msg.error("The ------X- test failed.");

		out.write('j');

		if (!rich_tool.askYesNo("-X-XXXXX?"))
			msg.error("The -X-XXXXX test failed");

		// end compass keyboard tests
		out.write('0');

		// pop the context of the test
		test_temp.pop();

		// report Test results
		rich_tool.getMessages();
	}
}