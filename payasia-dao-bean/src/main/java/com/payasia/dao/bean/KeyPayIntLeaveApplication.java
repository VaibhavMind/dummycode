package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the KeyPay_Int_Leave_Application database table.
 * 
 */
@Entity
@Table(name = "KeyPay_Int_Leave_Application")
public class KeyPayIntLeaveApplication extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "KeyPay_Int_Leave_Application_ID")
	private long id;

	@ManyToOne
	@JoinColumn(name = "Leave_Application_ID")
	private LeaveApplication leaveApplication;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "Employee_Number")
	private String employeeNumber;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	@Column(name = "End_Date")
	private Timestamp endDate;

	@Column(name = "Hours")
	private BigDecimal Hours;

	@Column(name = "Leave_Type_Name")
	private String leaveTypeName;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Leave_Status")
	private String leaveStatus;

	@Column(name = "Sync_Status")
	private int syncStatus;

	@Column(name = "External_Leave_Request_ID")
	private Long externalLeaveRequestId;

	@Column(name = "Cancel_Leave_Application_ID")
	private Long cancelLeaveApplicationId;

	public KeyPayIntLeaveApplication() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getHours() {
		return Hours;
	}

	public void setHours(BigDecimal hours) {
		Hours = hours;
	}

	public String getLeaveTypeName() {
		return leaveTypeName;
	}

	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLeaveStatus() {
		return leaveStatus;
	}

	public void setLeaveStatus(String leaveStatus) {
		this.leaveStatus = leaveStatus;
	}

	public int getSyncStatus() {
		return syncStatus;
	}

	public void setSyncStatus(int syncStatus) {
		this.syncStatus = syncStatus;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public Long getExternalLeaveRequestId() {
		return externalLeaveRequestId;
	}

	public void setExternalLeaveRequestId(Long externalLeaveRequestId) {
		this.externalLeaveRequestId = externalLeaveRequestId;
	}

	public LeaveApplication getLeaveApplication() {
		return leaveApplication;
	}

	public void setLeaveApplication(LeaveApplication leaveApplication) {
		this.leaveApplication = leaveApplication;
	}

	public Long getCancelLeaveApplicationId() {
		return cancelLeaveApplicationId;
	}

	public void setCancelLeaveApplicationId(Long cancelLeaveApplicationId) {
		this.cancelLeaveApplicationId = cancelLeaveApplicationId;
	}

}