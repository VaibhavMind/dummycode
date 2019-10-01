package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Employee_Document_History database table.
 * 
 */
@Entity
@Table(name = "Employee_Document_History")
public class EmployeeDocumentHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Document_History_ID")
	private long employeeDocumentHistoryId;

	 
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "Dynamic_Form_Table_Record_ID"),
	@JoinColumn(name = "Sequence") })
	private DynamicFormTableRecord dynamicFormTableRecord;

	@Column(name = "Field_Changed")
	private String fieldChanged;

	@Column(name = "Old_Value")
	private String oldValue;

	@Column(name = "New_Value")
	private String newValue;

	@Column(name = "Changed_Date")
	private Timestamp changedDate;

	@Column(name = "Changed_By")
	private String changedBy;

	public long getEmployeeDocumentHistoryId() {
		return employeeDocumentHistoryId;
	}

	public void setEmployeeDocumentHistoryId(long employeeDocumentHistoryId) {
		this.employeeDocumentHistoryId = employeeDocumentHistoryId;
	}

	public DynamicFormTableRecord getDynamicFormTableRecord() {
		return dynamicFormTableRecord;
	}

	public void setDynamicFormTableRecord(
			DynamicFormTableRecord dynamicFormTableRecord) {
		this.dynamicFormTableRecord = dynamicFormTableRecord;
	}

	public String getFieldChanged() {
		return fieldChanged;
	}

	public void setFieldChanged(String fieldChanged) {
		this.fieldChanged = fieldChanged;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public Timestamp getChangedDate() {
		return changedDate;
	}

	public void setChangedDate(Timestamp changedDate) {
		this.changedDate = changedDate;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

}