package org.openhab.binding.jeelinkitplus.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class SerialRunner implements Runnable {

	private static final Logger logger = 
			LoggerFactory.getLogger(JeelinkITPlusBinding.class);
	
	
	CommPortIdentifier serialPortId;
	Enumeration enumComm;
	SerialPort serialPort;
	// OutputStream outputStream;
	InputStream inputStream;
	Boolean serialPortGeoeffnet = false;

	int baudrate = 57600;
	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	String portName = "COM3";

	int secondsRuntime = 60;
	private JeelinkITPlusBinding binding;

	public SerialRunner(JeelinkITPlusBinding jeelinkITPlusBinding) {
		this.binding = jeelinkITPlusBinding;
		
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	@Override
	public void run() {
		Integer secondsRemaining = secondsRuntime;
		if (oeffneSerialPort(portName) != true)
			return;

		while (secondsRemaining > 0) {

			//secondsRemaining--;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		schliesseSerialPort();

	}

	boolean oeffneSerialPort(String portName) {
		Boolean foundPort = false;
		if (serialPortGeoeffnet != false) {
			logger.info("Serialport bereits geöffnet");
			return false;
		}
		logger.info("Öffne Serialport " + portName);
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			if (portName.contentEquals(serialPortId.getName())) {
				foundPort = true;
				break;
			}
		}
		if (foundPort != true) {
			logger.info("Serialport nicht gefunden: " + portName);
			return false;
		}
		try {
			serialPort = (SerialPort) serialPortId.open("Öffnen und Senden",
					500);
		} catch (PortInUseException e) {
			logger.info("Port belegt");
		}
		/*
		 * try { outputStream = serialPort.getOutputStream(); } catch
		 * (IOException e) {
		 * logger.info("Keinen Zugriff auf OutputStream"); }
		 */
		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
			logger.info("Keinen Zugriff auf InputStream");
		}
		try {
			serialPort.addEventListener(new serialPortEventListener());
		} catch (TooManyListenersException e) {
			logger.info("TooManyListenersException für Serialport");
		}
		serialPort.notifyOnDataAvailable(true);
		try {
			serialPort
					.setSerialPortParams(baudrate, dataBits, stopBits, parity);
		} catch (UnsupportedCommOperationException e) {
			logger.info("Konnte Schnittstellen-Paramter nicht setzen");
		}

		serialPortGeoeffnet = true;
		return true;
	}

	void schliesseSerialPort() {
		if (serialPortGeoeffnet == true) {
			logger.info("Schließe Serialport");
			serialPort.close();
			serialPortGeoeffnet = false;
		} else {
			logger.info("Serialport bereits geschlossen");
		}
	}

	void serialPortDatenVerfuegbar() {
		logger.info("Serial Interface is read");
		try {
			byte[] data = new byte[150];
			int num;
			while (inputStream.available() > 0) {
				num = inputStream.read(data, 0, data.length);

				// Konvert to String
				//String serialInp = new String(data, 0, num);
				
				JeelinkITPlusResponse response = new JeelinkITPlusResponse(data);
				if(response.isValid){
					binding.setTemperature(new DecimalType(response.getTemperature()), response.getSensorID());
					binding.setHumidity((new DecimalType(response.getHumidity())),response.getSensorID());
					
					
					
				}
				// Split in Lines
//				String[] seriallines = serialInp.split("\n");
				// Now get ID, Temp + Humidity
//				int sensorID = -1;
//				double temp = 99.0;
//				double hum = 100.0;

//				for (int i = 0; i < seriallines.length; i++) {
//					String line = seriallines[i];
//					String[] splittedLine = line.split(":");
//					if (splittedLine.length == 2) {
//						if (splittedLine[0].equalsIgnoreCase("ID")) {
//							binding.setSensorID(new StringType(splittedLine[1]));
//
//						} else if (splittedLine[0].equalsIgnoreCase("TEMP")) {
//							binding.setTemperature(new DecimalType(Double
//									.parseDouble(splittedLine[1])));
//
//							temp = Double.parseDouble(splittedLine[1]);
//
//						} else if (splittedLine[0].equalsIgnoreCase("HUM")) {
//
//							binding.setHumidity(new DecimalType(Double
//									.parseDouble(splittedLine[1])));
//						}
//
//					}
//
//				}
			}
		} catch (IOException e) {
			logger.info("Fehler beim Lesen empfangener Daten");
		}
	}

	class serialPortEventListener implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {

			switch (event.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				serialPortDatenVerfuegbar();
				break;
			case SerialPortEvent.BI:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.FE:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			case SerialPortEvent.PE:
			case SerialPortEvent.RI:
			default:
			}
		}
	}

}
