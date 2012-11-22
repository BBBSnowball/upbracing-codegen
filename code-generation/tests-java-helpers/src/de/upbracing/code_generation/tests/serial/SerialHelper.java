package de.upbracing.code_generation.tests.serial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.upbracing.code_generation.tests.Toolkit;
import de.upbracing.code_generation.tests.streams.MonitoredInputStream;
import de.upbracing.code_generation.tests.streams.MonitoredOutputStream;

import gnu.io.NRSerialPort;

public class SerialHelper {
	
	public static String DEFAULT_PORT = null;
	
	/** Gets the list of available RS232 ports.
	 * 
	 * @return list of available RS232 ports
	 */
	public static Set<String> getAvailablePorts() {
		return NRSerialPort.getAvailableSerialPorts();
	}
	
	private NRSerialPort port;
	private MonitoredInputStream mIns;
	private MonitoredOutputStream mOut;
	private List<SerialBaudrateChangedListener> listeners 
		= new ArrayList<SerialBaudrateChangedListener>();
	
	public SerialHelper(Toolkit tk, int baud) {
		// TODO: Implement me
	}
	
	/** Creates a new {@link SerialHelper} object, opens the DEFAULT_PORT.
	 * 
	 * @param baud Baud-Rate
	 * @throws Exception thrown, if no DEFAULT_PORT was set.
	 */
	public SerialHelper(int baud) throws Exception {
		if (DEFAULT_PORT == null || DEFAULT_PORT.trim().length() == 0) {
			throw new Exception("No default port set!");
		}
		
		port = new NRSerialPort(DEFAULT_PORT, baud);
		setupStreams();
		port.connect();
	}
	
	/** Creates a new {@link SerialHelper} object, opens the specified port.
	 * 
	 * @param portName
	 * @param baud
	 */
	public SerialHelper(String portName, int baud) {
		
		port = new NRSerialPort(portName, baud);
		setupStreams();
		port.connect();
	}
	
	private void setupStreams() {
		mIns = new MonitoredInputStream(port.getInputStream());
		mOut = new MonitoredOutputStream(port.getOutputStream());
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
	
	public void ensureBaudrate(int baud) throws IOException {
		if (port.getBaud() != baud) {
			// Reopen with new Baudrate
			mIns.close();
			mOut.close();
			port.disconnect();
			port.setBaud(baud);
			setupStreams();
			port.connect();
			
			// fire event
			for (SerialBaudrateChangedListener l : listeners) {
				l.handleBaudrateChanged(this);
			}
		}
	}
	
	public synchronized void addBaudrateChangedListener(SerialBaudrateChangedListener l) {
		listeners.add(l);
	}
	public synchronized void removeBaudrateChangedListener(SerialBaudrateChangedListener l) {
		listeners.remove(l);
	}
}
