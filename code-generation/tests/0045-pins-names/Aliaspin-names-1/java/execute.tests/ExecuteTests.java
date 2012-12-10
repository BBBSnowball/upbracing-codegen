package execute.tests;

public class ExecuteTests {
	StringBuffer str = new StringBuffer();

	public static executeTests(RichToolkit rich_tool) {
		//create instance of messages
		Messages msg = new Messages();
		
		//set appropriate error messages
//		msg[0].error("The LED for temperature error didn't blink");
//		msg[1].error("The LED for battery error didn't blink");
//		msg[2].error("The LED for oil pressure error didn't blink");
		
		//obtain instance of the serial helper
		SerialHelper serial_help = rich_tool.getSerial();
		
		//ensure a baudrate for the serial communication
		serial_help.ensureBaudrate(9600);
		
		//get input and output streams for communication
		InputStream in = serial_help.getInputStream();
		OutputStream out = serial_help.getOutputStream();
		
		//setup to begin testing phase
		rich_tool.start();
		
		SimpleTestContext test_temp = rich_tool.startTest("It consists of three sub tests : temperature, battery, and " +
				"oil pressure");
		
		//begin the first test
		out.write("Temperature");
		
		//read the response from the MCU
		while (int c = in.read() != -1) 
				str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Temperature test started")) {
			if(!rich_tool.askYesNo("Did you see the 4th LED from right blink?")) 
				msg.error("The LED for temperature error didn't blink");
			
			//tell the MCU to turn the LED low if the test has passed
			out.write("turn_off");
		}
		
		//begin the second test
		out.write("Battery");
		str.setLength(0);
		
		//read the response from the MCU
		while (int c = in.read()!= -1)
				str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Battery test started")) {
			if (!rich_tool.askYesNo("Did you see the 5th LED from right blink?")) {
				msg.error("The LED for battery error didn't blink");
			
			//tell the MCU to turn the LED low if the test has passed
			out.write("turn_off");
		}
		
		//begin the third test
		out.write("Oil pressure");
		str.setLength(0);
		
		//read the response from the MCU
		while (int c= in.read()! = -1)
				str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Oil pressure test started")) {
			if (!rich_tool.askYesNo("Did you see the 3rd LED from the right blink?")) {
				msg.error("The LED for oil pressure error didn't blink");
			
			//tell the MCU to turn the LED low if the test has passed
			out.write("turn_off");
		}
		
		//pop the context of the test
		test_temp.pop();
		
		//report Test results
		rich_tool.getMessages();
	}
	
}

