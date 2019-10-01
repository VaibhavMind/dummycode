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

/**
 * The persistent class for the Report_Master database table.
 * 
 */
@Entity
@Table(name = "Company_Payslip_Release")
public class CompanyPayslipRelease extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Company_Payslip_Release_ID")
	private long companyPayslipReleaseId;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "Name")
	private String name;

	@Column(name = "Year")
	private int year;

	 
	@ManyToOne
	@JoinColumn(name = "Month_ID")
	private MonthMaster monthMaster;

	@Column(name = "Part")
	private int part;

	@Column(name = "Released")
	private boolean released;
	
	@Column(name = "Release_Date_Time")
	private Timestamp releaseDateTime;
	
	@Column(name = "Email_To")
	private String emailTo;
	
	@Column(name = "Send_Email")
	private Boolean sendEmail;

	public CompanyPayslipRelease() {
	}

	public long getCompanyPayslipReleaseId() {
		return companyPayslipReleaseId;
	}

	public void setCompanyPayslipReleaseId(long companyPayslipReleaseId) {
		this.companyPayslipReleaseId = companyPayslipReleaseId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public MonthMaster getMonthMaster() {
		return monthMaster;
	}

	public void setMonthMaster(MonthMaster monthMaster) {
		this.monthMaster = monthMaster;
	}

	public int getPart() {
		return part;
	}

	public void setPart(int part) {
		this.part = part;
	}

	public boolean isReleased() {
		return released;
	}

	public void setReleased(boolean released) {
		this.released = released;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getReleaseDateTime() {
		return releaseDateTime;
	}

	public void setReleaseDateTime(Timestamp releaseDateTime) {
		this.releaseDateTime = releaseDateTime;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public Boolean getSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(Boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

}