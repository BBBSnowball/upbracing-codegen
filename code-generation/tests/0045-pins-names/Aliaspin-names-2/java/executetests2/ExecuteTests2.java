package execute2.tests;

public class ExecuteTests2 {
	StringBuffer str = new StringBuffer();

	public static executeTests2(RichToolkit rich_tool) {
		//create instance of messages
		Messages msg = rich_tool.getMessages();
		
		//obtain instance of the serial helper
		SerialHelper serial_help = rich_tool.getSerial();
		
		//ensure a baudrate for the serial communication
		serial_help.ensureBaudrate(9600);
		
		//get input and output streams for communication
		InputStream in = serial_help.getInputStream();
		OutputStream out = serial_help.getOutputStream();
		
		//setup to begin testing phase
		rich_tool.start();
		
		SimpleTestContext test_temp = rich_tool.startTest("Compass card keyboard test");
		
		//begin the first test
		out.write('L');
		
		//read the response from the MCU
		incomingData(in);
		
		//check whether it is the expected response
		if (str.toString().equals("Low Fuel test started\r\n")) {
			//begin PULLUP resistor test
			out.write('1');
			
			rich_tool.showInstrution("Press the C but don't release it.");
			out.write('p');
			
			incomingData(in);
			
			if (str.toString().equals("false"))
				msg.error("The PULLUP resistor test for PORTE.2 (Low fuel test) failed when C was pressed.");
			
			rich_tool.showInstruction("Release the button C now.");
			out.write('r');
			
			incomingData(in);
			
			if (str.toString().equals("false"))
				msg.error("The PULLUP resistor test for PORTE.2 (Low fuel test) failed when C was released.");
			
			//end the subtest
			out.write('d');
				
			//begin HIGH test
			out.write('2');
			
			rich_tool.showInstruction("Press C but don't release it.");
			out.write('p');
			
			incomingData(in);
			
			if (str.toString().equals("false"))
				msg.error("The HIGH test for PORTE.2 (Low fuel test) failed when C was pressed.");
			
			rich_tool.showInstruction("Release the button C now.");
			out.write('r');
			
			incomingData(in);
			
			if (str.toString().equals("false"))
				msg.error("The HIGH test for PORTE.2 (Low fuel test) failed when C was released.");
			
			//begin LOW test
			out.write('3');
			
			rich_tool.showInstruction("Press C but don't release it.");
			out.write('p');
			
			incomingData(in);
			
			if (str.toString().equals("false"))
				msg.error("The LOW test for PORTE.2 (Low fuel test) failed when C was pressed.");
			
			rich_tool.showInstruction("Release the button C now.");
			out.write('r');
			
			incomingData(in);
			
			if (str.toString().equals("false"))
				msg.error("The LOW test for PORTE.2 (Low fuel test) failed when C was released.");
			
			//begin TOGGLE test
			out.write('4')
			
			rich_tool.showInstruction("Press C but don't release it.");
			out.write('p');
			
			incomingData(in);
			
			if (str.toString().equals("false"))
				msg.error("The TOGGLE test for PORTE.2 (Low fuel test) failed when C was pressed.");
			
			rich_tool.showInstruction("Release the button C now.");
			out.write('r');
			
			incomingData(in);
			
			if (str.toString().equals("false"))
				msg.error("The TOGGLE test for PORTE.2 (Low fuel test) failed when C was released.");
			
			//end LOW FUEL test
			out.write('0');
			
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
		incomingData(in);
		
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
	
private void incomingData(InpuStream in) {
	str.setLength(0);
	while (int c = in.read() != -1)
		str.append(c);
}
	
}