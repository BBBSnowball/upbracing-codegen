package execute2.tests;

public class ExecuteTests2 {
	StringBuffer str = new StringBuffer();

	public static executeTests2(RichToolkit rich_tool) {
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
		
		SimpleTestContext test_temp = rich_tool.startTest("It consists of three sub tests : Low fuel, engine not working, " +
				"and headlight not working.");
		
		//begin the first test
		out.write("Low Fuel");
		
		//read the response from the MCU
		while (int c = in.read() != -1) 
				str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Low Fuel test started")) {
			
			//show different instructions to the user and ask him questions
			if(!rich_tool.askYesNo("Did you see an LED blink?")) 
				msg.error("The LED for temperature error didn't blink");
			
			rich_tool.showInstruction("Press the center button and keep it pressed.");
			
			if(!rich_tool.askYesNo("Did you see LED go off?"))
				msg.error("The LED for temperature did not turn off on pressing the center button.");
			
			rich_tool.showInstruction("Now release the center button");
			
			if(!rich_tool.askYesNo("Did you see the LED blink once again?"))
				msg.error("The LED for temperature did on blink on releasing the center button.");
			
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
			if (!rich_tool.askYesNo("Did you see an LED blink?")) 
				msg.error("The LED for battery error didn't blink");
			
			rich_tool.showInstruction("Press the North button");
			
			if (!rich_tool.askYesNo("Did the LED go off?"))
				msg.error("The LED for battery error didn't turn off.");
				
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
			if (!rich_tool.askYesNo("Did you see an LED blink?")) 
				msg.error("The LED for oil pressure error didn't blink");
			
			rich_tool.showInstruction("Press the East button");
			
			if (!rich_tool.askYesNo("Did you see the LED go off?"))
				msg.error("The LED for oil pressure didn't turn off.");
			
			//tell the MCU to turn the LED low if the test has passed
			out.write("turn_off");
		}
		
		//pop the context of the test
		test_temp.pop();
		
		//report Test results
		rich_tool.getMessages();
	}
	
}