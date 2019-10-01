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


/**
 * The persistent class for the Leave_Batch_Master database table.
 * 
 */
@Entity
@Table(name="Leave_Batch_Master")
public class LeaveBatchMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="Leave_Batch_ID")
	private long leaveBatchId;

	@Column(name="End_Date")
	private Timestamp endDate;

	@Column(name="Leave_Batch_Desc")
	private String leaveBatchDesc;

	@Column(name="Start_Date")
	private Timestamp startDate;

	 
    @ManyToOne
	@JoinColumn(name="Company_ID")
	private Company company;

    public LeaveBatchMaster() {
    }

	public long getLeaveBatchId() {
		return this.leaveBatchId;
	}

	public void setLeaveBatchId(long leaveBatchId) {
		this.leaveBatchId = leaveBatchId;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getLeaveBatchDesc() {
		return this.leaveBatchDesc;
	}

	public void setLeaveBatchDesc(String leaveBatchDesc) {
		this.leaveBatchDesc = leaveBatchDesc;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
}