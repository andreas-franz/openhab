/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkitplus.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openhab.binding.jeelinkitplus.JeelinkITPlusBindingProvider;

import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author AFranz
 * @since 1.4.0
 */
public class JeelinkITPlusGenericBindingProvider extends AbstractGenericBindingProvider implements JeelinkITPlusBindingProvider {
	
	
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "jeelinkitplus";
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		if (!(item instanceof NumberItem)) {
			throw new BindingConfigParseException("item '" + item.getName()
					+ "' is of type '" + item.getClass().getSimpleName()
					+ "', only Switch- and DimmerItems are allowed - please check your *.items configuration");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);
		JeelinkITPlusBindingConfig config = new JeelinkITPlusBindingConfig();
		
		//Check if TYPE and ID are Set
		String[] configParts = bindingConfig.trim().split(":");
		if (configParts.length != 2) {
			throw new BindingConfigParseException("JeeLinkITPlus Binding Configuration must contain 2 Parts");
		}
		config.setSensorID(configParts[0]);
		config.setSensorType(configParts[1]);
		
		addBindingConfig(item, config);		
		
		
		
		
		
	}
	
	
	class JeelinkITPlusBindingConfig implements BindingConfig {
		
		//HUM / TEMP
		private String sensorType; 
		private String sensorID;
		public String getSensorType() {
			return sensorType;
		}
		public void setSensorType(String sensorType) {
			this.sensorType = sensorType;
		}
		public String getSensorID() {
			return sensorID;
		}
		public void setSensorID(String sensorID) {
			this.sensorID = sensorID;
		}
		
	}


	@Override
	public String getSensorType(String itemName) {
		JeelinkITPlusBindingConfig config = (JeelinkITPlusBindingConfig) bindingConfigs.get(itemName);
		return config.getSensorType();
	}

	@Override
	public String getSensorID(String itemName) {
		JeelinkITPlusBindingConfig config = (JeelinkITPlusBindingConfig) bindingConfigs.get(itemName);
		return config.getSensorID();
	}
	
	
}
