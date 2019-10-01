package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Workday_Paygroup_Batch_Data")
public class WorkdayPaygroupBatchData extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Workday_Paygroup_Batch_Data_ID")
	private long workdayPaygroupBatchDataId;
	
	@ManyToOne
	@JoinColumn(name = "Workday_Paygroup_Batch_ID")
	private WorkdayPaygroupBatch workdayPaygroupBatch;
	
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;
	
	@Column(name = "Employee_XML")
	private String employeeXML;
	
	@Column(name = "New_Employee")
	private boolean isNewEmployee;
	
	@Column(name = "Effective_Date")
	private Timestamp effectiveDate;
	
	@Column(name = "Import_Type")
	private String importType;
	
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public long getWorkdayPaygroupBatchDataId() {
		return workdayPaygroupBatchDataId;
	}

	public void setWorkdayPaygroupBatchDataId(long workdayPaygroupBatchDataId) {
		this.workdayPaygroupBatchDataId = workdayPaygroupBatchDataId;
	}

	public WorkdayPaygroupBatch getWorkdayPaygroupBatch() {
		return workdayPaygroupBatch;
	}

	public void setWorkdayPaygroupBatch(WorkdayPaygroupBatch workdayPaygroupBatch) {
		this.workdayPaygroupBatch = workdayPaygroupBatch;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getEmployeeXML() {
		return employeeXML;
	}

	public void setEmployeeXML(String employeeXML) {
		this.employeeXML = employeeXML;
	}
	
	public boolean isNewEmployee() {
		return isNewEmployee;
	}

	public void setNewEmployee(boolean isNewEmployee) {
		this.isNewEmployee = isNewEmployee;
	}

	public Timestamp getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getImportType() {
		return importType;
	}

	public void setImportType(String importType) {
		this.importType = importType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}	
	
}
