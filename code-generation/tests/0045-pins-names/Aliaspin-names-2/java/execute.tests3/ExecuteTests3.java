package execute2.tests;

public class ExecuteTests3 {
	StringBuffer str = new StringBuffer();

	public static executeTests3(RichToolkit rich_tool) {
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
		
		SimpleTestContext test_temp = rich_tool.startTest("It consists of fourth sub tests : first gear, second gear, " +
				" third gear, and forth gear.");
		
		//begin the first test
		out.write("First_gear");
		
		//read the response from the MCU
		while (int c = in.read() != -1) 
				str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("First gear test started")) {
			
			//show different instructions to the user and ask him questions
			if(!rich_tool.askYesNo("Did you see LED 0 light up?")) 
				msg.error("The first gear LED didn't blink");
			
			//tell the MCU to turn off the LED
			out.write("turn_off");
		}
		
		//begin the second test
		out.write("Second_gear");
		str.setLength(0);
		
		//read the response from the MCU
		while (int c = in.read()!= -1)
				str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Second gear test started")) {
			if (!rich_tool.askYesNo("Did you see LED 1 light up?")) 
				msg.error("The second gear LED didn't blink");
			
			//tell the MCU to turn the LED low 
			out.write("turn_off");
		}
		
		//begin the third test
		out.write("Third_gear");
		str.setLength(0);
		
		//read the response from the MCU
		while (int c= in.read() != -1)
				str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Third gear test started")) {
			if (!rich_tool.askYesNo("Did you see LED 3 light up?")) 
				msg.error("The third gear LED didn't blink.");
				
			//tell the MCU to turn the LED low 
			out.write("turn_off");
		}
		
		
		//begin the fourth test
		out.write("Fourth_gear");
		str.setLength(0);
		
		//read the response from the MCU
		while (int c = in.read() != -1)
			 str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Fourth gear test started")) {
			if (!rich_tool.askYesNo("Did you see LED 5 light up?"))
				msg.error("The fourth gear LED didn't blink.");
			
			//tell the MCU to turn the LED low
			out.write("turn_off");
		}
		
		//pop the context of the test
		test_temp.pop();
		
		//report Test results
		rich_tool.getMessages();
	}
	
}