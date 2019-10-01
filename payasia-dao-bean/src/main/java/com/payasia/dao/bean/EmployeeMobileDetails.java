package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Employee_Mobile_Details")
public class EmployeeMobileDetails extends CompanyBaseEntity implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2754200879622562310L;

	@Id
	@GeneratedValue
	@Column(name = "Employee_Mobile_Details_Id")
	private long employeeMobileDetails;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Activation_Code_Id")
	private EmployeeActivationCode employeeActivationCode;

	@Column(name = "Resolution")
	private String resolution;

	@Column(name = "Pixel_Density")
	private String pixelDensity;

	@Column(name = "Active")
	private Boolean active;

	@Column(name = "Expired")
	private Boolean expired;

	@Column(name = "Device_ID")
	private String deviceID;

	@Column(name = "Device_IMEI")
	private String deviceIMEI;

	@Column(name = "Device_Mac_Id")
	private String deviceMacId;

	@Column(name = "Device_OS")
	private String deviceOS;

	@Column(name = "Device_OS_Version")
	private String deviceOSVersion;

	@Column(name = "Device_Token")
	private String deviceToken;

	@Column(name = "Activation_Date")
	private Timestamp activationDate;

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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
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

	public Timestamp getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Timestamp activationDate) {
		this.activationDate = activationDate;
	}

	public EmployeeActivationCode getEmployeeActivationCode() {
		return employeeActivationCode;
	}

	public void setEmployeeActivationCode(
			EmployeeActivationCode employeeActivationCode) {
		this.employeeActivationCode = employeeActivationCode;
	}

	public long getEmployeeMobileDetails() {
		return employeeMobileDetails;
	}

	public void setEmployeeMobileDetails(long employeeMobileDetails) {
		this.employeeMobileDetails = employeeMobileDetails;
	}

	public String getDeviceMacId() {
		return deviceMacId;
	}

	public void setDeviceMacId(String deviceMacId) {
		this.deviceMacId = deviceMacId;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

}
