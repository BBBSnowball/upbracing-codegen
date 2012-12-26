package execute.tests;

public class ExecuteTests {
	StringBuffer str = new StringBuffer();

	public static executeTests(RichToolkit rich_tool) {
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
		
		SimpleTestContext test_temp = rich_tool.startTest("PORTA test");
		
		//begin the first test
		out.write('T');
		
		//read the response from the MCU
		while (int c = in.read() != -1) 
				str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Temperature test started\r\n")) {
			
			//ask questions related to subtests
			out.write('a');
			
			if(!rich_tool.askYesNo("X-------?")) 
				msg.error("The X------- (HIGH temperature error) test failed.");
			
			out.write('b');
			
			if(!rich_tool.askYesNo("--------?")) 
				msg.error("The -------- (LOW temperature error) test failed.");
			
			out.write('c');
			
			if(!rich_tool.askYesNo("X-------?")) 
				msg.error("The X------- (TOGGLE temperature error) test failed.");
			
			out.write('d');
			
			if(!rich_tool.askYesNo("X-------?")) 
				msg.error("The X------- (SET temperature error) test failed.");
			
			out.write('e');
			
			if(!rich_tool.askYesNo("X-------?")) 
				msg.error("The X------- (IS_SET temperature error) test failed.");
			
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}
		
		//begin the second test
		out.write('B');
		str.setLength(0);
		
		//read the response from the MCU
		while (int c = in.read()!= -1)
				str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Battery test started\r\n")) {
			
			//ask questions related to subtests
			out.write('a');
			
			if (!rich_tool.askYesNo("-X------?")) {
				msg.error("The -X------ (HIGH battery error) test failed.");
				
			out.write('b');
				
			if(!rich_tool.askYesNo("--------?")) 
				msg.error("The -------- (LOW battery error) test failed.");
				
			out.write('c');
				
			if(!rich_tool.askYesNo("-X------?")) 
				msg.error("The -X------ (TOGGLE battery error) test failed.");
				
			out.write('d');
				
			if(!rich_tool.askYesNo("-X------?")) 
				msg.error("The -X------ (SET battery error) test failed.");
				
			out.write('e');
				
			if(!rich_tool.askYesNo("-X------?")) 
				msg.error("The -X------ (IS_SET battery error) test failed.");
				
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}
		
		//begin the third test
		out.write('O');
		str.setLength(0);
		
		//read the response from the MCU
		while (int c= in.read()! = -1)
				str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Oil pressure test started\r\n")) {
			
			//ask questions related to subtests
			out.write('a');
			
			if (!rich_tool.askYesNo("--X-----?")) {
				msg.error("The --X----- (HIGH oil level error) test failed.");
				
			out.write('b');
				
			if(!rich_tool.askYesNo("--------?")) 
				msg.error("The -------- (LOW oil level error) test failed.");
				
			out.write('c');
				
			if(!rich_tool.askYesNo("--X-----?")) 
				msg.error("The --X----- (TOGGLE oil level error) test failed.");
				
			out.write('d');
				
			if(!rich_tool.askYesNo("--X-----?")) 
				msg.error("The --X----- (SET oil level error) test failed.");
				
			out.write('e');
				
			if(!rich_tool.askYesNo("--X-----?")) 
				msg.error("The --X----- (IS_SET oil level error) test failed.");
				
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}
		
		//begin the fourth test
		out.write('1');
		str.setLength(0);
			
		//read the response from the MCU
		while (int c= in.read()! = -1)
				str.append(c);
			
		//check whether it is the expected response
		if (str.toString().equals("First gear test started\r\n")) {
			
			//ask questions related to sub tests
			out.write('a');
				
			if (!rich_tool.askYesNo("---X----?")) {
				msg.error("The ---X---- (HIGH first gear error) test failed.");
					
			out.write('b');
					
			if(!rich_tool.askYesNo("--------?")) 
				msg.error("The -------- (LOW first gear error) test failed.");
					
			out.write('c');
					
			if(!rich_tool.askYesNo("---X----?")) 
				msg.error("The ---X---- (TOGGLE first gear error) test failed.");
					
			out.write('d');
					
			if(!rich_tool.askYesNo("---X----?")) 
				msg.error("The ---X---- (SET first gear error) test failed.");
					
			out.write('e');
					
			if(!rich_tool.askYesNo("---X----?")) 
				msg.error("The ---X---- (IS_SET first gear error) test failed.");
					
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}	
		
		//begin the fifth test
		out.write('2');
		str.setLength(0);
				
		//read the response from the MCU
		while (int c= in.read()! = -1)
			str.append(c);
		
		//check whether it is the expected response
		if (str.toString().equals("Second gear test started\r\n")) {
			
			//ask questions related to sub tests
			out.write('a');
				
			if (!rich_tool.askYesNo("----X---?")) {
				msg.error("The ----X--- (HIGH Second gear error) test failed.");
					
			out.write('b');
					
			if(!rich_tool.askYesNo("--------?")) 
				msg.error("The -------- (LOW Second gear error) test failed.");
					
			out.write('c');
					
			if(!rich_tool.askYesNo("----X---?")) 
				msg.error("The ----X--- (TOGGLE Second gear error) test failed.");
					
			out.write('d');
					
			if(!rich_tool.askYesNo("----X---?")) 
				msg.error("The ----X--- (SET Second gear error) test failed.");
					
			out.write('e');
					
			if(!rich_tool.askYesNo("----X---?")) 
				msg.error("The ----X--- (IS_SET Second gear error) test failed.");
					
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}	
		
		//begin sixth test
		out.write('3');
		str.setLength(0);
					
		//read the response from the MCU
		while (int c= in.read()! = -1)
			str.append(c);
			
		if (str.toString().equals("Third gear test started\r\n")) {
				
			//ask questions related to sub tests
			out.write('a');
					
			if (!rich_tool.askYesNo("-----X--?")) {
				msg.error("The -----X-- (HIGH Third gear error) test failed.");
						
			out.write('b');
						
			if(!rich_tool.askYesNo("--------?")) 
				msg.error("The -------- (LOW Third gear error) test failed.");
						
			out.write('c');
						
			if(!rich_tool.askYesNo("-----X--?")) 
				msg.error("The -----X-- (TOGGLE Third gear error) test failed.");
						
			out.write('d');
						
			if(!rich_tool.askYesNo("-----X--?")) 
				msg.error("The -----X-- (SET Third gear error) test failed.");
						
			out.write('e');
						
			if(!rich_tool.askYesNo("-----X--?")) 
				msg.error("The -----X-- (IS_SET Third gear error) test failed.");
						
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}	
		
		//begin the seventh test
		out.write('4');
		str.setLength(0);
						
		//read the response from the MCU
		while (int c= in.read()! = -1)
			str.append(c);
			
		if (str.toString().equals("Fourth gear test started\r\n")) {
					
			//ask questions related to sub tests
			out.write('a');
						
			if (!rich_tool.askYesNo("------X-?")) {
				msg.error("The ------X- (HIGH Fourth gear error) test failed.");
							
			out.write('b');
							
			if(!rich_tool.askYesNo("--------?")) 
				msg.error("The -------- (LOW Fourth gear error) test failed.");
							
			out.write('c');
							
			if(!rich_tool.askYesNo("------X-?")) 
				msg.error("The ------X- (TOGGLE Fourth gear error) test failed.");
							
			out.write('d');
							
			if(!rich_tool.askYesNo("------X-?")) 
				msg.error("The ------X- (SET Fourth gear error) test failed.");
							
			out.write('e');
							
			if(!rich_tool.askYesNo("------X-?")) 
				msg.error("The ------X- (IS_SET Fourth gear error) test failed.");
							
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}	
				
		//begin the eigth test
		out.write('5');
		str.setLength(0);
							
		//read the response from the MCU
		while (int c= in.read()! = -1)
			str.append(c);
				
		if (str.toString().equals("Fifth gear test started\r\n")) {
						
		//ask questions related to sub tests
		out.write('a');
							
		if (!rich_tool.askYesNo("-------X?")) {
			msg.error("The -------X (HIGH Fifth gear error) test failed.");
								
		out.write('b');
								
		if(!rich_tool.askYesNo("--------?")) 
			msg.error("The -------- (LOW Fifth gear error) test failed.");
							
		out.write('c');
								
		if(!rich_tool.askYesNo("-------X?")) 
			msg.error("The -------X (TOGGLE Fifth gear error) test failed.");
								
		out.write('d');
								
		if(!rich_tool.askYesNo("-------X?")) 
			msg.error("The -------X (SET Fifth gear error) test failed.");
								
		out.write('e');
								
		if(!rich_tool.askYesNo("-------X?")) 
			msg.error("The -------X (IS_SET Fifth gear error) test failed.");
								
		//tell the MCU to turn the LED low if the test has passed
		out.write('0');
	}	
		
	//pop the context of the test
	test_temp.pop();
		
	//report Test results
	rich_tool.getMessages();
	}
}

