/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkitplus.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.jeelinkitplus.JeelinkITPlusBindingProvider;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
	

/**
 * Implement this class if you are going create an actively polling service
 * like querying a Website/Device.
 * 
 * @author AFranz
 * @since 1.4.0
 */
public class JeelinkITPlusBinding extends AbstractActiveBinding<JeelinkITPlusBindingProvider> implements ManagedService {

	private static final Logger logger = 
		LoggerFactory.getLogger(JeelinkITPlusBinding.class);

	
	/** 
	 * the refresh interval which is used to poll values from the JeelinkITPlus
	 * server (optional, defaults to 60000ms)
	 */
	
	
	private long refreshInterval = 5000;
	
	private SerialRunner serRunner;
	private Thread serThread;
	
	private String comPort;
	
	//Keeping the state
	private Map<String, DecimalType[]> sensorItems;
	

	public JeelinkITPlusBinding() {
	}
	public String getComPort() {
		return comPort;
	}


	public void setComPort(String comPort) {
		this.comPort = comPort;
	}

	
	public DecimalType getTemperature(String sensorID) {
		
		if(sensorItems.get(sensorID) == null){
			return null;
		}else{
			
			return sensorItems.get(sensorID)[0];
		}
		
		
	}


	public void setTemperature(DecimalType temp, String sensorID) {
		logger.debug("Setting Temperature of Sensor "+ sensorID +" to " + temp.doubleValue());
		if(sensorItems.get(sensorID) == null){
			//no Entry for Sensor found, creating
			DecimalType[] sensorInfo = new DecimalType[2];
			sensorInfo[0] = temp;
			sensorItems.put(sensorID, sensorInfo);
			
		}else{
			sensorItems.get(sensorID)[0] = temp;
			
		}
		
	
		
		
	
	}


	public DecimalType getHumidity(String sensorID) {
		if(sensorItems.get(sensorID) == null){
			return null;
		}else{
			
			return sensorItems.get(sensorID)[1];
		}
	}


	public void setHumidity(DecimalType humidity,String sensorID) {
		logger.debug("Setting Humidty of Sensor "+ sensorID +" to " + humidity.doubleValue());
		if(sensorItems.get(sensorID) == null){
			//no Entry for Sensor found, creating
			DecimalType[] sensorInfo = new DecimalType[2];
			sensorInfo[1] = humidity;
			sensorItems.put(sensorID, sensorInfo);
			
		}else{
			sensorItems.get(sensorID)[1] = humidity;
			
		}
	}


	


	public void activate() {
	//Init Serial Port
		sensorItems = new HashMap<String,DecimalType[]>();
		
		serRunner = new SerialRunner(this);
		serThread = new Thread(serRunner);
		serThread.start();
		
		logger.debug("Serial-Thread startet");
	
	}
	
	public void deactivate() {
		// deallocate resources here that are no longer needed and 
		// should be reset when activating this binding again
		serThread.stop();
		serRunner.serialPortDatenVerfuegbar();
		
	}

	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected String getName() {
		return "JeelinkITPlus Refresh Service";
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void execute() {
		if (!bindingsExist()) {
			logger.debug("There is no existing JeeLinkITPlus binding configuration => refresh cycle aborted!");
			return;
		}
		
		
				
		for (JeelinkITPlusBindingProvider provider : providers) {
			for (String itemName : provider.getItemNames()) {
				String sensID = provider.getSensorID(itemName);
				
				if(provider.getSensorType(itemName).equalsIgnoreCase("TEMP")){
					eventPublisher.postUpdate(itemName,getTemperature(sensID));	
					
				}else if(provider.getSensorType(itemName).equalsIgnoreCase("HUM")){
					eventPublisher.postUpdate(itemName,getHumidity(sensID));
				}
				
			}
		}
		logger.debug("execute() method is called!");
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");
	}
		
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException {
		if (config != null) {
			logger.debug("Jeelink: Reading Config");
			// to override the default refresh interval one has to add a 
			// parameter to openhab.cfg like <bindingName>:refresh=<intervalInMs>
			String refreshIntervalString = (String) config.get("refresh");
			if (StringUtils.isNotBlank(refreshIntervalString)) {
				refreshInterval = Long.parseLong(refreshIntervalString);
			}
			 this.comPort = (String) config.get("port");
			serRunner.setPortName(this.comPort);
			 logger.debug("DEBUG: COMPORT: " + this.comPort);
			 // read further config parameters here ...

			setProperlyConfigured(true);
		}
	}
	

}
