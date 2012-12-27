package executetests1;

import java.io.*;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.tests.RichToolkit;
import de.upbracing.code_generation.tests.RichToolkit.SimpleTestContext;
import de.upbracing.code_generation.tests.serial.SerialHelper;

public class ExecuteTests {
	static StringBuffer str = new StringBuffer();

	public static void executeTests(RichToolkit rich_tool) throws IOException {
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
		incomingData(in);
		
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
			incomingData(in);
			
			if(!str.toString().equals("yes")) 
				msg.error("The IS_SET temperature error test failed.");
			
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}
		
		//begin the second test
		out.write('B');
		str.setLength(0);
		
		//read the response from the MCU
		incomingData(in);
		
		//check whether it is the expected response
		if (str.toString().equals("Battery test started\r\n")) {
			
			//ask questions related to subtests
			out.write('a');
			
			if (!rich_tool.askYesNo("-X------?")) 
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

			incomingData(in);
			
			if(!str.toString().equals("yes")) 
				msg.error("The IS_SET battery error test failed.");
			
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}
		
		//begin the third test
		out.write('O');
		str.setLength(0);
		
		//read the response from the MCU
		incomingData(in);
		
		//check whether it is the expected response
		if (str.toString().equals("Oil pressure test started\r\n")) {
			
			//ask questions related to subtests
			out.write('a');
			
			if (!rich_tool.askYesNo("--X-----?")) 
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
		
			incomingData(in);
			
			if(!str.toString().equals("yes")) 
				msg.error("The IS_SET oil level error test failed.");
			
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}
		
		//begin the fourth test
		out.write('1');
		str.setLength(0);
			
		//read the response from the MCU
		incomingData(in);
			
		//check whether it is the expected response
		if (str.toString().equals("First gear test started\r\n")) {
			
			//ask questions related to sub tests
			out.write('a');
				
			if (!rich_tool.askYesNo("---X----?")) 
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
			incomingData(in);
			
			if(!str.toString().equals("yes")) 
				msg.error("The IS_SET first gear error test failed.");
					
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}	
		
		//begin the fifth test
		out.write('2');
		str.setLength(0);
				
		//read the response from the MCU
		incomingData(in);
		
		//check whether it is the expected response
		if (str.toString().equals("Second gear test started\r\n")) {
			
			//ask questions related to sub tests
			out.write('a');
				
			if (!rich_tool.askYesNo("----X---?")) 
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
			incomingData(in);
			
			if(!str.toString().equals("yes")) 
				msg.error("The IS_SET second gear error test failed.");
			
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}	
		
		//begin sixth test
		out.write('3');
		str.setLength(0);
					
		//read the response from the MCU
		incomingData(in);
			
		if (str.toString().equals("Third gear test started\r\n")) {
				
			//ask questions related to sub tests
			out.write('a');
					
			if (!rich_tool.askYesNo("-----X--?")) 
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
			incomingData(in);
			
			if(!str.toString().equals("yes")) 
				msg.error("The IS_SET third gear error test failed.");
			
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}	
		
		//begin the seventh test
		out.write('4');
		str.setLength(0);
						
		//read the response from the MCU
		incomingData(in);
			
		if (str.toString().equals("Fourth gear test started\r\n")) {
					
			//ask questions related to sub tests
			out.write('a');
						
			if (!rich_tool.askYesNo("------X-?")) 
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
			incomingData(in);
			
			if(!str.toString().equals("yes")) 
				msg.error("The IS_SET fourth gear error test failed.");
							
			//tell the MCU to turn the LED low if the test has passed
			out.write('0');
		}	
				
		//begin the eigth test
		out.write('5');
		str.setLength(0);
							
		//read the response from the MCU
		incomingData(in);
		
		if (str.toString().equals("Fifth gear test started\r\n")) {
						
		//ask questions related to sub tests
		out.write('a');
							
			if (!rich_tool.askYesNo("-------X?")) 
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
			incomingData(in);
		
			if(!str.toString().equals("yes")) 
				msg.error("The IS_SET fifth gear error test failed.");
			}
								
		//tell the MCU to turn the LED low if the test has passed
		out.write('0');
		
		//pop the context of the test
		test_temp.pop();
			
		//report Test results
		rich_tool.getMessages();
	}	
		


	private static void incomingData(InputStream in) throws IOException {
		str.setLength(0);
		int c;
		while ((c = in.read()) != -1)
			str.append(c);
	}
}