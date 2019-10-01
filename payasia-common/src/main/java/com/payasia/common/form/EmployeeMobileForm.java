package com.payasia.common.form;

import java.io.Serializable;

public class EmployeeMobileForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -324099426902202767L;
	private long employeeMobileDetailsId;
	private Long employeeId;
	private String employeeName;
	private String employeeNumber;
	private String firstName;
	private String status;
	

	private String resolution;

	private String pixelDensity;

	private String active;

	private String expired;

	private String deviceID;

	private String deviceIMEI;

	private String deviceOS;

	private String deviceOSVersion;

	private String activationCode;

	private String activationDate;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public long getEmployeeMobileDetailsId() {
		return employeeMobileDetailsId;
	}
	public void setEmployeeMobileDetailsId(long employeeMobileDetailsId) {
		this.employeeMobileDetailsId = employeeMobileDetailsId;
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
	
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getExpired() {
		return expired;
	}
	public void setExpired(String expired) {
		this.expired = expired;
	}
	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getDeviceIMEI() {
		return deviceIMEI;
	}
	public void setDeviceIMEI(String deviceIMEI) {
		this.deviceIMEI = deviceIMEI;
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
	public String getActivationCode() {
		return activationCode;
	}
	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}
	public String getActivationDate() {
		return activationDate;
	}
	
	
	
	

}
