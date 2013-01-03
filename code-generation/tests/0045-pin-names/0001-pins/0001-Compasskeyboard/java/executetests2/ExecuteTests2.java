package executetests2;

import java.io.IOException;
import java.io.OutputStream;

import de.upbracing.code_generation.tests.RichToolkit;
import de.upbracing.code_generation.tests.RichToolkit.SimpleTestContext;
import de.upbracing.code_generation.tests.TestFailedException;
import de.upbracing.code_generation.tests.serial.SerialHelper;

public class ExecuteTests2 {
	static StringBuffer str = new StringBuffer();

	public static void executeTests2(RichToolkit rich_tool) throws IOException,
			TestFailedException {
		
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

		// begin the tests
		out.write('L');

		// read the response from the MCU
		serial_help.expectString("Compass keyboard test started\r\n");

		//start sub-test 1
		out.write('a');
		
		//check whether the registers are set as expected
		serial_help.expectString("PORTE :0x34\r\n");
		serial_help.expectString("PORTD :0x00\r\n");
		
		//start sub-test 2
		out.write('b');
		
		//check whether the registers are set as expected
		serial_help.expectString("PORTE :0xc0\r\n");
		serial_help.expectString("PORTD :0x02\r\n");
		
		//start sub-test 3
		out.write('c');
		
		//check whether the registers are set as expected
		serial_help.expectString("PORTE :0x34\r\n");
		serial_help.expectString("PORTD :0x00\r\n");
		
		//start sub-test 4
		out.write('d');

		//check whether the registers are set as expected
		serial_help.expectString("DDRE :0x00\r\n");
		serial_help.expectString("DDRD :0x00\r\n");
			
		//start sub-test 5
		out.write('e');
		
		//check whether the registers are set as expected
		serial_help.expectString("PORTE :0xf4\r\n");
		serial_help.expectString("PORTD :0x02\r\n");
		
		//start sub-test 6
		out.write('f');
		
		serial_help.expectString("PORTE :0x00\r\n");
		serial_help.expectString("PORTD :0x00\r\n");

		//start sub-test 7
		out.write('g');
		
		serial_help.expectString("PORTE :0xf4\r\n");
		serial_help.expectString("PORTD :0x02\r\n");

		//start sub-test 8
		out.write('h');
		
		serial_help.expectString("PORTE :0x00\r\n");
		serial_help.expectString("PORTD :0x00\r\n");

		//start sub-test 9 (with user help)
		out.write('i');
		
		//PORTE.2 test
		rich_tool.showInstructions("Make sure that the center switch is configured for PORTE.2 mode.\r\n");
		rich_tool.showInstructions("Press the button C now but don't release it.\r\n");
		out.write('a');
		
		serial_help.expectString("PORTE.2 :0x00\r\n");		
		
		rich_tool.showInstructions("Release C now.\r\n");
		out.write('b');
		
		serial_help.expectString("PORTE.2 :0x01\r\n");
		
		//Test for other ports
		rich_tool.showInstructions("Configure the center switch for PORTD.1 mode now.\r\n");
		rich_tool.showInstructions("Press buttons N, S, W, C, and E now.");
		out.write('c');
		
		serial_help.expectString("PORTD.1 :0x00\r\n");
		serial_help.expectString("PORTE.2 :0x00\r\n");
		serial_help.expectString("PORTE.4 :0x00\r\n");
		serial_help.expectString("PORTE.5 :0x00\r\n");
		serial_help.expectString("PORTE.6 :0x00\r\n");
		serial_help.expectString("PORTE.7 :0x00\r\n");
		
		out.write('0');
		
		// end compass keyboard tests
		out.write('0');

		// pop the context of the test
		test_temp.pop();

		// report Test results
		rich_tool.getMessages();
	}
}