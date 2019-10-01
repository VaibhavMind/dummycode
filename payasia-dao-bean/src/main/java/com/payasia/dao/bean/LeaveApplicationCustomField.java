package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Leave_Application_Reviewer database table.
 * 
 */
@Entity
@Table(name = "Leave_Application_Custom_Field")
public class LeaveApplicationCustomField extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	@Column(name = "Leave_Application_Custom_Field_ID")
	private long leaveApplicationCustomFieldId;

	@Column(name = "Value")
	private String Value;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Application_ID")
	private LeaveApplication leaveApplication;

	 
	@ManyToOne
	@JoinColumn(name = "Custom_Field_ID")
	private LeaveSchemeTypeCustomField leaveSchemeTypeCustomField;

	public LeaveApplicationCustomField() {
	}

	public long getLeaveApplicationCustomFieldId() {
		return leaveApplicationCustomFieldId;
	}

	public void setLeaveApplicationCustomFieldId(
			long leaveApplicationCustomFieldId) {
		this.leaveApplicationCustomFieldId = leaveApplicationCustomFieldId;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public LeaveApplication getLeaveApplication() {
		return leaveApplication;
	}

	public void setLeaveApplication(LeaveApplication leaveApplication) {
		this.leaveApplication = leaveApplication;
	}

	public LeaveSchemeTypeCustomField getLeaveSchemeTypeCustomField() {
		return leaveSchemeTypeCustomField;
	}

	public void setLeaveSchemeTypeCustomField(
			LeaveSchemeTypeCustomField leaveSchemeTypeCustomField) {
		this.leaveSchemeTypeCustomField = leaveSchemeTypeCustomField;
	}

}