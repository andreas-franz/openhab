package org.openhab.binding.jeelinkitplus.internal;



public class JeelinkITPlusResponse {

	double temperature;
	double humidity;
	String sensorID;
	boolean hasNewBatterie;
	byte[] serialData;
	boolean isValid = false;

	public JeelinkITPlusResponse(byte[] serialData) {
		super();
		this.serialData = serialData;
		initalize();
	}

	private void initalize() {
		String serialString = new String(serialData).trim();
		/*
		 * String received from serial interface should look like this:
		 * Id: 11 - lcId: 44 R Temp: 21.8 Hygro: 53%
		 * 
		 */
		
		if (serialString.startsWith("Id") && serialString.endsWith("%")) {
			
			
			
			System.out.println("Start evaluation Serial data");
			try {
				//Setting Humidty
				setHumidity(Double.parseDouble(serialString.substring(serialString.length()-3, serialString.length()-1)));
				
				//Setting Sensor ID
				
				
				setSensorID(serialString.substring(serialString.indexOf("lcId: ") + 6, serialString.indexOf("lcId: ") + 8));
				//Set Temp
				setTemperature(Double.parseDouble(serialString.substring(serialString.indexOf("Temp: ")+6, serialString.indexOf("Temp: ")+10)));
			} catch (Exception e) {
				setValid(false);
			}
			
			
			
//			if (serialElements.length == 10) {
//				//Setting Humidity (easy one)
//				setHumidity(Double.parseDouble(serialElements[6]));
//				//Setting sensorID 
//				setSensorID(Integer.parseInt(serialElements[2]));
//				//Unclear how battery settings are uses
//				setHasNewBatterie(false);
//				//getting Temperature
//				byte[] tempBytes = new byte[4];
//				tempBytes[0] = serialElements[4].getBytes()[0];
//				
//				
//
//				
//
//				
//				System.arraycopy(serialElements[5].getBytes(), 0, tempBytes, 1, 3);
//				ByteBuffer buffer = ByteBuffer.wrap(tempBytes);
//				buffer.order(ByteOrder.BIG_ENDIAN);
//				int result = buffer.getShort();
//				System.out.println("Integer:" + result);
//				System.out.println(new String(tempBytes) );
//				ByteBuffer buffer = ByteBuffer.wrap(tempBytes);
//				float second = buffer.getFloat();
//				System.out.println("Second:"+ second);
			//	int first = buffer.getInt();
				
				//System.out.println("First: " + first);
				
//			}
			setValid(true);
		}else{
			setValid(false);
			
			
		}
				

	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public String getSensorID() {
		return sensorID;
	}

	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
	}

	public boolean isHasNewBatterie() {
		return hasNewBatterie;
	}

	public void setHasNewBatterie(boolean hasNewBatterie) {
		this.hasNewBatterie = hasNewBatterie;
	}

	public byte[] getSerialData() {
		return serialData;
	}

	public void setSerialData(byte[] serialData) {
		this.serialData = serialData;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

}
