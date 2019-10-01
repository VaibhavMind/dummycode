package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Payslip_Upload_History database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Payslip_Upload_History")
public class PayslipUploadHistory extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Payslip_Upload_History_ID")
	private long payslipUploadHistoryId;

	@Column(name = "Payslip_Upload_Date")
	private Timestamp payslip_Upload_Date;

	@Column(name = "Year")
	private int year;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Month_ID")
	private MonthMaster monthMaster;

	public PayslipUploadHistory() {
	}

	public long getPayslipUploadHistoryId() {
		return this.payslipUploadHistoryId;
	}

	public void setPayslipUploadHistoryId(long payslipUploadHistoryId) {
		this.payslipUploadHistoryId = payslipUploadHistoryId;
	}

	public Timestamp getPayslip_Upload_Date() {
		return this.payslip_Upload_Date;
	}

	public void setPayslip_Upload_Date(Timestamp payslip_Upload_Date) {
		this.payslip_Upload_Date = payslip_Upload_Date;
	}

	public int getYear() {
		return this.year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public MonthMaster getMonthMaster() {
		return this.monthMaster;
	}

	public void setMonthMaster(MonthMaster monthMaster) {
		this.monthMaster = monthMaster;
	}

}