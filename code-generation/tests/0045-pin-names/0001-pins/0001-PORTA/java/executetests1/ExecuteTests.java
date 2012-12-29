package executetests1;

import java.io.*;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.tests.RichToolkit;
import de.upbracing.code_generation.tests.RichToolkit.SimpleTestContext;
import de.upbracing.code_generation.tests.serial.SerialHelper;

public class ExecuteTests {
	static StringBuffer str = new StringBuffer();

	public static void executeTests(RichToolkit rich_tool) throws IOException {
		// create instance of messages
		Messages msg = rich_tool.getMessages();

		// obtain instance of the serial helper
		SerialHelper serial_help = rich_tool.getSerial();

		// ensure a baudrate for the serial communication
		serial_help.ensureBaudrate(9600);

		// get input and output streams for communication
		InputStream in = serial_help.getInputStream();
		OutputStream out = serial_help.getOutputStream();

		// setup to begin testing phase
		rich_tool.start();

		SimpleTestContext test_temp = rich_tool.startTest("PORTA test");

		// begin the first test
		out.write('L');

		// read the response from the MCU
		incomingData(in);

		// check whether it is the expected response
		serial_help.expectString("LED pattern tests started\r\n");

		// ask questions related to subtests
		out.write('a');

		if (!rich_tool.askYesNo("-----XXX?"))
			msg.error("The -----XXX  test failed.");

		out.write('b');

		if (!rich_tool.askYesNo("XXXXX---?"))
			msg.error("The XXXXX--- test failed.");

		out.write('c');

		if (!rich_tool.askYesNo("XXXXXXXX?"))
			msg.error("The XXXXXXXX test failed.");

		out.write('d');

		if (!rich_tool.askYesNo("X-------?"))
			msg.error("The X------- test failed.");

		out.write('e');

		if (!rich_tool.askYesNo(" -XXXXXXX?"))
			msg.error("The -XXXXXXX test failed.");

		out.write('f');

		if (!rich_tool.askYesNo("XXXX----?"))
			msg.error("The XXXX---- test failed.");

		out.write('g');

		if (!rich_tool.askYesNo("----XXXX?"))
			msg.error("The----XXXX test failed.");

		out.write('h');

		if (!rich_tool.askYesNo("------XX?"))
			msg.error("The ------XX test failed.");

		
		// tell the MCU to exit the tests
		out.write('0');

		// pop the context of the test
		test_temp.pop();

		// report Test results
		rich_tool.getMessages();
	}
}