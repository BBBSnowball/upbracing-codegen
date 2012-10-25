package de.upbracing.code_generation;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.upbracing.code_generation.utils.Util;

/**
 * Helper class for keeping track of messages (e.g. errors and warnings) during validation and code-generation
 * 
 * @author Benjamin
 */
public class Messages {
	private static final String NL = Util.nl();
	
	/** severity of a message */
	public static enum Severity {
		// NONE is not allowed for a message
		NONE(null, null),
		
		TRACE  ("TRACE: ", "// "),
		DEBUG  ("DEBUG: ", "// "),
		INFO   ("INFO:  ", "// "),
		WARNING("WARN:  ", "#warning "),
		ERROR  ("ERROR: ", "#error "),
		FATAL  ("FATAL: ", "#error ");
		
		public static final Severity LOWEST = TRACE;
		public static final Severity HIGHEST = FATAL;
		
		private String normal_prefix, code_prefix;
		
		private Severity(String normal_prefix, String code_prefix) {
			this.normal_prefix = normal_prefix;
			this.code_prefix = code_prefix;
		}

		public String getPrefix() {
			return normal_prefix;
		}

		public String getCodePrefix() {
			return code_prefix;
		}
	}
	
	/** context of a message, e.g. the for which validation has failed */
	public class Context {
		private List<Object> context;

		private Context(List<Object> context) {
			this.context = Collections.unmodifiableList(new ArrayList<Object>(context));
		}

		public List<Object> getContext() {
			return context;
		}
		
		public List<String> getFormattedContext() {
			List<String> strings = new ArrayList<String>(context.size());
			for (Object obj : context) {
				Object fmt = formatLong(obj);
				if (fmt != null)
					strings.add(obj.toString());
			}
			return strings;
		}
		
		public void toString(StringBuffer sb) {
			boolean first = true;
			for (Object obj : context) {
				Object fmt = formatShort(obj);
				if (fmt == null)
					continue;
				
				if (first)
					first = false;
				else
					sb.append(" -> ");
				
				sb.append(fmt);
			}
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			toString(sb);
			return sb.toString();
		}
		
		public void toLongString(String indent, StringBuffer sb) {
			for (int i=context.size()-1;i>=0;i--) {
				Object obj = context.get(i);
				
				Object fmt = formatLong(obj);
				if (fmt == null)
					continue;
				
				sb.append(NL + indent + "in ");

				sb.append(fmt);
			}
		}

		public boolean isEmpty() {
			return context.isEmpty();
		}
		
		public boolean isParentOf(Context child) {
			if (this.context.size() > child.context.size())
				// nesting level of c is not deep enough
				// -> c cannot be the child
				return false;
			
			// all items must be the same, but this
			// context may have fewer entries
			Iterator<Object> it_parent = this.context.iterator();
			Iterator<Object> it_child  = child.context.iterator();
			while (it_parent.hasNext() && it_child.hasNext()) {
				if (it_parent.next() != it_child.next())
					// not the same -> not a child
					return false;
			}
			
			if (!it_parent.hasNext())
				// parent doesn't have any more entries and the
				// child had matching entries up to this point
				// -> it is a child
				return true;
			else
				// loop ended due to another reason
				// -> parent has more entries than child
				// -> it cannot be its parent
				// (Should be handled above, but we do it again
				//  to make sure that it always works.)
				return false;
		}
	}
	
	/** a message with context and severity */
	public static class Message {
		private Severity severity;
		private Context context;
		private String message;
		
		public Message(Severity severity, Context context, String message) {
			super();
			this.severity = severity;
			this.context = context;
			this.message = message;
		}
		
		public Severity getSeverity() {
			return severity;
		}
		
		public Context getContext() {
			return context;
		}
		
		public String getMessage() {
			return message;
		}

		public void formatForCode(StringBuffer sb) {
			final String NL = System.getProperties().getProperty("line.separator");
			
			sb.append(NL);
			sb.append(NL);
			
			sb.append(getSeverity().getCodePrefix());
			
			sb.append(getMessage());
			
			if (!getContext().isEmpty()) {
				sb.append(" in context ");
				getContext().toString(sb);
			}
			
			sb.append(NL);
		}
		
		public void format(StringBuffer sb) {
			sb.append(getSeverity().getPrefix());
			
			sb.append(getMessage());
			
			getContext().toLongString("\t", sb);
			
			sb.append(NL);
		}
	}
	
	/** a message listener */
	public interface MessageListener {
		/** called for each message */
		void message(Message msg);
	}
	
	/** format an object of type T to a string */
	public interface ObjectFormatter<T> {
		/** short format, e.g. for one-line context */
		public final int SHORT  = 1;
		/** normal format, e.g. in an error message */
		public final int NORMAL = 2;
		/** long format, e.g. in a multi-line context */
		public final int LONG   = 3;
		
		/** format an object
		 * 
		 * Return null, if the object should be ignored. It might
		 * be printed as "null", if that is not supported by the
		 * caller.
		 * 
		 * @param type one of SHORT, NORMAL or LONG
		 * @param obj the object to format
		 * @return object as a string or null
		 */
		String format(int type, T obj);
	}

	/** can be used to remove an item from the context, returned by {@link #pushContext(Object)} */
	public interface ContextItem {
		/** remove this item from the context */
		void pop();
	}
	
	private List<Message> messages = new LinkedList<Message>();
	private LinkedList<Object> context = new LinkedList<Object>();
	private List<MessageListener> listeners = new LinkedList<MessageListener>();
	private Map<Class<?>, ObjectFormatter<Object>> formatters
		= new HashMap<Class<?>, ObjectFormatter<Object>>();
	
	/** add a message listener */
	public void addMessageListener(MessageListener listener) {
		listeners.add(listener);
	}
	
	/** remove a message listener */
	public void removeMessageListener(MessageListener listener) {
		listeners.remove(listener);
	}
	
	/** add an object formatter
	 * 
	 * @param forClass the class or interface that this formatter can handle
	 * @param formatter the formatter
	 */
	@SuppressWarnings("unchecked")
	public <T> void addObjectFormatter(Class<T> forClass, ObjectFormatter<? super T> formatter) {
		formatters.put(forClass, (ObjectFormatter<Object>) formatter);
	}
	
	/** format an object
	 * 
	 * @param type one of SHORT, NORMAL or LONG
	 * @param obj the object to format
	 * @return object as a string, the object itself or null
	 */
	public Object format(int type, Object obj) {
		for (Class<?> cls : getAllSupertypes(obj.getClass())) {
			ObjectFormatter<Object> formatter = this.formatters.get(cls);
			if (formatter != null)
				return formatter.format(type, obj);
		}
		
		return obj;
	}
	
	/** format an object with SHORT format
	 * 
	 * @param obj the object to format
	 * @return object as a string, the object itself or null
	 */
	public Object formatShort(Object obj) {
		return format(ObjectFormatter.SHORT, obj);
	}
	
	/** format an object with NORMAL format
	 * 
	 * @param obj the object to format
	 * @return object as a string, the object itself or null
	 */
	public Object formatNormal(Object obj) {
		return format(ObjectFormatter.NORMAL, obj);
	}
	
	/** format an object with LONG format
	 * 
	 * @param obj the object to format
	 * @return object as a string, the object itself or null
	 */
	public Object formatLong(Object obj) {
		return format(ObjectFormatter.LONG, obj);
	}

	/** get a list of supertypes and interfaces
	 * 
	 * The list includes the class itself. A type can appear
	 * in the list more than once.
	 * 
	 * @param cls the class
	 * @return list of supertypes and interfaces
	 */
	// This is public, so it can be tested individually.
	public static Collection<Class<?>> getAllSupertypes(Class<?> cls) {
		List<Class<?>> types1 = new LinkedList<Class<?>>();
		
		// add the class and all supertypes
		do {
			types1.add(cls);
			cls = cls.getSuperclass();
		} while (cls != null);
		
		// add all interfaces implemented by the class itself and its superclasses and interfaces
		List<Class<?>> types2 = new LinkedList<Class<?>>();
		List<Class<?>> all_types = new LinkedList<Class<?>>();
		all_types.addAll(types1);
		do {
			types2.clear();
			
			for (Class<?> type : types1) {
				types2.addAll(Arrays.asList(type.getInterfaces()));
			}
			
			all_types.addAll(types2);
			
			types1.clear();
			types1.addAll(types2);
		} while (!types1.isEmpty());
		
		return all_types;
	}
	
	/** add a message and relay it to the listeners
	 * 
	 * @param severity severity of the message
	 * @param message the message text
	 * @return the message object
	 */
	public Message addMessage(Severity severity, String message) {
		if (severity == Severity.NONE)
			throw new IllegalArgumentException("Severity NONE is not allowed");
		
		Message msg = new Message(severity, new Context(context), message);
		messages.add(msg);
		
		for (MessageListener listener : listeners)
			listener.message(msg);
		
		return msg;
	}
	
	/** format the message text and add it to the list using {@link #addMessage(Severity, String)}
	 * 
	 * @param severity severity of the message
	 * @param format message template with special characters, see {@link String#format(String, Object...)
	 * @param args arguments for the format string
	 * @return the message object
	 */
	public Message addMessage(Severity severity, String format, Object... args) {
		for (int i=0;i<args.length;i++) {
			if (args[i] != null)
				args[i] = formatNormal(args[i]);
		}
		
		String message = String.format(format, args);
		
		return addMessage(severity, message);
	}

	/** format and add a message with severity TRACE
	 * 
	 * @param format message template with special characters, see {@link String#format(String, Object...)
	 * @param args arguments for the format string
	 * @return the message object
	 */
	public Message trace(String format, Object... args) {
		return addMessage(Severity.TRACE, format, args);
	}

	/** format and add a message with severity DEBUG
	 * 
	 * @param format message template with special characters, see {@link String#format(String, Object...)
	 * @param args arguments for the format string
	 * @return the message object
	 */
	public Message debug(String format, Object... args) {
		return addMessage(Severity.DEBUG, format, args);
	}

	/** format and add a message with severity INFO
	 * 
	 * @param format message template with special characters, see {@link String#format(String, Object...)
	 * @param args arguments for the format string
	 * @return the message object
	 */
	public Message info(String format, Object... args) {
		return addMessage(Severity.INFO, format, args);
	}

	/** format and add a message with severity WARNING
	 * 
	 * @param format message template with special characters, see {@link String#format(String, Object...)
	 * @param args arguments for the format string
	 * @return the message object
	 */
	public Message warn(String format, Object... args) {
		return addMessage(Severity.WARNING, format, args);
	}

	/** format and add a message with severity ERROR
	 * 
	 * @param format message template with special characters, see {@link String#format(String, Object...)
	 * @param args arguments for the format string
	 * @return the message object
	 */
	public Message error(String format, Object... args) {
		return addMessage(Severity.ERROR, format, args);
	}

	/** format and add a message with severity FATAL
	 * 
	 * @param format message template with special characters, see {@link String#format(String, Object...)
	 * @param args arguments for the format string
	 * @return the message object
	 */
	public Message fatal(String format, Object... args) {
		return addMessage(Severity.FATAL, format, args);
	}
	
	/** append to context
	 * 
	 * @param context_item the item to append
	 */
	public ContextItem pushContext(final Object context_item) {
		context.addLast(context_item);
		
		final int context_size = context.size();
		
		return new ContextItem() {
			@Override
			public void pop() {
				if (context.isEmpty())
					throw new IllegalStateException("context is empty");
				
				// This test is necessary, if an item can be on the stack
				// more than once. Furthermore, the result is more useful
				// for the user.
				if (context_size != context.size())
					throw new IllegalStateException("Context has "
							+ context.size() + " items, but we expected "
							+ context_size + ".");
				
				Object top = context.getLast();
				if (top != context_item)
					throw new IllegalStateException("You tried to pop the wrong item");
				
				context.removeLast();
			}
		};
	}
	
	/** remove and return inner-most item from context
	 * 
	 * @deprecated use {@link ContextItem#pop()} instead
	 * 
	 * @return the removed item
	 */
	@Deprecated
	public Object popContext() {
		return context.removeLast();
	}
	
	/** return a list of all messages
	 * 
	 * @return the list
	 */
	public List<Message> getMessages() {
		return Collections.unmodifiableList(messages);
	}
	
	/** return a list of messages with minimum severity
	 * 
	 * @param severity minimum severity of messages
	 * @return the list
	 */
	public List<Message> getMessages(Severity severity) {
		List<Message> list = new LinkedList<Messages.Message>();
		for (Message msg : messages) {
			if (msg.getSeverity().ordinal() >= severity.ordinal())
				list.add(msg);
		}
		return list;
	}
	
	/** get highest severity that occurs in the list of messages
	 * 
	 * @return the highest severity or NONE, if the list is empty
	 */
	public Severity getHighestSeverity() {
		Severity highest = Severity.NONE;
		
		for (Message msg : messages) {
			if (msg.getSeverity().ordinal() > highest.ordinal())
				highest = msg.getSeverity();
		}
		
		return highest;
	}
	
	/** get highest severity that occurs in the list of messages that have the current context
	 * 
	 * @return the highest severity or NONE, if the list is empty
	 */
	public Severity getHighestSeverityInContext() {
		Severity highest = Severity.NONE;
		
		Context current_context = new Context(context);
		for (Message msg : messages) {
			if (current_context.isParentOf(msg.getContext()) && msg.getSeverity().ordinal() > highest.ordinal())
				highest = msg.getSeverity();
		}
		
		return highest;
	}

	/**
	 * Format a list of messages
	 * 
	 * @return a string with all warnings
	 */
	public void summarizeForCode(StringBuffer sb) {
		Severity highest_severity = getHighestSeverity();
		
		List<Message> warnings_and_errors = getMessages(Severity.WARNING);
		int count = warnings_and_errors.size();
		
		if (count <= 0)
			return;
		
		sb.append(NL + NL);
		
		sb.append(highest_severity.getCodePrefix());
		
		if (count == 1)
			sb.append("There was one warning or error" + NL + "/*");
		else
			sb.append("There were " + count + " warnings and/or errors" + NL + "/*" + NL);
		
		for (Message msg : warnings_and_errors) {
			msg.format(sb);
		}
		
		sb.append("*/");
	}
	
	/** from now on print messages to a stream with high severity
	 * 
	 * @param stream the stream to print to
	 * @param minimum_severity lowest severity that should be printed
	 * @return itself, for use in fluent style
	 */
	public Messages withOutputTo(final PrintStream stream, final Severity minimum_severity) {
		addMessageListener(new MessageListener() {
			@Override
			public void message(Message msg) {
				if (msg.getSeverity().ordinal() < minimum_severity.ordinal())
					return;
				
				StringBuffer sb = new StringBuffer();
				msg.format(sb);
				stream.println(sb.toString());
			}
		});
		
		return this;
	}
	
	/** from now on print all messages to a stream
	 * 
	 * @param stream the stream to print to
	 * @return itself, for use in fluent style
	 */
	public Messages withOutputTo(final PrintStream stream) {
		return withOutputTo(stream, Severity.LOWEST);
	}
	
	/** add a message listener, fluent interface 
	 * @return itself
	 */
	public Messages withMessageListener(MessageListener listener) {
		addMessageListener(listener);
		return this;
	}
	
	/** add an object formatter, fluent interface
	 * 
	 * @param forClass the class or interface that this formatter can handle
	 * @param formatter the formatter
	 * @return itself
	 */
	public <T> Messages withObjectFormatter(Class<T> forClass, ObjectFormatter<? super T> formatter) {
		addObjectFormatter(forClass, formatter);
		return this;
	}

	/** is the list of messages empty?
	 * 
	 * @return true, if there are no messages; false, otherwise
	 */
	public boolean isEmpty() {
		return messages.isEmpty();
	}
}
