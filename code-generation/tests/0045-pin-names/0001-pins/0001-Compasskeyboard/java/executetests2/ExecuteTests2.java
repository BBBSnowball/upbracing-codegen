package executetests2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.tests.RichToolkit;
import de.upbracing.code_generation.tests.RichToolkit.SimpleTestContext;
import de.upbracing.code_generation.tests.TestFailedException;
import de.upbracing.code_generation.tests.serial.SerialHelper;

public class ExecuteTests2 {
	static StringBuffer str = new StringBuffer();

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
		InputStream in = serial_help.getInputStream();

		// setup to begin testing phase
		rich_tool.start();
		
		SimpleTestContext test_temp = rich_tool
				.startTest("Compass keyboard test\r\n");

		// begin the tests
		out.write('L');

		// read the response from the MCU
		serial_help.expectString("Compass keyboard test started\r\n");

		//start sub-test 1
		out.write('a');
		
		//check whether the registers are set as expected
		if (!getIncomingData(in).equals("PORTE :0x34\r\n"))
			msg.error("Compass = 'a' test for PORTE failed.");

		if (!getIncomingData(in).equals("PORTD :0x01\r\n"))
			msg.error("Compass = 'a' test for PORTD failed.");
		
		//start sub-test 2
		out.write('b');
		
		//check whether the registers are set as expected
		if (!getIncomingData(in).equals("PORTE :0xc0\r\n"))
			msg.error("Compass = 'b' test for PORTE failed.");

		if (!getIncomingData(in).equals("PORTD :0x00\r\n"))
			msg.error("Compass = 'b' test for PORTD failed.");
		
		//start sub-test 3
		out.write('c');
		
		//check whether the registers are set as expected
		if (!getIncomingData(in).equals("PORTE :0x34\r\n"))
			msg.error("Compass = 'c' test for PORTE failed.");

		if (!getIncomingData(in).equals("PORTD :0x01\r\n"))
			msg.error("Compass = 'c' test for PORTD failed.");
		
		//start sub-test 4
		out.write('d');

		//check whether the registers are set as expected
		if (!getIncomingData(in).equals("DDRE :0x00\r\n"))
			msg.error("Compass = 'd' test for DDRE failed.");

		if (!getIncomingData(in).equals("DDD :0x00\r\n"))
			msg.error("Compass = 'd' test for DDRD failed.");
		
		//start sub-test 5
		out.write('e');
		
		//check whether the registers are set as expected
		if (!getIncomingData(in).equals("PORTE :0xf4\r\n"))
			msg.error("Compass = 'e' test for PORTE failed.");

		if (!getIncomingData(in).equals("PORTD :0x01\r\n"))
			msg.error("Compass = 'e' test for PORTD failed.");
		
		//start sub-test 6
		out.write('f');

		if (!getIncomingData(in).equals("PORTE :0x00\r\n"))
			msg.error("Compass = 'f' test for PORTE failed.");

		if (!getIncomingData(in).equals("PORTD :0x00\r\n"))
			msg.error("Compass = 'f' test for PORTD failed.");
		
		//start sub-test 7
		out.write('g');

		if (!getIncomingData(in).equals("PORTE :0xf4\r\n"))
			msg.error("Compass = 'g' test for PORTE failed.");

		if (!getIncomingData(in).equals("PORTD :0x01\r\n"))
			msg.error("Compass = 'g' test for PORTD failed.");
		
		//start sub-test 8
		out.write('h');

		if (!getIncomingData(in).equals("PORTE :0x00"))
			msg.error("Compass = 'h' test for PORTE failed.");

		if (!getIncomingData(in).equals("PORTD :0x00"))
			msg.error("Compass = 'h' test for PORTD failed.");
		
		//start sub-test 9 (with user help)
		out.write('i');
		
		//PORTE.2 test
		rich_tool.showInstructions("Make sure that the center switch is configured for PORTE.2 mode.\r\n");
		rich_tool.showInstructions("Press the button C now but don't release it.\r\n");
		out.write('a');

		if (getIncomingData(in).equals("\r\n"))
			msg.error("is_set = 'a' test failed for C button.");
		
		rich_tool.showInstructions("Release C now.\r\n");
		out.write('b');
		
		if (getIncomingData(in).equals("\r\n"))	
			msg.error("is_set = 'b' test failed for C button ");
		
		//Test for other ports
		rich_tool.showInstructions("Configure the center switch for PORTD.1 mode now.\r\n");
		rich_tool.showInstructions("Press buttons N, S, W, C, and E now.");
		out.write('c');
		
		if (getIncomingData(in).equals("\r\n"))
			msg.error("is_set = 'c' test failed for N, S , W, C, or E button.");
		
		out.write('0');
		
		// end compass keyboard tests
		out.write('0');

		// pop the context of the test
		test_temp.pop();

		// report Test results
		rich_tool.getMessages();
	}

	private static String getIncomingData(InputStream in) throws IOException {
		int c;
		while ((c = in.read()) != '\n')
			str.append(c);

		return str.toString();
	}
}