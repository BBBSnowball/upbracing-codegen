package de.upbracing.code_generation.tests.serial;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;

import de.upbracing.code_generation.Messages.ContextItem;
import de.upbracing.code_generation.tests.RichToolkit;
import de.upbracing.code_generation.tests.TestFailedException;
import de.upbracing.code_generation.tests.Toolkit;
import de.upbracing.code_generation.tests.context.RegexMatcher;
import de.upbracing.code_generation.tests.context.StringMatcher;
import de.upbracing.code_generation.tests.streams.MonitoredInputStream;
import de.upbracing.code_generation.tests.streams.MonitoredOutputStream;

import gnu.io.NRSerialPort;

public class SerialHelper {
	public interface SerialPortProvider {
		String[] getSerialPorts();
	}
	
	private SerialPortProvider default_ports;
	
	/** Gets the list of available RS232 ports.
	 * 
	 * @return list of available RS232 ports
	 */
	public static Set<String> getAvailablePorts() {
		return NRSerialPort.getAvailableSerialPorts();
	}
	
	private Toolkit toolkit;
	private RichToolkit rich_toolkit;
	private int port_no;
	private NRSerialPort port;
	private MonitoredInputStream mIns;
	private MonitoredOutputStream mOut;
	private List<SerialBaudrateChangedListener> listeners 
		= new ArrayList<SerialBaudrateChangedListener>();

	/** Creates a new {@link SerialHelper} object
	 * 
	 * @param toolkit the toolkit that should be used to ask
	 *                for missing information
	 * @param port_no 0 is the first serial port, 1 is the second
	 */
	public SerialHelper(Toolkit toolkit, int port_no) {
		this.toolkit = toolkit;
		this.port_no = port_no;
	}
	
	/** Insert RichToolkit
	 * 
	 * This must be done after instantiation because RichToolkit cannot
	 * influence the creation of this object and Toolkit doesn't know
	 * about RichToolkit
	 */
	public void setRichToolkit(RichToolkit rich_toolkit, SerialPortProvider ports) {
		if (toolkit == null || rich_toolkit.getInner() != toolkit)
			throw new IllegalArgumentException("The RichToolkit must match the Toolkit");
		
		this.rich_toolkit = rich_toolkit;
		this.default_ports = ports;
	}
	
	/** Release resources used by this object. You mustn't
	 *  use the object after calling this method.
	 */
	public void dispose() {
		closeStreams();
		port = null;
	}
	
	/** Connect to serial port
	 * 
	 * @param baud baud rate
	 */
	private boolean init(int baud) {
		boolean connected = false;
		
		//TODO NRSerialPort prints to System.err - we should use toolkit.getMessages instead
		String[] default_ports = null;
		if (this.default_ports != null)
			default_ports = this.default_ports.getSerialPorts();
		if (default_ports != null && default_ports[port_no] != null 
				&& !default_ports[port_no].trim().isEmpty()) {
			port = new NRSerialPort(default_ports[port_no], baud);
			if (port.connect())
				// successfull
				connected = true;
			else
				toolkit.getMessages().warn("default serial port '%s' couldn't be opened", default_ports[port_no]);
		}
		
		if (!connected) {
			do {
				SortedSet<String> ports = new TreeSet<String>(NRSerialPort.getAvailableSerialPorts());
				ports.add("rescan");
				ports.add("other");
				String portname = toolkit.askOptions("Please select a serial port or 'rescan' to update the list.", null, ports.toArray(new String[ports.size()]));
				
				if (portname.equals("rescan"))
					continue;
				else if (portname.equals("other"))
					portname = toolkit.ask("Serial port:", null);
				
				port = new NRSerialPort(portname, baud);
				if (!port.connect()) {
					toolkit.getMessages().error("Couldn't open serial port '%s'");
				}
			} while (port == null || !port.isConnected());
		}
		
		if (connected)
			setupStreams();
		
		return connected;
	}
	
	/** Creates a new {@link SerialHelper} object, opens the specified port.
	 * 
	 * @param portName
	 * @param baud
	 */
	public SerialHelper(String portName, int baud) {
		port = new NRSerialPort(portName, baud);
		port.connect();
		setupStreams();
	}
	
	private void setupStreams() {
		mIns = new MonitoredInputStream(port.getInputStream());
		mOut = new MonitoredOutputStream(port.getOutputStream());
		
		fireBaudrateChanged();
	}
	
	private void closeStreams() {
		if (mIns != null) {
			//TODO read data in buffer (at least part of it) to give the monitors a chance to display it
			
			try {
				mIns.close();
			} catch (IOException e) {
				if (toolkit != null)
					toolkit.getMessages().error(e);
				else
					e.printStackTrace();
			}
			
			mIns = null;
		}
		
		if (mOut != null) {
			try {
				mOut.close();
			} catch (IOException e) {
				if (toolkit != null)
					toolkit.getMessages().error(e);
				else
					e.printStackTrace();
			}
			
			mOut = null;
		}

		if (port != null)
			port.disconnect();
	}
	
	/** Returns the wrapped {@link MonitoredInputStream} for this RS232 port.
	 * 
	 * @return wrapped {@link MonitoredInputStream}
	 */
	public MonitoredInputStream getInputStream() {
		if (mIns == null)
			throw new IllegalStateException("not open! call ensureBaudrate first");
		return mIns;
	}
	
	/** Returns the wrapped {@link MonitoredOutputStream} for this RS232 port.
	 * 
	 * @return wrapped {@link MonitoredOutputStream}
	 */
	public MonitoredOutputStream getOutputStream() {
		if (mOut == null)
			throw new IllegalStateException("not open! call ensureBaudrate first");
		return mOut;
	}
	
	/** Make sure that the serial port is opened and set to the right baud rate
	 * 
	 * @param baud required baud rate
	 * @return true, if successful; false, in case of an error
	 */
	public boolean ensureBaudrate(int baud) {
		if (port == null)
			return init(baud);
		else if (port.getBaud() != baud) {
			// Reopen with new Baudrate
			closeStreams();
			
			port.setBaud(baud);
			if (!port.connect()) {
				if (toolkit != null) {
					toolkit.getMessages().error("Couldn't open serial port with %d baud", baud);
					toolkit.waitForUser("serial error");
				}
				return false;
			}
			
			setupStreams();
		}
		
		return true;
	}

	private synchronized void fireBaudrateChanged() {
		// fire event
		for (SerialBaudrateChangedListener l : listeners) {
			l.handleBaudrateChanged(this);
		}
	}
	
	public synchronized void addBaudrateChangedListener(SerialBaudrateChangedListener l) {
		listeners.add(l);
	}
	public synchronized void removeBaudrateChangedListener(SerialBaudrateChangedListener l) {
		listeners.remove(l);
	}
	
	
	public void expectString(String expected, int timeout_millis) throws TestFailedException {
		// create a matcher
		StringMatcher m = new StringMatcher(this, expected);
		
		// put the matcher into the context, if possible
		ContextItem context = null;
		if (toolkit != null)
			context = toolkit.getMessages().pushContext(m);
		
		try {
			// run matcher
			m.run(timeout_millis);
		} finally {
			// remove it from context (if we pushed it)
			if (context != null)
				context.pop();
		}
	}
	
	public void expectString(byte[] expected, int timeout_millis) throws TestFailedException {
		// create a matcher
		StringMatcher m = new StringMatcher(this, expected);
		
		// put the matcher into the context, if possible
		ContextItem context = null;
		if (toolkit != null)
			context = toolkit.getMessages().pushContext(m);
		
		try {
			// run matcher
			m.run(timeout_millis);
		} finally {
			// remove it from context (if we pushed it)
			if (context != null)
				context.pop();
		}
	}

	// default timeout: 30 sec
	public void expectString(String expected) throws TestFailedException {
		expectString(expected, 30*1000);
	}

	// default timeout: 30 sec
	public void expectString(byte[] expected) throws TestFailedException {
		expectString(expected, 30*1000);
	}

	private static byte[] readStream(InputStream stream, int length) throws IOException {
		byte buf[] = new byte[length];
		int i = 0;
		int len;
		while (i < length && (len = stream.read(buf, 0, length-i)) > 0)
			i += len;
		if (i < length)
			throw new IOException("premature end of stream");
		return buf;
	}

	public void expectFile(String filename, int timeout_millis) throws IOException, TestFailedException {
		if (rich_toolkit != null)
			filename = rich_toolkit.makeAbsolute(filename);
		
		File f = new File(filename);
		byte[] expected = readStream(new FileInputStream(f), (int)f.length());
		
		expectString(expected, timeout_millis);
	}

	// default timeout: 30 sec
	public void expectFile(String filename) throws IOException, TestFailedException {
		expectFile(filename, 30*1000);
	}

	public boolean isOpen() {
		return mIns != null && mOut != null;
	}
	
	public void write(String str) throws IOException {
		getOutputStream().write(str.getBytes());
	}
	
	public Matcher expectRegex(String expected, int timeout_millis) throws TestFailedException {
		// create a matcher
		RegexMatcher m = new RegexMatcher(this, expected);
		
		// put the matcher into the context, if possible
		ContextItem context = null;
		if (toolkit != null)
			context = toolkit.getMessages().pushContext(m);
		
		try {
			// run matcher
			return m.run(timeout_millis);
		} finally {
			// remove it from context (if we pushed it)
			if (context != null)
				context.pop();
		}
	}

	public Matcher expectRegex(String regex) throws TestFailedException {
		return expectRegex(regex, 30*1000);
	}
}
