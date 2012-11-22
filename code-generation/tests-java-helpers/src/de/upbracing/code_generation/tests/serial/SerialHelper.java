package de.upbracing.code_generation.tests.serial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.upbracing.code_generation.tests.Toolkit;
import de.upbracing.code_generation.tests.streams.MonitoredInputStream;
import de.upbracing.code_generation.tests.streams.MonitoredOutputStream;

import gnu.io.NRSerialPort;

public class SerialHelper {
	/** default serial port */
	public static String DEFAULT_PORT = null;
	
	/** Gets the list of available RS232 ports.
	 * 
	 * @return list of available RS232 ports
	 */
	public static Set<String> getAvailablePorts() {
		return NRSerialPort.getAvailableSerialPorts();
	}
	
	private Toolkit toolkit;
	private NRSerialPort port;
	private MonitoredInputStream mIns;
	private MonitoredOutputStream mOut;
	private List<SerialBaudrateChangedListener> listeners 
		= new ArrayList<SerialBaudrateChangedListener>();

	/** Creates a new {@link SerialHelper} object
	 * 
	 * @param toolkit the toolkit that should be used to ask
	 *                for missing information
	 */
	public SerialHelper(Toolkit toolkit) {
		this.toolkit = toolkit;
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
		//TODO NRSerialPort prints to System.err - we should use toolkit.getMessages instead
		if (DEFAULT_PORT != null && !DEFAULT_PORT.trim().isEmpty()) {
			port = new NRSerialPort(DEFAULT_PORT, baud);
			if (port.connect())
				// successfull
				return true;
			else
				toolkit.getMessages().warn("default serial port '%s' couldn't be opened", DEFAULT_PORT);
		}
		
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
		
		setupStreams();
		
		return true;
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
		return mIns;
	}
	
	/** Returns the wrapped {@link MonitoredOutputStream} for this RS232 port.
	 * 
	 * @return wrapped {@link MonitoredOutputStream}
	 */
	public MonitoredOutputStream getOutputStream() {
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
}
