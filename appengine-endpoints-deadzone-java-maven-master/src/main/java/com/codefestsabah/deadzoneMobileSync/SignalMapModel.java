package com.codefestsabah.deadzoneMobile;

public class SignalMapModel {
	private String operator;
	private String network;
	private String timeStamp;
	private float latitude;
	private float longitude;
	private float signalStrength;
	
	public SignalMapModel() {};
	
	public String getOperator() {
		return operator;
	}
	
	public String getNetwork() {
		return network;
	}
	
	public String getTimestamp() {
		return timeStamp;
	}
	
	public String getLatitude() {
		return Float.toString(latitude);
	}
	
	public String getLongitude() {
		return Float.toString(longitude);
	}
	
	public String getSignalStrength() {
		return Float.toString(signalStrength);
	}
	
	public void setOperator(String value) {
		operator = value;
	}
	
	public void setNetwork(String value) {
		network = value;
	}

	public void setTimestamp(String value) {
		timeStamp = value;
	}	
	
	public void setLatitude(float value) {
		latitude = value;
	}
	
	public void setLongitude(float value) {
		longitude = value;
	}
	
	public void setSignalStrength(float value) {
		signalStrength = value;
	}
}
