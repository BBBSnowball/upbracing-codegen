package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.Context;
import de.upbracing.code_generation.Messages.ContextItem;
import de.upbracing.code_generation.Messages.Message;
import de.upbracing.code_generation.Messages.ObjectFormatter;
import de.upbracing.code_generation.Messages.Severity;

import static de.upbracing.code_generation.test.TestHelpers.assertListEquals;

public class TestMessages {
	// we have a formatter for IDummy, Dummy and IDummy3
	
	interface IDummy { }
	interface IDummy3 { }
	interface IDummy4 extends IDummy { }
	// formatted by the formatter for Dummy (the type itself)
	class Dummy implements IDummy { }
	// formatted by the formatter for Dummy (supertype)
	class Dummy2 extends Dummy { }
	// formatted by the formatter for Dummy (supertype has priority over interface)
	class Dummy3 extends Dummy implements IDummy3 { }
	// formatted by the formatter for IDummy (indirect interface)
	class Dummy4 implements IDummy4 { }
	
	class Formatter implements Messages.ObjectFormatter<Object> {
		private String forType;
		
		public Formatter(String forType) {
			super();
			this.forType = forType;
		}

		@Override
		public String format(int type, Object obj) {
			String str_type;
			switch (type) {
			case ObjectFormatter.SHORT:  str_type = "SHORT";  break;
			case ObjectFormatter.NORMAL: str_type = "NORMAL"; break;
			case ObjectFormatter.LONG:   str_type = "LONG";   break;
			default: str_type = Integer.toString(type); break;
			}
			
			return obj.getClass().getSimpleName() + ",by " + forType + "," + str_type;
		}
	}
	
	class MessageListener implements Messages.MessageListener {
		public LinkedList<Message> messages = new LinkedList<Message>();

		@Override
		public void message(Message msg) {
			messages.add(msg);
		}
		
		public Message getNewestMessage() {
			if (!messages.isEmpty())
				return messages.getLast();
			else
				return null;
		}
		
		public String getNewestMessageText() {
			if (!messages.isEmpty())
				return messages.getLast().getMessage();
			else
				return null;
		}
		
		public int getMessageCount() {
			return messages.size();
		}
	}
	
	@Test
	public void testSupertypes() {
		assertListEquals(
				Messages.getAllSupertypes(Dummy3.class),
				Dummy3.class, Dummy.class, Object.class, IDummy3.class, IDummy.class);
		
		assertListEquals(
				Messages.getAllSupertypes(Dummy4.class),
				Dummy4.class, Object.class, IDummy4.class);
	}

	@Test
	public void test() {
		Messages msgs = new Messages();
		
		msgs.addObjectFormatter(IDummy.class,  new Formatter("IDummy" ));
		msgs.addObjectFormatter( Dummy.class,  new Formatter( "Dummy" ));
		msgs.addObjectFormatter(IDummy3.class, new Formatter("IDummy3"));
		
		MessageListener listener1 = new MessageListener();
		MessageListener listener2 = new MessageListener();
		msgs.addMessageListener(listener1);
		msgs.addMessageListener(listener2);
		
		
		assertEquals(Severity.NONE, msgs.getHighestSeverity());
		
		
		msgs.trace("abc");
		
		assertEquals(1, listener1.getMessageCount());
		assertEquals(1, listener2.getMessageCount());
		assertEquals("abc", listener1.getNewestMessageText());
		assertEquals(Severity.TRACE, msgs.getHighestSeverity());
		
		
		msgs.trace("def: %s", "x");
		
		assertEquals(2, listener1.getMessageCount());
		assertEquals(2, listener2.getMessageCount());
		assertEquals("def: x", listener1.getNewestMessageText());
		assertEquals(Severity.TRACE, msgs.getHighestSeverity());
		
		
		msgs.debug("ghi: %02d", 7);
		
		assertEquals(3, listener1.getMessageCount());
		assertEquals(3, listener2.getMessageCount());
		assertEquals("ghi: 07", listener1.getNewestMessageText());
		assertEquals(Severity.DEBUG, msgs.getHighestSeverity());
		
		
		msgs.info("jkl");
		
		assertEquals(4, listener1.getMessageCount());
		assertEquals("jkl", listener1.getNewestMessageText());
		assertEquals(Severity.INFO, msgs.getHighestSeverity());
		
		
		msgs.warn("mno");
		
		assertEquals(5, listener1.getMessageCount());
		assertEquals("mno", listener1.getNewestMessageText());
		assertEquals(Severity.WARNING, msgs.getHighestSeverity());
		
		
		msgs.error("pqr");
		
		assertEquals(6, listener1.getMessageCount());
		assertEquals("pqr", listener1.getNewestMessageText());
		assertEquals(Severity.ERROR, msgs.getHighestSeverity());
		
		
		msgs.fatal("stu");
		
		assertEquals(7, listener1.getMessageCount());
		assertEquals("stu", listener1.getNewestMessageText());
		assertEquals(Severity.FATAL, msgs.getHighestSeverity());
		
		
		
		msgs.pushContext("C1");
		ContextItem c2 = msgs.pushContext("C2");
		
		msgs.warn("vwx");
		
		assertEquals(8, listener1.getMessageCount());
		assertEquals("C1 -> C2", listener1.getNewestMessage().getContext().toString());

		assertEquals(Severity.FATAL, msgs.getHighestSeverity());
		
		
		c2.pop();

		assertEquals("C1 -> C2", listener1.getNewestMessage().getContext().toString());

		
		msgs.warn("yz");
		
		assertEquals(9, listener1.getMessageCount());
		assertEquals("C1", listener1.getNewestMessage().getContext().toString());
		
		
		
		assertEquals("Dummy: Dummy,by Dummy,NORMAL\nDummy2: Dummy2,by Dummy,NORMAL\nDummy3: Dummy3,by Dummy,NORMAL\nDummy4: Dummy4,by IDummy,NORMAL\nint: 42\n",
				msgs.info("Dummy: %s\nDummy2: %s\nDummy3: %s\nDummy4: %s\nint: %s\n",
						new Dummy(), new Dummy2(), new Dummy3(), new Dummy4(), 42)
					.getMessage());
		
		
		msgs.pushContext(new Dummy());
		
		assertEquals("C1 -> Dummy,by Dummy,SHORT", msgs.info("").getContext().toString());
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("before");
		msgs.info("").getContext().toLongString("-- ", sb);
		sb.append("\nafter");
		assertEquals("before\n-- in Dummy,by Dummy,LONG\n-- in C1\nafter", sb.toString());
	}

	@Test
	public void testHighestSeverityInContext() {
		Messages msgs = new Messages();
		
		assertEquals(Severity.NONE, msgs.getHighestSeverityInContext());
		
		ContextItem context1 = msgs.pushContext("abc");
		
		msgs.warn("blub");
		assertEquals(Severity.WARNING, msgs.getHighestSeverityInContext());

		
		ContextItem context2 = msgs.pushContext("def");

		assertEquals(Severity.NONE, msgs.getHighestSeverityInContext());
		
		msgs.error("blub2");
		assertEquals(Severity.ERROR, msgs.getHighestSeverityInContext());
		
		context2.pop();

		
		assertEquals(Severity.ERROR, msgs.getHighestSeverityInContext());
		
		context1.pop();

		assertEquals(Severity.ERROR, msgs.getHighestSeverityInContext());
		
		
		context1 = msgs.pushContext("ghi");
		
		assertEquals(Severity.NONE, msgs.getHighestSeverityInContext());
		
		context2 = msgs.pushContext("def");
		
		assertEquals(Severity.NONE, msgs.getHighestSeverityInContext());
		
		context2.pop();
		
		context1.pop();
		

		assertEquals(Severity.ERROR, msgs.getHighestSeverityInContext());
	}
}
