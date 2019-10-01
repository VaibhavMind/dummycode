package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Workday_Paygroup_Batch")
public class WorkdayPaygroupBatch extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Workday_Paygroup_Batch_ID")
	private long workdayPaygroupBatchId;
	
	@ManyToOne
	@JoinColumn(name = "Workday_App_Code_ID")
	private WorkdayAppCodeMaster workdayAppCode;
	
	@ManyToOne
	@JoinColumn(name = "Batch_Type_App_Code_ID")
	private WorkdayAppCodeMaster batchTypeAppCode;
	
	@Column(name = "Pay_Period_Start_Date")
	private Date payPeriodStartDate;
	
	@Column(name = "Pay_Period_End_Date")
	private Date payPeriodEndDate;
	
	@Column(name = "Is_Employee_Data")
	private boolean isEmployeeData;
	
	@Column(name = "Is_Latest")
	private boolean isLatest;
	
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;	

	public long getWorkdayPaygroupBatchId() {
		return workdayPaygroupBatchId;
	}

	public void setWorkdayPaygroupBatchId(long workdayPaygroupBatchId) {
		this.workdayPaygroupBatchId = workdayPaygroupBatchId;
	}

	public WorkdayAppCodeMaster getWorkdayAppCode() {
		return workdayAppCode;
	}

	public void setWorkdayAppCode(WorkdayAppCodeMaster workdayAppCode) {
		this.workdayAppCode = workdayAppCode;
	}
	
	public WorkdayAppCodeMaster getBatchTypeAppCode() {
		return batchTypeAppCode;
	}

	public void setBatchTypeAppCode(WorkdayAppCodeMaster batchTypeAppCode) {
		this.batchTypeAppCode = batchTypeAppCode;
	}

	public Date getPayPeriodStartDate() {
		return payPeriodStartDate;
	}

	public void setPayPeriodStartDate(Date payPeriodStartDate) {
		this.payPeriodStartDate = payPeriodStartDate;
	}

	public Date getPayPeriodEndDate() {
		return payPeriodEndDate;
	}

	public void setPayPeriodEndDate(Date payPeriodEndDate) {
		this.payPeriodEndDate = payPeriodEndDate;
	}

	public boolean isEmployeeData() {
		return isEmployeeData;
	}

	public void setEmployeeData(boolean isEmployeeData) {
		this.isEmployeeData = isEmployeeData;
	}

	public boolean isLatest() {
		return isLatest;
	}

	public void setLatest(boolean isLatest) {
		this.isLatest = isLatest;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
}
