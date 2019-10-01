package com.payasia.common.dto;

import java.io.Serializable;

public class DeviceDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1289369638858348762L;
	private String macId ;
	private String resolution ;
	private String pixelDensity ;
	private String deviceId ;
	private String deviceOS ;
	private String deviceOSVersion ;
	private Long employeeActivationCodeId;
	private String deviceToken;
	
	public String getMacId() {
		return macId;
	}
	public void setMacId(String macId) {
		this.macId = macId;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getPixelDensity() {
		return pixelDensity;
	}
	public void setPixelDensity(String pixelDensity) {
		this.pixelDensity = pixelDensity;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getDeviceOS() {
		return deviceOS;
	}
	public void setDeviceOS(String deviceOS) {
		this.deviceOS = deviceOS;
	}
	public String getDeviceOSVersion() {
		return deviceOSVersion;
	}
	public void setDeviceOSVersion(String deviceOSVersion) {
		this.deviceOSVersion = deviceOSVersion;
	}
	public Long getEmployeeActivationCodeId() {
		return employeeActivationCodeId;
	}
	public void setEmployeeActivationCodeId(Long employeeActivationCodeId) {
		this.employeeActivationCodeId = employeeActivationCodeId;
	}
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	
	
	
	

}
