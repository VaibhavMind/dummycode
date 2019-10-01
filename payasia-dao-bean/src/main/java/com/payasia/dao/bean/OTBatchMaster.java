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
 * The persistent class for the OT_Batch_Master database table.
 * 
 */
@Entity
@Table(name="OT_Batch_Master")
public class OTBatchMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="OT_Batch_ID")
	private long OTBatchId;

	@Column(name="End_Date")
	private Timestamp endDate;

	@Column(name="OT_Batch_Desc")
	private String OTBatchDesc;

	@Column(name="Start_Date")
	private Timestamp startDate;

	 
    @ManyToOne
	@JoinColumn(name="Company_ID")
	private Company company;

    public OTBatchMaster() {
    }

	public long getOTBatchId() {
		return this.OTBatchId;
	}

	public void setOTBatchId(long OTBatchId) {
		this.OTBatchId = OTBatchId;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getOTBatchDesc() {
		return this.OTBatchDesc;
	}

	public void setOTBatchDesc(String OTBatchDesc) {
		this.OTBatchDesc = OTBatchDesc;
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