package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the HRIS_Change_Request database table.
 * 
 */
@Entity
@Table(name = "HRIS_Change_Request")
public class HRISChangeRequest extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HRIS_Change_Request_ID ")
	private long hrisChangeRequestId;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID ")
	private Employee employee;

	 
	@ManyToOne
	@JoinColumn(name = "Data_Dictionary_ID")
	private DataDictionary dataDictionary;

	@Column(name = "Old_Value ")
	private String oldValue;

	@Column(name = "New_Value ")
	private String newValue;

	 
	@ManyToOne
	@JoinColumn(name = "HRIS_Status_ID")
	private HRISStatusMaster hrisStatusMaster;

	 
	@OneToMany(mappedBy = "hrisChangeRequest")
	private Set<HRISChangeRequestWorkflow> changeRequestWorkflows;

	 
	@OneToMany(mappedBy = "hrisChangeRequest")
	private Set<HRISChangeRequestReviewer> hrisChangeRequestReviewers;

	@Column(name = "Table_Record_Sequence")
	private Integer tableRecordSequence;

	public HRISChangeRequest() {
	}

	public long getHrisChangeRequestId() {
		return hrisChangeRequestId;
	}

	public void setHrisChangeRequestId(long hrisChangeRequestId) {
		this.hrisChangeRequestId = hrisChangeRequestId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public DataDictionary getDataDictionary() {
		return dataDictionary;
	}

	public void setDataDictionary(DataDictionary dataDictionary) {
		this.dataDictionary = dataDictionary;
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

	public HRISStatusMaster getHrisStatusMaster() {
		return hrisStatusMaster;
	}

	public void setHrisStatusMaster(HRISStatusMaster hrisStatusMaster) {
		this.hrisStatusMaster = hrisStatusMaster;
	}

	public Set<HRISChangeRequestWorkflow> getChangeRequestWorkflows() {
		return changeRequestWorkflows;
	}

	public void setChangeRequestWorkflows(
			Set<HRISChangeRequestWorkflow> changeRequestWorkflows) {
		this.changeRequestWorkflows = changeRequestWorkflows;
	}

	public Set<HRISChangeRequestReviewer> getHrisChangeRequestReviewers() {
		return hrisChangeRequestReviewers;
	}

	public void setHrisChangeRequestReviewers(
			Set<HRISChangeRequestReviewer> hrisChangeRequestReviewers) {
		this.hrisChangeRequestReviewers = hrisChangeRequestReviewers;
	}

	public Integer getTableRecordSequence() {
		return tableRecordSequence;
	}

	public void setTableRecordSequence(Integer tableRecordSequence) {
		this.tableRecordSequence = tableRecordSequence;
	}

}