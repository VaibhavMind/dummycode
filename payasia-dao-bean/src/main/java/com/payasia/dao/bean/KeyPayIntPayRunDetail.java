package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the KeyPay_Int_PayRun_Details database table.
 * 
 */
@Entity
@Table(name = "KeyPay_Int_PayRun_Detail")
public class KeyPayIntPayRunDetail extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "KeyPay_Int_PayRun_Detail_ID")
	private long detailId;

	@ManyToOne
	@JoinColumn(name = "KeyPay_Int_PayRun_ID")
	private KeyPayIntPayRun keyPayIntPayRun;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@Column(name = "External_ID")
	private String externalId;

	@Column(name = "Leave_Category_ID")
	private Long leaveCategoryId;

	@Column(name = "Leave_Type_Name")
	private String leaveTypeName;

	@Column(name = "Leave_Entitlement")
	private BigDecimal leaveEntitlement;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Employee_Leave_Scheme_Type_History_ID")
	private Long employeeLeaveSchemeTypeHistoryId;

	public KeyPayIntPayRunDetail() {
	}

	public long getDetailId() {
		return detailId;
	}

	public void setDetailId(long detailId) {
		this.detailId = detailId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Long getLeaveCategoryId() {
		return leaveCategoryId;
	}

	public void setLeaveCategoryId(Long leaveCategoryId) {
		this.leaveCategoryId = leaveCategoryId;
	}

	public String getLeaveTypeName() {
		return leaveTypeName;
	}

	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
	}

	public BigDecimal getLeaveEntitlement() {
		return leaveEntitlement;
	}

	public void setLeaveEntitlement(BigDecimal leaveEntitlement) {
		this.leaveEntitlement = leaveEntitlement;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public KeyPayIntPayRun getKeyPayIntPayRun() {
		return keyPayIntPayRun;
	}

	public void setKeyPayIntPayRun(KeyPayIntPayRun keyPayIntPayRun) {
		this.keyPayIntPayRun = keyPayIntPayRun;
	}

	public Long getEmployeeLeaveSchemeTypeHistoryId() {
		return employeeLeaveSchemeTypeHistoryId;
	}

	public void setEmployeeLeaveSchemeTypeHistoryId(
			Long employeeLeaveSchemeTypeHistoryId) {
		this.employeeLeaveSchemeTypeHistoryId = employeeLeaveSchemeTypeHistoryId;
	}

}